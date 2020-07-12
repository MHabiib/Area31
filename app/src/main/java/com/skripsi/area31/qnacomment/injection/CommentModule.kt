package com.skripsi.area31.qnacomment.injection

import com.skripsi.area31.qnacomment.network.CommentApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module class CommentModule {
  @Provides fun provideCommentApi(retrofit: Retrofit): CommentApi {
    return retrofit.create(CommentApi::class.java)
  }
}