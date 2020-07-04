package com.skripsi.area31.courseresource.injection

import com.skripsi.area31.courseresource.network.ResourceApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module class ResourceModule {
  @Provides fun provideResourceApi(retrofit: Retrofit): ResourceApi {
    return retrofit.create(ResourceApi::class.java)
  }
}