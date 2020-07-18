package com.skripsi.area31.complaint.presenter

import android.util.Log
import com.skripsi.area31.complaint.network.ComplaintApi
import com.skripsi.area31.complaint.view.ComplaintContract
import com.skripsi.area31.core.base.BasePresenter
import com.skripsi.area31.utils.Constants.Companion.COMPLAINT
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ComplaintPresenter @Inject constructor(private val complaintApi: ComplaintApi) :
    BasePresenter<ComplaintContract>() {
  fun getListComplaint(accessToken: String, idCourse: String) {
    subscriptions.add(
        complaintApi.getAllStudentComplaint(accessToken, idCourse).subscribeOn(Schedulers.io()).observeOn(
            AndroidSchedulers.mainThread()).subscribe({
          view?.getListComplaintSuccess(it)
        }, {
          Log.e(COMPLAINT, it.message.toString())
        }))
  }
}