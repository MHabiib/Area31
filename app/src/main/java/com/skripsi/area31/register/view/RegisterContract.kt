package com.skripsi.area31.register.view

import com.skripsi.area31.core.base.BaseView
import com.skripsi.area31.core.model.Token

interface RegisterContract : BaseView {
  fun onSuccess(token: Token)
}