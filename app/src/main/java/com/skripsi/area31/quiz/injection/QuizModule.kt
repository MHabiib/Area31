package com.skripsi.area31.quiz.injection

import com.skripsi.area31.quiz.network.QuizApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module class QuizModule {
  @Provides fun provideQuizApi(retrofit: Retrofit): QuizApi {
    return retrofit.create(QuizApi::class.java)
  }
}