package com.skripsi.area31.qnareplies.injection

import com.skripsi.area31.core.base.BaseComponent
import com.skripsi.area31.qnareplies.view.RepliesActivity
import dagger.Component

@Component(dependencies = [BaseComponent::class], modules = [RepliesModule::class])
interface RepliesComponent {
  fun inject(repliesActivity: RepliesActivity)
}