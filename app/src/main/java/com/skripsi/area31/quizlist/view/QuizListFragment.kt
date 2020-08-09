package com.skripsi.area31.quizlist.view

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
import com.skripsi.area31.core.model.Token
import com.skripsi.area31.course.view.CourseActivity
import com.skripsi.area31.databinding.FragmentQuizListBinding
import com.skripsi.area31.home.view.HomeFragment
import com.skripsi.area31.quiz.view.QuizActivity
import com.skripsi.area31.quizlist.adapter.ListQuizAdapter
import com.skripsi.area31.quizlist.injection.DaggerQuizListComponent
import com.skripsi.area31.quizlist.injection.QuizListComponent
import com.skripsi.area31.quizlist.model.ListQuizResponse
import com.skripsi.area31.quizlist.model.Quiz
import com.skripsi.area31.quizlist.presenter.QuizListPresenter
import com.skripsi.area31.utils.Constants
import com.skripsi.area31.utils.Constants.Companion.COURSE_ID
import com.skripsi.area31.utils.Constants.Companion.ID_QUIZ
import com.skripsi.area31.utils.Constants.Companion.QUIZ_LIST_FRAGMENT
import com.skripsi.area31.utils.Constants.Companion.QUIZ_SCORE
import com.skripsi.area31.utils.PaginationScrollListener
import java.util.*
import javax.inject.Inject

class QuizListFragment : BottomSheetDialogFragment(), QuizListContract {
  private var daggerBuild: QuizListComponent = DaggerQuizListComponent.builder().baseComponent(
      BaseApp.instance.baseComponent).build()

  init {
    daggerBuild.inject(this)
  }

  @Inject lateinit var presenter: QuizListPresenter
  @Inject lateinit var gson: Gson
  private lateinit var listQuizAdapter: ListQuizAdapter
  private lateinit var binding: FragmentQuizListBinding
  private var currentPage = 0
  private var isLastPage = false
  private var isLoading = false
  private lateinit var accessToken: String
  private lateinit var idCourse: String

  companion object {
    const val TAG: String = QUIZ_LIST_FRAGMENT
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
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_quiz_list, container, false)
    val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    with(binding) {
      shimmerQuiz.startShimmer()
      listQuizAdapter = ListQuizAdapter()
      rvQuiz.layoutManager = linearLayoutManager
      rvQuiz.adapter = listQuizAdapter
      listQuizAdapter.onItemClick = {
        if (it.score == null && it.assignedAt != null) {
          Toast.makeText(context, getString(R.string.you_have_submitted_quiz),
              Toast.LENGTH_SHORT).show()
        } else if (it.score == null && it.quizDate + it.quizDuration < Calendar.getInstance(
                TimeZone.getTimeZone(getString(R.string.asiajakarta))).timeInMillis) {
          Toast.makeText(context, getString(R.string.quiz_passed), Toast.LENGTH_SHORT).show()
        } else {
          quizItemClick(it)
        }
      }
      rvQuiz.isNestedScrollingEnabled = false
      rvQuiz.addOnScrollListener(object :
          PaginationScrollListener(linearLayoutManager, isLastPage) {
        override fun loadMoreItems() {
          if (!isLoading && !isLastPage) {
            isLoading = true
            presenter.getListQuiz(accessToken, idCourse, currentPage)
          }
        }
      })
      ibBack.setOnClickListener {
        listQuizAdapter.clear()
        listQuizAdapter.notifyDataSetChanged()
        currentPage = 0
        isLastPage = false
        val activity = activity as CourseActivity
        activity.dismissQuizDialog()
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
      getListQuiz(accessToken, idCourse, currentPage)
    }
  }

  override fun getListQuizSuccess(response: ListQuizResponse) {
    with(binding) {
      rvQuiz.isEnabled = true
      shimmerQuiz.visibility = View.GONE
      shimmerQuiz.stopShimmer()
    }

    if (currentPage != 0) {
      if (currentPage <= response.totalPages - 1) {
        listQuizAdapter.addAll(response.listQuiz)
        currentPage += 1
      } else {
        isLastPage = true
      }
    } else {
      listQuizAdapter.addAll(response.listQuiz)
      if (response.empty) {
        binding.ivDontHaveQuiz.visibility = View.VISIBLE
        binding.tvIvDontHaveQuiz.visibility = View.VISIBLE
      }
      if (currentPage >= response.totalPages - 1) {
        isLastPage = true
      } else {
        currentPage += 1
      }
    }
    isLoading = false
  }

  private fun quizItemClick(quizItems: Quiz) {
    if (quizItems.quizDate < Calendar.getInstance(
            TimeZone.getTimeZone(getString(R.string.asiajakarta))).timeInMillis) {
      val activity = activity as CourseActivity
      activity.dismissQuizDialog()
      val intent = Intent(context, QuizActivity::class.java)
      intent.putExtra(ID_QUIZ, quizItems.idQuiz)
      if (quizItems.score != null) {
        intent.putExtra(QUIZ_SCORE, quizItems.score.toString())
      }
      listQuizAdapter.clear()
      listQuizAdapter.notifyDataSetChanged()
      currentPage = 0
      isLastPage = false
      startActivity(intent)
    } else {
      Toast.makeText(context, "Quiz not started yet !", Toast.LENGTH_SHORT).show()
    }
  }

  override fun onCancel(dialog: DialogInterface) {
    listQuizAdapter.clear()
    listQuizAdapter.notifyDataSetChanged()
    currentPage = 0
    isLastPage = false
    super.onCancel(dialog)
  }

  override fun onFailed(message: String) {
    isLoading = true
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
  }
}