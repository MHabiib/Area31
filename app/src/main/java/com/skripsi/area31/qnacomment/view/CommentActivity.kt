package com.skripsi.area31.qnacomment.view

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.skripsi.area31.BaseApp
import com.skripsi.area31.R
import com.skripsi.area31.core.base.BaseActivity
import com.skripsi.area31.core.model.SimpleCustomResponse
import com.skripsi.area31.core.model.Token
import com.skripsi.area31.databinding.ActivityCommentBinding
import com.skripsi.area31.qnacomment.adapter.ListCommentAdapter
import com.skripsi.area31.qnacomment.injection.CommentComponent
import com.skripsi.area31.qnacomment.injection.DaggerCommentComponent
import com.skripsi.area31.qnacomment.model.Comment
import com.skripsi.area31.qnacomment.model.ListCommentResponse
import com.skripsi.area31.qnacomment.model.SerializableComment
import com.skripsi.area31.qnacomment.presenter.CommentPresenter
import com.skripsi.area31.qnareplies.model.SerializableReplies
import com.skripsi.area31.qnareplies.view.RepliesActivity
import com.skripsi.area31.utils.Constants
import com.skripsi.area31.utils.Constants.Companion.COMMENT_BODY
import com.skripsi.area31.utils.Constants.Companion.SERIALIZABLE_COMMENT
import com.skripsi.area31.utils.Constants.Companion.SERIALIZABLE_REPLIES
import com.skripsi.area31.utils.PaginationScrollListener
import javax.inject.Inject

class CommentActivity : BaseActivity(), CommentContract {
  private var daggerBuild: CommentComponent = DaggerCommentComponent.builder().baseComponent(
      BaseApp.instance.baseComponent).build()

  init {
    daggerBuild.inject(this)
  }

  @Inject lateinit var presenter: CommentPresenter
  @Inject lateinit var gson: Gson
  private lateinit var listCommentAdapter: ListCommentAdapter
  private lateinit var binding: ActivityCommentBinding
  private var currentPage = 0
  private var isLastPage = false
  private var idUser: String? = null
  private var idComment: String? = null
  private var isLoading = false
  private lateinit var accessToken: String
  private lateinit var idCourse: String
  private lateinit var idPost: String
  private lateinit var postName: String
  private lateinit var postBody: String
  private lateinit var postTitle: String
  private var itemPosition: Int = 0
  private var bottomsheet = EditCommentBottomsheetFragment()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_comment)
    accessToken = gson.fromJson(
        this.getSharedPreferences(Constants.AUTHENTICATION, Context.MODE_PRIVATE)?.getString(
            Constants.TOKEN, null), Token::class.java).accessToken

    presenter.attach(this)
    presenter.subscribe()
    val serializableComment: SerializableComment = intent.getSerializableExtra(
        SERIALIZABLE_COMMENT) as SerializableComment
    serializableComment.let {
      idPost = it.idPost
      presenter.getListComment(accessToken, idPost, currentPage)
      postName = it.name
      postBody = it.body
      idUser = it.idUser
      postTitle = it.title
      binding.tvPostName.text = postName
      binding.tvPostBody.text = postBody
      binding.tvPostTitle.text = postTitle
    }

    val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    with(binding) {
      shimmerComment.startShimmer()
      listCommentAdapter = ListCommentAdapter()
      rvComment.layoutManager = linearLayoutManager
      rvComment.adapter = listCommentAdapter
      listCommentAdapter.onItemClick = {
        commentItemClick(it)
      }

      rvComment.isNestedScrollingEnabled = false
      rvComment.addOnScrollListener(object :
          PaginationScrollListener(linearLayoutManager, isLastPage) {
        override fun loadMoreItems() {
          if (!isLoading && !isLastPage) {
            isLoading = true
            presenter.getListComment(accessToken, idCourse, currentPage)
          }
        }
      })

      ibBack.setOnClickListener {
        finish()
      }

      ibSendComment.setOnClickListener {
        addComment.text?.let {
          if (it.isNotEmpty()) {
            presenter.postCommentStudent(idPost, addComment.text.toString(), accessToken)
            it.clear()
          } else {
            Toast.makeText(this@CommentActivity, getString(R.string.comment_cant_be_empty),
                Toast.LENGTH_SHORT).show()
          }
        }
      }
      idUser?.let { listCommentAdapter.getIdUser(it) }
    }
  }

  override fun getListCommentSuccess(response: ListCommentResponse) {
    with(binding) {
      ibSendComment.visibility = View.VISIBLE
      rvComment.isEnabled = true
      shimmerComment.visibility = View.GONE
      shimmerComment.stopShimmer()
    }

    if (currentPage != 0) {
      if (currentPage <= response.totalPages - 1) {
        listCommentAdapter.addAll(response.listComment)
        currentPage += 1
      } else {
        isLastPage = true
      }
    } else {
      listCommentAdapter.addAll(response.listComment)
      if (response.empty) {
        binding.ivDontHaveComment.visibility = View.VISIBLE
        binding.tvIvDontHaveComment.visibility = View.VISIBLE
      }
      if (currentPage >= response.totalPages - 1) {
        isLastPage = true
      } else {
        currentPage += 1
      }
    }
    isLoading = false
  }

  fun deleteComment(idComment: String, adapterPosition: Int) {
    val mAlertDialog = AlertDialog.Builder(this@CommentActivity)
    mAlertDialog.setTitle(getString(R.string.delete_comment_title))
    mAlertDialog.setMessage(getString(R.string.delete_comment_message))
    mAlertDialog.setPositiveButton(getString(R.string.delete)) { _, _ ->
      itemPosition = adapterPosition
      presenter.deleteCommentStudent(idComment, accessToken)
    }
    mAlertDialog.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
      dialog.dismiss()
    }
    mAlertDialog.show()
  }

  fun editComment(idComment: String, body: String, adapterPosition: Int) {
    this.idComment = idComment
    itemPosition = adapterPosition
    val bundle = Bundle()
    bundle.putString(COMMENT_BODY, body.toString())
    bottomsheet.arguments = bundle
    if (!bottomsheet.isAdded) {
      this@CommentActivity.supportFragmentManager.let { fragmentManager ->
        bottomsheet.show(fragmentManager, bottomsheet.tag)
      }
    }
  }

  fun updateComment(comment: String) {
    idComment?.let { presenter.updateCommentStudent(it, comment, accessToken) }
    bottomsheet.dismiss()
  }

  override fun updateCommentStudentSuccess(comment: Comment) {
    listCommentAdapter.remove(itemPosition)
    listCommentAdapter.addAt(itemPosition, comment)
    listCommentAdapter.notifyItemChanged(itemPosition)
  }

  override fun deleteCommentStudentSuccess(it: SimpleCustomResponse) {
    listCommentAdapter.remove(itemPosition)
  }

  override fun postCommentStudentSuccess(comment: Comment) {
    if (binding.ivDontHaveComment.visibility == View.VISIBLE) {
      binding.ivDontHaveComment.visibility = View.GONE
      binding.tvIvDontHaveComment.visibility = View.GONE
    }
    listCommentAdapter.addAt(0, comment)
    listCommentAdapter.notifyDataSetChanged()
  }

  private fun commentItemClick(commentItems: Comment) {
    val intent = Intent(this, RepliesActivity::class.java)
    intent.putExtra(SERIALIZABLE_REPLIES, idUser?.let {
      SerializableReplies(postTitle, postBody, postName, commentItems.body, commentItems.name,
          commentItems.idComment, it)
    })
    startActivity(intent)
  }

  override fun onFailed(message: String) {
    isLoading = true
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
  }
}