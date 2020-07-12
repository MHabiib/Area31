package com.skripsi.area31.qnapost.network

import com.skripsi.area31.qnapost.model.ListPostResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface PostApi {
  @GET("api/student/course/qna/post") fun getListPost(@Query("access_token") accessToken: String?,
      @Query("id_course") idCourse: String?, @Query("page")
      page: Int?): Observable<ListPostResponse>
}