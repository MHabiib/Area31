package com.skripsi.area31.qnacomment.injection

import com.skripsi.area31.core.base.BaseComponent
import com.skripsi.area31.qnacomment.view.CommentActivity
import dagger.Component

@Component(dependencies = [BaseComponent::class], modules = [CommentModule::class])
interface CommentComponent {
  fun inject(commentActivity: CommentActivity)
}