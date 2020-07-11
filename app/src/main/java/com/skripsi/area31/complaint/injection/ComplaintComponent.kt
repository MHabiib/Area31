package com.skripsi.area31.complaint.injection

import com.skripsi.area31.complaint.view.ComplaintFragment
import com.skripsi.area31.core.base.BaseComponent
import dagger.Component

@Component(dependencies = [BaseComponent::class], modules = [ComplaintModule::class])
interface ComplaintComponent {
  fun inject(complaintFragment: ComplaintFragment)
}