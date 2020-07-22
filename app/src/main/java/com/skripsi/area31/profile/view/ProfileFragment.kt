package com.skripsi.area31.profile.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.skripsi.area31.BaseApp
import com.skripsi.area31.R
import com.skripsi.area31.changepassword.view.ChangePasswordFragment
import com.skripsi.area31.core.model.SimpleCustomResponse
import com.skripsi.area31.core.model.Token
import com.skripsi.area31.core.network.Authentication
import com.skripsi.area31.databinding.FragmentProfileBinding
import com.skripsi.area31.login.view.LoginActivity
import com.skripsi.area31.main.view.MainActivity
import com.skripsi.area31.profile.injection.DaggerProfileComponent
import com.skripsi.area31.profile.injection.ProfileComponent
import com.skripsi.area31.profile.model.ProfileResponse
import com.skripsi.area31.profile.presenter.ProfilePresenter
import com.skripsi.area31.register.model.RegisterStudent
import com.skripsi.area31.utils.Constants.Companion.AUTHENTICATION
import com.skripsi.area31.utils.Constants.Companion.PROFILE_FRAGMENT
import com.skripsi.area31.utils.Constants.Companion.ROLE_STUDENT
import com.skripsi.area31.utils.Constants.Companion.TOKEN
import okhttp3.ResponseBody
import retrofit2.Response
import java.util.*
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
  private val bottomSheetFragment = ChangePasswordFragment()
  private val spinnerItems = ArrayList<String>()
  private var email = ""
  private var name = ""
  private var phone = ""

  companion object {
    const val TAG: String = PROFILE_FRAGMENT
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
      btnSaveChanges.setOnClickListener {
        btnSaveChanges.isEnabled = false
        if (profileEmail.text.toString() != "") {
          email = profileEmail.text.toString()
        }
        if (profileName.text.toString() != "") {
          name = profileName.text.toString()
        }
        if (profilePhoneNumber.text.toString() != "") {
          phone = profilePhoneNumber.text.toString()
        }
        showProgress(true)
        presenter.updateUser(accessToken, "", RegisterStudent(email, name, "", phone, ROLE_STUDENT))
      }
      btnEditPassword.setOnClickListener {
        if (!bottomSheetFragment.isAdded) {

          activity?.supportFragmentManager?.let { fragmentManager ->
            bottomSheetFragment.show(fragmentManager, bottomSheetFragment.tag)
          }
        }
      }
      profileName.addTextChangedListener {
        enableSaveButton()
      }
      profileEmail.addTextChangedListener {
        enableSaveButton()
      }
      profilePhoneNumber.addTextChangedListener {
        enableSaveButton()
      }

      val adapter = context?.let {
        ArrayAdapter(it, R.layout.spinner_style_small_text, spinnerItems)
      }
      adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
      val shp = activity?.getSharedPreferences("com.skripsi.area31.PREFERENCES",
          Context.MODE_PRIVATE)
      val editor = shp?.edit()

      val firstItem: String
      val secondItem: String
      if (shp?.getString("USER_LANGUAGE", "en") == "en") {
        spinnerItems.add("English")
        spinnerItems.add("Bahasa Indonesia")
        firstItem = "en"
        secondItem = "in"
      } else {
        spinnerItems.add("Bahasa Indonesia")
        spinnerItems.add("English")
        firstItem = "in"
        secondItem = "en"
      }
      language.adapter = adapter
      var isSpinnerInitial = true
      language.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(p0: AdapterView<*>?) {
          //No implementation needed
        }

        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
          (p0?.getChildAt(0) as TextView).setTextColor(resources.getColor(R.color.white))

          if (isSpinnerInitial) {
            isSpinnerInitial = false
            return
          }
          when (p2) {
            0 -> {
              editor?.putString("USER_LANGUAGE", firstItem)
              editor?.apply()
              Handler().postDelayed({
                val intent = Intent(context, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                Runtime.getRuntime().exit(0)
              }, 1000)
            }
            else -> {
              editor?.putString("USER_LANGUAGE", secondItem)
              editor?.apply()
              Handler().postDelayed({
                val intent = Intent(context, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                Runtime.getRuntime().exit(0)
              }, 1000)
            }
          }
        }
      }
    }
    return binding.root
  }

  private fun FragmentProfileBinding.enableSaveButton() {
    btnSaveChanges.isEnabled = true
    btnSaveChanges.setTextColor(resources.getColor(R.color.colorPrimary))
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

  override fun getStudentProfileSuccess(student: Response<ProfileResponse>) {
    showProgress(false)
    with(binding) {
      name = student.body()?.user?.name.toString()
      email = student.body()?.user?.email.toString()
      phone = student.body()?.user?.phone.toString()

      profileNameDisplay.text = "Hi $name"
      profileName.hint = name
      profileEmail.hint = email
      profilePhoneNumber.hint = phone
      profilePassword.hint = student.body()?.user?.password
    }
  }

  override fun updateUserSuccess(message: String) {
    showProgress(false)
    with(binding) {
      profileName.text?.clear()
      profileEmail.text?.clear()
      profilePhoneNumber.text?.clear()
      btnSaveChanges.isEnabled = false
      btnSaveChanges.setTextColor(resources.getColor(R.color.darkGrey))
    }
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    presenter.loadData(accessToken)
  }

  override fun onBadRequest(message: ResponseBody) {
    showProgress(false)
    binding.btnSaveChanges.isEnabled = true
    Toast.makeText(context,
        gson.fromJson(message.string(), SimpleCustomResponse::class.java).message,
        Toast.LENGTH_SHORT).show()
  }

  override fun showProgress(show: Boolean) {
    if (show) {
      binding.progressBar.visibility = View.VISIBLE
    } else {
      binding.progressBar.visibility = View.GONE
    }
  }

  override fun onFailed(message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    binding.btnSaveChanges.isEnabled = true
    showProgress(true)
    presenter.loadData(accessToken)
  }

  override fun onLogout() {
    binding.btnLogout.visibility = View.GONE
    context?.let { context -> Authentication.delete(context) }
    val intent = Intent(activity, LoginActivity::class.java)
    startActivity(intent)
    activity?.finish()
    presenter.logout(accessToken)
  }

  override fun refreshToken() {
    context?.let { Authentication.getRefresh(it) }?.let { presenter.refreshToken(it) }
  }

  override fun onSuccessRefresh(token: Token) {
    context?.let {
      Authentication.save(it, token, gson.fromJson(
          context?.getSharedPreferences(AUTHENTICATION, Context.MODE_PRIVATE)?.getString(TOKEN,
              null), Token::class.java).role)
    }
    presenter.loadData(accessToken)
  }

  override fun onLogin() {
    val activity = activity as MainActivity
    activity.showLogin()
  }

  private fun setLocale(localeName: String) {
    val myLocale = Locale(localeName)
    val res = resources
    val dm = res.displayMetrics
    val conf = res.configuration
    conf.locale = myLocale
    res.updateConfiguration(conf, dm)
  }

  fun dismissDialog() {
    bottomSheetFragment.dismiss()
  }

  override fun onDestroy() {
    presenter.detach()
    super.onDestroy()
  }
}