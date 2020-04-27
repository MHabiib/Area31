package com.skripsi.area31.profile.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.skripsi.area31.BaseApp
import com.skripsi.area31.R
import com.skripsi.area31.databinding.FragmentProfileBinding
import com.skripsi.area31.exam.view.ExamFragment
import com.skripsi.area31.login.view.LoginActivity
import com.skripsi.area31.main.view.MainActivity
import com.skripsi.area31.profile.injection.DaggerProfileComponent
import com.skripsi.area31.profile.injection.ProfileComponent
import com.skripsi.area31.profile.presenter.ProfilePresenter
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

  fun newInstance(): ExamFragment = ExamFragment()

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
    presenter.attach(this)
    presenter.apply {
      subscribe()
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
    TODO(
        "not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun onLogout() {
    val intent = Intent(activity, LoginActivity::class.java)
    startActivity(intent)
    activity?.finish()
  }

  override fun onDestroy() {
    presenter.detach()
    super.onDestroy()
  }
}