package com.skripsi.area31.utils

import com.skripsi.area31.utils.Constants.Companion.SHORT_MONTH_DATE_TIME_FORMAT
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class Utils {
  companion object {
    fun convertLongToMinutes(time: Long): String {
      return TimeUnit.MILLISECONDS.toMinutes(time).toString()
    }

    fun convertLongToTimeShortMonth(time: Long): String {
      return SimpleDateFormat(SHORT_MONTH_DATE_TIME_FORMAT, Locale.getDefault()).format(Date(time))
    }

    fun convertLongToSimpleTime(time: Long): String {
      var minutes = TimeUnit.MILLISECONDS.toMinutes(time)
      val converted: String
      if (minutes > 59) {
        minutes = TimeUnit.MILLISECONDS.toHours(time)
        converted = "$minutes hours ago"
      } else {
        converted = "$minutes minutes ago"
      }
      return converted
    }
  }
}