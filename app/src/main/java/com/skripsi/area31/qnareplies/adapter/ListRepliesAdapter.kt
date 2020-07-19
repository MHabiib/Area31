package com.skripsi.area31.qnareplies.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.skripsi.area31.R
import com.skripsi.area31.qnareplies.model.Replies
import com.skripsi.area31.qnareplies.view.RepliesActivity
import java.util.*
import java.util.concurrent.TimeUnit

class ListRepliesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
  var onItemClick: ((Replies) -> Unit)? = null
  private var repliesList: MutableList<Replies>? = LinkedList()
  private var isLoadingAdded = false
  private val loading = 0
  private val item = 1
  private lateinit var idUser: String
  private var pageNumber = 0

  override fun onCreateViewHolder(parent: ViewGroup,
      viewType: Int): RecyclerView.ViewHolder = BookingViewHolder(
      LayoutInflater.from(parent.context).inflate(R.layout.item_layout_qna_replies, parent, false))

  @SuppressLint("SetTextI18n") override fun onBindViewHolder(holder: RecyclerView.ViewHolder,
      position: Int) {
    val replies = repliesList?.get(position)
    if (getItemViewType(position) == item) {
      val repliesViewHolder = holder as BookingViewHolder
      repliesViewHolder.name.text = "${replies?.name} | ${replies?.createdAt?.let {
        val time = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta")).timeInMillis - it
        var minutes = TimeUnit.MILLISECONDS.toMinutes(time)
        if (minutes > 59) {
          minutes = TimeUnit.MILLISECONDS.toHours(time)
          minutes.toString() + holder.itemView.resources.getString(R.string.hours_ago)
        } else {
          minutes.toString() + holder.itemView.resources.getString(R.string.minutes_ago)
        }
      }}"
      if (idUser == replies?.idUser) {
        repliesViewHolder.layoutEdit.visibility = View.VISIBLE
        repliesViewHolder.edit.setOnClickListener {

          (it.context as RepliesActivity).editReplies(replies.idReplies, replies.body,
              holder.adapterPosition)
        }
        repliesViewHolder.delete.setOnClickListener {

          (it.context as RepliesActivity).deleteReplies(replies.idReplies, holder.adapterPosition)
        }
      } else {
        repliesViewHolder.layoutEdit.visibility = View.GONE
      }
      repliesViewHolder.description.text = replies?.body
    }
  }

  override fun getItemCount(): Int = repliesList?.size ?: 0

  override fun getItemViewType(position: Int): Int = if (position == repliesList?.size?.minus(
          1) && isLoadingAdded) loading else item

  fun add(replies: Replies) {
    repliesList?.add(replies)
    repliesList?.size?.minus(1)?.let { notifyItemInserted(it) }
  }

  fun addAll(repliesResult: List<Replies>) {
    for (result in repliesResult) {
      add(result)
    }
  }

  fun getIdUser(idUser: String) {
    this.idUser = idUser
  }

  fun addAt(position: Int, replies: Replies) = repliesList?.add(position, replies)

  fun remove(position: Int) {
    repliesList?.removeAt(position)
    notifyItemRemoved(position)
    repliesList?.size?.let { notifyItemRangeChanged(position, it) }
  }

  fun clear() = repliesList?.clear()

  inner class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val name: TextView = itemView.findViewById<View>(R.id.item_name) as TextView
    val description: TextView = itemView.findViewById<View>(R.id.item_description) as TextView
    val edit: TextView = itemView.findViewById<View>(R.id.tv_edit) as TextView
    val delete: TextView = itemView.findViewById<View>(R.id.tv_delete) as TextView
    val layoutEdit: LinearLayout = itemView.findViewById<View>(R.id.layout_edit) as LinearLayout

    init {
      itemView.setOnClickListener {
        repliesList?.get(adapterPosition)?.let { content ->
          pageNumber = adapterPosition
          onItemClick?.invoke(content)
        }
      }
    }
  }
}
