package com.skripsi.area31.qnareplies.view

import com.skripsi.area31.core.base.BaseView
import com.skripsi.area31.qnareplies.model.ListRepliesResponse
import com.skripsi.area31.qnareplies.model.Replies

interface RepliesContract : BaseView {
  fun getListRepliesSuccess(response: ListRepliesResponse)

  fun postRepliesStudentSuccess(replies: Replies)

  fun updateRepliesStudentSuccess(replies: Replies)

  fun deleteRepliesStudentSuccess()
}