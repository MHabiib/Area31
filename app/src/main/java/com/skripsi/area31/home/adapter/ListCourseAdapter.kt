package com.skripsi.area31.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.skripsi.area31.R
import com.skripsi.area31.home.model.Course
import java.util.*

class ListCourseAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
  var onItemClick: ((Course) -> Unit)? = null
  private var courseItemsList: MutableList<Course>? = null
  private lateinit var publicParent: ViewGroup
  private var isLoadingAdded = false
  private val loading = 0
  private val item = 1
  private var pageNumber = 0

  init {
    courseItemsList = LinkedList()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    publicParent = parent
    return CourseViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_layout_course, parent, false))
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val courseItems = courseItemsList?.get(position)
    if (getItemViewType(position) == item) {
      val historyViewHolder = holder as CourseViewHolder
      historyViewHolder.itemTitle.text = courseItems?.name
      historyViewHolder.itemName.text = courseItems?.instructorName
    }
  }

  override fun getItemCount(): Int = courseItemsList?.size ?: 0

  override fun getItemViewType(position: Int): Int = if (position == courseItemsList?.size?.minus(
          1) && isLoadingAdded) loading else item

  fun add(courseItems: Course) {
    courseItemsList?.add(courseItems)
    courseItemsList?.size?.minus(1)?.let { notifyItemInserted(it) }
  }

  fun addAll(courseResult: List<Course>) {
    for (result in courseResult) {
      add(result)
    }
  }

  fun pageNumber(page: Int) {
    pageNumber = page
  }

  fun addAt(position: Int, courseItems: Course) = courseItemsList?.add(position, courseItems)

  fun remove(position: Int) {
    courseItemsList?.removeAt(position)
    notifyItemRemoved(position)
    courseItemsList?.size?.let { notifyItemRangeChanged(position, it) }
  }

  fun clear() = courseItemsList?.clear()

  inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val itemTitle: TextView = itemView.findViewById<View>(R.id.item_title) as TextView
    val itemName: TextView = itemView.findViewById<View>(R.id.item_name) as TextView

    init {
      itemView.setOnClickListener {
        courseItemsList?.get(adapterPosition)?.let { courseItems ->
          pageNumber = adapterPosition
          onItemClick?.invoke(courseItems)
        }
      }
    }
  }
}
