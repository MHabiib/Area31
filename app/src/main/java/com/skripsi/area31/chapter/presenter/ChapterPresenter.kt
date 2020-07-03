package com.skripsi.area31.chapter.presenter

import com.skripsi.area31.chapter.network.ChapterApi
import com.skripsi.area31.chapter.view.ChapterContract
import com.skripsi.area31.core.base.BasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ChapterPresenter @Inject constructor(private val chapterApi: ChapterApi) :
    BasePresenter<ChapterContract>() {
  fun getListChapter(accessToken: String, idCourse: String, page: Int) {
    subscriptions.add(chapterApi.getListChapter(accessToken, idCourse, page).subscribeOn(
        Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({ response ->
      view?.getListChapterSuccess(response)
    }, {
      view?.onFailed(it.message.toString())
    }))
  }
}