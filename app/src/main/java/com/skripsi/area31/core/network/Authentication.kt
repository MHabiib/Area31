package com.skripsi.area31.core.network

import android.content.Context
import com.google.gson.Gson
import com.skripsi.area31.core.model.Token
import com.skripsi.area31.utils.Constants.Companion.AUTHENTICATION
import com.skripsi.area31.utils.Constants.Companion.TOKEN
import java.util.*

object Authentication {
  private fun put(context: Context, obj: Token): Boolean {
    val preferences = context.getSharedPreferences(AUTHENTICATION, Context.MODE_PRIVATE)
    val editor = preferences.edit()
    return editor.putString(TOKEN, Gson().toJson(obj)).commit()
  }

  fun get(context: Context?): Token? {
    val preferences = context?.getSharedPreferences(AUTHENTICATION, Context.MODE_PRIVATE)
    val json = preferences?.getString(TOKEN, null)
    if (json != null) return Gson().fromJson(json, Token::class.java)
    return null
  }

  fun save(context: Context, obj: Token, role: String): Boolean {
    val calendar = GregorianCalendar.getInstance()
    var expiresIn: Long = calendar.time.time
    expiresIn += obj.expiresIn
    obj.expiresIn = expiresIn
    obj.role = role
    return put(context, obj)
  }

  private fun isExpired(context: Context?): Boolean {
    val token = get(context)
    if (token != null) {
      val calendar = GregorianCalendar.getInstance()
      val currentTime = calendar.time.time
      val expiresIn = token.expiresIn
      if (expiresIn == 0L) throw WithoutAuthenticatedException()
      return currentTime > expiresIn
    } else {
      throw WithoutAuthenticatedException()
    }
  }

  fun isAuthenticated(context: Context?): Boolean = !isExpired(context)

  class WithoutAuthenticatedException : Exception()

  fun getRefresh(context: Context): String {
    val token = get(context)
    if (null != token) {
      return token.refreshToken
    } else {
      throw WithoutAuthenticatedException()
    }
  }

  fun delete(context: Context) {
    val preferences = context.getSharedPreferences(AUTHENTICATION, Context.MODE_PRIVATE)
    val editor = preferences.edit()
    println(TOKEN + preferences.all[TOKEN])
    editor.remove(TOKEN).apply()
    println(preferences.all[TOKEN])
  }
}