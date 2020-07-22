package com.skripsi.area31.profile.presenter

import android.util.Log
import com.skripsi.area31.core.base.BasePresenter
import com.skripsi.area31.core.model.Token
import com.skripsi.area31.profile.network.ProfileApi
import com.skripsi.area31.profile.view.ProfileContract
import com.skripsi.area31.register.model.RegisterStudent
import com.skripsi.area31.utils.Constants
import com.skripsi.area31.utils.Constants.Companion.ON_ERROR
import com.skripsi.area31.utils.Constants.Companion.PROFILE
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ProfilePresenter @Inject constructor(private val profileApi: ProfileApi) :
    BasePresenter<ProfileContract>() {

  fun loadData(accessToken: String) {
    subscriptions.add(
        profileApi.getStudentProfile(accessToken).subscribeOn(Schedulers.io()).observeOn(
            AndroidSchedulers.mainThread()).subscribe({
          view?.getStudentProfileSuccess(it)
        }, {
          if (it.message.equals("HTTP 401 ")) {
            view?.refreshToken()
          } else {
            view?.onFailed(it.message.toString())
          }
        }))
  }

  fun updateUser(accessToken: String, newPassword: String, student: RegisterStudent) {
    subscriptions.add(profileApi.updateUser(accessToken, newPassword, student).subscribeOn(
        Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
      when {
        it.code() == 400 -> it.errorBody()?.let { message -> view?.onBadRequest(message) }
        it.body() != null -> it.body()?.let { response ->
          if (response.message.contains("email changing")) view?.onLogout()
          else it.body()?.message?.let { message -> view?.updateUserSuccess(message) }
        }
      }
    }, {
      Log.e(PROFILE, ON_ERROR, it)
    }))
  }

  fun refreshToken(refreshToken: String) {
    subscriptions.add(profileApi.refresh(Constants.GRANT_TYPE_REFRESH, refreshToken).subscribeOn(
        Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({ token: Token ->
      view?.onSuccessRefresh(token)
    }, {
      view?.onLogin()
    }))
  }

  fun logout(accessToken: String) {
    subscriptions.add(profileApi.logout(accessToken).subscribeOn(Schedulers.io()).observeOn(
        AndroidSchedulers.mainThread()).subscribe())
  }

}