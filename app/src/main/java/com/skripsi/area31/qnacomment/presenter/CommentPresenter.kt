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

  fun postCommentStudent(idPost: String, body: String, accessToken: String) {
    subscriptions.add(commentApi.postCommentStudent(accessToken, idPost, body).subscribeOn(
        Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
      view?.postCommentStudentSuccess(it)
    }, {
      view?.onFailed(it.message.toString())
    }))
  }

  fun updateCommentStudent(idComment: String, body: String, accessToken: String) {
    subscriptions.add(commentApi.updateCommentStudent(accessToken, idComment, body).subscribeOn(
        Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
      view?.updateCommentStudentSuccess(it)
    }, {
      view?.onFailed(it.message.toString())
    }))
  }

  fun deleteCommentStudent(idComment: String, accessToken: String) {
    subscriptions.add(commentApi.deleteCommentStudent(accessToken, idComment).subscribeOn(
        Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
      view?.deleteCommentStudentSuccess(it)
    }, {
      view?.onFailed(it.message.toString())
    }))
  }
}