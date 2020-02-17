package com.skripsi.area31.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.skripsi.area31.MainActivity
import com.skripsi.area31.R
import com.skripsi.area31.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

  private lateinit var binding: ActivitySplashBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

    Thread.sleep(2000)

    startActivity(Intent(this, MainActivity::class.java))
  }
}