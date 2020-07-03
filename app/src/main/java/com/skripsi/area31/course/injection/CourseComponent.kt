package com.skripsi.area31.course.injection

import com.skripsi.area31.core.base.BaseComponent
import com.skripsi.area31.course.view.CourseActivity
import dagger.Component

@Component(dependencies = [BaseComponent::class], modules = [CourseModule::class])
interface CourseComponent {
  fun inject(courseActivity: CourseActivity)
}