package com.skripsi.area31.profile.view

import com.skripsi.area31.core.base.BaseView
import com.skripsi.area31.profile.model.ProfileResponse
import okhttp3.ResponseBody
import retrofit2.Response

interface ProfileContract : BaseView {
  fun showProgress(show: Boolean)
  fun onLogout()
  fun getStudentProfileSuccess(student: Response<ProfileResponse>)
  fun updateUserSuccess(message: String)
  fun onBadRequest(message: ResponseBody)
}