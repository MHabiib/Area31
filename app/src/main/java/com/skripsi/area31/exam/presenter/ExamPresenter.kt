package com.skripsi.area31.exam.presenter

import com.skripsi.area31.core.base.BasePresenter
import com.skripsi.area31.exam.network.ExamApi
import com.skripsi.area31.exam.view.ExamContract
import javax.inject.Inject

class ExamPresenter @Inject constructor(private val examApi: ExamApi) :
    BasePresenter<ExamContract>()