package com.skripsi.area31.main.injection

import com.skripsi.area31.core.base.BaseComponent
import com.skripsi.area31.main.view.MainActivity
import dagger.Component

@Component(dependencies = [BaseComponent::class]) interface MainComponent {
  fun inject(mainActivity: MainActivity)
}