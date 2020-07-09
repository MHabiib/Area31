package com.skripsi.area31.quizlist.view

import com.skripsi.area31.core.base.BaseView
import com.skripsi.area31.quizlist.model.ListQuizResponse

interface QuizListContract : BaseView {
  fun getListQuizSuccess(response: ListQuizResponse)

}