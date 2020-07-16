package com.skripsi.area31.quiz.model

import com.google.gson.annotations.SerializedName

data class QuizReport(@SerializedName("description") val description: String,
    @SerializedName("duration") val duration: Long, @SerializedName("reportQuizResponses")
    val reportQuizResponses: List<ReportQuizResponse>, @SerializedName("title") val title: String,
    @SerializedName("quizDate") val quizDate: Long, @SerializedName("assignAt") val assignAt: Long,
    @SerializedName("score") val score: Int)