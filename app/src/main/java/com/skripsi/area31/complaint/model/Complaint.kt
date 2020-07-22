package com.skripsi.area31.complaint.model

data class Complaint(val assignedAt: Long, val description: String, val idComplaint: String,
    val quizDate: Long, val quizTitle: String, val reason: String, val score: Int,
    val status: String)