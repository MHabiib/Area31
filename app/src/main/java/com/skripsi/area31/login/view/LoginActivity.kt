package com.skripsi.area31.login.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.skripsi.area31.BaseApp
import com.skripsi.area31.R
import com.skripsi.area31.core.base.BaseActivity
import com.skripsi.area31.core.model.SimpleCustomResponse
import com.skripsi.area31.core.model.Token
import com.skripsi.area31.core.network.Authentication
import com.skripsi.area31.databinding.ActivityLoginBinding
import com.skripsi.area31.login.injection.DaggerLoginComponent
import com.skripsi.area31.login.injection.LoginComponent
import com.skripsi.area31.login.presenter.LoginPresenter
import com.skripsi.area31.main.view.MainActivity
import com.skripsi.area31.register.view.RegisterActivity
import com.skripsi.area31.utils.Constants.Companion.AUTHENTICATION
import com.skripsi.area31.utils.Constants.Companion.ROLE_STUDENT
import com.skripsi.area31.utils.Constants.Companion.TOKEN
import javax.inject.Inject

class LoginActivity : BaseActivity(), LoginContract {
  private var daggerBuild: LoginComponent = DaggerLoginComponent.builder().baseComponent(
      BaseApp.instance.baseComponent).build()

  init {
    daggerBuild.inject(this)
  }

  @Inject lateinit var presenter: LoginPresenter
  @Inject lateinit var gson: Gson
  private lateinit var binding: ActivityLoginBinding
  private lateinit var countDownTimer: CountDownTimer
  private var email = ""

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
    presenter.attach(this)
    presenter.subscribe()
    with(binding) {
      btnLogin.setOnClickListener {
        if (isValid()) {
          loading(true)
          hideKeyboard()
          presenter.login(etEmail.text.toString(), etPassword.text.toString())
        } else {
          Toast.makeText(this@LoginActivity, getString(R.string.fill_all_the_entries),
              Toast.LENGTH_SHORT).show()
        }
      }

      tvForgotPassword.setOnClickListener {
        layoutLogin.visibility = View.GONE
        layoutForgotPassword.visibility = View.VISIBLE
        btnBackToLogin.visibility = View.VISIBLE
        layoutForgotPasswordFirstStep.visibility = View.VISIBLE
      }
      btnBackToLogin.setOnClickListener {
        layoutLogin.visibility = View.VISIBLE
        layoutForgotPassword.visibility = View.GONE
        layoutForgotPasswordFirstStep.visibility = View.GONE
        layoutForgotPasswordNextStep.visibility = View.GONE
        layoutNewPassword.visibility = View.GONE
        btnBackToLogin.visibility = View.GONE
        countDownTimer.cancel()
      }
      btnSubmit.setOnClickListener {
        if (etEmailReset.text.toString().isNotEmpty()) {
          loadingForgot(true)
          hideKeyboard()
          email = etEmailReset.text.toString()
          presenter.forgotPassword(email)
        } else {
          Toast.makeText(this@LoginActivity, getString(R.string.enter_the_email),
              Toast.LENGTH_SHORT).show()
        }
      }
      btnSubmitNextStep.setOnClickListener {
        if (etResetCode.text.toString().isNotEmpty()) {
          loadingForgot(true)
          hideKeyboard()
          presenter.forgotPasswordNextStep(email, etResetCode.text.toString().toInt())
        } else {
          Toast.makeText(this@LoginActivity, getString(R.string.enter_the_code),
              Toast.LENGTH_SHORT).show()
        }
      }
      btnNewPassword.setOnClickListener {
        if (etRetypeNewPassword.text.toString().isNotEmpty() && etRetypeNewPassword.text.toString() == etNewPassword.text.toString()) {
          loadingForgot(true)
          hideKeyboard()
          presenter.resetPassword(email, etRetypeNewPassword.text.toString())
        } else {
          Toast.makeText(this@LoginActivity, getString(R.string.enter_password_match),
              Toast.LENGTH_SHORT).show()
        }
      }
      tvResendCode.setOnClickListener {
        tvResendCode.isClickable = false
        presenter.forgotPassword(email)
      }

      btnRegister.setOnClickListener {
        val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
        startActivity(intent)
        finish()
      }
    }
  }

  private fun loading(isLoading: Boolean) {
    with(binding) {
      if (isLoading) {
        progressBar.visibility = View.VISIBLE
        btnLogin.isEnabled = false
        btnSubmit.isClickable = false
        btnSubmitNextStep.isClickable = false
      } else {
        etPassword.text?.clear()
        progressBar.visibility = View.GONE
        btnLogin.isEnabled = true
        btnSubmit.isClickable = true
        btnSubmitNextStep.isClickable = true
      }
    }
  }

  private fun loadingForgot(isLoading: Boolean) {
    with(binding) {
      if (isLoading) {
        progressBarReset.visibility = View.VISIBLE
        btnSubmit.isClickable = false
        btnSubmitNextStep.isClickable = false
      } else {
        progressBarReset.visibility = View.GONE
        btnSubmit.isClickable = true
        btnSubmitNextStep.isClickable = true
      }
    }
  }

  private fun isValid(): Boolean {
    with(binding) {
      if (etEmail.text.toString().isEmpty()) return false
      if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.text.toString()).matches()) return false
      if (etPassword.text.toString().isEmpty()) return false
      return true
    }
  }

  private fun hideKeyboard() {
    val view = currentFocus
    view?.let {
      val mInputMethodManager = getSystemService(
          Activity.INPUT_METHOD_SERVICE) as InputMethodManager
      mInputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
    }
  }

  override fun onSuccess(token: Token) {
    Authentication.save(this, token, ROLE_STUDENT)
    presenter.isAuthorize(gson.fromJson(
        this.getSharedPreferences(AUTHENTICATION, Context.MODE_PRIVATE)?.getString(TOKEN, null),
        Token::class.java).accessToken)
  }

  override fun onAuthorized() {
    val intent = Intent(this, MainActivity::class.java)
    startActivity(intent)
    finish()
  }

  override fun forgotPasswordSuccess() {
    loadingForgot(false)
    binding.layoutForgotPasswordFirstStep.visibility = View.GONE
    binding.layoutForgotPasswordNextStep.visibility = View.VISIBLE
    countDownTimer = object : CountDownTimer(60000, 1000) {
      @SuppressLint("SetTextI18n") override fun onTick(millisUntilFinished: Long) {
        var diff = millisUntilFinished
        val secondsInMilli: Long = 1000
        val minutesInMilli = secondsInMilli * 60

        val elapsedMinutes = diff / minutesInMilli
        diff %= minutesInMilli

        val elapsedSeconds = diff / secondsInMilli
        binding.tvResendCode.text = "resend in $elapsedMinutes : $elapsedSeconds"
        binding.tvResendCode.isClickable = false
      }

      override fun onFinish() {
        binding.tvResendCode.text = getString(R.string.resend_the_code)
        binding.tvResendCode.isClickable = true
      }
    }.start()
  }

  override fun forgotPasswordNextStepSuccess() {
    loadingForgot(false)
    binding.layoutForgotPasswordNextStep.visibility = View.GONE
    binding.layoutNewPassword.visibility = View.VISIBLE
  }

  override fun resetPasswordSuccess() {
    loadingForgot(false)
    Toast.makeText(this, getString(R.string.success_reset_password), Toast.LENGTH_SHORT).show()
    binding.layoutLogin.visibility = View.VISIBLE
    binding.layoutForgotPassword.visibility = View.GONE
    binding.btnBackToLogin.visibility = View.GONE
    binding.layoutNewPassword.visibility = View.GONE
  }

  override fun onFailed(message: String) {
    loading(false)
    Toast.makeText(this, getString(R.string.the_email_or_password_incorrct),
        Toast.LENGTH_LONG).show()
    Authentication.delete(this)
  }

  override fun onFailedReset(string: String) {
    loadingForgot(false)
    Toast.makeText(this, gson.fromJson(string, SimpleCustomResponse::class.java).message,
        Toast.LENGTH_SHORT).show()
  }

  override fun onBackPressed() {
    super.onBackPressed()
    this.finishAffinity()
  }

  override fun onDestroy() {
    presenter.detach()
    if (::countDownTimer.isInitialized) {
      countDownTimer.cancel()
    }
    super.onDestroy()
  }
}
