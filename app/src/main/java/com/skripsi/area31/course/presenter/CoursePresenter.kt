package com.skripsi.area31.course.presenter

import android.util.Log
import com.skripsi.area31.core.base.BasePresenter
import com.skripsi.area31.course.network.CourseApi
import com.skripsi.area31.course.view.CourseContract
import com.skripsi.area31.utils.Constants.Companion.COURSE
import com.skripsi.area31.utils.Constants.Companion.ON_ERROR
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class CoursePresenter @Inject constructor(private val courseApi: CourseApi) :
    BasePresenter<CourseContract>() {
  fun courseDetails(accessToken: String, idCourse: String) {
    subscriptions.add(
        courseApi.courseDetails(accessToken, idCourse).subscribeOn(Schedulers.io()).observeOn(
            AndroidSchedulers.mainThread()).subscribe({
          when {
            it.code() == 400 -> it.errorBody().let { message -> view?.onBadRequest(message) }
            else -> view?.courseDetailsSuccess(it.body()?.courseDetails)
          }
        }, {
          Log.e(COURSE, ON_ERROR, it)
        }))
  }

  fun leaveCourse(accessToken: String, idCourse: String) {
    subscriptions.add(
        courseApi.leaveCourse(accessToken, idCourse).subscribeOn(Schedulers.io()).observeOn(
            AndroidSchedulers.mainThread()).subscribe({
          when {
            it.code() == 400 -> it.errorBody().let { message -> view?.onBadRequest(message) }
            else -> it.body()?.message.let { message ->
              message?.let { response ->
                view?.leaveCourseSuccess(response)
              }
            }
          }
        }, {
          Log.e(COURSE, ON_ERROR, it)
        }))
  }
}