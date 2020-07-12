package com.skripsi.area31.qnapost.injection

import com.skripsi.area31.core.base.BaseComponent
import com.skripsi.area31.qnapost.view.PostFragment
import dagger.Component

@Component(dependencies = [BaseComponent::class], modules = [PostModule::class])
interface PostComponent {
  fun inject(postFragment: PostFragment)
}