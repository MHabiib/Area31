package com.skripsi.area31.quiz.model

import com.google.gson.annotations.SerializedName

data class AnsweredQuestion(@SerializedName("studentAnswer") val answer: String,
    @SerializedName("idQuestionFulfilled") val idQuestion: String)