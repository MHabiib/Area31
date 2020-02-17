package com.skripsi.area31

import android.app.Application
import com.skripsi.area31.core.base.BaseComponent
import com.skripsi.area31.core.base.BaseModule
import com.skripsi.area31.core.base.DaggerBaseComponent

class BaseApp : Application() {

  lateinit var baseComponent: BaseComponent

  companion object {
    lateinit var instance: BaseApp
  }

  override fun onCreate() {
    super.onCreate()
    baseComponent = DaggerBaseComponent.builder().baseModule(BaseModule(this)).build()
    baseComponent.inject(this)
    instance = this
  }
}