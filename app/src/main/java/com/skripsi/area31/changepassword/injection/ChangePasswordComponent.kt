package com.skripsi.area31.changepassword.injection

import com.skripsi.area31.changepassword.view.ChangePasswordFragment
import com.skripsi.area31.core.base.BaseComponent
import dagger.Component

@Component(dependencies = [BaseComponent::class], modules = [ChangePasswordModule::class])
interface ChangePasswordComponent {
  fun inject(changePasswordFragment: ChangePasswordFragment)
}