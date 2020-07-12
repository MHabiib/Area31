package com.skripsi.area31.qnareplies.network

import com.skripsi.area31.qnareplies.model.ListRepliesResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface RepliesApi {
  @GET("api/student/course/qna/replies") fun getListReplies(@Query("access_token")
  accessToken: String?, @Query("id_comment") idComment: String?, @Query("page")
  page: Int?): Observable<ListRepliesResponse>
}