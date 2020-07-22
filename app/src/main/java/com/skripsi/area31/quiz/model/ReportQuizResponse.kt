package com.skripsi.area31.quiz.model

import com.google.gson.annotations.SerializedName

data class ReportQuizResponse(@SerializedName("answer") val answer: List<String>,
    @SerializedName("answerKey") val answerKey: String, @SerializedName("question")
    val question: String, @SerializedName("questionType") val questionType: String,
    @SerializedName("score") val score: Int, @SerializedName("studentAnswer")
    val studentAnswer: String, @SerializedName("studentScore") val studentScore: Int,
    val ratioMap: HashMap<String, Ratio>)