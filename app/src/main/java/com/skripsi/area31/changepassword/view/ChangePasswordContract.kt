package com.skripsi.area31.changepassword.view

import com.skripsi.area31.core.base.BaseView
import okhttp3.ResponseBody

interface ChangePasswordContract : BaseView {
  fun onBadRequest(message: ResponseBody?)

  fun updateUserPasswordSuccess(message: String)
}