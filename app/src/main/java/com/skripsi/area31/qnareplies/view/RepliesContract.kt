package com.skripsi.area31.qnareplies.view

import com.skripsi.area31.core.base.BaseView
import com.skripsi.area31.qnareplies.model.ListRepliesResponse

interface RepliesContract : BaseView {
  fun getListRepliesSuccess(response: ListRepliesResponse)

}