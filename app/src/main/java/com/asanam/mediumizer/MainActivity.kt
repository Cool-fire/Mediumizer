package com.asanam.mediumizer

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import android.util.Patterns
import android.view.View
import android.webkit.*
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

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
        webview.settings.builtInZoomControls = true
        webview.settings.displayZoomControls = false
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
                progress_circular.isVisible = false
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
                    makeSnackBar("No links Found")
                    showErrorPage()
                } else {
                    link = links[0]
                    loadUrl(link)
                }
            }
        } else {
            makeSnackBar("Other apps are not Supported")
            showErrorPage()
        }
    }

    private fun showErrorPage() {
        progress_circular.isVisible = false
        webview.isVisible = false
        error_scene.isVisible = true
    }

    private fun makeSnackBar(text: String) {
        Snackbar.make(constraint,text,Snackbar.LENGTH_LONG).setAction("open medium") {
            openMediumApp()
        }.setActionTextColor(resources.getColor(R.color.colorAccent)).show()
    }

    private fun openMediumApp() {
        var intent = applicationContext.packageManager.getLaunchIntentForPackage(MEDIUM_TAG)
        if (intent == null) {
            intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("market://details?id=$MEDIUM_TAG")
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        applicationContext.startActivity(intent)
    }

    private fun loadUrl(link: String) {
        webview.clearCache(true)
        clearCookies()
        error_scene.isVisible = false
        webview.isVisible = true
        webview.loadUrl(link)
    }

    private fun clearCookies() {
        val cookieManager = CookieManager.getInstance()
        cookieManager.removeAllCookies(null)
        cookieManager.flush()
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
