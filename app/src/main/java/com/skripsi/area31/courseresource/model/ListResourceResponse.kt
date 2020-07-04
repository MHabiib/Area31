package com.skripsi.area31.courseresource.model

data class ListResourceResponse(val code: Int, val message: String,
    val resourceList: List<Resource>)