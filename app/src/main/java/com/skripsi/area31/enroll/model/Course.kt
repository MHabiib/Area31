package com.skripsi.area31.enroll.model

import com.google.gson.annotations.SerializedName

data class Course(@SerializedName("courseId") val courseId: String,
    @SerializedName("coursePassword") val coursePassword: Any, @SerializedName("description")
    val description: String, @SerializedName("idCourse") val idCourse: Any,
    @SerializedName("idInstructor") val idInstructor: Any, @SerializedName("idStudent")
    val idStudent: Any, @SerializedName("instructorName") val instructorName: String,
    @SerializedName("name") val name: String, @SerializedName("qr") val qr: String,
    @SerializedName("status") val status: String)