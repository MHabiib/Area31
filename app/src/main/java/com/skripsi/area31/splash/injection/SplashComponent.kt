package com.skripsi.area31.splash.injection

import com.skripsi.area31.core.base.BaseComponent
import com.skripsi.area31.splash.view.SplashActivity
import dagger.Component

@Component(dependencies = [BaseComponent::class], modules = [SplashModule::class])
interface SplashComponent {
  fun inject(splashActivity: SplashActivity)
}