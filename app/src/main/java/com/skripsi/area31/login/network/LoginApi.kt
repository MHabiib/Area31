package com.skripsi.area31.login.network

import com.skripsi.area31.core.model.SimpleCustomResponse
import com.skripsi.area31.core.model.Token
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface LoginApi {
  @FormUrlEncoded @POST("oauth/token") fun auth(@Field("username") username: String,
      @Field("password") password: String, @Field("grant_type")
      grantType: String): Observable<Token>

  @GET("isAuthorize") fun isAuthorize(@Query("access_token") accessToken: String?, @Query("role")
  role: String?): Observable<Response<SimpleCustomResponse>>

  @POST("forgot-password") fun forgotPassword(@Query("email")
  email: String?): Observable<Response<SimpleCustomResponse>>

  @POST("forgot-password/code") fun forgotPasswordNextStep(@Query("email") email: String?,
      @Query("code") code: Int?): Observable<Response<SimpleCustomResponse>>

  @POST("forgot-password/resetPassword") fun resetPassword(@Query("email") email: String?,
      @Query("password") password: String?, @Query("code") code: Int?): Observable<Response<SimpleCustomResponse>>
}