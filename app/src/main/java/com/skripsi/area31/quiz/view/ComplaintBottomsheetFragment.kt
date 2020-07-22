package com.skripsi.area31.quiz.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skripsi.area31.R
import com.skripsi.area31.databinding.FragmentBottomsheetComplaintBinding
import com.skripsi.area31.utils.Constants.Companion.ASSIGNN_AT
import com.skripsi.area31.utils.Constants.Companion.QUIZ_DATE
import com.skripsi.area31.utils.Constants.Companion.SCORE_REPORT
import com.skripsi.area31.utils.Utils

class ComplaintBottomsheetFragment : BottomSheetDialogFragment() {
  private lateinit var binding: FragmentBottomsheetComplaintBinding
  private var quizDate: Long? = null
  private var assignAt: Long? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bottomsheet_complaint, container,
        false)
    return binding.root
  }

  @SuppressLint("SetTextI18n") override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    this.arguments?.getString(QUIZ_DATE)?.let {
      quizDate = it.toLong()
      binding.tvStartedComplaint.text = ": ${Utils.convertLongToTimeShortMonth(it.toLong())}"
    }
    this.arguments?.getString(ASSIGNN_AT)?.let {
      assignAt = it.toLong()
      binding.tvCompletedComplaint.text = ": ${Utils.convertLongToTimeShortMonth(it.toLong())}"
    }
    this.arguments?.getString(SCORE_REPORT)?.let {
      binding.tvScoreComplaint.text = ": ${it}"
    }
    with(binding) {
      if (assignAt != null && quizDate != null) {
        assignAt?.let { assignAt ->
          quizDate?.let { quizDate ->
            tvTimeTakenComplaint.text = ": ${Utils.convertLongToMinutes(assignAt - quizDate)}"
          }
        }
      }

      val activity = activity as QuizActivity
      btnSendComplaint.setOnClickListener {
        val complaint = complaintDesc.text.toString()
        if (complaint.isNotEmpty()) {
          activity.sendComplaint(complaint)
        } else {
          Toast.makeText(context, getString(R.string.complaint_description_empty),
              Toast.LENGTH_SHORT).show()
        }
      }
    }
  }
}