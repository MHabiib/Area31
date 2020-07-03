package com.skripsi.area31.core.model

import com.google.gson.annotations.SerializedName

data class SimpleCustomResponse(@SerializedName("code") val code: Int, @SerializedName("message")
val message: String)