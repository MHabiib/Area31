package com.skripsi.area31.courseresource.network

import com.skripsi.area31.courseresource.model.ListResourceResponse
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ResourceApi {
  @GET("api/student/course/resource") fun getListResource(@Query("access_token")
  accessToken: String?, @Query("idCourse")
  idCourse: String?): Observable<Response<ListResourceResponse>>
}