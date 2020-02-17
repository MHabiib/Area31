package com.skripsi.area31.core.base

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.skripsi.area31.core.network.BasicAuthInterceptor
import com.skripsi.area31.utils.Constants.Companion.BASE
import com.skripsi.area31.utils.Constants.Companion.PASSWORD
import com.skripsi.area31.utils.Constants.Companion.READ_TIMEOUT
import com.skripsi.area31.utils.Constants.Companion.USERNAME
import com.skripsi.area31.utils.Constants.Companion.WRITE_TIMEOUT
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module class BaseNetworkModule {
  @Provides fun provideGson(): Gson {
    return GsonBuilder().setLenient().create()
  }

  @Provides fun provideOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder().addInterceptor(
        BasicAuthInterceptor(USERNAME, PASSWORD)).writeTimeout(WRITE_TIMEOUT,
        TimeUnit.SECONDS).readTimeout(READ_TIMEOUT, TimeUnit.SECONDS).build()
  }

  @Provides fun provideNetworkManager(gson: Gson, client: OkHttpClient): Retrofit {
    return Retrofit.Builder().addConverterFactory(
        GsonConverterFactory.create(gson)).addCallAdapterFactory(
        RxJava2CallAdapterFactory.create()).baseUrl(BASE).client(client).build()
  }
}