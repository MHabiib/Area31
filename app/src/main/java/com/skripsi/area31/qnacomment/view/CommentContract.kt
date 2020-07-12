package com.skripsi.area31.qnacomment.view

import com.skripsi.area31.core.base.BaseView
import com.skripsi.area31.core.model.SimpleCustomResponse
import com.skripsi.area31.qnacomment.model.Comment
import com.skripsi.area31.qnacomment.model.ListCommentResponse

interface CommentContract : BaseView {
  fun getListCommentSuccess(response: ListCommentResponse)

  fun postCommentStudentSuccess(comment: Comment)

  fun updateCommentStudentSuccess(comment: Comment)

  fun deleteCommentStudentSuccess(it: SimpleCustomResponse)

}