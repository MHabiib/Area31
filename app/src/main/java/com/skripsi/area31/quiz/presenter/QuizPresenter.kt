package com.skripsi.area31.quiz.presenter

import android.util.Log
import com.skripsi.area31.core.base.BasePresenter
import com.skripsi.area31.quiz.network.QuizApi
import com.skripsi.area31.quiz.view.QuizContract
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class QuizPresenter @Inject constructor(private val quizApi: QuizApi) :
    BasePresenter<QuizContract>() {
    fun getQuizData(accessToken: String, idQuiz: String) {
        subscriptions.add(
            quizApi.getQuizData(accessToken, idQuiz).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe({ quizResponse ->
                view?.getQuizDataSuccess(quizResponse)
            }, {1
                Log.e("PROFILE", "onError: ", it)
            }))
    }
}