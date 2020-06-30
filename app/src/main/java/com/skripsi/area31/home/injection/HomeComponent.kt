package com.skripsi.area31.home.injection

import com.skripsi.area31.core.base.BaseComponent
import com.skripsi.area31.home.view.HomeFragment
import dagger.Component

@Component(dependencies = [BaseComponent::class], modules = [HomeModule::class])
interface HomeComponent {
  fun inject(homeFragment: HomeFragment)
}