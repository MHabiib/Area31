package com.skripsi.area31.home.injection

import com.skripsi.area31.home.network.HomeApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module class HomeModule {
  @Provides fun provideHomeApi(retrofit: Retrofit): HomeApi {
    return retrofit.create(HomeApi::class.java)
  }
}