package com.skripsi.area31.quiz.view

import com.skripsi.area31.core.base.BaseView
import com.skripsi.area31.quiz.model.QuizResponse

interface QuizContract : BaseView {
  fun getQuizDataSuccess(quizResponse: QuizResponse)
}