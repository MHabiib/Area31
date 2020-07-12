package com.skripsi.area31.qnapost.injection

import com.skripsi.area31.qnapost.network.PostApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module class PostModule {
  @Provides fun providePostApi(retrofit: Retrofit): PostApi {
    return retrofit.create(PostApi::class.java)
  }
}