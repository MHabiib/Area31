package com.skripsi.area31.chapter.injection

import com.skripsi.area31.chapter.view.ChapterFragment
import com.skripsi.area31.core.base.BaseComponent
import dagger.Component

@Component(dependencies = [BaseComponent::class], modules = [ChapterModule::class])
interface ChapterComponent {
  fun inject(chapterFragment: ChapterFragment)
}