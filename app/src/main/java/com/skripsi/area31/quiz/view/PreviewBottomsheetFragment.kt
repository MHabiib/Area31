package com.skripsi.area31.quiz.view

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skripsi.area31.R
import com.skripsi.area31.databinding.FragmentBottomsheetPreviewBinding
import com.skripsi.area31.quiz.model.AnsweredQuestion
import com.skripsi.area31.utils.Constants.Companion.PREVIEW_FRAGMENT
import com.skripsi.area31.utils.Constants.Companion.TOTAL_QUESTIONS

class PreviewBottomsheetFragment : BottomSheetDialogFragment() {
  private lateinit var binding: FragmentBottomsheetPreviewBinding
  private var answeredQuestion = mutableMapOf<Int, AnsweredQuestion>()
  private var totalQuestions: Int = 0

  private lateinit var layout: ScrollView
  private lateinit var layoutPark: LinearLayout
  private lateinit var parkingLayout: LinearLayout
  private lateinit var haventAnsewerdNumber: String

  companion object {
    const val TAG: String = PREVIEW_FRAGMENT
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bottomsheet_preview, container,
        false)
    layout = binding.layoutQuestionNumber
    binding.btnSubmit.setOnClickListener {
      val activity = activity as QuizActivity
      activity.submit(haventAnsewerdNumber)
    }
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    this.arguments?.getInt(TOTAL_QUESTIONS)?.let {
      totalQuestions = it
    }
    with(binding) {
      val activity = activity as QuizActivity
      answeredQuestion = activity.getAnsweredQuestion()
    }
    showQuestionNumber()
  }

  private fun showQuestionNumber() {
    haventAnsewerdNumber = ""
    layoutPark = LinearLayout(context)
    val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT)

    layoutPark.apply {
      orientation = LinearLayout.VERTICAL
      layoutParams = params
      setPadding(12, 12, 12, 12)
    }

    layout.addView(layoutPark)


    for (i in 0 until totalQuestions) {

      if (i == 0 || i % 5 == 0) {
        parkingLayout = LinearLayout(context)
        parkingLayout.orientation = LinearLayout.HORIZONTAL
        layoutPark.addView(parkingLayout)
      }
      val dynamicButton = Button(context)

      val lp = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
      lp.weight = 1f
      dynamicButton.layoutParams = lp

      val parameter = dynamicButton.layoutParams as LinearLayout.LayoutParams
      parameter.setMargins(12, 12, 12, 12)

      dynamicButton.layoutParams = parameter
      dynamicButton.text = (i + 1).toString()
      dynamicButton.typeface = Typeface.DEFAULT_BOLD
      if (answeredQuestion[i]?.answer != null) {
        context?.resources?.getColor(R.color.white)?.let { dynamicButton.setTextColor(it) }
        dynamicButton.setBackgroundResource(R.drawable.card_primary)
      } else {
        haventAnsewerdNumber += ((i + 1).toString() + " ")
        context?.resources?.getColor(R.color.darkGrey)?.let { dynamicButton.setTextColor(it) }
        dynamicButton.setBackgroundResource(R.drawable.card_base_white)
      }
      dynamicButton.setOnClickListener(View.OnClickListener {
        val activity = activity as QuizActivity
        activity.jumpToQuestion(i)
      })
      parkingLayout.addView(dynamicButton)
    }
  }
}