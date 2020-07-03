package com.skripsi.area31.profile.model

import com.google.gson.annotations.SerializedName

data class User(@SerializedName("email") val email: String, @SerializedName("idCourse")
val idCourse: List<String>, @SerializedName("idUser") val idUser: String, @SerializedName("name")
val name: String, @SerializedName("password") val password: String, @SerializedName("phone")
val phone: String, @SerializedName("role") val role: String, @SerializedName("version")
val version: Int)