package com.skripsi.area31.login.view

import com.skripsi.area31.core.base.BaseView
import com.skripsi.area31.core.model.Token

interface LoginContract : BaseView {
  fun onSuccess(token: Token)
}