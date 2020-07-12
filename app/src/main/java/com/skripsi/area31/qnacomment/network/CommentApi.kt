package com.skripsi.area31.qnacomment.network

import com.skripsi.area31.qnacomment.model.ListCommentResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface CommentApi {
  @GET("api/student/course/qna/comment") fun getListComment(@Query("access_token")
  accessToken: String?, @Query("id_post") idPost: String?, @Query("page")
  page: Int?): Observable<ListCommentResponse>
}