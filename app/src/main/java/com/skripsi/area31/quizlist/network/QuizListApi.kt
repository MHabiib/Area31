package com.skripsi.area31.quizlist.network

import com.skripsi.area31.quizlist.model.ListQuizResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface QuizListApi {
  @GET("api/student/quiz") fun getListQuiz(@Query("access_token") accessToken: String?,
      @Query("id_course") idCourse: String?, @Query("page")
      page: Int?): Observable<ListQuizResponse>
}