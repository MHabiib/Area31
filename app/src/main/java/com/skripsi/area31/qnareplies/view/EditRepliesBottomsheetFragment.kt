package com.skripsi.area31.qnareplies.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skripsi.area31.R
import com.skripsi.area31.databinding.FragmentBottomsheetEditRepliesBinding
import com.skripsi.area31.utils.Constants.Companion.REPLIES_BODY

class EditRepliesBottomsheetFragment : BottomSheetDialogFragment() {
  private lateinit var binding: FragmentBottomsheetEditRepliesBinding

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bottomsheet_edit_replies,
        container, false)
    setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppBottomSheetDialogTheme)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    this.arguments?.getString(REPLIES_BODY)?.let {
      binding.addReplies.setText(it)
    }
    with(binding) {
      val activity = activity as RepliesActivity
      ibSendReplies.setOnClickListener {
        addReplies.text?.let {
          if (it.isNotEmpty()) {
            activity.updateReplies(it.toString())
          } else {
            Toast.makeText(context, "Replies can't be empty", Toast.LENGTH_SHORT).show()
          }
        }
      }
    }
  }

  override fun onResume() {
    super.onResume()
    this.arguments?.getString(REPLIES_BODY)?.let {
      binding.addReplies.setText(it)
    }
  }
}