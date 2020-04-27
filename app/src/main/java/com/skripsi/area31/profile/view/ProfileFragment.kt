package com.skripsi.area31.profile.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.skripsi.area31.BaseApp
import com.skripsi.area31.R
import com.skripsi.area31.core.model.Token
import com.skripsi.area31.core.network.Authentication
import com.skripsi.area31.databinding.FragmentProfileBinding
import com.skripsi.area31.login.view.LoginActivity
import com.skripsi.area31.main.view.MainActivity
import com.skripsi.area31.profile.injection.DaggerProfileComponent
import com.skripsi.area31.profile.injection.ProfileComponent
import com.skripsi.area31.profile.model.Student
import com.skripsi.area31.profile.presenter.ProfilePresenter
import com.skripsi.area31.utils.Constants.Companion.AUTHENTICATION
import com.skripsi.area31.utils.Constants.Companion.TOKEN
import javax.inject.Inject

class ProfileFragment : Fragment(), ProfileContract {
  private var daggerBuild: ProfileComponent = DaggerProfileComponent.builder().baseComponent(
      BaseApp.instance.baseComponent).build()

  init {
    daggerBuild.inject(this)
  }

  @Inject lateinit var presenter: ProfilePresenter
  @Inject lateinit var gson: Gson
  private lateinit var binding: FragmentProfileBinding
  private lateinit var accessToken: String
  private var editMode = false

  companion object {
    const val TAG: String = "ProfileFragment"
  }

  fun newInstance(): ProfileFragment = ProfileFragment()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requireActivity().onBackPressedDispatcher.addCallback(this) {
      val activity = activity as MainActivity?
      activity?.presenter?.onHomeIconClick()
    }
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
    with(binding) {
      val logout = btnLogout
      logout.setOnClickListener {
        btnLogout.visibility = View.GONE
        onLogout()
      }
    }
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    accessToken = gson.fromJson(
        context?.getSharedPreferences(AUTHENTICATION, Context.MODE_PRIVATE)?.getString(TOKEN, null),
        Token::class.java).accessToken
    presenter.attach(this)
    presenter.apply {
      subscribe()
      showProgress(true)
      loadData(accessToken)
    }
  }

  override fun getStudentProfileSuccess(student: Student) {
    showProgress(false)
    with(binding) {
      profileNameDisplay.text = "Hi " + student.name
      profileName.setText(student.name)
      profileEmail.setText(student.email)
      profilePhoneNumber.setText(student.phone)
      profilePassword.hint = student.password
    }
  }

  override fun showProgress(show: Boolean) {
    if (show) {
      binding.progressBar.visibility = View.VISIBLE
    } else {
      binding.progressBar.visibility = View.GONE
    }
  }

  override fun onFailed(message: String) {
    showProgress(false)
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
  }

  override fun onLogout() {
    binding.btnLogout.visibility = View.GONE
    context?.let { context -> Authentication.delete(context) }
    val intent = Intent(activity, LoginActivity::class.java)
    startActivity(intent)
    activity?.finish()
    presenter.logout(accessToken)
  }

  override fun onDestroy() {
    presenter.detach()
    super.onDestroy()
  }
}