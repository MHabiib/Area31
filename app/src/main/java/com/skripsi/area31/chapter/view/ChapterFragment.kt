package com.skripsi.area31.chapter.view

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.skripsi.area31.BaseApp
import com.skripsi.area31.R
import com.skripsi.area31.chapter.adapter.ListChapterAdapter
import com.skripsi.area31.chapter.injection.ChapterComponent
import com.skripsi.area31.chapter.injection.DaggerChapterComponent
import com.skripsi.area31.chapter.model.Chapter
import com.skripsi.area31.chapter.model.ListChapterResponse
import com.skripsi.area31.chapter.presenter.ChapterPresenter
import com.skripsi.area31.core.model.Token
import com.skripsi.area31.course.view.CourseActivity
import com.skripsi.area31.databinding.FragmentChapterBinding
import com.skripsi.area31.home.view.HomeFragment
import com.skripsi.area31.utils.Constants
import com.skripsi.area31.utils.Constants.Companion.CHAPTER_FRAGMENT
import com.skripsi.area31.utils.Constants.Companion.COURSE_ID
import com.skripsi.area31.utils.Constants.Companion.ID_CHAPTER
import com.skripsi.area31.utils.PaginationScrollListener
import javax.inject.Inject

class ChapterFragment : BottomSheetDialogFragment(), ChapterContract {
  private var daggerBuild: ChapterComponent = DaggerChapterComponent.builder().baseComponent(
      BaseApp.instance.baseComponent).build()

  init {
    daggerBuild.inject(this)
  }

  @Inject lateinit var presenter: ChapterPresenter
  @Inject lateinit var gson: Gson
  private lateinit var listChapterAdapter: ListChapterAdapter
  private lateinit var binding: FragmentChapterBinding
  private var currentPage = 0
  private var isLastPage = false
  private var isLoading = false
  private lateinit var accessToken: String
  private lateinit var idCourse: String

  companion object {
    const val TAG: String = CHAPTER_FRAGMENT
  }

  fun newInstance(): HomeFragment = HomeFragment()

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    disableBottomSheetDraggableBehavior()
  }

  private fun disableBottomSheetDraggableBehavior() {
    this.isCancelable = false
    this.dialog?.setCanceledOnTouchOutside(true)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chapter, container, false)
    val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

    with(binding) {
      shimmerChapter.startShimmer()
      listChapterAdapter = ListChapterAdapter()
      rvChapter.layoutManager = linearLayoutManager
      rvChapter.adapter = listChapterAdapter
      listChapterAdapter.onItemClick = {
        chapterItemClick(it)
      }

      rvChapter.isNestedScrollingEnabled = false
      rvChapter.addOnScrollListener(object :
          PaginationScrollListener(linearLayoutManager, isLastPage) {
        override fun loadMoreItems() {
          if (!isLoading && !isLastPage) {
            isLoading = true
            presenter.getListChapter(accessToken, idCourse, currentPage)
          }
        }
      })

      ibBack.setOnClickListener {
        listChapterAdapter.clear()
        listChapterAdapter.notifyDataSetChanged()
        currentPage = 0
        isLastPage = false
        val activity = activity as CourseActivity
        activity.dismissChapterDialog()
      }
    }
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    idCourse = this.arguments?.getString(COURSE_ID).toString()
    accessToken = gson.fromJson(
        context?.getSharedPreferences(Constants.AUTHENTICATION, Context.MODE_PRIVATE)?.getString(
            Constants.TOKEN, null), Token::class.java).accessToken
    presenter.attach(this)
    presenter.apply {
      subscribe()
      getListChapter(accessToken, idCourse, currentPage)
    }
  }

  override fun getListChapterSuccess(response: ListChapterResponse) {
    with(binding) {
      rvChapter.isEnabled = true
      shimmerChapter.visibility = View.GONE
      shimmerChapter.stopShimmer()
    }

    if (currentPage != 0) {
      if (currentPage <= response.totalPages - 1) {
        listChapterAdapter.addAll(response.listChapter)
        currentPage += 1
      } else {
        isLastPage = true
      }
    } else {
      listChapterAdapter.addAll(response.listChapter)
      if (response.empty) {
        binding.ivDontHaveChapter.visibility = View.VISIBLE
        binding.tvIvDontHaveChapter.visibility = View.VISIBLE
      }
      if (currentPage >= response.totalPages - 1) {
        isLastPage = true
      } else {
        currentPage += 1
      }
    }
    isLoading = false
  }

  private fun chapterItemClick(chapterItems: Chapter) {
    val intent = Intent(context, ReadChapterActivity::class.java)
    //    intent.putExtra(ID_CHAPTER, "https://google.com/search?q=" + chapterItems.idChapter)
    intent.putExtra(ID_CHAPTER, "https://google.com/search?q=" + chapterItems.title)
    startActivity(intent)
  }

  override fun onCancel(dialog: DialogInterface) {
    listChapterAdapter.clear()
    listChapterAdapter.notifyDataSetChanged()
    currentPage = 0
    isLastPage = false
    super.onCancel(dialog)
  }

  override fun onFailed(message: String) {
    isLoading = true
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
  }
}