package com.skripsi.area31.profile.view

import com.skripsi.area31.core.base.BaseView
import com.skripsi.area31.profile.model.Student

interface ProfileContract : BaseView {
  fun showProgress(show: Boolean)
  fun onLogout()
  fun getStudentProfileSuccess(student: Student)
}