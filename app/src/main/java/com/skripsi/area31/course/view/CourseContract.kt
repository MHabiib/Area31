package com.skripsi.area31.course.view

import com.skripsi.area31.core.base.BaseView
import com.skripsi.area31.course.model.CourseDetails
import okhttp3.ResponseBody

interface CourseContract : BaseView {
  fun courseDetailsSuccess(courseDetails: CourseDetails?)
  fun onBadRequest(message: ResponseBody?)
  fun leaveCourseSuccess(response: String)
}