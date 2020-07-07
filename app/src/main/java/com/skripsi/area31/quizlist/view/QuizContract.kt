package com.skripsi.area31.quizlist.view

import com.skripsi.area31.core.base.BaseView
import com.skripsi.area31.quizlist.model.ListQuizResponse

interface QuizContract : BaseView {
  fun getListQuizSuccess(response: ListQuizResponse)

}