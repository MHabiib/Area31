package com.skripsi.area31.courseresource.view

import com.skripsi.area31.core.base.BaseView
import com.skripsi.area31.courseresource.model.Resource

interface ResourceContract : BaseView {
  fun getListResourceSuccess(response: List<Resource>)

}