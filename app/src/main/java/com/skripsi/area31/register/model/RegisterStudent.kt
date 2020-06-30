package com.skripsi.area31.register.model

import com.google.gson.annotations.SerializedName

data class RegisterStudent(@SerializedName("email") val email: String, @SerializedName("name")
val name: String, @SerializedName("password") val password: String, @SerializedName("phone")
val phone: String, @SerializedName("role") val role: String)