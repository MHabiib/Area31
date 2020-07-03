package com.skripsi.area31.changepassword.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.skripsi.area31.BaseApp
import com.skripsi.area31.R
import com.skripsi.area31.changepassword.injection.ChangePasswordComponent
import com.skripsi.area31.changepassword.injection.DaggerChangePasswordComponent
import com.skripsi.area31.changepassword.model.ChangePassword
import com.skripsi.area31.changepassword.presenter.ChangePasswordPresenter
import com.skripsi.area31.core.model.SimpleCustomResponse
import com.skripsi.area31.core.model.Token
import com.skripsi.area31.databinding.FragmentBottomsheetChangePasswordBinding
import com.skripsi.area31.profile.view.ProfileFragment
import com.skripsi.area31.utils.Constants
import com.skripsi.area31.utils.Constants.Companion.PROFILE_FRAGMENT
import okhttp3.ResponseBody
import javax.inject.Inject

class ChangePasswordFragment : BottomSheetDialogFragment(), ChangePasswordContract {
  private var daggerBuild: ChangePasswordComponent = DaggerChangePasswordComponent.builder().baseComponent(
      BaseApp.instance.baseComponent).build()

  init {
    daggerBuild.inject(this)
  }

  @Inject lateinit var presenter: ChangePasswordPresenter

  @Inject lateinit var gson: Gson
  private lateinit var binding: FragmentBottomsheetChangePasswordBinding
  private lateinit var accessToken: String

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bottomsheet_change_password,
        container, false)
    setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppBottomSheetDialogTheme)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    accessToken = gson.fromJson(
        context?.getSharedPreferences(Constants.AUTHENTICATION, Context.MODE_PRIVATE)?.getString(
            Constants.TOKEN, null), Token::class.java).accessToken

    presenter.attach(this)
    presenter.subscribe()

    with(binding) {
      btnChangePassword.setOnClickListener {
        if (etRecentPassword.text.toString() == "" || etNewPassword.text.toString() == "" || etRetypeNewPassword.text.toString() == "") {
          Toast.makeText(context, "Password can't be empty", Toast.LENGTH_LONG).show()
        } else if (etNewPassword.text.toString() != etRetypeNewPassword.text.toString()) {
          Toast.makeText(context, "New password must match", Toast.LENGTH_LONG).show()
        } else {
          showProgress(true)
          etRecentPassword.text?.clear()
          etNewPassword.text?.clear()
          etRetypeNewPassword.text?.clear()
          presenter.updateUserPassword(accessToken, etNewPassword.text.toString(),
              ChangePassword(etRecentPassword.text.toString()))
        }
      }
    }
  }

  override fun updateUserPasswordSuccess(message: String) {
    showProgress(false)
    val profileFragment = fragmentManager?.findFragmentByTag(PROFILE_FRAGMENT) as ProfileFragment
    profileFragment.dismissDialog()
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
  }

  override fun onBadRequest(message: ResponseBody?) {
    showProgress(false)
    Toast.makeText(context,
        gson.fromJson(message?.string(), SimpleCustomResponse::class.java).message,
        Toast.LENGTH_SHORT).show()
  }

  override fun onFailed(message: String) {
    Log.e("CHANGE_PASSWORD", message)
  }

  private fun showProgress(show: Boolean) {
    with(binding) {
      if (show) {
        progressBar.visibility = View.VISIBLE
        btnChangePassword.isEnabled = false
      } else {
        progressBar.visibility = View.GONE
        btnChangePassword.isEnabled = true
      }
    }
  }

}