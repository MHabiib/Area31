package com.skripsi.area31.core.base

import android.content.Context
import com.skripsi.area31.BaseApp
import dagger.Module
import dagger.Provides

@Module class BaseModule(private val baseApplication: BaseApp) {
  @Provides fun provideContext(): Context {
    return baseApplication.applicationContext
  }
}