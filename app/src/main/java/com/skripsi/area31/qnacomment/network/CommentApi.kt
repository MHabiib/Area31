package com.skripsi.area31.qnacomment.network

import com.skripsi.area31.core.model.SimpleCustomResponse
import com.skripsi.area31.qnacomment.model.Comment
import com.skripsi.area31.qnacomment.model.ListCommentResponse
import io.reactivex.Observable
import retrofit2.http.*

interface CommentApi {
  @GET("api/student/course/qna/comment") fun getListComment(@Query("access_token")
  accessToken: String?, @Query("id_post") idPost: String?, @Query("page")
  page: Int?): Observable<ListCommentResponse>

  @POST("api/student/course/qna/comment") fun postCommentStudent(@Query("access_token")
  accessToken: String?, @Query("id_post") idPost: String?, @Query("body")
  body: String?): Observable<Comment>

  @PUT("api/student/course/qna/comment") fun updateCommentStudent(@Query("access_token")
  accessToken: String?, @Query("id_comment") idComment: String?, @Query("body")
  body: String?): Observable<Comment>

  @DELETE("api/student/course/qna/comment") fun deleteCommentStudent(@Query("access_token")
  accessToken: String?, @Query("id_comment") idComment: String?): Observable<SimpleCustomResponse>
}