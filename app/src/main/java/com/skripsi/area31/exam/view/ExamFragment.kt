package com.skripsi.area31.exam.view

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
import com.skripsi.area31.databinding.FragmentExamBinding
import com.skripsi.area31.exam.injection.DaggerExamComponent
import com.skripsi.area31.exam.injection.ExamComponent
import com.skripsi.area31.exam.presenter.ExamPresenter
import com.skripsi.area31.login.view.LoginActivity
import com.skripsi.area31.main.view.MainActivity
import javax.inject.Inject

class ExamFragment : Fragment(), ExamContract {
  private var daggerBuild: ExamComponent = DaggerExamComponent.builder().baseComponent(
      BaseApp.instance.baseComponent).build()

  init {
    daggerBuild.inject(this)
  }

  @Inject lateinit var presenter: ExamPresenter
  @Inject lateinit var gson: Gson
  private lateinit var binding: FragmentExamBinding
  private lateinit var accessToken: String
  private var editMode = false

  companion object {
    const val TAG: String = "ExamFragment"
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
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_exam, container, false)
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