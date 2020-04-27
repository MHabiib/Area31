package com.skripsi.area31.splash.injection

import com.skripsi.area31.splash.network.SplashApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module class SplashModule {
  @Provides fun provideSplashApi(retrofit: Retrofit): SplashApi {
    return retrofit.create(SplashApi::class.java)
  }
}