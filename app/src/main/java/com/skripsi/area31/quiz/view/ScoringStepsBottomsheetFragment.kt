package com.skripsi.area31.quiz.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skripsi.area31.R
import com.skripsi.area31.databinding.FragmentBottomsheetScoringStepBinding
import com.skripsi.area31.quiz.adapter.ScoringStepsAdapter
import com.skripsi.area31.utils.Constants.Companion.SCORING_STEPS_ANSWER
import com.skripsi.area31.utils.Constants.Companion.SCORING_STEPS_ANSWER_KEY
import com.skripsi.area31.utils.Constants.Companion.SCORING_STEPS_ANSWER_SCORE
import com.skripsi.area31.utils.Constants.Companion.SCORING_STEPS_ANSWER_SCORE_STUDENT
import java.math.RoundingMode
import java.text.DecimalFormat

class ScoringStepsBottomsheetFragment : BottomSheetDialogFragment() {
  private lateinit var binding: FragmentBottomsheetScoringStepBinding
  private lateinit var scoringStepsAdapter: ScoringStepsAdapter
  private var currentPage = 0
  private var score = ""
  private var isLastPage = false
  private var isLoading = false

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val dialog = BottomSheetDialog(requireContext(), theme)
    dialog.setOnShowListener {

      val bottomSheetDialog = it as BottomSheetDialog
      val parentLayout = bottomSheetDialog.findViewById<View>(
          com.google.android.material.R.id.design_bottom_sheet)
      parentLayout?.let { it ->
        val behaviour = BottomSheetBehavior.from(it)
        setupFullHeight(it)
        behaviour.state = BottomSheetBehavior.STATE_EXPANDED
      }
    }
    return dialog
  }

  private fun setupFullHeight(bottomSheet: View) {
    val layoutParams = bottomSheet.layoutParams
    layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
    bottomSheet.layoutParams = layoutParams
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    disableBottomSheetDraggableBehavior()
  }

  private fun disableBottomSheetDraggableBehavior() {
    this.isCancelable = false
    this.dialog?.setCanceledOnTouchOutside(true)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bottomsheet_scoring_step,
        container, false)
    setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppBottomSheetDialogTheme)
    val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    with(binding) {
      scoringStepsAdapter = ScoringStepsAdapter()
      rvWordList.layoutManager = linearLayoutManager
      rvWordList.adapter = scoringStepsAdapter
      rvWordList.isNestedScrollingEnabled = false

      ibBack.setOnClickListener {
        scoringStepsAdapter.clear()
        scoringStepsAdapter.notifyDataSetChanged()
        currentPage = 0
        isLastPage = false
        val activity = activity as QuizActivity
        activity.dismissScoringDialog()
      }
    }
    return binding.root
  }

  @SuppressLint("SetTextI18n") override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val activity = activity as QuizActivity
    val ratioList = activity.getRatioList()
    ratioList?.let { list ->
      list.sortBy {
        it.word
      }
      scoringStepsAdapter.addAll(list)
    }

    with(binding) {
      if (ratioList != null) {
        val ratioAnswer = mutableListOf<Int>()
        val ratioAnswerKey = mutableListOf<Int>()
        var stringStepTwo = ""
        var stringStepThree = ""
        var scoreStepThree = 0
        var stringStepFour = ""
        var scoreStepFour = 0
        var scoreStepTwo = 0
        for (ratio in ratioList) {
          ratioAnswer.add(ratio.ratioAnswer)
          ratioAnswerKey.add(ratio.ratioAnswerKey)
          stringStepTwo = if (stringStepTwo == "") {
            stringStepTwo.plus(
                "(" + ratio.ratioAnswerKey.toString() + "." + ratio.ratioAnswer.toString() + ")")
          } else {
            stringStepTwo.plus(
                " + " + "(" + ratio.ratioAnswerKey.toString() + "." + ratio.ratioAnswer.toString() + ")")
          }
          scoreStepTwo += ratio.ratioAnswerKey * ratio.ratioAnswer
          stringStepThree = if (stringStepThree == "") {
            stringStepThree.plus(ratio.ratioAnswerKey.toString() + "\u00B2")
          } else {
            stringStepThree.plus(" + " + ratio.ratioAnswerKey.toString() + "\u00B2")
          }
          scoreStepThree += ratio.ratioAnswerKey * ratio.ratioAnswerKey
          stringStepFour = if (stringStepFour == "") {
            stringStepFour.plus(ratio.ratioAnswer.toString() + "\u00B2")
          } else {
            stringStepFour.plus(" + " + ratio.ratioAnswer.toString() + "\u00B2")
          }
          scoreStepFour += ratio.ratioAnswer * ratio.ratioAnswer
        }

        tvAnswer.text = getString(R.string.your_answer_two_dots) + " " + ratioAnswer.toString()
        tvAnswerKey.text = getString(R.string.answer_key_two_dots) + " " + ratioAnswerKey.toString()
        tvStepTwo.text = "$stringStepTwo = $scoreStepTwo"
        tvStepThree.text = "\u221a ($stringStepThree) = \u221a$scoreStepThree"
        tvStepFour.text = "\u221a ($stringStepFour) = \u221a$scoreStepFour"

        val outputScore = scoreStepTwo / (Math.sqrt(scoreStepThree.toDouble()) * Math.sqrt(
            scoreStepFour.toDouble()))
        val df = DecimalFormat("#.###")
        df.roundingMode = RoundingMode.CEILING
        tvStepFive.text = "$scoreStepTwo/(√$scoreStepThree \u2022 √$scoreStepFour) = ${df.format(
            outputScore)}"
        score = df.format(outputScore)
      }
    }

  }

  @SuppressLint("SetTextI18n") override fun onResume() {
    super.onResume()

    this.arguments?.getString(SCORING_STEPS_ANSWER)?.let {
      binding.answerEssay.setText(it)
    }
    this.arguments?.getString(SCORING_STEPS_ANSWER_KEY)?.let {
      binding.answerEssayStudent.setText(it)
    }
    this.arguments?.getInt(SCORING_STEPS_ANSWER_SCORE)?.let {
      binding.tvStepSix.text = getString(R.string.bobot_nilai_dari) + " " + it + getString(
          R.string.maka_hasil_yang) + " " + score + " " + getString(
          R.string.dikalikan_dengan) + " " + it + " " + getString(R.string.dengan_pembulatan_maka)
    }
    this.arguments?.getInt(SCORING_STEPS_ANSWER_SCORE_STUDENT)?.let {
      binding.tvScore.text = it.toString()
    }
  }

  override fun onCancel(dialog: DialogInterface) {
    scoringStepsAdapter.clear()
    scoringStepsAdapter.notifyDataSetChanged()
    currentPage = 0
    isLastPage = false
    super.onCancel(dialog)
  }
}