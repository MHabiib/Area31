package com.skripsi.area31.enroll.presenter

import android.util.Log
import com.skripsi.area31.core.base.BasePresenter
import com.skripsi.area31.enroll.network.EnrollApi
import com.skripsi.area31.enroll.view.EnrollContract
import com.skripsi.area31.utils.Constants.Companion.ENROLL
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class EnrollPresenter @Inject constructor(private val enrollApi: EnrollApi) :
    BasePresenter<EnrollContract>() {
  fun checkCourse(accessToken: String, courseId: String) {
    subscriptions.add(
        enrollApi.checkCourse(accessToken, courseId).subscribeOn(Schedulers.io()).observeOn(
            AndroidSchedulers.mainThread()).subscribe({
          if (it.code() == 400) {
            it.errorBody()?.let { error -> view?.onBadRequest(error) }
          } else {
            it.body()?.course?.let { course -> view?.checkCourseSuccess(course) }
          }
        }, {
          Log.e(ENROLL, it.message.toString())
        }))
  }

  fun joinCourse(accessToken: String, courseId: String, password: String) {
    subscriptions.add(enrollApi.joinCourse(accessToken, courseId, password).subscribeOn(
        Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
      if (it.code() == 400) {
        it.errorBody()?.let { error -> view?.onBadRequest(error) }
      } else {
        it.body()?.message?.let { message -> view?.joinCourseSuccess(message) }
      }
    }, {
      Log.e(ENROLL, it.message.toString())
    }))
  }
}