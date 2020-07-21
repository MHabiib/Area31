package com.skripsi.area31.home.view

import com.skripsi.area31.core.base.BaseView
import com.skripsi.area31.core.model.Token
import com.skripsi.area31.home.model.ListCourse

interface HomeContract : BaseView {
  fun loadListCourseSuccess(listCourse: ListCourse)

  fun refreshToken()

  fun onLogin()

  fun onSuccessRefresh(token: Token)
}