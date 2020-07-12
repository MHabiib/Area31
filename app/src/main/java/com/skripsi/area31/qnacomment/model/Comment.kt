package com.skripsi.area31.qnacomment.model

data class Comment(val body: String, val createdAt: Long, val idComment: String, val idPost: String,
    val idUser: String, val name: String, val totalReplies: Int, val updatedAt: Any)