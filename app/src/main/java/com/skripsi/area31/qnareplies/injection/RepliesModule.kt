package com.skripsi.area31.qnareplies.injection

import com.skripsi.area31.qnareplies.network.RepliesApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module class RepliesModule {
  @Provides fun provideRepliesApi(retrofit: Retrofit): RepliesApi {
    return retrofit.create(RepliesApi::class.java)
  }
}