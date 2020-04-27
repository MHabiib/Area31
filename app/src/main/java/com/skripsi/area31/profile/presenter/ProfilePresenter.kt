package com.skripsi.area31.profile.presenter

import com.skripsi.area31.core.base.BasePresenter
import com.skripsi.area31.profile.model.Student
import com.skripsi.area31.profile.network.ProfileApi
import com.skripsi.area31.profile.view.ProfileContract
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ProfilePresenter @Inject constructor(private val profileApi: ProfileApi) :
    BasePresenter<ProfileContract>() {

  fun loadData(accessToken: String) {
    subscriptions.add(
        profileApi.getStudentProfile(accessToken).subscribeOn(Schedulers.io()).observeOn(
            AndroidSchedulers.mainThread()).subscribe({ student: Student ->
          view?.getStudentProfileSuccess(student)
        }, {
          view?.onFailed(it.message.toString())
        }))
  }

  fun logout(accessToken: String) {
    subscriptions.add(profileApi.logout(accessToken).subscribeOn(Schedulers.io()).observeOn(
        AndroidSchedulers.mainThread()).subscribe())
  }

}