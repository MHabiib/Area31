package com.skripsi.area31.login.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.skripsi.area31.BaseApp
import com.skripsi.area31.R
import com.skripsi.area31.core.base.BaseActivity
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
          Toast.makeText(this@LoginActivity, "Fill all the entries with valid value.",
              Toast.LENGTH_SHORT).show()
        }
      }
      tvForgotPassword.setOnClickListener {
        Toast.makeText(this@LoginActivity, "Under construction.", Toast.LENGTH_SHORT).show()
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
      } else {
        etPassword.text?.clear()
        progressBar.visibility = View.GONE
        btnLogin.isEnabled = true
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

  override fun onFailed(message: String) {
    loading(false)
    Toast.makeText(this, "The email or password is incorrect.", Toast.LENGTH_LONG).show()
    Authentication.delete(this)
  }

  override fun onBackPressed() {
    super.onBackPressed()
    this.finishAffinity()
  }

  override fun onDestroy() {
    presenter.detach()
    super.onDestroy()
  }
}
