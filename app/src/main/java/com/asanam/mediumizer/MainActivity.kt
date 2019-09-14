package com.asanam.mediumizer

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.nio.file.Files.size
import android.util.Patterns
import android.view.View
import android.webkit.*
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.StringBuilder

private const val MEDIUM_TAG = "com.medium.reader"
class MainActivity : AppCompatActivity() {

    private lateinit var link: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initWebView()
        when {
            intent?.action == Intent.ACTION_SEND -> {
                if ("text/plain" == intent.type) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                        if (savedInstanceState == null) {
                            handleSendText(intent)
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        initWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        webview.settings.javaScriptEnabled = true
        webview.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        webview.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        webview.clearCache(true)
        setwebViewClient()
        setwebChromeClient()
    }

    private fun setwebChromeClient() {
        webview.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress == 100) {
                    progress_circular.isVisible = false
                }
            }
        }
    }

    private fun setwebViewClient() {
        webview.webViewClient = object: WebViewClient() {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                request?.let {
                    val url = it.url.toString()
                    return if(url.startsWith("medium")) {
                        error_scene.isVisible = false
                        progress_circular.isVisible = true
                        true
                    } else {
                        clearCookies()
                        view?.loadUrl(it.url.toString())
                        false
                    }
                }
                return false
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                progress_circular.isVisible = true
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                webview.isVisible = false
                error_scene.isVisible = true
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        webview.saveState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        webview.restoreState(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private fun handleSendText(intent: Intent) {
        val callingApp = this.referrer?.host
        if(callingApp == MEDIUM_TAG) {
            intent.getStringExtra(Intent.EXTRA_TEXT)?.let { text ->
                val links = extractLinks(text)
                if(links.size == 0) {
                    makeToast("No Links found")
                } else {
                    link = links[0]
                    loadUrl(link)
                }
            }
        } else {
            makeToast("Not supported")
        }
    }

    private fun loadUrl(link: String) {
        webview.clearCache(true)
        clearCookies()
        error_scene.isVisible = false
        webview.loadUrl(link)
    }

    private fun clearCookies() {
        val cookieManager = CookieManager.getInstance()
        cookieManager.removeAllCookies(null)
        cookieManager.flush()
    }

    private fun makeToast(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }

    private fun extractLinks(text: String): ArrayList<String> {
        val links = ArrayList<String>()
        val m = Patterns.WEB_URL.matcher(text)
        while (m.find()) {
            val url = m.group()
            links.add(url)
        }
        return links
    }
}
