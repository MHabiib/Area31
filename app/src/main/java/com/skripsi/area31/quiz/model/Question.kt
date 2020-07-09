package com.skripsi.area31.quiz.model

import com.google.gson.annotations.SerializedName

data class Question(
    @SerializedName("answer") val answer: List<String>,
    @SerializedName("answerKey") val answerKey: Any,
    @SerializedName("idQuestion") val idQuestion: String,
    @SerializedName("question") val question: String,
    @SerializedName("questionType") val questionType: String,
    @SerializedName("score") val score: Int
)