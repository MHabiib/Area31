package com.skripsi.area31.home.network

import com.skripsi.area31.home.model.ListCourse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface HomeApi {
  @GET("api/student/course") fun loadListCourse(@Query("access_token") accessToken: String?,
      @Query("page") page: Int?): Observable<ListCourse>
}