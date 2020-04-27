package com.skripsi.area31.login.injection

import com.skripsi.area31.core.base.BaseComponent
import com.skripsi.area31.login.view.LoginActivity
import dagger.Component

@Component(dependencies = [BaseComponent::class], modules = [LoginModule::class])
interface LoginComponent {
  fun inject(loginActivity: LoginActivity)
}