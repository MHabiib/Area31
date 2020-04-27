package com.skripsi.area31.core.base

import io.reactivex.disposables.CompositeDisposable

open class BasePresenter<V : BaseView> {

  protected var subscriptions = CompositeDisposable()
  protected var view: V? = null

  fun subscribe() {}

  fun attach(view: V?) {
    this.view = view
  }

  fun detach() {
    view = null
    subscriptions.clear()
  }
}