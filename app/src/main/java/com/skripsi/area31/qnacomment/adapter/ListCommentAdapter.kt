package com.skripsi.area31.qnacomment.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.skripsi.area31.R
import com.skripsi.area31.qnacomment.model.Comment
import com.skripsi.area31.qnacomment.view.CommentActivity
import com.skripsi.area31.utils.Utils
import java.util.*

class ListCommentAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
  var onItemClick: ((Comment) -> Unit)? = null
  private var commentList: MutableList<Comment>? = LinkedList()
  private var isLoadingAdded = false
  private val loading = 0
  private val item = 1
  private lateinit var idUser: String
  private var pageNumber = 0

  override fun onCreateViewHolder(parent: ViewGroup,
      viewType: Int): RecyclerView.ViewHolder = BookingViewHolder(
      LayoutInflater.from(parent.context).inflate(R.layout.item_layout_qna_comment, parent, false))

  @SuppressLint("SetTextI18n") override fun onBindViewHolder(holder: RecyclerView.ViewHolder,
      position: Int) {
    val comment = commentList?.get(position)
    if (getItemViewType(position) == item) {
      val commentViewHolder = holder as BookingViewHolder
      commentViewHolder.name.text = "${comment?.name} | ${comment?.createdAt?.let {
        Utils.convertLongToSimpleTime(
            Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta")).timeInMillis - it)
      }}"
      commentViewHolder.description.text = comment?.body
      commentViewHolder.replies.text = "${comment?.totalReplies.toString()} replies"
      if (idUser == comment?.idUser) {
        commentViewHolder.layoutEdit.visibility = View.VISIBLE
        commentViewHolder.edit.setOnClickListener {
          (it.context as CommentActivity).editComment(comment.idComment, comment.body,
              holder.adapterPosition)
        }
        commentViewHolder.delete.setOnClickListener {
          (it.context as CommentActivity).deleteComment(comment.idComment, holder.adapterPosition)
        }
      } else {
        commentViewHolder.layoutEdit.visibility = View.GONE
      }
    }
  }

  override fun getItemCount(): Int = commentList?.size ?: 0

  override fun getItemViewType(position: Int): Int = if (position == commentList?.size?.minus(
          1) && isLoadingAdded) loading else item

  fun add(comment: Comment) {
    commentList?.add(comment)
    commentList?.size?.minus(1)?.let { notifyItemInserted(it) }
  }

  fun addAll(commentResult: List<Comment>) {
    for (result in commentResult) {
      add(result)
    }
  }

  fun getIdUser(idUser: String) {
    this.idUser = idUser
  }

  fun addAt(position: Int, comment: Comment) = commentList?.add(position, comment)

  fun remove(position: Int) {
    commentList?.removeAt(position)
    notifyItemRemoved(position)
    commentList?.size?.let { notifyItemRangeChanged(position, it) }
  }

  fun clear() = commentList?.clear()

  inner class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val name: TextView = itemView.findViewById<View>(R.id.item_name) as TextView
    val description: TextView = itemView.findViewById<View>(R.id.item_description) as TextView
    val replies: TextView = itemView.findViewById<View>(R.id.item_replies) as TextView
    val edit: TextView = itemView.findViewById<View>(R.id.tv_edit) as TextView
    val delete: TextView = itemView.findViewById<View>(R.id.tv_delete) as TextView
    val layoutEdit: LinearLayout = itemView.findViewById<View>(R.id.layout_edit) as LinearLayout

    init {
      itemView.setOnClickListener {
        commentList?.get(adapterPosition)?.let { content ->
          pageNumber = adapterPosition
          onItemClick?.invoke(content)
        }
      }
    }
  }
}
