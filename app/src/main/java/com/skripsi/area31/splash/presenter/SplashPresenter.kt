package com.skripsi.area31.splash.presenter

import com.skripsi.area31.core.base.BasePresenter
import com.skripsi.area31.core.model.Token
import com.skripsi.area31.splash.network.SplashApi
import com.skripsi.area31.splash.view.SplashContract
import com.skripsi.area31.utils.Constants.Companion.GRANT_TYPE_REFRESH
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SplashPresenter @Inject constructor(private val splashApi: SplashApi) :
    BasePresenter<SplashContract>() {

  fun refreshToken(refreshToken: String) {
    subscriptions.add(
        splashApi.refresh(GRANT_TYPE_REFRESH, refreshToken).subscribeOn(Schedulers.io()).observeOn(
            AndroidSchedulers.mainThread()).subscribe({ token: Token ->
          view?.onSuccess(token)
        }, {
          view?.onLogin()
        }))
  }
}