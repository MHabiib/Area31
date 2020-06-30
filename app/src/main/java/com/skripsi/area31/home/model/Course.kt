package com.skripsi.area31.home.model

import com.google.gson.annotations.SerializedName

data class Course(@SerializedName("courseId") val courseId: String,
    @SerializedName("coursePassword") val coursePassword: String, @SerializedName("description")
    val description: String, @SerializedName("idCourse") val idCourse: String,
    @SerializedName("idInstructor") val idInstructor: String, @SerializedName("idStudent")
    val idStudent: List<String>, @SerializedName("instructorName") val instructorName: String,
    @SerializedName("name") val name: String)