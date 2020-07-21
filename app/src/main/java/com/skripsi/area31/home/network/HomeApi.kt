package com.skripsi.area31.home.network

import com.skripsi.area31.core.model.Token
import com.skripsi.area31.home.model.ListCourse
import io.reactivex.Observable
import retrofit2.http.*

interface HomeApi {
  @GET("api/student/course") fun loadListCourse(@Query("access_token") accessToken: String?,
      @Query("page") page: Int?): Observable<ListCourse>

  @FormUrlEncoded @POST("oauth/token") fun refresh(@Field("grant_type") grantType: String,
      @Field("refresh_token") refreshAuth: String): Observable<Token>
}