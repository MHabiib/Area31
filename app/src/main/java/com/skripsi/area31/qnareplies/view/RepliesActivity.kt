package com.skripsi.area31.qnareplies.view

import android.app.Activity
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
import com.skripsi.area31.core.model.Token
import com.skripsi.area31.databinding.ActivityRepliesBinding
import com.skripsi.area31.qnareplies.adapter.ListRepliesAdapter
import com.skripsi.area31.qnareplies.injection.DaggerRepliesComponent
import com.skripsi.area31.qnareplies.injection.RepliesComponent
import com.skripsi.area31.qnareplies.model.ListRepliesResponse
import com.skripsi.area31.qnareplies.model.Replies
import com.skripsi.area31.qnareplies.model.SerializableReplies
import com.skripsi.area31.qnareplies.presenter.RepliesPresenter
import com.skripsi.area31.utils.Constants
import com.skripsi.area31.utils.Constants.Companion.REPLIES_BODY
import com.skripsi.area31.utils.Constants.Companion.SERIALIZABLE_REPLIES
import com.skripsi.area31.utils.PaginationScrollListener
import javax.inject.Inject

class RepliesActivity : BaseActivity(), RepliesContract {
  private var daggerBuild: RepliesComponent = DaggerRepliesComponent.builder().baseComponent(
      BaseApp.instance.baseComponent).build()

  init {
    daggerBuild.inject(this)
  }

  @Inject lateinit var presenter: RepliesPresenter
  @Inject lateinit var gson: Gson
  private lateinit var listRepliesAdapter: ListRepliesAdapter
  private lateinit var binding: ActivityRepliesBinding
  private var currentPage = 0
  private var adapterPosition = 0
  private var isLastPage = false
  private var isLoading = false
  private var idUser: String? = null
  private var idReplies: String? = null
  private lateinit var accessToken: String
  private lateinit var idComment: String
  private var itemPosition: Int = 0
  private var addDeleteComment = 0
  private var bottomsheet = EditRepliesBottomsheetFragment()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_replies)
    accessToken = gson.fromJson(
        this.getSharedPreferences(Constants.AUTHENTICATION, Context.MODE_PRIVATE)?.getString(
            Constants.TOKEN, null), Token::class.java).accessToken

    presenter.attach(this)
    presenter.subscribe()
    val serializableReplies: SerializableReplies = intent.getSerializableExtra(
        SERIALIZABLE_REPLIES) as SerializableReplies
    serializableReplies.let {
      idComment = it.idComment
      presenter.getListReplies(accessToken, idComment, currentPage)
      binding.tvPostName.text = it.namePost
      binding.tvCommentName.text = it.nameComment
      binding.tvCommentBody.text = it.bodyComment
      binding.tvPostBody.text = it.bodyPost
      binding.tvPostTitle.text = it.title
      idUser = it.idUser
    }

    val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    with(binding) {
      shimmerReplies.startShimmer()
      listRepliesAdapter = ListRepliesAdapter()
      rvReplies.layoutManager = linearLayoutManager
      rvReplies.adapter = listRepliesAdapter

      rvReplies.isNestedScrollingEnabled = false
      rvReplies.addOnScrollListener(object :
          PaginationScrollListener(linearLayoutManager, isLastPage) {
        override fun loadMoreItems() {
          if (!isLoading && !isLastPage) {
            isLoading = true
            presenter.getListReplies(accessToken, idComment, currentPage)
          }
        }
      })

      ibBack.setOnClickListener {
        val intent = Intent()
        if (addDeleteComment != 0) {
          setResult(Activity.RESULT_OK, intent)
        }
        finish()
      }

      ibSendReplies.setOnClickListener {
        addReplies.text?.let {
          if (it.isNotEmpty()) {
            presenter.postRepliesStudent(idComment, it.toString(), accessToken)
            it.clear()
          } else {
            Toast.makeText(this@RepliesActivity, getString(R.string.replies_cant_be_empty),
                Toast.LENGTH_SHORT).show()
          }
        }
      }
      idUser?.let { listRepliesAdapter.getIdUser(it) }
    }
  }

  override fun getListRepliesSuccess(response: ListRepliesResponse) {
    with(binding) {
      ibSendReplies.visibility = View.VISIBLE
      rvReplies.isEnabled = true
      shimmerReplies.visibility = View.GONE
      shimmerReplies.stopShimmer()
    }

    if (currentPage != 0) {
      if (currentPage <= response.totalPages - 1) {
        listRepliesAdapter.addAll(response.listReplies)
        currentPage += 1
      } else {
        isLastPage = true
      }
    } else {
      listRepliesAdapter.addAll(response.listReplies)
      if (response.empty) {
        binding.ivDontHaveReplies.visibility = View.VISIBLE
        binding.tvIvDontHaveReplies.visibility = View.VISIBLE
      }
      if (currentPage >= response.totalPages - 1) {
        isLastPage = true
      } else {
        currentPage += 1
      }
    }
    isLoading = false
  }

  fun deleteReplies(idReplies: String, adapterPosition: Int) {
    val mAlertDialog = AlertDialog.Builder(this@RepliesActivity)
    mAlertDialog.setTitle(R.string.delete_replies_title)
    mAlertDialog.setMessage(getString(R.string.delete_replies_message))
    mAlertDialog.setPositiveButton(getString(R.string.delete)) { _, _ ->
      itemPosition = adapterPosition
      presenter.deleteRepliesStudent(idReplies, accessToken)
    }
    mAlertDialog.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
      dialog.dismiss()
    }
    mAlertDialog.show()
  }

  fun editReplies(idReplies: String, body: String, adapterPosition: Int) {
    this.idReplies = idReplies
    this.adapterPosition = adapterPosition
    val bundle = Bundle()
    bundle.putString(REPLIES_BODY, body)
    bottomsheet.arguments = bundle
    if (!bottomsheet.isAdded) {
      this@RepliesActivity.supportFragmentManager.let { fragmentManager ->
        bottomsheet.show(fragmentManager, bottomsheet.tag)
      }
    }
  }

  fun updateReplies(replies: String) {
    idReplies?.let { presenter.updateRepliesStudent(it, replies, accessToken) }
    bottomsheet.dismiss()
  }

  override fun updateRepliesStudentSuccess(replies: Replies) {
    listRepliesAdapter.remove(itemPosition)
    listRepliesAdapter.addAt(itemPosition, replies)
    listRepliesAdapter.notifyItemChanged(itemPosition)
  }

  override fun deleteRepliesStudentSuccess() {
    addDeleteComment -= 1
    listRepliesAdapter.remove(itemPosition)
  }

  override fun onBackPressed() {
    val intent = Intent()
    if (addDeleteComment != 0) {
      setResult(Activity.RESULT_OK, intent)
    }
    super.onBackPressed()
  }

  override fun postRepliesStudentSuccess(replies: Replies) {
    addDeleteComment += 1
    if (binding.ivDontHaveReplies.visibility == View.VISIBLE) {
      binding.ivDontHaveReplies.visibility = View.GONE
      binding.tvIvDontHaveReplies.visibility = View.GONE
    }
    listRepliesAdapter.addAt(0, replies)
    listRepliesAdapter.notifyDataSetChanged()
  }

  override fun onFailed(message: String) {
    isLoading = true
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
  }
}