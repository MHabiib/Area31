package com.skripsi.area31.register.presenter

import android.util.Log
import com.skripsi.area31.core.base.BasePresenter
import com.skripsi.area31.core.model.Token
import com.skripsi.area31.register.model.RegisterStudent
import com.skripsi.area31.register.network.RegisterApi
import com.skripsi.area31.register.view.RegisterContract
import com.skripsi.area31.utils.Constants.Companion.GRANT_TYPE
import com.skripsi.area31.utils.Constants.Companion.ON_ERROR
import com.skripsi.area31.utils.Constants.Companion.REGISTER
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class RegisterPresenter @Inject constructor(private val registerApi: RegisterApi) :
    BasePresenter<RegisterContract>() {
  fun register(username: String, password: String, phone: String, name: String) {
    val student = RegisterStudent(username, name, password, phone, "ROLE_STUDENT")
    subscriptions.add(registerApi.createStudent(student).subscribeOn(Schedulers.io()).observeOn(
        AndroidSchedulers.mainThread()).subscribe({
      if (it.code() == 400) {
        it.errorBody()?.let { it1 -> view?.onBadRequest(it1) }
      } else {
        login(username, password)
      }
    }, {
      Log.e(REGISTER, ON_ERROR, it)
    }))
  }

  private fun login(username: String, password: String) {
    subscriptions.add(
        registerApi.auth(username, password, GRANT_TYPE).subscribeOn(Schedulers.io()).observeOn(
            AndroidSchedulers.mainThread()).subscribe({ token: Token ->
          view?.onSuccess(token)
        }, {
          view?.onFailed(it.message.toString())
        }))
  }
}