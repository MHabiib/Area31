package com.skripsi.area31.courseresource.presenter

import android.util.Log
import com.skripsi.area31.core.base.BasePresenter
import com.skripsi.area31.courseresource.network.ResourceApi
import com.skripsi.area31.courseresource.view.ResourceContract
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ResourcePresenter @Inject constructor(private val resourceApi: ResourceApi) :
    BasePresenter<ResourceContract>() {
  fun getListResource(accessToken: String, idCourse: String) {
    subscriptions.add(
        resourceApi.getListResource(accessToken, idCourse).subscribeOn(Schedulers.io()).observeOn(
            AndroidSchedulers.mainThread()).subscribe({
          it.body()?.resourceList.let { resourceList ->
            resourceList?.let { list ->
              view?.getListResourceSuccess(list)
            }
          }
        }, {
          Log.e("ENROLL", it.message.toString())
        }))
  }
}