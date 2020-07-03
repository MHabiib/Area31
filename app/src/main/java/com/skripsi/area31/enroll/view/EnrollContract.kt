package com.skripsi.area31.enroll.view

import com.skripsi.area31.core.base.BaseView
import com.skripsi.area31.enroll.model.Course
import okhttp3.ResponseBody

interface EnrollContract : BaseView {
  fun checkCourseSuccess(course: Course)
  fun joinCourseSuccess(message: String)
  fun onBadRequest(error: ResponseBody)
}