package com.skripsi.area31.main.presenter

import com.skripsi.area31.main.view.MainContract
import javax.inject.Inject

class MainPresenter @Inject constructor() {
  private lateinit var view: MainContract

  fun onHomeIconClick() {
    view.showHomeFragment()
  }

  fun onProfileIconClick() {
    view.showProfileFragment()
  }

  fun attach(view: MainContract) {
    this.view = view
    view.showHomeFragment()
  }
}