package com.skripsi.area31.register.network

import com.skripsi.area31.core.model.Token
import com.skripsi.area31.register.model.RegisterStudent
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface RegisterApi {
  @POST("api/user") fun createStudent(@Body student: RegisterStudent): Observable<String>

  @FormUrlEncoded @POST("oauth/token") fun auth(@Field("username") username: String,
      @Field("password") password: String, @Field("grant_type")
      grantType: String): Observable<Token>
}