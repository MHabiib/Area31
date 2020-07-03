package com.skripsi.area31.chapter.model

import com.google.gson.annotations.SerializedName

data class Chapter(@SerializedName("idChapter") val idChapter: String,
    @SerializedName("createdDate") val createdDate: Long, @SerializedName("description")
    val description: String, @SerializedName("idCourse") val idCourse: String,
    @SerializedName("lectureNote") val lectureNote: String, @SerializedName("title")
    val title: String)