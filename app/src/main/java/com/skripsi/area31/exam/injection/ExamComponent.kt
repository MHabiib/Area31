package com.skripsi.area31.exam.injection

import com.skripsi.area31.core.base.BaseComponent
import com.skripsi.area31.exam.view.ExamFragment
import dagger.Component

@Component(dependencies = [BaseComponent::class], modules = [ExamModule::class])
interface ExamComponent {
  fun inject(examFragment: ExamFragment)
}