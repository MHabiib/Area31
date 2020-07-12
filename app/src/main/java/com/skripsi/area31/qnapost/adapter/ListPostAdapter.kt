package com.skripsi.area31.qnapost.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.skripsi.area31.R
import com.skripsi.area31.qnapost.model.Post
import com.skripsi.area31.utils.Utils
import java.util.*

class ListPostAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
  var onItemClick: ((Post) -> Unit)? = null
  private var postList: MutableList<Post>? = LinkedList()
  private var isLoadingAdded = false
  private val loading = 0
  private val item = 1
  private var pageNumber = 0

  override fun onCreateViewHolder(parent: ViewGroup,
      viewType: Int): RecyclerView.ViewHolder = BookingViewHolder(
      LayoutInflater.from(parent.context).inflate(R.layout.item_layout_qna_post, parent, false))

  @SuppressLint("SetTextI18n") override fun onBindViewHolder(holder: RecyclerView.ViewHolder,
      position: Int) {
    val post = postList?.get(position)
    if (getItemViewType(position) == item) {
      val postViewHolder = holder as BookingViewHolder
      postViewHolder.title.text = "${post?.title} | ${post?.createdAt?.let {
        Utils.convertLongToSimpleTime(
            Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta")).timeInMillis - it)
      }}"
      postViewHolder.description.text = post?.body
      postViewHolder.comment.text = "${post?.totalComment.toString()} comments"
    }
  }

  override fun getItemCount(): Int = postList?.size ?: 0

  override fun getItemViewType(position: Int): Int = if (position == postList?.size?.minus(
          1) && isLoadingAdded) loading else item

  fun add(post: Post) {
    postList?.add(post)
    postList?.size?.minus(1)?.let { notifyItemInserted(it) }
  }

  fun addAll(postResult: List<Post>) {
    for (result in postResult) {
      add(result)
    }
  }

  fun clear() = postList?.clear()

  inner class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val title: TextView = itemView.findViewById<View>(R.id.item_title) as TextView
    val description: TextView = itemView.findViewById<View>(R.id.item_description) as TextView
    val comment: TextView = itemView.findViewById<View>(R.id.item_comment) as TextView

    init {
      itemView.setOnClickListener {
        postList?.get(adapterPosition)?.let { content ->
          pageNumber = adapterPosition
          onItemClick?.invoke(content)
        }
      }
    }
  }
}
