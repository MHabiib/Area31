package com.skripsi.area31.qnapost.presenter

import com.skripsi.area31.core.base.BasePresenter
import com.skripsi.area31.qnapost.network.PostApi
import com.skripsi.area31.qnapost.view.PostContract
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class PostPresenter @Inject constructor(private val postApi: PostApi) :
    BasePresenter<PostContract>() {
  fun getListPost(accessToken: String, idCourse: String, page: Int) {
    subscriptions.add(
        postApi.getListPost(accessToken, idCourse, page).subscribeOn(Schedulers.io()).observeOn(
            AndroidSchedulers.mainThread()).subscribe({ response ->
          view?.getListPostSuccess(response)
        }, {
          view?.onFailed(it.message.toString())
        }))
  }
}