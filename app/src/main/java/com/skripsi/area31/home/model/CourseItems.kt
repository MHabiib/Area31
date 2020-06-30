package com.skripsi.area31.home.model

import com.google.gson.annotations.SerializedName

data class CourseItems(@SerializedName("courseName") val courseName: String,
    @SerializedName("courseInstructor") val courseInstructor: String, @SerializedName("courseId")
    val courseId: String, var position: Int)