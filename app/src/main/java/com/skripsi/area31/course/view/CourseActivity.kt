package com.skripsi.area31.course.view

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.skripsi.area31.BaseApp
import com.skripsi.area31.R
import com.skripsi.area31.chapter.view.ChapterFragment
import com.skripsi.area31.core.base.BaseActivity
import com.skripsi.area31.core.model.SimpleCustomResponse
import com.skripsi.area31.core.model.Token
import com.skripsi.area31.course.injection.CourseComponent
import com.skripsi.area31.course.injection.DaggerCourseComponent
import com.skripsi.area31.course.model.CourseDetails
import com.skripsi.area31.course.presenter.CoursePresenter
import com.skripsi.area31.courseresource.view.ResourceFragment
import com.skripsi.area31.databinding.ActivityCourseBinding
import com.skripsi.area31.utils.Constants
import com.skripsi.area31.utils.Constants.Companion.COURSE_ID
import com.skripsi.area31.utils.Constants.Companion.REFRESH_COURSE
import okhttp3.ResponseBody
import javax.inject.Inject

class CourseActivity : BaseActivity(), CourseContract {
  private var daggerBuild: CourseComponent = DaggerCourseComponent.builder().baseComponent(
      BaseApp.instance.baseComponent).build()

  init {
    daggerBuild.inject(this)
  }

  @Inject lateinit var presenter: CoursePresenter
  @Inject lateinit var gson: Gson
  private lateinit var binding: ActivityCourseBinding
  private lateinit var accessToken: String
  private var courseId: String? = null
  private val bottomSheetFragmentChapter = ChapterFragment()
  private val bottomSheetFragmentResource = ResourceFragment()
  private val bottomSheetFragment = LeaveCourseBottomsheetFragment()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_course)
    presenter.attach(this)
    presenter.subscribe()

    accessToken = gson.fromJson(
        this.getSharedPreferences(Constants.AUTHENTICATION, Context.MODE_PRIVATE)?.getString(
            Constants.TOKEN, null), Token::class.java).accessToken

    if (this.intent.getStringExtra(COURSE_ID) != null) {
      courseId = this.intent.getStringExtra(COURSE_ID)
    }
    showProgress(true)
    courseId?.let { presenter.courseDetails(accessToken, it) }

    with(binding) {
      ibBack.setOnClickListener {
        this@CourseActivity.finish()
      }
      ibLeave.setOnClickListener {
        this@CourseActivity.supportFragmentManager.let { fragmentManager ->
          bottomSheetFragment.show(fragmentManager, bottomSheetFragment.tag)
        }
      }
      layoutChapter.setOnClickListener {
        val bundle = Bundle()
        bundle.putString(COURSE_ID, courseId)
        bottomSheetFragmentChapter.arguments = bundle
        this@CourseActivity.supportFragmentManager.let { fragmentManager ->
          bottomSheetFragmentChapter.show(fragmentManager, bottomSheetFragmentChapter.tag)
        }
      }
      layoutQuiz.setOnClickListener {
        Toast.makeText(this@CourseActivity, "Under construction.", Toast.LENGTH_SHORT).show()
      }
      layoutQna.setOnClickListener {
        Toast.makeText(this@CourseActivity, "Under construction.", Toast.LENGTH_SHORT).show()
      }
      layoutResource.setOnClickListener {
        val bundle = Bundle()
        bundle.putString(COURSE_ID, courseId)
        bottomSheetFragmentResource.arguments = bundle
        this@CourseActivity.supportFragmentManager.let { fragmentManager ->
          bottomSheetFragmentResource.show(fragmentManager, bottomSheetFragmentResource.tag)
        }
      }
      layoutComplaint.setOnClickListener {
        Toast.makeText(this@CourseActivity, "Under construction.", Toast.LENGTH_SHORT).show()
      }
    }
  }

  override fun courseDetailsSuccess(courseDetails: CourseDetails?) {
    showProgress(false)
    with(binding) {
      by.visibility = View.VISIBLE
      ivMembers.visibility = View.VISIBLE
      tvCourseName.text = courseDetails?.name
      tvInstructorName.text = courseDetails?.instructorName
      tvCourseDescription.text = courseDetails?.description
      tvCourseDescription.text = courseDetails?.description
      tvTotalMembers.text = courseDetails?.totalMembers.toString()
    }
  }

  fun leaveCourse() {
    bottomSheetFragment.dismiss()
    courseId?.let { presenter.leaveCourse(accessToken, it) }
  }

  fun dismissDialog() {
    bottomSheetFragment.dismiss()
  }

  fun dismissChapterDialog() {
    bottomSheetFragmentChapter.dismiss()
  }

  fun dismissResourceDialog() {
    bottomSheetFragmentResource.dismiss()
  }

  override fun leaveCourseSuccess(response: String) {
    showProgress(false)
    Toast.makeText(this, response, Toast.LENGTH_LONG).show()

    setResult(REFRESH_COURSE)
    this@CourseActivity.finish()
  }

  override fun onBadRequest(message: ResponseBody?) {
    showProgress(false)
    Toast.makeText(this, gson.fromJson(message?.string(), SimpleCustomResponse::class.java).message,
        Toast.LENGTH_SHORT).show()
  }

  override fun onFailed(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
  }

  private fun showProgress(show: Boolean) {
    with(binding) {
      if (show) {
        progressBar.visibility = View.VISIBLE
      } else {
        progressBar.visibility = View.GONE
      }
    }
  }
}
