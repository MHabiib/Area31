package com.skripsi.area31.splash.view

import android.content.Context
import com.skripsi.area31.core.base.BaseView
import com.skripsi.area31.core.model.Token

interface SplashContract : BaseView {
  fun onSuccess(token: Token)
  fun onLogin()
  fun isAuthenticated(): Context?
  fun refreshToken()
  fun onAuthenticated()
}