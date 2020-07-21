package com.skripsi.area31.quizlist.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.skripsi.area31.R
import com.skripsi.area31.quizlist.model.Quiz
import com.skripsi.area31.utils.Utils
import java.util.*
import java.util.concurrent.TimeUnit

class ListQuizAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
  var onItemClick: ((Quiz) -> Unit)? = null
  private var quizItemsList: MutableList<Quiz>? = null
  private lateinit var publicParent: ViewGroup
  private var isLoadingAdded = false
  private val loading = 0
  private val item = 1
  private var pageNumber = 0

  init {
    quizItemsList = LinkedList()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    publicParent = parent
    return QuizViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_layout_quiz, parent, false))
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val quizItems = quizItemsList?.get(position)
    if (getItemViewType(position) == item) {
      val quizViewHolder = holder as QuizViewHolder
      quizViewHolder.itemTitle.text = quizItems?.titleQuiz
      quizViewHolder.itemDate.text = quizItems?.quizDate?.let {
        holder.itemView.context.getString(R.string.date) + Utils.convertLongToTimeShortMonth(it)
      }
      quizViewHolder.itemDuration.text = quizItems?.quizDuration?.let {
        holder.itemView.context.getString(R.string.duration) + Utils.convertLongToMinutes(
            it) + " " + holder.itemView.context.getString(R.string.minutes)
      }
      if (quizItems?.score != null) {
        quizViewHolder.layoutScore.visibility = View.VISIBLE
        quizViewHolder.score.text = quizItems.score.toString()
        quizViewHolder.itemLayout.setBackgroundResource(R.drawable.card_quiz_purple)
      } else if (quizItems?.assignedAt != null) {
        quizViewHolder.layoutScore.visibility = View.GONE
        quizViewHolder.ongoing.visibility = View.GONE
        quizViewHolder.ibStartQuiz.visibility = View.GONE
      } else {
        quizViewHolder.layoutScore.visibility = View.GONE
        if (quizItems?.quizDate != null && quizItems.quizDate < System.currentTimeMillis() && quizItems.quizDate + quizItems.quizDuration > System.currentTimeMillis()) {
          quizViewHolder.ongoing.visibility = View.VISIBLE
          quizViewHolder.ibStartQuiz.visibility = View.VISIBLE
          quizViewHolder.layoutMinutes.visibility = View.GONE
          quizViewHolder.itemLayout.setBackgroundResource(R.drawable.card_quiz_green)
        } else {
          val quizTime = quizItems?.quizDate?.minus(System.currentTimeMillis())
          if (quizTime != null) {
            if (quizTime > 0) {
              quizViewHolder.layoutMinutes.visibility = View.VISIBLE
              quizViewHolder.layoutMinutesPast.visibility = View.GONE
              quizViewHolder.ongoing.visibility = View.GONE
              quizViewHolder.ibStartQuiz.visibility = View.GONE
              quizViewHolder.itemLayout.setBackgroundResource(R.drawable.card_quiz_orange)
              quizViewHolder.minutes.text = quizTime.let {
                val time = it
                var minutes = TimeUnit.MILLISECONDS.toMinutes(time)
                if (minutes > 59) {
                  minutes = TimeUnit.MILLISECONDS.toHours(time)
                  minutes.toString() + " " + holder.itemView.resources.getString(R.string.hours)
                } else {
                  minutes.toString() + " " + holder.itemView.resources.getString(
                    R.string.minutes)
                }
              }
            } else {
              quizViewHolder.layoutMinutesPast.visibility = View.VISIBLE
              quizViewHolder.layoutMinutes.visibility = View.GONE
              quizViewHolder.ongoing.visibility = View.GONE
              quizViewHolder.ibStartQuiz.visibility = View.GONE
              quizViewHolder.itemLayout.setBackgroundResource(R.drawable.card_quiz_red)
              quizViewHolder.minutesPast.text = (quizTime * -1).let {
                val time = it
                var minutes = TimeUnit.MILLISECONDS.toMinutes(time)
                if (minutes > 59) {
                  minutes = TimeUnit.MILLISECONDS.toHours(time)
                  minutes.toString() + " " + holder.itemView.resources.getString(R.string.hours_ago)
                } else {
                  minutes.toString() + " " + holder.itemView.resources.getString(
                      R.string.minutes_ago)
                }
              }
            }
          }
        }
      }
    }
  }

  override fun getItemCount(): Int = quizItemsList?.size ?: 0

  override fun getItemViewType(position: Int): Int = if (position == quizItemsList?.size?.minus(
          1) && isLoadingAdded) loading else item

  fun add(quizItems: Quiz) {
    quizItemsList?.add(quizItems)
    quizItemsList?.size?.minus(1)?.let { notifyItemInserted(it) }
  }

  fun addAll(quizResult: List<Quiz>) {
    for (result in quizResult) {
      add(result)
    }
  }

  fun clear() = quizItemsList?.clear()

  inner class QuizViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val itemTitle: TextView = itemView.findViewById<View>(R.id.item_title) as TextView
    val itemDate: TextView = itemView.findViewById<View>(R.id.item_date) as TextView
    val itemDuration: TextView = itemView.findViewById<View>(R.id.item_duration) as TextView
    val score: TextView = itemView.findViewById<View>(R.id.score) as TextView
    val minutes: TextView = itemView.findViewById<View>(R.id.minutes) as TextView
    val minutesPast: TextView = itemView.findViewById<View>(R.id.minutes_past) as TextView
    val ongoing: TextView = itemView.findViewById<View>(R.id.tv_ongoing) as TextView
    val ibStartQuiz: ImageButton = itemView.findViewById<View>(R.id.iv_start_quiz) as ImageButton
    val layoutScore: LinearLayout = itemView.findViewById<View>(R.id.layout_score) as LinearLayout
    val itemLayout: ConstraintLayout = itemView.findViewById<View>(R.id.item_layout) as ConstraintLayout
    val layoutMinutes: LinearLayout = itemView.findViewById<View>(
        R.id.layout_minutes) as LinearLayout
    val layoutMinutesPast: LinearLayout = itemView.findViewById<View>(
        R.id.layout_minutes_past) as LinearLayout

    init {
      itemView.setOnClickListener {
        quizItemsList?.get(adapterPosition)?.let { quizItems ->
          pageNumber = adapterPosition
          onItemClick?.invoke(quizItems)
        }
      }
    }
  }
}
