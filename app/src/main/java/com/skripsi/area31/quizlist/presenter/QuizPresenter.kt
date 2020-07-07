package com.skripsi.area31.quizlist.presenter

import com.skripsi.area31.core.base.BasePresenter
import com.skripsi.area31.quizlist.network.QuizApi
import com.skripsi.area31.quizlist.view.QuizContract
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class QuizPresenter @Inject constructor(private val quizApi: QuizApi) :
    BasePresenter<QuizContract>() {
  fun getListQuiz(accessToken: String, idCourse: String, page: Int) {
    subscriptions.add(
        quizApi.getListQuiz(accessToken, idCourse, page).subscribeOn(Schedulers.io()).observeOn(
            AndroidSchedulers.mainThread()).subscribe({ response ->
          view?.getListQuizSuccess(response)
        }, {
          view?.onFailed(it.message.toString())
        }))
  }
}