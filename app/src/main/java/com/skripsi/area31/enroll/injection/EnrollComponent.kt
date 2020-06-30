package com.skripsi.area31.enroll.injection

import com.skripsi.area31.core.base.BaseComponent
import com.skripsi.area31.enroll.view.EnrollFragment
import dagger.Component

@Component(dependencies = [BaseComponent::class], modules = [EnrollModule::class])
interface EnrollComponent {
  fun inject(enrollFragment: EnrollFragment)
}