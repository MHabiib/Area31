package com.skripsi.area31.core.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize data class Token(@SerializedName("scope") val scope: String,
    @SerializedName("token_type") val tokenType: String, @SerializedName("expires_in")
    var expiresIn: Long, @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("id_token") val idToken: String, @SerializedName("access_token")
    val accessToken: String, var role: String) : Parcelable
