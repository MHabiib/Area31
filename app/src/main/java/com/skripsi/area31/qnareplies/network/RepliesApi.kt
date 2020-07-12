package com.skripsi.area31.qnareplies.network

import com.skripsi.area31.core.model.SimpleCustomResponse
import com.skripsi.area31.qnareplies.model.ListRepliesResponse
import com.skripsi.area31.qnareplies.model.Replies
import io.reactivex.Observable
import retrofit2.http.*

interface RepliesApi {
  @GET("api/student/course/qna/replies") fun getListReplies(@Query("access_token")
  accessToken: String?, @Query("id_comment") idComment: String?, @Query("page")
  page: Int?): Observable<ListRepliesResponse>

  @POST("api/student/course/qna/replies") fun postRepliesStudent(@Query("access_token")
  accessToken: String?, @Query("id_comment") idComment: String?, @Query("body")
  body: String?): Observable<Replies>

  @PUT("api/student/course/qna/replies") fun updateRepliesStudent(@Query("access_token")
  accessToken: String?, @Query("id_replies") idReplies: String?, @Query("body")
  body: String?): Observable<Replies>

  @DELETE("api/student/course/qna/replies") fun deleteRepliesStudent(@Query("access_token")
  accessToken: String?, @Query("id_replies") idReplies: String?): Observable<SimpleCustomResponse>
}