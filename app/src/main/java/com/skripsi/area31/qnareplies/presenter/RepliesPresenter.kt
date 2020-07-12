package com.skripsi.area31.qnareplies.presenter

import com.skripsi.area31.core.base.BasePresenter
import com.skripsi.area31.qnareplies.network.RepliesApi
import com.skripsi.area31.qnareplies.view.RepliesContract
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class RepliesPresenter @Inject constructor(private val repliesApi: RepliesApi) :
    BasePresenter<RepliesContract>() {
  fun getListReplies(accessToken: String, idComment: String, page: Int) {
    subscriptions.add(repliesApi.getListReplies(accessToken, idComment, page).subscribeOn(
        Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({ response ->
      view?.getListRepliesSuccess(response)
    }, {
      view?.onFailed(it.message.toString())
    }))
  }
}