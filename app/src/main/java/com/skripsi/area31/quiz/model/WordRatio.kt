package com.skripsi.area31.quiz.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize data class WordRatio(val word: String, val ratioAnswer: Int, val ratioAnswerKey: Int) :
    Parcelable