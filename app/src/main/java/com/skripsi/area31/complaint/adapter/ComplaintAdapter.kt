package com.skripsi.area31.complaint.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.skripsi.area31.R
import com.skripsi.area31.complaint.model.Complaint
import java.util.*

class ComplaintAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
  var onItemClick: ((Complaint) -> Unit)? = null
  private var complaintItemsList: MutableList<Complaint>? = null
  private lateinit var publicParent: ViewGroup
  private var isLoadingAdded = false
  private val loading = 0
  private val item = 1
  private var pageNumber = 0

  init {
    complaintItemsList = LinkedList()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    publicParent = parent
    return ComplaintViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_layout_complaint, parent, false))
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val complaintItems = complaintItemsList?.get(position)
    if (getItemViewType(position) == item) {
      val complaintViewHolder = holder as ComplaintViewHolder
      complaintViewHolder.itemTitle.text = complaintItems?.quizTitle
      complaintViewHolder.itemStatus.text = complaintItems?.status
    }
  }

  override fun getItemCount(): Int = complaintItemsList?.size ?: 0

  override fun getItemViewType(
      position: Int): Int = if (position == complaintItemsList?.size?.minus(
          1) && isLoadingAdded) loading else item

  fun add(complaintItems: Complaint) {
    complaintItemsList?.add(complaintItems)
    complaintItemsList?.size?.minus(1)?.let { notifyItemInserted(it) }
  }

  fun addAll(complaintResult: List<Complaint>) {
    for (result in complaintResult) {
      add(result)
    }
  }

  fun clear() = complaintItemsList?.clear()

  inner class ComplaintViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val itemTitle: TextView = itemView.findViewById<View>(R.id.item_title) as TextView
    val itemStatus: TextView = itemView.findViewById<View>(R.id.item_status) as TextView

    init {
      itemView.setOnClickListener {
        complaintItemsList?.get(adapterPosition)?.let { complaintItems ->
          pageNumber = adapterPosition
          onItemClick?.invoke(complaintItems)
        }
      }
    }
  }
}