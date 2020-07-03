package com.skripsi.area31.chapter.injection

import com.skripsi.area31.chapter.network.ChapterApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module class ChapterModule {
  @Provides fun provideChapterApi(retrofit: Retrofit): ChapterApi {
    return retrofit.create(ChapterApi::class.java)
  }
}