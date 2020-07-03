package com.skripsi.area31.login.presenter

import android.util.Log
import com.skripsi.area31.core.base.BasePresenter
import com.skripsi.area31.core.model.Token
import com.skripsi.area31.login.network.LoginApi
import com.skripsi.area31.login.view.LoginContract
import com.skripsi.area31.utils.Constants.Companion.GRANT_TYPE
import com.skripsi.area31.utils.Constants.Companion.ROLE_STUDENT
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class LoginPresenter @Inject constructor(private val loginApi: LoginApi) :
    BasePresenter<LoginContract>() {
  fun login(username: String, password: String) {
    subscriptions.add(
        loginApi.auth(username, password, GRANT_TYPE).subscribeOn(Schedulers.io()).observeOn(
            AndroidSchedulers.mainThread()).subscribe({ token: Token ->
          view?.let { view?.onSuccess(token) }
        }, { view?.onFailed(it.message.toString()) }))
  }

  fun isAuthorize(accessToken: String) {
    subscriptions.add(
        loginApi.isAuthorize(accessToken, ROLE_STUDENT).subscribeOn(Schedulers.io()).observeOn(
            AndroidSchedulers.mainThread()).subscribe({
          if (it.code() == 400 || it.code() == 403) {
            it.errorBody()?.let { error -> view?.onFailed(error.string()) }
          } else {
            it.body()?.message?.let { message -> view?.onAuthorized() }
          }
        }, {
          Log.e("LOGIN", it.message.toString())
        }))
  }
}