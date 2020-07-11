package com.skripsi.area31.quiz.presenter

import android.util.Log
import com.skripsi.area31.core.base.BasePresenter
import com.skripsi.area31.quiz.model.AnsweredQuestion
import com.skripsi.area31.quiz.network.QuizApi
import com.skripsi.area31.quiz.view.QuizContract
import com.skripsi.area31.utils.Constants.Companion.ON_ERROR
import com.skripsi.area31.utils.Constants.Companion.QUIZ
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
        }, {
          Log.e(QUIZ, ON_ERROR, it)
        }))
  }

  fun getQuizReport(accessToken: String, idQuiz: String) {
    subscriptions.add(
        quizApi.getQuizReport(accessToken, idQuiz).subscribeOn(Schedulers.io()).observeOn(
            AndroidSchedulers.mainThread()).subscribe({ quizResponse ->
          view?.getQuizReportuccess(quizResponse)
        }, {
          Log.e(QUIZ, ON_ERROR, it)
        }))
  }

  fun submitQuiz(fcm: String, accessToken: String, idQuiz: String,
      answeredQuestion: MutableMap<Int, AnsweredQuestion>) {
    subscriptions.add(quizApi.submitQuiz(fcm, accessToken, idQuiz, answeredQuestion).subscribeOn(
        Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({ quizResponse ->
      quizResponse.body()?.message?.let { message -> view?.submitQuizSuccess(message) }
    }, {
      Log.e(QUIZ, it.message.toString())
    }))
  }

  fun createComplaint(accessToken: String, fcm: String, idQuiz: String, complaint: String) {
    subscriptions.add(quizApi.createComplaint(fcm, idQuiz, accessToken, complaint).subscribeOn(
        Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({ quizResponse ->
      quizResponse.body()?.message?.let { message -> view?.createComplaintSuccess(message) }
    }, {
      Log.e(QUIZ, it.message.toString())
    }))
  }
}