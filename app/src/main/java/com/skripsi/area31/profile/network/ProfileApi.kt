package com.skripsi.area31.profile.network

import com.skripsi.area31.core.model.SimpleCustomResponse
import com.skripsi.area31.core.model.Token
import com.skripsi.area31.profile.model.ProfileResponse
import com.skripsi.area31.register.model.RegisterStudent
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface ProfileApi {
  @GET("api/student/profile") fun getStudentProfile(@Query("access_token")
  accessToken: String?): Observable<Response<ProfileResponse>>

  @PUT("api/student/user") fun updateUser(@Query("access_token") accessToken: String?,
      @Query("new_password") newPassword: String?, @Body
      student: RegisterStudent): Observable<Response<SimpleCustomResponse>>

  @FormUrlEncoded @POST("oauth/token") fun refresh(@Field("grant_type") grantType: String,
      @Field("refresh_token") refreshAuth: String): Observable<Token>

  @POST("logout-account") fun logout(@Query("access_token")
  accessToken: String?): Observable<String>
}