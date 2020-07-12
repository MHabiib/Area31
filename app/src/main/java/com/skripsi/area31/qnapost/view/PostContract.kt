package com.skripsi.area31.qnapost.view

import com.skripsi.area31.core.base.BaseView
import com.skripsi.area31.qnapost.model.ListPostResponse

interface PostContract : BaseView {
  fun getListPostSuccess(response: ListPostResponse)

}