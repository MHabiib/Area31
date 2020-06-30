package com.skripsi.area31.register.injection

import com.skripsi.area31.register.network.RegisterApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module class RegisterModule {
  @Provides fun provideRegisterApi(retrofit: Retrofit): RegisterApi {
    return retrofit.create(RegisterApi::class.java)
  }
}