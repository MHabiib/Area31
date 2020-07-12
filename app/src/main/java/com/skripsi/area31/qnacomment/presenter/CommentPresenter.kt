package com.skripsi.area31.qnacomment.presenter

import com.skripsi.area31.core.base.BasePresenter
import com.skripsi.area31.qnacomment.network.CommentApi
import com.skripsi.area31.qnacomment.view.CommentContract
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class CommentPresenter @Inject constructor(private val commentApi: CommentApi) :
    BasePresenter<CommentContract>() {
  fun getListComment(accessToken: String, idPost: String, page: Int) {
    subscriptions.add(
        commentApi.getListComment(accessToken, idPost, page).subscribeOn(Schedulers.io()).observeOn(
            AndroidSchedulers.mainThread()).subscribe({ response ->
          view?.getListCommentSuccess(response)
        }, {
          view?.onFailed(it.message.toString())
        }))
  }
}