package com.skripsi.area31.changepassword.network

import com.skripsi.area31.changepassword.model.ChangePassword
import com.skripsi.area31.core.model.SimpleCustomResponse
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Query

interface ChangePasswordApi {
  @PUT("api/student/user") fun updateUserPassword(@Query("access_token") accessToken: String?,
      @Query("new_password") newPassword: String?, @Body
      changePassword: ChangePassword): Observable<Response<SimpleCustomResponse>>
}