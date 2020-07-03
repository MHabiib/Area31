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
}