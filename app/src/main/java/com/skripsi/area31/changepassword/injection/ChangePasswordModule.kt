package com.skripsi.area31.changepassword.injection

import com.skripsi.area31.changepassword.network.ChangePasswordApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module class ChangePasswordModule {
  @Provides fun providesChangePasswordApi(retrofit: Retrofit): ChangePasswordApi {
    return retrofit.create(ChangePasswordApi::class.java)
  }
}