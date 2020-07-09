package com.skripsi.area31.quiz.network

import com.skripsi.area31.quiz.model.QuizResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface QuizApi {
  @GET("api/student/quiz/start") fun getQuizData(@Query("access_token") accessToken: String?,
      @Query("id_quiz") idQuiz: String?): Observable<QuizResponse>
}