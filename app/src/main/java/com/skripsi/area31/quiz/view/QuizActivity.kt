package com.skripsi.area31.quiz.view

import android.app.ActionBar
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.skripsi.area31.BaseApp
import com.skripsi.area31.R
import com.skripsi.area31.core.base.BaseActivity
import com.skripsi.area31.core.model.Token
import com.skripsi.area31.databinding.ActivityQuizBinding
import com.skripsi.area31.quiz.injection.DaggerQuizComponent
import com.skripsi.area31.quiz.injection.QuizComponent
import com.skripsi.area31.quiz.model.AnsweredQuestion
import com.skripsi.area31.quiz.model.Question
import com.skripsi.area31.quiz.model.QuizResponse
import com.skripsi.area31.quiz.presenter.QuizPresenter
import com.skripsi.area31.utils.Constants
import com.skripsi.area31.utils.Constants.Companion.ID_QUIZ
import com.skripsi.area31.utils.Constants.Companion.TOTAL_QUESTIONS
import java.util.*
import javax.inject.Inject
import android.os.CountDownTimer as CountDownTimer1

class QuizActivity : BaseActivity(), QuizContract {
  private var daggerBuild: QuizComponent = DaggerQuizComponent.builder().baseComponent(
      BaseApp.instance.baseComponent).build()

  init {
    daggerBuild.inject(this)
  }

  @Inject lateinit var presenter: QuizPresenter
  @Inject lateinit var gson: Gson
  private lateinit var binding: ActivityQuizBinding
  private lateinit var accessToken: String
  private var idQuiz: String? = null
  private lateinit var countDownTimer: CountDownTimer1
  private val bottomsheetFragment = ExitQuizBottomsheetFragment()
  private val bottomsheetFragmentPreview = PreviewBottomsheetFragment()

  private var listQuestion: List<Question>? = null
  private val answeredQuestion = mutableMapOf<Int, AnsweredQuestion>()
  private var indexAt = 0

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = DataBindingUtil.setContentView(this, R.layout.activity_quiz)
    presenter.attach(this)
    presenter.subscribe()

    accessToken = gson.fromJson(
        this.getSharedPreferences(Constants.AUTHENTICATION, Context.MODE_PRIVATE)?.getString(
            Constants.TOKEN, null), Token::class.java).accessToken

    if (this.intent.getStringExtra(ID_QUIZ) != null) {
      idQuiz = this.intent.getStringExtra(ID_QUIZ)
    }
    idQuiz?.let {
      presenter.getQuizData(accessToken, it)
    }
    showProgress(true)

    with(binding) {
      btnExit.setOnClickListener {
        if (!bottomsheetFragment.isAdded) {
          this@QuizActivity.supportFragmentManager.let { fragmentManager ->
            bottomsheetFragment.show(fragmentManager, bottomsheetFragment.tag)
          }
        }
      }

      btnStartQuiz.setOnClickListener {
        layoutQuizDetails.visibility = View.GONE
        quizTime.visibility = View.VISIBLE
        layoutQuestion.visibility = View.VISIBLE
        btnNext.visibility = View.VISIBLE
        btnPreview.visibility = View.VISIBLE
        btnPreview.isClickable = true
        tvQuestionNumberHeader.visibility = View.VISIBLE

        tvQuestionNumber.text = "No. " + (indexAt + 1).toString()
        tvScore.text = "Score: " + listQuestion?.get(indexAt)?.score.toString()
        tvQuestion.text = listQuestion?.get(indexAt)?.question.toString()
        if (listQuestion?.get(indexAt)?.questionType == "MULTIPLECHOICE") {
          createRadioButton()
          radiobuttons.visibility = View.VISIBLE
          layoutAnswerEssay.visibility = View.GONE
        } else {
          radiobuttons.visibility = View.GONE
          layoutAnswerEssay.visibility = View.VISIBLE
        }
      }

      btnNext.setOnClickListener {
        SaveAnsweredQuestion()

        if (indexAt == 0) {
          btnBack.visibility = View.VISIBLE
          btnBack.isClickable = true
        }
        indexAt += 1
        btnBack.visibility = View.VISIBLE
        tvQuestionNumber.text = "No. " + (indexAt + 1).toString()
        tvScore.text = "Score: " + listQuestion?.get(indexAt)?.score.toString()
        tvQuestion.text = listQuestion?.get(indexAt)?.question.toString()
        tvQuestionNumberHeader.text = (indexAt + 1).toString() + "/" + listQuestion?.size.toString()

        if (listQuestion?.get(indexAt)?.questionType == "MULTIPLECHOICE") {
          createRadioButton()
          radiobuttons.visibility = View.VISIBLE
          layoutAnswerEssay.visibility = View.GONE
        } else {
          radiobuttons.visibility = View.GONE
          layoutAnswerEssay.visibility = View.VISIBLE
          if (answeredQuestion[indexAt] != null) {
            answerEssay.setText(answeredQuestion[indexAt]?.answer)
          }
        }
        if (indexAt + 1 == listQuestion?.size) {
          btnNext.visibility = View.INVISIBLE
          btnNext.isClickable = false
        }
      }

      btnBack.setOnClickListener {
        SaveAnsweredQuestion()

        if (indexAt + 1 == listQuestion?.size) {
          btnNext.visibility = View.VISIBLE
          btnNext.isClickable = true
        }
        indexAt -= 1
        tvQuestionNumber.text = "No. " + (indexAt + 1).toString()
        tvScore.text = "Score: " + listQuestion?.get(indexAt)?.score.toString()
        tvQuestion.text = listQuestion?.get(indexAt)?.question.toString()
        tvQuestionNumberHeader.text = (indexAt + 1).toString() + "/" + listQuestion?.size.toString()

        if (listQuestion?.get(indexAt)?.questionType == "MULTIPLECHOICE") {
          createRadioButton()
          radiobuttons.visibility = View.VISIBLE
          layoutAnswerEssay.visibility = View.GONE
        } else {
          radiobuttons.visibility = View.GONE
          layoutAnswerEssay.visibility = View.VISIBLE
          if (answeredQuestion[indexAt] != null) {
            answerEssay.setText(answeredQuestion[indexAt]?.answer)
          }
        }
        if (indexAt == 0) {
          btnBack.visibility = View.INVISIBLE
          btnBack.isClickable = false
        }
      }

      btnPreview.setOnClickListener {
        SaveAnsweredQuestion()
        val bundle = Bundle()
        listQuestion?.size?.let { totalQuestions -> bundle.putInt(TOTAL_QUESTIONS, totalQuestions) }
        bottomsheetFragmentPreview.arguments = bundle
        if (!bottomsheetFragmentPreview.isAdded) {
          this@QuizActivity.supportFragmentManager.let { fragmentManager ->
            bottomsheetFragmentPreview.show(fragmentManager, bottomsheetFragmentPreview.tag)
          }
        }
      }
    }
  }

  private fun ActivityQuizBinding.SaveAnsweredQuestion() {
    if (listQuestion?.get(indexAt)?.questionType == "MULTIPLECHOICE") {
      val selectedRadioButton = findViewById<RadioButton>(radiobuttons.checkedRadioButtonId)
      if (selectedRadioButton != null) {
        listQuestion?.get(indexAt)?.idQuestion?.let { idQuestion ->
          answeredQuestion.put(indexAt,
              AnsweredQuestion(selectedRadioButton.text.toString(), idQuestion))
        }
      }
    } else {
      if (answerEssay.text.toString() != "") {
        listQuestion?.get(indexAt)?.idQuestion?.let { idQuestion ->
          answeredQuestion.put(indexAt, AnsweredQuestion(answerEssay.text.toString(), idQuestion))
        }
      }
    }
  }

  override fun getQuizDataSuccess(quizResponse: QuizResponse) {
    showProgress(false)
    with(binding) {
      tvTimeLeft.visibility = View.VISIBLE
      quizTimeDetails.visibility = View.VISIBLE
      btnStartQuiz.visibility = View.VISIBLE
      tvQuizTitle.text = quizResponse.title
      tvQuizDetails.text = quizResponse.description
      printDifferenceDateForHours(
          (quizResponse.startDate + quizResponse.duration - 5000) - Calendar.getInstance(
              TimeZone.getTimeZone(getString(R.string.asiajakarta))).timeInMillis)
      //5000 -> minus 5s from real time

      listQuestion = quizResponse.questionList
      indexAt = 0
      btnBack.visibility = View.GONE
      listQuestion?.let {
        tvQuestionNumberHeader.text = (indexAt + 1).toString() + "/" + it.size.toString()
      }
    }
    println(quizResponse)
  }

  private fun createRadioButton() {
    val rgp = binding.radiobuttons as RadioGroup
    rgp.removeAllViews()
    rgp.invalidate()
    var rprms: RadioGroup.LayoutParams

    listQuestion?.get(indexAt)?.answer?.size?.let {
      for (i in 0 until it) {
        val radioButton = RadioButton(this)
        radioButton.text = listQuestion?.get(indexAt)?.answer?.get(i)
        radioButton.id = View.generateViewId()
        rprms = RadioGroup.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
            ActionBar.LayoutParams.WRAP_CONTENT)
        rprms.setMargins(0, 12, 12, 12)
        if (answeredQuestion[indexAt] != null && radioButton.text == answeredQuestion[indexAt]?.answer) {
          rgp.check(i)
          radioButton.isChecked = true
        }
        rgp.addView(radioButton, rprms)
      }
    }
  }

  fun getAnsweredQuestion() : MutableMap<Int, AnsweredQuestion> {
    return this.answeredQuestion
  }

  private fun printDifferenceDateForHours(different: Long) {
    countDownTimer = object : CountDownTimer1(different, 1000) {

      override fun onTick(millisUntilFinished: Long) {
        var diff = millisUntilFinished
        val secondsInMilli: Long = 1000
        val minutesInMilli = secondsInMilli * 60
        val hoursInMilli = minutesInMilli * 60

        val elapsedHours = diff / hoursInMilli
        diff %= hoursInMilli

        val elapsedMinutes = diff / minutesInMilli
        diff %= minutesInMilli

        val elapsedSeconds = diff / secondsInMilli

        binding.quizTimeDetails.text = "$elapsedHours : $elapsedMinutes : $elapsedSeconds"
        binding.quizTime.text = "$elapsedHours : $elapsedMinutes : $elapsedSeconds"
      }

      override fun onFinish() {
        binding.quizTimeDetails.text = "Time's up!"
        binding.quizTime.text = "Time's up!"
      }
    }.start()
  }

  override fun onBackPressed() {
    if (!bottomsheetFragment.isAdded) {
      this@QuizActivity.supportFragmentManager.let { fragmentManager ->
        bottomsheetFragment.show(fragmentManager, bottomsheetFragment.tag)
      }
    }
  }

  fun jumpToQuestion(questionNumber: Int) {
    bottomsheetFragmentPreview.dismiss()
    indexAt = questionNumber
    with(binding) {
      tvQuestionNumber.text = "No. " + (indexAt + 1).toString()
      tvScore.text = "Score: " + listQuestion?.get(indexAt)?.score.toString()
      tvQuestion.text = listQuestion?.get(indexAt)?.question.toString()
      tvQuestionNumberHeader.text = (indexAt + 1).toString() + "/" + listQuestion?.size.toString()

      if (listQuestion?.get(indexAt)?.questionType == "MULTIPLECHOICE") {
        createRadioButton()
        radiobuttons.visibility = View.VISIBLE
        layoutAnswerEssay.visibility = View.GONE
      } else {
        radiobuttons.visibility = View.GONE
        layoutAnswerEssay.visibility = View.VISIBLE
        if (answeredQuestion[indexAt] != null) {
          answerEssay.setText(answeredQuestion[indexAt]?.answer)
        }
      }
      when {
        indexAt + 1 == listQuestion?.size -> {
          btnNext.visibility = View.INVISIBLE
          btnNext.isClickable = false
          btnBack.visibility = View.VISIBLE
          btnBack.isClickable = true
        }
        indexAt == 0 -> {
          btnBack.visibility = View.INVISIBLE
          btnBack.isClickable = false
          btnNext.visibility = View.VISIBLE
          btnNext.isClickable = true
        }
        else -> {
          btnNext.visibility = View.VISIBLE
          btnNext.isClickable = true
          btnBack.visibility = View.VISIBLE
          btnBack.isClickable = true
        }
      }
    }
  }

  fun dismissDialog() {
    bottomsheetFragment.dismiss()
  }

  fun leaveQuiz() {
    bottomsheetFragment.dismiss()
    this@QuizActivity.finish()
  }

  override fun onFailed(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
  }

  private fun showProgress(show: Boolean) {
    with(binding) {
      if (show) {
        progressBar.visibility = View.VISIBLE
      } else {
        progressBar.visibility = View.GONE
      }
    }
  }

  override fun onDestroy() {
    countDownTimer.cancel()
    super.onDestroy()
  }
}
