package com.skripsi.area31.enroll.injection

import com.skripsi.area31.enroll.network.EnrollApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module class EnrollModule {
  @Provides fun providesEnrollApi(retrofit: Retrofit): EnrollApi {
    return retrofit.create(EnrollApi::class.java)
  }
}