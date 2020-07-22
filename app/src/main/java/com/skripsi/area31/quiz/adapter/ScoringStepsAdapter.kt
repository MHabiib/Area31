package com.skripsi.area31.quiz.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.skripsi.area31.R
import com.skripsi.area31.quiz.model.WordRatio
import java.util.*

class ScoringStepsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
  private var scoringItemsList: MutableList<WordRatio>
  private lateinit var publicParent: ViewGroup
  private var isLoadingAdded = false
  private val loading = 0
  private val item = 1

  init {
    scoringItemsList = LinkedList()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    publicParent = parent
    return ScoringViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_layout_word_list, parent, false))
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val scoringItems = scoringItemsList.get(position)
    if (getItemViewType(position) == item) {
      val scoringViewHolder = holder as ScoringViewHolder
      scoringViewHolder.name.text = scoringItems.word
      scoringViewHolder.ratioAnswer.text = scoringItems.ratioAnswer.toString()
      scoringViewHolder.ratioAnswerKey.text = scoringItems.ratioAnswerKey.toString()
    }
  }

  override fun getItemCount(): Int = scoringItemsList.size

  override fun getItemViewType(position: Int): Int = if (position == scoringItemsList.size.minus(
          1) && isLoadingAdded) loading else item

  fun add(scoringItems: WordRatio) {
    scoringItemsList.add(scoringItems)
    scoringItemsList.size.minus(1).let { notifyItemInserted(it) }
  }

  fun addAll(scoringResult: List<WordRatio>) {
    for (result in scoringResult) {
      add(result)
    }
  }

  fun clear() = scoringItemsList.clear()

  inner class ScoringViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val name: TextView = itemView.findViewById<View>(R.id.tv_word_name) as TextView
    val ratioAnswerKey: TextView = itemView.findViewById<View>(R.id.tv_ratio_answer_key) as TextView
    val ratioAnswer: TextView = itemView.findViewById<View>(R.id.tv_ratio_answer) as TextView
  }
}
