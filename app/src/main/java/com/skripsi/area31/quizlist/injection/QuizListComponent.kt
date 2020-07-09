package com.skripsi.area31.quizlist.injection

import com.skripsi.area31.core.base.BaseComponent
import com.skripsi.area31.quizlist.view.QuizListFragment
import dagger.Component

@Component(dependencies = [BaseComponent::class], modules = [QuizListModule::class])
interface QuizListComponent {
  fun inject(quizFragment: QuizListFragment)
}