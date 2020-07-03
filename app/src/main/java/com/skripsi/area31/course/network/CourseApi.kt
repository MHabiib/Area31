package com.skripsi.area31.course.network

import com.skripsi.area31.core.model.SimpleCustomResponse
import com.skripsi.area31.course.model.CourseDetailsResponse
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query

interface CourseApi {
  @GET("api/student/course/details") fun courseDetails(@Query("access_token") accessToken: String?,
      @Query("idCourse") idCourse: String?): Observable<Response<CourseDetailsResponse>>

  @PUT("api/student/course/leave") fun leaveCourse(@Query("access_token") accessToken: String?,
      @Query("idCourse") idCourse: String?): Observable<Response<SimpleCustomResponse>>
}