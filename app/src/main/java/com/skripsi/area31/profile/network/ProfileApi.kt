package com.skripsi.area31.profile.network

import com.skripsi.area31.profile.model.Student
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ProfileApi {
  @GET("api/student/profile") fun getStudentProfile(@Query("access_token")
  accessToken: String?): Observable<Student>

  @POST("logout-account") fun logout(@Query("access_token")
  accessToken: String?): Observable<String>
}