package com.skripsi.area31.courseresource.injection

import com.skripsi.area31.core.base.BaseComponent
import com.skripsi.area31.courseresource.view.ResourceFragment
import dagger.Component

@Component(dependencies = [BaseComponent::class], modules = [ResourceModule::class])
interface ResourceComponent {
  fun inject(resourceFragment: ResourceFragment)
}