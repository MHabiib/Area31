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

  fun postRepliesStudent(idPost: String, body: String, accessToken: String) {
    subscriptions.add(repliesApi.postRepliesStudent(accessToken, idPost, body).subscribeOn(
        Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
      view?.postRepliesStudentSuccess(it)
    }, {
      view?.onFailed(it.message.toString())
    }))
  }

  fun updateRepliesStudent(idReplies: String, body: String, accessToken: String) {
    subscriptions.add(repliesApi.updateRepliesStudent(accessToken, idReplies, body).subscribeOn(
        Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
      view?.updateRepliesStudentSuccess(it)
    }, {
      view?.onFailed(it.message.toString())
    }))
  }

  fun deleteRepliesStudent(idReplies: String, accessToken: String) {
    subscriptions.add(repliesApi.deleteRepliesStudent(accessToken, idReplies).subscribeOn(
        Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
      view?.deleteRepliesStudentSuccess()
    }, {
      view?.onFailed(it.message.toString())
    }))
  }
}