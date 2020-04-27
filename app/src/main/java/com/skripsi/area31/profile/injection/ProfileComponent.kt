package com.skripsi.area31.profile.injection

import com.skripsi.area31.core.base.BaseComponent
import com.skripsi.area31.profile.view.ProfileFragment
import dagger.Component

@Component(dependencies = [BaseComponent::class], modules = [ProfileModule::class])
interface ProfileComponent {
  fun inject(profileFragment: ProfileFragment)
}