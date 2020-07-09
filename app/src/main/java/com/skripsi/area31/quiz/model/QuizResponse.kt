package com.skripsi.area31.quiz.model

import com.google.gson.annotations.SerializedName

data class QuizResponse(
    @SerializedName("description") val description: String,
    @SerializedName("startDate") val startDate: Long,
    @SerializedName("duration") val duration: Long,
    @SerializedName("questionList") val questionList: List<Question>,
    @SerializedName("title") val title: String
)