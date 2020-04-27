package com.skripsi.area31.splash.view

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.skripsi.area31.BaseApp
import com.skripsi.area31.R
import com.skripsi.area31.core.base.BaseActivity
import com.skripsi.area31.core.model.Token
import com.skripsi.area31.core.network.Authentication
import com.skripsi.area31.databinding.ActivitySplashBinding
import com.skripsi.area31.login.view.LoginActivity
import com.skripsi.area31.main.view.MainActivity
import com.skripsi.area31.splash.injection.DaggerSplashComponent
import com.skripsi.area31.splash.injection.SplashComponent
import com.skripsi.area31.splash.presenter.SplashPresenter
import com.skripsi.area31.utils.Constants.Companion.AUTHENTICATION
import com.skripsi.area31.utils.Constants.Companion.TOKEN
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class SplashActivity : BaseActivity(), SplashContract {
  private var daggerBuild: SplashComponent = DaggerSplashComponent.builder().baseComponent(
      BaseApp.instance.baseComponent).build()

  init {
    daggerBuild.inject(this)
  }

  @Inject lateinit var presenter: SplashPresenter
  @Inject lateinit var gson: Gson
  private lateinit var binding: ActivitySplashBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
    val settings = getSharedPreferences("prefs", 0)
    val firstRun = settings.getBoolean("firstRun", false)
    if (!firstRun) {
      presenter.attach(this)
      if (isOnline()) {
        checkAuthenticated()
      } else {
        binding.ibRefresh.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        Toast.makeText(this, "No network connection", Toast.LENGTH_LONG).show()
        binding.ibRefresh.setOnClickListener {
          if (isOnline()) {
            binding.ibRefresh.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
            checkAuthenticated()
          } else {
            Toast.makeText(this, "No network connection", Toast.LENGTH_LONG).show()
          }
        }
      }
    } else {
      val a = Intent(this, Dispatchers.Main::class.java)
      startActivity(a)
    }
  }

  override fun onSuccess(token: Token) {
    Authentication.save(this, token, gson.fromJson(
        this.getSharedPreferences(AUTHENTICATION, Context.MODE_PRIVATE)?.getString(TOKEN, null),
        Token::class.java).role)
    goToHomePage()
  }

  override fun onAuthenticated() = goToHomePage()

  private fun goToHomePage() {
    startActivity(Intent(this, MainActivity::class.java))
    finish()
  }

  override fun onLogin() = showLogin()

  override fun isAuthenticated(): Context? {
    return applicationContext
  }

  private fun showLogin() {
    val intent = Intent(this, LoginActivity::class.java)
    startActivity(intent)
    finish()
  }

  private fun isOnline(): Boolean {
    val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
    val activeNetwork = cm?.activeNetworkInfo
    return activeNetwork != null && activeNetwork.isConnected
  }

  private fun checkAuthenticated() {
    try {
      if (Authentication.isAuthenticated(isAuthenticated())) {
        onAuthenticated()
      } else {
        refreshToken()
      }
    } catch (e: Authentication.WithoutAuthenticatedException) {
      onLogin()
    }
  }

  override fun refreshToken() = presenter.refreshToken(Authentication.getRefresh(this))

  override fun onFailed(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    showLogin()
  }

  override fun onDestroy() {
    presenter.detach()
    super.onDestroy()
  }
}