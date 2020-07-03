package com.skripsi.area31.enroll.model

import com.google.gson.annotations.SerializedName

data class EnrollStepOneResponse(@SerializedName("code") val code: Int, @SerializedName("course")
val course: Course, @SerializedName("message") val message: Any)