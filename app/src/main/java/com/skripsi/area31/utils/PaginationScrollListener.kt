package com.skripsi.area31.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

open class PaginationScrollListener(private val layoutManager: LinearLayoutManager,
    private val isLastPage: Boolean) : RecyclerView.OnScrollListener() {

  override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
    recyclerView.let { super.onScrolled(it, dx, dy) }

    val visibleItemCount = layoutManager.childCount
    val totalItemCount = layoutManager.itemCount
    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
    val isLastPage = isLastPage

    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount - 1 && firstVisibleItemPosition >= 0 && !isLastPage && dy > 0) {
      loadMoreItems()
    }
  }

  protected open fun loadMoreItems() {}

}