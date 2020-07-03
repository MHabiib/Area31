package com.skripsi.area31.chapter.network

import com.skripsi.area31.chapter.model.ListChapterResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface ChapterApi {
  @GET("api/student/chapter") fun getListChapter(@Query("access_token") accessToken: String?,
      @Query("id_course") idCourse: String?, @Query("page")
      page: Int?): Observable<ListChapterResponse>
}