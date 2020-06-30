package com.skripsi.area31.register.injection

import com.skripsi.area31.core.base.BaseComponent
import com.skripsi.area31.register.view.RegisterActivity
import dagger.Component

@Component(dependencies = [BaseComponent::class], modules = [RegisterModule::class])
interface RegisterComponent {
  fun inject(registerActivity: RegisterActivity)
}