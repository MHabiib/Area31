package com.skripsi.area31.chapter.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.skripsi.area31.R
import com.skripsi.area31.chapter.model.Chapter
import java.util.*

class ListChapterAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
  var onItemClick: ((Chapter) -> Unit)? = null
  private var chapterItemsList: MutableList<Chapter>? = null
  private lateinit var publicParent: ViewGroup
  private var isLoadingAdded = false
  private val loading = 0
  private val item = 1
  private var pageNumber = 0

  init {
    chapterItemsList = LinkedList()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    publicParent = parent
    return ChapterViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_layout_chapter, parent, false))
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val chapterItems = chapterItemsList?.get(position)
    if (getItemViewType(position) == item) {
      val chapterViewHolder = holder as ChapterViewHolder
      chapterViewHolder.itemTitle.text = chapterItems?.title
      chapterViewHolder.itemDescription.text = chapterItems?.description
    }
  }

  override fun getItemCount(): Int = chapterItemsList?.size ?: 0

  override fun getItemViewType(position: Int): Int = if (position == chapterItemsList?.size?.minus(
          1) && isLoadingAdded) loading else item

  fun add(chapterItems: Chapter) {
    chapterItemsList?.add(chapterItems)
    chapterItemsList?.size?.minus(1)?.let { notifyItemInserted(it) }
  }

  fun addAll(chapterResult: List<Chapter>) {
    for (result in chapterResult) {
      add(result)
    }
  }

  fun clear() = chapterItemsList?.clear()

  inner class ChapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val itemTitle: TextView = itemView.findViewById<View>(R.id.item_title) as TextView
    val itemDescription: TextView = itemView.findViewById<View>(R.id.item_description) as TextView

    init {
      itemView.setOnClickListener {
        chapterItemsList?.get(adapterPosition)?.let { chapterItems ->
          pageNumber = adapterPosition
          onItemClick?.invoke(chapterItems)
        }
      }
    }
  }
}
