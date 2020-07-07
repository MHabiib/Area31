package com.skripsi.area31.quizlist.injection

import com.skripsi.area31.core.base.BaseComponent
import com.skripsi.area31.quizlist.view.QuizFragment
import dagger.Component

@Component(dependencies = [BaseComponent::class], modules = [QuizModule::class])
interface QuizComponent {
  fun inject(quizFragment: QuizFragment)
}