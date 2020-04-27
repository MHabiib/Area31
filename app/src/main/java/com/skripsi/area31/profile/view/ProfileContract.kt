package com.skripsi.area31.profile.view

import com.skripsi.area31.core.base.BaseView

interface ProfileContract : BaseView {
  fun showProgress(show: Boolean)
  fun onLogout()
}