package com.skripsi.area31.changepassword.presenter

import android.util.Log
import com.skripsi.area31.changepassword.model.ChangePassword
import com.skripsi.area31.changepassword.network.ChangePasswordApi
import com.skripsi.area31.changepassword.view.ChangePasswordContract
import com.skripsi.area31.core.base.BasePresenter
import com.skripsi.area31.utils.Constants.Companion.ON_ERROR
import com.skripsi.area31.utils.Constants.Companion.PROFILE
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ChangePasswordPresenter @Inject constructor(
    private val changePasswordApi: ChangePasswordApi) : BasePresenter<ChangePasswordContract>() {
  fun updateUserPassword(accessToken: String, newPassword: String, changePassword: ChangePassword) {
    subscriptions.add(
        changePasswordApi.updateUserPassword(accessToken, newPassword, changePassword).subscribeOn(
            Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
          when {
            it.code() == 400 -> it.errorBody().let { message -> view?.onBadRequest(message) }
            else -> it.body()?.message.let { message ->
              message?.let { it1 ->
                view?.updateUserPasswordSuccess(it1)
              }
            }
          }
        }, {
          Log.e(PROFILE, ON_ERROR, it)
        }))
  }

}