package com.skripsi.area31.courseresource.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.skripsi.area31.R
import com.skripsi.area31.courseresource.model.Resource
import java.util.*

class ListResourceAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
  var onItemClick: ((Resource) -> Unit)? = null
  private var resourceItemsList: MutableList<Resource>? = null
  private lateinit var publicParent: ViewGroup
  private var isLoadingAdded = false
  private val loading = 0
  private val item = 1
  private var pageNumber = 0

  init {
    resourceItemsList = LinkedList()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    publicParent = parent
    return ResourceViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_layout_resource, parent, false))
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val resourceItems = resourceItemsList?.get(position)
    if (getItemViewType(position) == item) {
      val resourceViewHolder = holder as ResourceViewHolder
      resourceViewHolder.itemTitle.text = resourceItems?.fileName
    }
  }

  override fun getItemCount(): Int = resourceItemsList?.size ?: 0

  override fun getItemViewType(position: Int): Int = if (position == resourceItemsList?.size?.minus(
          1) && isLoadingAdded) loading else item

  fun add(resourceItems: Resource) {
    resourceItemsList?.add(resourceItems)
    resourceItemsList?.size?.minus(1)?.let { notifyItemInserted(it) }
  }

  fun addAll(resourceResult: List<Resource>) {
    for (result in resourceResult) {
      add(result)
    }
  }

  fun clear() = resourceItemsList?.clear()

  inner class ResourceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val itemTitle: TextView = itemView.findViewById<View>(R.id.item_title) as TextView

    init {
      itemView.setOnClickListener {
        resourceItemsList?.get(adapterPosition)?.let { resourceItems ->
          pageNumber = adapterPosition
          onItemClick?.invoke(resourceItems)
        }
      }
    }
  }
}
