package com.skripsi.area31.exam.view

import com.skripsi.area31.core.base.BaseView

interface ExamContract : BaseView {
  fun showProgress(show: Boolean)
  fun onLogout()
}