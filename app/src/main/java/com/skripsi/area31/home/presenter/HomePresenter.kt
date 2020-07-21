package com.skripsi.area31.home.presenter

import com.skripsi.area31.core.base.BasePresenter
import com.skripsi.area31.core.model.Token
import com.skripsi.area31.home.network.HomeApi
import com.skripsi.area31.home.view.HomeContract
import com.skripsi.area31.utils.Constants
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class HomePresenter @Inject constructor(private val homeApi: HomeApi) :
    BasePresenter<HomeContract>() {
  fun loadListCourse(accessToken: String, page: Int) {
    subscriptions.add(
        homeApi.loadListCourse(accessToken, page).subscribeOn(Schedulers.io()).observeOn(
            AndroidSchedulers.mainThread()).subscribe({
          view?.loadListCourseSuccess(it)
        }, {
            if (it.message.equals("HTTP 401 ")) {
                view?.refreshToken()
            } else {
                view?.onFailed(it.message.toString())
            }
        }))
  }

  fun refreshToken(refreshToken: String) {
    subscriptions.add(
        homeApi.refresh(Constants.GRANT_TYPE_REFRESH, refreshToken).subscribeOn(Schedulers.io()).observeOn(
            AndroidSchedulers.mainThread()).subscribe({ token: Token ->
          view?.onSuccessRefresh(token)
        }, {
          view?.onLogin()
        }))
  }
}