package com.skripsi.area31.enroll.network

import com.skripsi.area31.core.model.SimpleCustomResponse
import com.skripsi.area31.enroll.model.EnrollStepOneResponse
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query

interface EnrollApi {
  @GET("api/student/course/check") fun checkCourse(@Query("access_token") accessToken: String?,
      @Query("courseId") courseId: String?): Observable<Response<EnrollStepOneResponse>>

  @PUT("api/student/course/joinStepOne") fun joinCourse(@Query("access_token") accessToken: String?,
      @Query("courseId") courseId: String?, @Query("password")
      password: String?): Observable<Response<SimpleCustomResponse>>
}