package com.skripsi.area31.complaint.injection

import com.skripsi.area31.complaint.network.ComplaintApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module class ComplaintModule {
  @Provides fun provideComplaintApi(retrofit: Retrofit): ComplaintApi {
    return retrofit.create(ComplaintApi::class.java)
  }
}