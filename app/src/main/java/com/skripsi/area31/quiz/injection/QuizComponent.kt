package com.skripsi.area31.quiz.injection

import com.skripsi.area31.core.base.BaseComponent
import com.skripsi.area31.quiz.view.QuizActivity
import dagger.Component

@Component(dependencies = [BaseComponent::class], modules = [QuizModule::class])
interface QuizComponent {
  fun inject(quizActivity: QuizActivity)
}