package com.skripsi.area31.quiz.network

import com.skripsi.area31.core.model.SimpleCustomResponse
import com.skripsi.area31.quiz.model.AnsweredQuestion
import com.skripsi.area31.quiz.model.QuizReport
import com.skripsi.area31.quiz.model.QuizResponse
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query

interface QuizApi {
  @GET("api/student/quiz/start") fun getQuizData(@Query("access_token") accessToken: String?,
      @Query("id_quiz") idQuiz: String?): Observable<QuizResponse>

  @GET("api/student/quiz/start") fun getQuizReport(@Query("access_token") accessToken: String?,
      @Query("id_quiz") idQuiz: String?): Observable<QuizReport>

  @PUT("api/student/quiz/submit") fun submitQuiz(@Query("fcm") fcm: String, @Query("access_token")
  accessToken: String, @Query("id_quiz") idQuiz: String, @Body
  answeredQuestion: MutableMap<Int, AnsweredQuestion>): Observable<Response<SimpleCustomResponse>>
}