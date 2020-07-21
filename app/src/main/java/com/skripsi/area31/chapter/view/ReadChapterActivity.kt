package com.skripsi.area31.chapter.view

import android.annotation.SuppressLint
import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.databinding.DataBindingUtil
import com.skripsi.area31.R
import com.skripsi.area31.core.base.BaseActivity
import com.skripsi.area31.databinding.ActivityReadChapterBinding
import com.skripsi.area31.utils.Constants.Companion.PAGE_URL

class ReadChapterActivity : BaseActivity() {
  private lateinit var pageUrl: String
  private lateinit var idChapter: String
  private lateinit var binding: ActivityReadChapterBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_read_chapter)
    pageUrl = intent.getStringExtra(PAGE_URL) ?: throw IllegalStateException(
        "field $PAGE_URL missing in Intent")
    initWebView()
    setWebClient()
    handlePullToRefresh()
    loadUrl(pageUrl)
  }

  private fun handlePullToRefresh() {
  }

  @SuppressLint("SetJavaScriptEnabled") private fun initWebView() {
    with(binding) {
      webView.settings.javaScriptEnabled = true
      webView.settings.loadWithOverviewMode = true
      webView.settings.useWideViewPort = true
      webView.settings.domStorageEnabled = true
      webView.settings.builtInZoomControls = true
      webView.settings.displayZoomControls = true
      webView.webViewClient = object : WebViewClient() {
        override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?,
            error: SslError?) {
          handler?.proceed()
        }
      }
    }
  }

  private fun setWebClient() {
    with(binding) {
      webView.webChromeClient = object : WebChromeClient() {
        override fun onProgressChanged(view: WebView, newProgress: Int) {
          super.onProgressChanged(view, newProgress)
          progressBar.progress = newProgress
          if (newProgress < 100 && progressBar.visibility == ProgressBar.GONE) {
            progressBar.visibility = ProgressBar.VISIBLE
          }
          if (newProgress == 100) {
            progressBar.visibility = ProgressBar.GONE
          }
        }
      }
    }
  }

  override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
    with(binding) {
      if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
        webView.goBack()
        return true
      }
      return super.onKeyDown(keyCode, event)
    }
  }

  private fun loadUrl(pageUrl: String) {
    binding.webView.loadData(pageUrl, "text/html; charset=utf-8", "UTF-8")
  }

  override fun onFailed(message: String) {
    Log.e("READ_CHAPTER", message)
  }

}