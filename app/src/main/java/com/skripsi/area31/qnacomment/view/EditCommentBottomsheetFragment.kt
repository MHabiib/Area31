package com.skripsi.area31.qnacomment.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skripsi.area31.R
import com.skripsi.area31.databinding.FragmentBottomsheetEditCommentBinding
import com.skripsi.area31.utils.Constants.Companion.COMMENT_BODY

class EditCommentBottomsheetFragment : BottomSheetDialogFragment() {
  private lateinit var binding: FragmentBottomsheetEditCommentBinding

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bottomsheet_edit_comment,
        container, false)
    setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppBottomSheetDialogTheme)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    this.arguments?.getString(COMMENT_BODY)?.let {
      binding.addComment.setText(it)
    }
    with(binding) {
      val activity = activity as CommentActivity
      ibSendComment.setOnClickListener {
        addComment.text?.let {
          if (it.isNotEmpty()) {
            activity.updateComment(it.toString())
          } else {
            Toast.makeText(context, "Comment can't be empty", Toast.LENGTH_SHORT).show()
          }
        }
      }
    }
  }

  override fun onResume() {
    super.onResume()
    this.arguments?.getString(COMMENT_BODY)?.let {
      binding.addComment.setText(it)
    }
  }
}