package com.skripsi.area31.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.skripsi.area31.R
import com.skripsi.area31.databinding.FragmentHomeBinding
import java.util.*
import kotlin.math.sqrt

class HomeFragment : Fragment() {
  private lateinit var binding: FragmentHomeBinding
  private var handler = Handler(Looper.getMainLooper() /*UI thread*/)
  private var workRunnable: Runnable? = null
  private var searchByName = ""

  companion object {
    const val TAG: String = "Home Fragment"
  }

  fun newInstance(): HomeFragment = HomeFragment()

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
    with(binding) {
      txtAnswer.addTextChangedListener {
        handler.removeCallbacks(workRunnable)
        searchByName = txtAnswer.text.toString().toLowerCase(Locale.ROOT)
        workRunnable = Runnable {
          tvResult.text = "0"
          checkSimilarity(searchByName)
        }
        handler.postDelayed(workRunnable, 500 /*delay*/)
      }
    }
    return binding.root
  }

  private fun checkSimilarity(answer: String) {
    with(binding) {
      val answerKeyWords = txtAnswerKey.text.toString().toLowerCase(Locale.ROOT).split(
          ' ').groupingBy { it }.eachCount()
      val answerWords = answer.split(' ').groupingBy { it }.eachCount()

      val answerKeyWordsMerged = answerKeyWords.mergeWith(answerWords)
      val answerWordsMerged = answerWords.mergeWith(answerKeyWords)
      tvResult.text = answerKeyWordsMerged.toString()
      tvResult2.text = answerWordsMerged.toString()
      tvResult3.text = answerKeyWordsMerged.result(answerWordsMerged).toString()
    }
  }

  private fun Map<String, Int>.mergeWith(another: Map<String, Int>): Map<String, Int> {
    val unionList: MutableMap<String, Int> = toMutableMap()
    for ((key, _) in another) {
      if (unionList[key] == null) {
        unionList[key] = 0
      }
    }
    return unionList
  }

  private fun Map<String, Int>.result(another: Map<String, Int>): Double {
    val unionList: MutableMap<String, Int> = toMutableMap()
    val result: Double
    var n = 0
    var n1 = 0.0
    var n2 = 0.0
    for ((key, _) in another) {
      val result1 = another[key]?.let { unionList[key]?.times(it) }
      val resultN1 = another[key]?.let { another[key]?.times(it) }
      val resultN2 = unionList[key]?.let { unionList[key]?.times(it) }
      if (result1 != null && resultN1 != null && resultN2 != null) {
        n += result1
        n1 += resultN1
        n2 += resultN2
      }
    }
    result = (n / (sqrt(n1) * sqrt(n2))) * 100
    return result
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.txtAnswerKey.setText("Julie loves me more than linda loves me")
    binding.txtAnswer.setText("Jane likes me more than Julie loves me")
  }
}

