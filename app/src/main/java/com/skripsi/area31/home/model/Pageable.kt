package com.skripsi.area31.home.model

import com.google.gson.annotations.SerializedName

data class Pageable(@SerializedName("unpaged") val unpaged: Boolean, @SerializedName("offset")
val offset: Int, @SerializedName("pageNumber") val pageNumber: Int, @SerializedName("pageSize")
val pageSize: Int, @SerializedName("paged") val paged: Boolean, @SerializedName("sort")
val sort: Sort)