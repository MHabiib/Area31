package com.skripsi.area31.quizlist.model

import com.google.gson.annotations.SerializedName

data class Quiz(@SerializedName("answeredQuestionList") val answeredQuestionList: Any,
    @SerializedName("assignedAt") val assignedAt: Any, @SerializedName("descriptionQuiz")
    val descriptionQuiz: String, @SerializedName("idCourse") val idCourse: String,
    @SerializedName("idQuiz") val idQuiz: String, @SerializedName("idReport") val idReport: String,
    @SerializedName("idStudent") val idStudent: String, @SerializedName("quizDate")
    val quizDate: Long, @SerializedName("quizDuration") val quizDuration: Long,
    @SerializedName("score") val score: Any, @SerializedName("titleQuiz") val titleQuiz: String)