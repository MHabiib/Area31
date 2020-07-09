package com.skripsi.area31.quiz.model

import com.google.gson.annotations.SerializedName

data class AnsweredQuestion(
    @SerializedName("answer") val answer: String,
    @SerializedName("idQuestion") val idQuestion: String
)