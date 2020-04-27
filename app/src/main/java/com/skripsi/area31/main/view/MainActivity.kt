package com.skripsi.area31.main.view

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.skripsi.area31.BaseApp
import com.skripsi.area31.R
import com.skripsi.area31.databinding.ActivityMainBinding
import com.skripsi.area31.home.HomeFragment
import com.skripsi.area31.main.injection.DaggerMainComponent
import com.skripsi.area31.main.injection.MainComponent
import com.skripsi.area31.main.presenter.MainPresenter
import com.skripsi.area31.profile.view.ProfileFragment
import javax.inject.Inject

class MainActivity : AppCompatActivity(), MainContract {
  private var daggerBuild: MainComponent = DaggerMainComponent.builder().baseComponent(
      BaseApp.instance.baseComponent).build()

  init {
    daggerBuild.inject(this)
  }

  @Inject lateinit var presenter: MainPresenter
  private lateinit var binding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
    presenter.attach(this)
    binding.ibNavigationHome.setOnClickListener {
      presenter.onHomeIconClick()
    }
    binding.ibNavigationProfile.setOnClickListener {
      presenter.onProfileIconClick()
    }
    binding.fabExam.setOnClickListener {
      Toast.makeText(this, "Arraa araa", Toast.LENGTH_LONG).show()
    }
  }

  private var doubleBackToExitPressedOnce = false

  override fun onBackPressed() {
    if (doubleBackToExitPressedOnce) {
      finishAffinity()
      return
    }

    this.doubleBackToExitPressedOnce = true
    Toast.makeText(this, "Please click back again to exit", Toast.LENGTH_SHORT).show()

    Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
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
}
