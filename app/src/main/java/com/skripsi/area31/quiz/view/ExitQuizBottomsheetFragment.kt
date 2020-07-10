package com.skripsi.area31.quiz.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skripsi.area31.R
import com.skripsi.area31.databinding.FragmentBottomsheetLeaveQuizBinding

class ExitQuizBottomsheetFragment : BottomSheetDialogFragment() {
  private lateinit var binding: FragmentBottomsheetLeaveQuizBinding

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bottomsheet_leave_quiz, container,
        false)
    setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppBottomSheetDialogTheme)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    with(binding) {
      val activity = activity as QuizActivity
      btnCancel.setOnClickListener {
        activity.dismissDialog()
      }
      btnLeave.setOnClickListener {
        activity.leaveQuiz()
      }
    }
  }
}