package com.skripsi.area31.home.presenter

import com.skripsi.area31.core.base.BasePresenter
import com.skripsi.area31.home.network.HomeApi
import com.skripsi.area31.home.view.HomeContract
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
          view?.onFailed(it.message.toString())
        }))
  }

}