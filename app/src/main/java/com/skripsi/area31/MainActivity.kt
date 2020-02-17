package com.skripsi.area31

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.skripsi.area31.databinding.ActivityMainBinding
import com.skripsi.area31.home.HomeFragment

class MainActivity : AppCompatActivity() {
  private lateinit var binding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
    supportFragmentManager.beginTransaction().add(R.id.frame, HomeFragment().newInstance(),
        HomeFragment.TAG).commit()
  }
}
