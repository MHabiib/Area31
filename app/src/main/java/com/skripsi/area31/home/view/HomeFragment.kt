package com.skripsi.area31.home.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.skripsi.area31.BaseApp
import com.skripsi.area31.R
import com.skripsi.area31.core.model.Token
import com.skripsi.area31.course.view.CourseActivity
import com.skripsi.area31.databinding.FragmentHomeBinding
import com.skripsi.area31.home.adapter.ListCourseAdapter
import com.skripsi.area31.home.injection.DaggerHomeComponent
import com.skripsi.area31.home.injection.HomeComponent
import com.skripsi.area31.home.model.Course
import com.skripsi.area31.home.model.ListCourse
import com.skripsi.area31.home.presenter.HomePresenter
import com.skripsi.area31.utils.Constants
import com.skripsi.area31.utils.Constants.Companion.COURSE_ID
import com.skripsi.area31.utils.Constants.Companion.HOME_FRAGMENT
import com.skripsi.area31.utils.Constants.Companion.LAUNCH_SECOND_ACTIVITY
import com.skripsi.area31.utils.PaginationScrollListener
import java.util.*
import javax.inject.Inject

class HomeFragment : Fragment(), HomeContract {
  private var daggerBuild: HomeComponent = DaggerHomeComponent.builder().baseComponent(
      BaseApp.instance.baseComponent).build()

  init {
    daggerBuild.inject(this)
  }

  @Inject lateinit var presenter: HomePresenter
  @Inject lateinit var gson: Gson
  private lateinit var listCourseAdapter: ListCourseAdapter
  private lateinit var binding: FragmentHomeBinding
  private var currentPage = 0
  private var isLastPage = false
  private var isLoading = false
  private lateinit var accessToken: String

  companion object {
    const val TAG: String = HOME_FRAGMENT
  }

  fun newInstance(): HomeFragment = HomeFragment()

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
    val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

    with(binding) {
      shimmerCourse.startShimmer()
      refreshCourse.setOnRefreshListener {
        refreshListCourse()
      }
      listCourseAdapter = ListCourseAdapter()
      rvCourse.layoutManager = linearLayoutManager
      rvCourse.adapter = listCourseAdapter
      listCourseAdapter.onItemClick = {
        courseItemClick(it)
      }
      rvCourse.addOnScrollListener(object :
          PaginationScrollListener(linearLayoutManager, isLastPage) {
        override fun loadMoreItems() {
          if (!isLoading && !isLastPage) {
            isLoading = true
            presenter.loadListCourse(accessToken, currentPage)
          }
        }
      })
    }
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    accessToken = gson.fromJson(
        context?.getSharedPreferences(Constants.AUTHENTICATION, Context.MODE_PRIVATE)?.getString(
            Constants.TOKEN, null), Token::class.java).accessToken
    presenter.attach(this)
    presenter.apply {
      subscribe()
      loadListCourse(accessToken, currentPage)
    }
    binding.tvAnnounce.text = getTextAnnounce()
  }

  override fun loadListCourseSuccess(listCourse: ListCourse) {
    with(binding) {
      rvCourse.isEnabled = true
      refreshCourse.isEnabled = true
      shimmerCourse.visibility = View.GONE
      shimmerCourse.stopShimmer()
    }

    if (currentPage != 0) {
      if (currentPage <= listCourse.totalPages - 1) {
        listCourseAdapter.addAll(listCourse.course)
        currentPage += 1
      } else {
        isLastPage = true
      }
    } else {
      listCourseAdapter.addAll(listCourse.course)
      if (listCourse.empty) {
        binding.ivDontHaveCourse.visibility = View.VISIBLE
        binding.tvIvDontHaveCourse.visibility = View.VISIBLE
      }
      if (currentPage >= listCourse.totalPages - 1) {
        isLastPage = true
      } else {
        currentPage += 1
      }
    }
    isLoading = false
  }

  fun successJoinCourse() {
    currentPage = 0
    refreshListCourse()
  }

  override fun onFailed(message: String) {
    binding.refreshCourse.isEnabled = true
    binding.shimmerCourse.visibility = View.GONE
    binding.shimmerCourse.stopShimmer()
    isLoading = false
    Toast.makeText(context, "Error nih", Toast.LENGTH_SHORT).show()
  }

  private fun getTextAnnounce(): String {
    return when (Calendar.getInstance(TimeZone.getTimeZone(getString(R.string.asiajakarta))).get(
        Calendar.HOUR_OF_DAY)) {
      in 0 .. 11 -> getString(R.string.good_morning)
      in 12 .. 15 -> getString(R.string.good_afternoon)
      in 16 .. 20 -> getString(R.string.good_evening)
      in 21 .. 23 -> getString(R.string.good_night)
      else -> {
        getString(R.string.hello)
      }
    }
  }

  fun refreshListCourse() {
    with(binding) {
      ivDontHaveCourse.visibility = View.GONE
      tvIvDontHaveCourse.visibility = View.GONE
      listCourseAdapter.clear()
      listCourseAdapter.notifyDataSetChanged()
      currentPage = 0
      isLastPage = false
      shimmerCourse.visibility = View.VISIBLE
      shimmerCourse.startShimmer()
      presenter.loadListCourse(accessToken, currentPage)
      refreshCourse.isRefreshing = false
      refreshCourse.isEnabled = false
    }
  }

  private fun courseItemClick(courseItems: Course) {
    val intent = Intent(context, CourseActivity::class.java)
    intent.putExtra(COURSE_ID, courseItems.idCourse)
    startActivityForResult(intent, LAUNCH_SECOND_ACTIVITY)
  }
}

