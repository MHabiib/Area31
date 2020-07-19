package com.skripsi.area31.main.view

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.skripsi.area31.BaseApp
import com.skripsi.area31.R
import com.skripsi.area31.databinding.ActivityMainBinding
import com.skripsi.area31.enroll.view.EnrollFragment
import com.skripsi.area31.home.view.HomeFragment
import com.skripsi.area31.main.injection.DaggerMainComponent
import com.skripsi.area31.main.injection.MainComponent
import com.skripsi.area31.main.presenter.MainPresenter
import com.skripsi.area31.profile.view.ProfileFragment
import com.skripsi.area31.utils.Constants
import com.skripsi.area31.utils.Constants.Companion.MY_FIREBASE_MESSAGING
import com.skripsi.area31.utils.Constants.Companion.REFRESH_COURSE
import com.skripsi.area31.utils.Constants.Companion.REQUEST_CAMERA_PERMISSION
import com.skripsi.area31.utils.Constants.Companion.STUDENT_NAME
import java.util.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), MainContract {
  private var daggerBuild: MainComponent = DaggerMainComponent.builder().baseComponent(
      BaseApp.instance.baseComponent).build()

  init {
    daggerBuild.inject(this)
  }

  @Inject lateinit var presenter: MainPresenter
  private lateinit var binding: ActivityMainBinding
  private val bottomSheetFragment = EnrollFragment()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setupLanguage()
    binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
    presenter.attach(this)
    binding.ibNavigationHome.setOnClickListener {
      presenter.onHomeIconClick()
    }
    binding.ibNavigationProfile.setOnClickListener {
      presenter.onProfileIconClick()
    }
    binding.fabExam.setOnClickListener {
      when (this.let {
        ActivityCompat.checkSelfPermission(it, Manifest.permission.CAMERA)
      } != PackageManager.PERMISSION_GRANTED) {
        true -> {
          ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),
              REQUEST_CAMERA_PERMISSION)
        }
        else -> {
          supportFragmentManager.let { fragmentManager ->
            if (!bottomSheetFragment.isAdded) {
              bottomSheetFragment.show(fragmentManager, bottomSheetFragment.tag)
            }
          }
        }
      }
    }
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
      grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == REQUEST_CAMERA_PERMISSION) {
      if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        if (!bottomSheetFragment.isAdded) {
          bottomSheetFragment.show(this.supportFragmentManager, bottomSheetFragment.tag)
        }
      } else {
        showHomeFragment()
      }
    }
  }

  private fun setupLanguage() {
    val shp = getSharedPreferences("com.skripsi.area31.PREFERENCES", Context.MODE_PRIVATE)
    val language = shp.getString("USER_LANGUAGE", "en")
    val locale = Locale(language)
    Locale.setDefault(locale)
    val config = Configuration()
    config.locale = locale
    resources.updateConfiguration(config, resources.displayMetrics)
  }

  private var doubleBackToExitPressedOnce = false

  override fun onBackPressed() {
    if (doubleBackToExitPressedOnce) {
      finishAffinity()
      return
    }
    this.doubleBackToExitPressedOnce = true
    Toast.makeText(this, getString(R.string.click_back_again_exit), Toast.LENGTH_SHORT).show()

    Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
  }

  fun dismissDialog() {
    bottomSheetFragment.dismiss()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (resultCode == REFRESH_COURSE) {
      val homeFragment = supportFragmentManager.findFragmentByTag(
          Constants.HOME_FRAGMENT) as HomeFragment
      homeFragment.refreshListCourse()
      super.onActivityResult(requestCode, resultCode, intent)
    }
  }

  override fun showHomeFragment() {
    binding.homeIndicator.visibility = View.VISIBLE
    binding.profileIndicator.visibility = View.GONE
    if (supportFragmentManager.findFragmentByTag(HomeFragment.TAG) == null) {
      supportFragmentManager.beginTransaction().add(R.id.frame, HomeFragment().newInstance(),
          HomeFragment.TAG).commit()
    } else {
      supportFragmentManager.run { findFragmentByTag(HomeFragment.TAG) }?.let {
        supportFragmentManager.beginTransaction().show(it).commit()
      }
    }
    if (supportFragmentManager.findFragmentByTag(ProfileFragment.TAG) != null) {
      supportFragmentManager.run { findFragmentByTag(ProfileFragment.TAG) }?.let {
        supportFragmentManager.beginTransaction().hide(it).commit()
      }
    }
  }

  override fun showProfileFragment() {
    binding.profileIndicator.visibility = View.VISIBLE
    binding.homeIndicator.visibility = View.GONE
    if (supportFragmentManager.findFragmentByTag(ProfileFragment.TAG) == null) {
      supportFragmentManager.beginTransaction().add(R.id.frame, ProfileFragment().newInstance(),
          ProfileFragment.TAG).commit()
    } else {
      supportFragmentManager.run { findFragmentByTag(ProfileFragment.TAG) }?.let {
        supportFragmentManager.beginTransaction().show(it).commit()
      }
    }
    if (supportFragmentManager.findFragmentByTag(HomeFragment.TAG) != null) {
      supportFragmentManager.run { findFragmentByTag(HomeFragment.TAG) }?.let {
        supportFragmentManager.beginTransaction().hide(it).commit()
      }
    }
  }

  private val mMessageReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
      Toast.makeText(this@MainActivity,
          "Hi ${intent.getStringExtra(STUDENT_NAME)} Your quiz score have been updated !",
          Toast.LENGTH_SHORT).show()
    }
  }

  override fun onResume() {
    super.onResume()
    LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
        IntentFilter(MY_FIREBASE_MESSAGING))
  }

  override fun onPause() {
    LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver)
    super.onPause()
  }
}
