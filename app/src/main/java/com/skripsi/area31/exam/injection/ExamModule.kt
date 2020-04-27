package com.skripsi.area31.exam.injection

import com.skripsi.area31.exam.network.ExamApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module class ExamModule {
  @Provides fun provideExamApi(retrofit: Retrofit): ExamApi {
    return retrofit.create(ExamApi::class.java)
  }
}