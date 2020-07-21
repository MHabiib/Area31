package com.skripsi.area31.qnapost.view

import android.app.Activity.RESULT_OK
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
import com.skripsi.area31.databinding.FragmentPostBinding
import com.skripsi.area31.home.view.HomeFragment
import com.skripsi.area31.qnacomment.model.SerializableComment
import com.skripsi.area31.qnacomment.view.CommentActivity
import com.skripsi.area31.qnapost.adapter.ListPostAdapter
import com.skripsi.area31.qnapost.injection.DaggerPostComponent
import com.skripsi.area31.qnapost.injection.PostComponent
import com.skripsi.area31.qnapost.model.ListPostResponse
import com.skripsi.area31.qnapost.model.Post
import com.skripsi.area31.qnapost.presenter.PostPresenter
import com.skripsi.area31.utils.Constants
import com.skripsi.area31.utils.Constants.Companion.LAUNCH_COMMENT_ACTIVITY
import com.skripsi.area31.utils.Constants.Companion.SERIALIZABLE_COMMENT
import com.skripsi.area31.utils.PaginationScrollListener
import javax.inject.Inject

class PostFragment : BottomSheetDialogFragment(), PostContract {
  private var daggerBuild: PostComponent = DaggerPostComponent.builder().baseComponent(
      BaseApp.instance.baseComponent).build()

  init {
    daggerBuild.inject(this)
  }

  @Inject lateinit var presenter: PostPresenter
  @Inject lateinit var gson: Gson
  private lateinit var listPostAdapter: ListPostAdapter
  private lateinit var binding: FragmentPostBinding
  private var currentPage = 0
  private var isLastPage = false
  private var isLoading = false
  private var idUser: String? = null
  private lateinit var accessToken: String
  private lateinit var idCourse: String

  companion object {
    const val TAG: String = Constants.CHAPTER_FRAGMENT
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
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_post, container, false)
    val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

    with(binding) {
      shimmerPost.startShimmer()
      listPostAdapter = ListPostAdapter()
      rvPost.layoutManager = linearLayoutManager
      rvPost.adapter = listPostAdapter
      listPostAdapter.onItemClick = {
        postItemClick(it)
      }

      rvPost.isNestedScrollingEnabled = false
      rvPost.addOnScrollListener(object :
          PaginationScrollListener(linearLayoutManager, isLastPage) {
        override fun loadMoreItems() {
          if (!isLoading && !isLastPage) {
            isLoading = true
            presenter.getListPost(accessToken, idCourse, currentPage)
          }
        }
      })

      ibBack.setOnClickListener {
        listPostAdapter.clear()
        listPostAdapter.notifyDataSetChanged()
        currentPage = 0
        isLastPage = false
        val activity = activity as CourseActivity
        activity.dismissPostDialog()
      }
    }
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    idCourse = this.arguments?.getString(Constants.COURSE_ID).toString()
    idUser = this.arguments?.getString(Constants.ID_USER).toString()
    accessToken = gson.fromJson(
        context?.getSharedPreferences(Constants.AUTHENTICATION, Context.MODE_PRIVATE)?.getString(
            Constants.TOKEN, null), Token::class.java).accessToken
    presenter.attach(this)
    presenter.apply {
      subscribe()
      getListPost(accessToken, idCourse, currentPage)
    }
  }

  override fun getListPostSuccess(response: ListPostResponse) {
    with(binding) {
      rvPost.isEnabled = true
      shimmerPost.visibility = View.GONE
      shimmerPost.stopShimmer()
    }

    if (currentPage != 0) {
      if (currentPage <= response.totalPages - 1) {
        listPostAdapter.addAll(response.listPost)
        currentPage += 1
      } else {
        isLastPage = true
      }
    } else {
      listPostAdapter.addAll(response.listPost)
      if (response.empty) {
        binding.ivDontHavePost.visibility = View.VISIBLE
        binding.tvIvDontHavePost.visibility = View.VISIBLE
      }
      if (currentPage >= response.totalPages - 1) {
        isLastPage = true
      } else {
        currentPage += 1
      }
    }
    isLoading = false
  }

  private fun postItemClick(postItems: Post) {
    val intent = Intent(context, CommentActivity::class.java)
    intent.putExtra(SERIALIZABLE_COMMENT, idUser?.let {
      SerializableComment(postItems.body, postItems.title, postItems.idPost, postItems.name, it)
    })
    startActivityForResult(intent, LAUNCH_COMMENT_ACTIVITY)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == LAUNCH_COMMENT_ACTIVITY) {
      if (resultCode == RESULT_OK) {
        with(binding) {
          rvPost.isEnabled = false
          shimmerPost.visibility = View.VISIBLE
          shimmerPost.startShimmer()
          listPostAdapter.clear()
          listPostAdapter.notifyDataSetChanged()
          currentPage = 0
          presenter.getListPost(accessToken, idCourse, currentPage)
        }
      }
    }
  }

  override fun onCancel(dialog: DialogInterface) {
    listPostAdapter.clear()
    listPostAdapter.notifyDataSetChanged()
    currentPage = 0
    isLastPage = false
    super.onCancel(dialog)
  }

  override fun onFailed(message: String) {
    isLoading = true
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
  }
}

