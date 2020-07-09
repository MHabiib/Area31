package com.skripsi.area31.quizlist.injection

import com.skripsi.area31.quizlist.network.QuizListApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module class QuizListModule {
  @Provides fun provideQuizApi(retrofit: Retrofit): QuizListApi {
    return retrofit.create(QuizListApi::class.java)
  }
}