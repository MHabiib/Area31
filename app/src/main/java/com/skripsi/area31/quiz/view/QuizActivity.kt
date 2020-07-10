package com.skripsi.area31.quiz.view

import android.app.ActionBar
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.skripsi.area31.BaseApp
import com.skripsi.area31.R
import com.skripsi.area31.core.base.BaseActivity
import com.skripsi.area31.core.model.Token
import com.skripsi.area31.databinding.ActivityQuizBinding
import com.skripsi.area31.quiz.injection.DaggerQuizComponent
import com.skripsi.area31.quiz.injection.QuizComponent
import com.skripsi.area31.quiz.model.*
import com.skripsi.area31.quiz.presenter.QuizPresenter
import com.skripsi.area31.utils.Constants
import com.skripsi.area31.utils.Constants.Companion.ID_QUIZ
import com.skripsi.area31.utils.Constants.Companion.QUIZ_SCORE
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
  private var score: String? = null
  private lateinit var countDownTimer: CountDownTimer1
  private val bottomsheetFragment = ExitQuizBottomsheetFragment()
  private val bottomsheetFragmentPreview = PreviewBottomsheetFragment()
  private lateinit var rgp: RadioGroup
  private var completed = false
  private var fcm = ""

  private var listQuestion: List<Question>? = null
  private var listQuestionReport: List<ReportQuizResponse>? = null
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
    if (this.intent.getStringExtra(QUIZ_SCORE) != null) {
      score = this.intent.getStringExtra(QUIZ_SCORE)
    }
    idQuiz?.let {
      if (score == null) {
        presenter.getQuizData(accessToken, it)
      } else {
        presenter.getQuizReport(accessToken, it)
      }
    }
    showProgress(true)

    with(binding) {
      rgp = binding.radiobuttons
      btnExit.setOnClickListener {
        if (!bottomsheetFragment.isAdded && listQuestion != null) {
          this@QuizActivity.supportFragmentManager.let { fragmentManager ->
            bottomsheetFragment.show(fragmentManager, bottomsheetFragment.tag)
          }
        } else {
          finish()
        }
      }

      btnReviewQuiz.setOnClickListener {
        tvClearAnswer.visibility = View.GONE
        layoutQuizDetails.visibility = View.GONE
        layoutQuestion.visibility = View.VISIBLE
        btnNext.visibility = View.VISIBLE
        btnComplaint.visibility = View.VISIBLE
        btnComplaint.isClickable = true
        tvQuestionNumberHeader.visibility = View.VISIBLE
        tvQuestionNumber.text = "No. " + (indexAt + 1).toString()
        tvScore.text = "Score: " + listQuestionReport?.get(
            indexAt)?.score.toString() + " | Your Score: " + listQuestionReport?.get(
            indexAt)?.studentScore.toString()
        tvQuestionNumberHeader.text = (indexAt + 1).toString() + "/" + listQuestionReport?.size.toString()
        tvQuestion.text = listQuestionReport?.get(indexAt)?.question.toString()
        if (listQuestionReport?.get(indexAt)?.questionType == "MULTIPLECHOICE") {
          createRadioButtonReview()
          radiobuttons.visibility = View.VISIBLE
          layoutAnswerEssay.visibility = View.GONE
          layoutAnswerEssayStudent.visibility = View.GONE
          tvYourAnswer.visibility = View.VISIBLE
          tvYourAnswer.text = "Your answer : " + listQuestionReport?.get(indexAt)?.studentAnswer
        } else {
          radiobuttons.visibility = View.GONE
          tvYourAnswer.visibility = View.GONE
          layoutAnswerEssay.visibility = View.VISIBLE
          layoutAnswerEssayStudent.visibility = View.VISIBLE
          answerEssay.isFocusable = false
          answerEssayStudent.isFocusable = false
          answerEssay.setText(listQuestionReport?.get(indexAt)?.studentAnswer)
          answerEssayStudent.setText(listQuestionReport?.get(indexAt)?.answerKey)
        }
      }

      btnStartQuiz.setOnClickListener {
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
          if (task.isSuccessful) {
            fcm = task.result?.token.toString() //asyc
          }
        }
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
        if (listQuestion != null) {
          SaveAnsweredQuestion()
        }
        indexAt += 1
        btnBack.visibility = View.VISIBLE
        btnBack.isClickable = true
        tvQuestionNumber.text = "No. " + (indexAt + 1).toString()
        if (listQuestion != null) {
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
        } else {
          tvQuestionNumber.text = "No. " + (indexAt + 1).toString()
          tvScore.text = "Score: " + listQuestionReport?.get(
              indexAt)?.score.toString() + " | Your Score: " + listQuestionReport?.get(
              indexAt)?.studentScore.toString()
          tvQuestion.text = listQuestionReport?.get(indexAt)?.question.toString()
          tvQuestionNumberHeader.text = (indexAt + 1).toString() + "/" + listQuestionReport?.size.toString()
          if (listQuestionReport?.get(indexAt)?.questionType == "MULTIPLECHOICE") {
            createRadioButtonReview()
            radiobuttons.visibility = View.VISIBLE
            layoutAnswerEssay.visibility = View.GONE
            layoutAnswerEssayStudent.visibility = View.GONE
            tvYourAnswer.visibility = View.VISIBLE
            tvYourAnswer.text = "Your answer : " + listQuestionReport?.get(indexAt)?.studentAnswer
          } else {
            radiobuttons.visibility = View.GONE
            tvYourAnswer.visibility = View.GONE
            layoutAnswerEssay.visibility = View.VISIBLE
            layoutAnswerEssayStudent.visibility = View.VISIBLE
            answerEssay.isFocusable = false
            answerEssayStudent.isFocusable = false
            answerEssay.setText(listQuestionReport?.get(indexAt)?.studentAnswer)
            answerEssayStudent.setText(listQuestionReport?.get(indexAt)?.answerKey)
          }
          if (indexAt + 1 == listQuestionReport?.size) {
            btnNext.visibility = View.INVISIBLE
            btnNext.isClickable = false
          }
        }
      }

      btnBack.setOnClickListener {
        if (listQuestion != null) {
          SaveAnsweredQuestion()
        }
        btnNext.visibility = View.VISIBLE
        btnNext.isClickable = true
        indexAt -= 1
        tvQuestionNumber.text = "No. " + (indexAt + 1).toString()
        if (listQuestion != null) {
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
        } else {
          tvQuestionNumber.text = "No. " + (indexAt + 1).toString()
          tvScore.text = "Score: " + listQuestionReport?.get(
              indexAt)?.score.toString() + " | Your Score: " + listQuestionReport?.get(
              indexAt)?.studentScore.toString()
          tvQuestion.text = listQuestionReport?.get(indexAt)?.question.toString()
          tvQuestionNumberHeader.text = (indexAt + 1).toString() + "/" + listQuestionReport?.size.toString()
          if (listQuestionReport?.get(indexAt)?.questionType == "MULTIPLECHOICE") {
            createRadioButtonReview()
            radiobuttons.visibility = View.VISIBLE
            layoutAnswerEssay.visibility = View.GONE
            layoutAnswerEssayStudent.visibility = View.GONE
            tvYourAnswer.visibility = View.VISIBLE
            tvYourAnswer.text = "Your answer : " + listQuestionReport?.get(indexAt)?.studentAnswer
          } else {
            radiobuttons.visibility = View.GONE
            tvYourAnswer.visibility = View.GONE
            layoutAnswerEssay.visibility = View.VISIBLE
            layoutAnswerEssayStudent.visibility = View.VISIBLE
            answerEssay.isFocusable = false
            answerEssayStudent.isFocusable = false
            answerEssay.setText(listQuestionReport?.get(indexAt)?.studentAnswer)
            answerEssayStudent.setText(listQuestionReport?.get(indexAt)?.answerKey)
          }
          if (indexAt == 0) {
            btnBack.visibility = View.INVISIBLE
            btnBack.isClickable = false
          }
        }
      }

      btnPreview.setOnClickListener {
        SaveAnsweredQuestion()
        val bundle = Bundle()
        listQuestion?.size?.let { totalQuestions ->
          bundle.putInt(TOTAL_QUESTIONS, totalQuestions)
        }
        bottomsheetFragmentPreview.arguments = bundle
        if (!bottomsheetFragmentPreview.isAdded) {
          this@QuizActivity.supportFragmentManager.let { fragmentManager ->
            bottomsheetFragmentPreview.show(fragmentManager, bottomsheetFragmentPreview.tag)
          }
        }
      }

      tvClearAnswer.setOnClickListener {
        answeredQuestion.remove(indexAt)
        if (listQuestion?.get(indexAt)?.questionType == "MULTIPLECHOICE") {
          rgp.clearCheck()
        } else {
          answerEssay.text?.clear()
        }
      }

      btnGoToQuizList.setOnClickListener {
        finish()
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
      } else {
        answeredQuestion.remove(indexAt)
        answerEssay.text?.clear()
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
  }

  override fun getQuizReportuccess(quizResponse: QuizReport) {
    showProgress(false)
    with(binding) {
      btnReviewQuiz.visibility = View.VISIBLE
      tvQuizTitle.text = quizResponse.title
      tvQuizDetails.text = quizResponse.description
      listQuestionReport = quizResponse.reportQuizResponses
      indexAt = 0
      listQuestionReport?.let {
        tvQuestionNumberHeader.text = (indexAt + 1).toString() + "/" + it.size.toString()
      }
    }
  }

  private fun createRadioButton() {
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

  private fun createRadioButtonReview() {
    rgp.removeAllViews()
    rgp.invalidate()
    var rprms: RadioGroup.LayoutParams

    listQuestionReport?.get(indexAt)?.answer?.size?.let {
      for (i in 0 until it) {
        val radioButton = RadioButton(this)
        radioButton.text = listQuestionReport?.get(indexAt)?.answer?.get(i)
        radioButton.id = View.generateViewId()
        rprms = RadioGroup.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
            ActionBar.LayoutParams.WRAP_CONTENT)
        rprms.setMargins(0, 12, 12, 12)
        if (radioButton.text == listQuestionReport?.get(indexAt)?.answerKey) {
          rgp.check(i)
          radioButton.text = listQuestionReport?.get(indexAt)?.answer?.get(i) + " (Answer Key)"
          radioButton.isChecked = true
        }
        radioButton.isClickable = false
        rgp.addView(radioButton, rprms)
      }
    }
  }

  fun getAnsweredQuestion(): MutableMap<Int, AnsweredQuestion> {
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
    if (!bottomsheetFragment.isAdded && listQuestion != null) {
      this@QuizActivity.supportFragmentManager.let { fragmentManager ->
        bottomsheetFragment.show(fragmentManager, bottomsheetFragment.tag)
      }
    } else {
      finish()
    }
  }

  fun submit(haventAnsewerdNumber: String) {
    completed = true
    var answerAll = true

    bottomsheetFragmentPreview.dismiss()
    val mAlertDialog = AlertDialog.Builder(this@QuizActivity)
    val message: String = if (answeredQuestion.size == listQuestion?.size) {
      "You answered all the questions !"
    } else {
      answerAll = false
      "You haven't answer question number $haventAnsewerdNumber"
    }

    mAlertDialog.setTitle("Are you sure to submit this quiz?")
    mAlertDialog.setMessage(message)
    mAlertDialog.setPositiveButton("Yes") { dialog, id ->
      if (!answerAll) {
        val haventAnswerdList = haventAnsewerdNumber.split(" ").toTypedArray()
        for (havent in haventAnswerdList) {
          if (havent != "") {
            val index = havent.toInt() - 1
            listQuestion?.get(index)?.idQuestion?.let { AnsweredQuestion(" ", it) }?.let {
              answeredQuestion.put(index, it)
            }
          }
        }
      }
      idQuiz?.let {
        presenter.submitQuiz(fcm, accessToken, it, answeredQuestion)
      }
      with(binding) {
        layoutQuizDetails.visibility = View.VISIBLE
        btnGoToQuizList.visibility = View.VISIBLE
        quizTime.visibility = View.GONE
        layoutQuestion.visibility = View.GONE
        btnExit.visibility = View.GONE
        btnBack.visibility = View.GONE
        btnNext.visibility = View.GONE
        btnNext.visibility = View.GONE
        btnPreview.visibility = View.GONE
        btnPreview.isClickable = false
        tvQuestionNumberHeader.visibility = View.GONE
        tvTimeLeft.visibility = View.GONE
        quizTimeDetails.visibility = View.GONE
        btnStartQuiz.visibility = View.GONE
        tvQuizDetails.text = "You have completed this quiz"
      }
    }
    mAlertDialog.setNegativeButton("No") { dialog, id ->
      dialog.dismiss()
    }
    mAlertDialog.show()
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

  override fun submitQuizSuccess(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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
    if (::countDownTimer.isInitialized) {
      countDownTimer.cancel()
    }
    super.onDestroy()
  }
}
