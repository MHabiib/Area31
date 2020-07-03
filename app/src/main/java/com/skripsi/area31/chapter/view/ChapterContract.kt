package com.skripsi.area31.chapter.view

import com.skripsi.area31.chapter.model.ListChapterResponse
import com.skripsi.area31.core.base.BaseView

interface ChapterContract : BaseView {
  fun getListChapterSuccess(response: ListChapterResponse)

}