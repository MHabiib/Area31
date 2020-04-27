package com.skripsi.area31.login.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.gson.Gson
import com.skripsi.area31.BaseApp
import com.skripsi.area31.R
import com.skripsi.area31.core.base.BaseActivity
import com.skripsi.area31.core.model.Token
import com.skripsi.area31.core.network.Authentication
import com.skripsi.area31.login.injection.DaggerLoginComponent
import com.skripsi.area31.login.injection.LoginComponent
import com.skripsi.area31.login.presenter.LoginPresenter
import com.skripsi.area31.main.view.MainActivity
import com.skripsi.area31.utils.Constants.Companion.ROLE_STUDENT
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

class LoginActivity : BaseActivity(), LoginContract {
  private var daggerBuild: LoginComponent = DaggerLoginComponent.builder().baseComponent(
      BaseApp.instance.baseComponent).build()

  init {
    daggerBuild.inject(this)
  }

  @Inject lateinit var presenter: LoginPresenter
  @Inject lateinit var gson: Gson

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)
    presenter.attach(this)
    presenter.subscribe()
    btnSign.setOnClickListener {
      if (isValid()) {
        loading(true)
        hideKeyboard()
        presenter.login(txtEmail.text.toString(), txtPassword.text.toString())
      }
    }
  }

  private fun loading(isLoading: Boolean) {
    if (isLoading) {
      inputLayoutEmail.visibility = View.GONE
      progressBar.visibility = View.VISIBLE
      inputLayoutPassword.visibility = View.GONE
      btnSign.visibility = View.GONE
    } else {
      txtPassword.text?.clear()
      progressBar.visibility = View.GONE
      inputLayoutEmail.visibility = View.VISIBLE
      inputLayoutPassword.visibility = View.VISIBLE
      btnSign.visibility = View.VISIBLE
    }
  }

  private fun isValid(): Boolean {
    if (txtEmail?.text.toString().isEmpty() && Patterns.EMAIL_ADDRESS.matcher(
            txtEmail?.text.toString()).matches()) return false
    if (txtPassword?.text.toString().isEmpty()) return false
    return true
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
