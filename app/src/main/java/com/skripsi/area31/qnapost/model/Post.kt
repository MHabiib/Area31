package com.skripsi.area31.qnapost.model

data class Post(val body: String, val createdAt: Long, val idCourse: String, val idPost: String,
    val idUser: String, val name: String, val title: String, val totalComment: Int,
    val updatedAt: Any)