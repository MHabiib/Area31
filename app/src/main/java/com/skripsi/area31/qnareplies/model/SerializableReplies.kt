package com.skripsi.area31.qnareplies.model

import java.io.Serializable

class SerializableReplies(val title: String, val bodyPost: String, val namePost: String,
    val bodyComment: String, val nameComment: String, val idComment: String) : Serializable