package com.skripsi.area31.profile.model

import com.google.gson.annotations.SerializedName

data class ProfileResponse(@SerializedName("code") val code: Int, @SerializedName("message")
val message: Any, @SerializedName("user") val user: User)