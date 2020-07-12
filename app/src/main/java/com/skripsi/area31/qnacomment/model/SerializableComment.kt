package com.skripsi.area31.qnacomment.model

import java.io.Serializable

class SerializableComment(val body: String, val title: String, val idPost: String, val name: String,
    val idUser: String) : Serializable