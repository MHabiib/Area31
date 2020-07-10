package com.skripsi.area31.register.view

import android.app.Activity
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
import com.skripsi.area31.core.model.SimpleCustomResponse
import com.skripsi.area31.core.model.Token
import com.skripsi.area31.core.network.Authentication
import com.skripsi.area31.databinding.ActivityRegisterBinding
import com.skripsi.area31.login.view.LoginActivity
import com.skripsi.area31.main.view.MainActivity
import com.skripsi.area31.register.injection.DaggerRegisterComponent
import com.skripsi.area31.register.injection.RegisterComponent
import com.skripsi.area31.register.presenter.RegisterPresenter
import com.skripsi.area31.utils.Constants.Companion.ROLE_STUDENT
import okhttp3.ResponseBody
import javax.inject.Inject

class RegisterActivity : BaseActivity(), RegisterContract {
  private var daggerBuild: RegisterComponent = DaggerRegisterComponent.builder().baseComponent(
      BaseApp.instance.baseComponent).build()

  init {
    daggerBuild.inject(this)
  }

  @Inject lateinit var presenter: RegisterPresenter
  @Inject lateinit var gson: Gson
  private lateinit var binding: ActivityRegisterBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_register)
    presenter.attach(this)
    presenter.subscribe()
    with(binding) {
      btnLogin.setOnClickListener {
        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
      }
      btnRegister.setOnClickListener {
        if (isValid()) {
          hideKeyboard()
          loading(true)
          presenter.register(etEmail.text.toString(), etPassword.text.toString(),
              etPhone.text.toString(), etName.text.toString())
        } else {
          Toast.makeText(this@RegisterActivity, resources.getString(R.string.fill_all_the_entries),
              Toast.LENGTH_LONG).show()
        }
      }
    }
  }

  override fun onSuccess(token: Token) {
    loading(false)
    Authentication.save(this, token, ROLE_STUDENT)
    val intent = Intent(this, MainActivity::class.java)
    startActivity(intent)
    finish()
  }

  override fun onFailed(message: String) {
    loading(false)
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
  }

  override fun onBadRequest(responseBody: ResponseBody) {
    loading(false)
    Toast.makeText(this,
        gson.fromJson(responseBody.string(), SimpleCustomResponse::class.java).message,
        Toast.LENGTH_SHORT).show()
  }

  private fun isValid(): Boolean {
    with(binding) {
      if (etEmail.text.toString().isEmpty()) return false
      if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.text.toString()).matches()) return false
      if (etName.text.toString().isEmpty()) return false
      if (etPhone.text.toString().isEmpty()) return false
      if (etPassword.text.toString().isEmpty()) return false
      return true
    }
  }

  private fun loading(isLoading: Boolean) {
    with(binding) {
      if (isLoading) {
        progressBar.visibility = View.VISIBLE
        btnRegister.isEnabled = false
      } else {
        progressBar.visibility = View.GONE
        btnRegister.isEnabled = true
      }
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
}