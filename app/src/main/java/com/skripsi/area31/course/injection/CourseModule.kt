package com.skripsi.area31.course.injection

import com.skripsi.area31.course.network.CourseApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module class CourseModule {
  @Provides fun provideCourseApi(retrofit: Retrofit): CourseApi {
    return retrofit.create(CourseApi::class.java)
  }
}