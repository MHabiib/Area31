package com.skripsi.area31.enroll.presenter

import com.skripsi.area31.core.base.BasePresenter
import com.skripsi.area31.enroll.network.EnrollApi
import com.skripsi.area31.enroll.view.EnrollContract
import javax.inject.Inject

class EnrollPresenter @Inject constructor(private val enrollApi: EnrollApi) :
    BasePresenter<EnrollContract>()