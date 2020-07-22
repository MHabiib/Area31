package com.skripsi.area31.complaint.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skripsi.area31.R
import com.skripsi.area31.databinding.FragmentBottomsheetComplaintDetailsBinding
import com.skripsi.area31.utils.Constants.Companion.ASSIGNN_AT
import com.skripsi.area31.utils.Constants.Companion.DESCRIPTION
import com.skripsi.area31.utils.Constants.Companion.QUIZ_DATE
import com.skripsi.area31.utils.Constants.Companion.REASON
import com.skripsi.area31.utils.Constants.Companion.SCORE_REPORT
import com.skripsi.area31.utils.Utils

class ComplainDetailsBottomsheet : BottomSheetDialogFragment() {
  private lateinit var binding: FragmentBottomsheetComplaintDetailsBinding
  private var quizDate: Long? = null
  private var assignAt: Long? = null

  @SuppressLint("SetTextI18n") override fun onCreateView(inflater: LayoutInflater,
      container: ViewGroup?, savedInstanceState: Bundle?): View? {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bottomsheet_complaint_details,
        container, false)
    setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppBottomSheetDialogTheme)
    this.arguments?.getString(QUIZ_DATE)?.let {
      quizDate = it.toLong()
      binding.tvStartedComplaint.text = ": ${Utils.convertLongToTimeShortMonth(it.toLong())}"
    }
    this.arguments?.getString(ASSIGNN_AT)?.let {
      assignAt = it.toLong()
      binding.tvCompletedComplaint.text = ": ${Utils.convertLongToTimeShortMonth(it.toLong())}"
    }
    this.arguments?.getString(DESCRIPTION)?.let {
      binding.complaintDesc.setText(it)
    }
    this.arguments?.getString(REASON)?.let {
      if (it != null) {
        binding.layoutReasonDesc.visibility = View.VISIBLE
        binding.reasonDesc.setText(it)
      }
    }
    this.arguments?.getString(SCORE_REPORT)?.let {
      binding.tvScoreComplaint.text = ": ${it}"
    }
    with(binding) {
      if (assignAt != null && quizDate != null) {
        assignAt?.let { assignAt ->
          quizDate?.let { quizDate ->
            tvTimeTakenComplaint.text = ": ${Utils.convertLongToMinutes(
                assignAt - quizDate)} ${resources.getString(R.string.minutes)}"
          }
        }
      }
    }
    return binding.root
  }

}