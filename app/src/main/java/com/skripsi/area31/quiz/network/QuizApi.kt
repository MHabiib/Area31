package com.skripsi.area31.quiz.network

import com.skripsi.area31.core.model.SimpleCustomResponse
import com.skripsi.area31.quiz.model.AnsweredQuestion
import com.skripsi.area31.quiz.model.QuizReport
import com.skripsi.area31.quiz.model.QuizResponse
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface QuizApi {
  @GET("api/student/quiz/start") fun getQuizData(@Query("access_token") accessToken: String?,
      @Query("id_quiz") idQuiz: String?): Observable<QuizResponse>

  @GET("api/student/quiz/start") fun getQuizReport(@Query("access_token") accessToken: String?,
      @Query("id_quiz") idQuiz: String?): Observable<QuizReport>

  @FormUrlEncoded @POST("api/student/quiz/complaint") fun createComplaint(@Field("fcm") fcm: String,
      @Field("id_quiz") idQuiz: String, @Field("access_token") accessToken: String,
      @Field("complaint") complaint: String): Observable<Response<SimpleCustomResponse>>

  @PUT("api/student/quiz/submit") fun submitQuiz(@Query("fcm") fcm: String, @Query("access_token")
  accessToken: String, @Query("id_quiz") idQuiz: String, @Body
  answeredQuestion: MutableMap<Int, AnsweredQuestion>): Observable<Response<SimpleCustomResponse>>
}