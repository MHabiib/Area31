package com.skripsi.area31.complaint.network

import com.skripsi.area31.complaint.model.Complaint
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface ComplaintApi {
  @GET("api/student/complaint") fun getAllStudentComplaint(@Query("access_token")
  accessToken: String?, @Query("id_course")
  idCourse: String?): Observable<List<Complaint>>
}