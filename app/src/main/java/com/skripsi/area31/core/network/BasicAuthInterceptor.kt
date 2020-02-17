package com.skripsi.area31.core.network

import com.skripsi.area31.utils.Constants.Companion.AUTHORIZATION
import okhttp3.Credentials
import okhttp3.Interceptor

class BasicAuthInterceptor(username: String, password: String) : Interceptor {
  private var credentials: String = Credentials.basic(username, password)

  override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
    var request = chain.request()
    request = request.newBuilder().header(AUTHORIZATION, credentials).build()
    return chain.proceed(request)
  }
}
