package com.logicline.mydining.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import com.logicline.mydining.databinding.ActivityGenericWebviewBinding
import com.logicline.mydining.utils.BaseActivity
import com.logicline.mydining.utils.Constant
import com.logicline.mydining.utils.LoadingDialog
import com.logicline.mydining.utils.MyExtensions.shortToast
import java.net.URISyntaxException


class GenericWebViewActivity : BaseActivity() {
    lateinit var binding: ActivityGenericWebviewBinding
    lateinit var loadingDialog: LoadingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGenericWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()

        intent?.let {
            val pageName = it.getStringExtra(Constant.ACTIVITY_NAME);
            supportActionBar?.title = pageName
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeButtonEnabled(true)


            val url = it.getStringExtra(Constant.WEB_URL)
            url?.let {
                binding.webView.loadUrl(it)
            }


        }



    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initViews() {
        loadingDialog = LoadingDialog(this)

        val webview = binding.webView
        webview.webChromeClient = MyChromeWebClient()
        webview.webViewClient = MyWebCViewClient()
        webview.settings.javaScriptEnabled = true
    }

    inner class MyChromeWebClient : WebChromeClient(){
        override fun onReceivedTouchIconUrl(view: WebView?, url: String?, precomposed: Boolean) {
            super.onReceivedTouchIconUrl(view, url, precomposed)
        }



    }

    inner class MyWebCViewClient : WebViewClient(){
        override fun onPageFinished(view: WebView?, url: String?) {
            binding.progress.visibility = View.GONE
            super.onPageFinished(view, url)
        }

        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            binding.progress.visibility = View.VISIBLE
            super.onPageStarted(view, url, favicon)
        }

        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {

            if (request!!.url.toString().startsWith("tel:") || request.url.toString()
                    .startsWith("whatsapp:")
            ) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(request.url.toString())
                startActivity(intent)
                return true
            }else if (request.url.toString().contains("mailto:")) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(request.url.toString())))
                return true

            } else if (request.url.toString().startsWith("intent://")) {
                try {
                    val intent = Intent.parseUri(request.url.toString(), Intent.URI_INTENT_SCHEME);

                    if (intent != null) {
                        val packageManager = packageManager
                        val info = packageManager.resolveActivity(
                            intent,
                            PackageManager.MATCH_DEFAULT_ONLY
                        );
                        if (info != null) {
                            startActivity(intent);
                        } else {
                            val fallbackUrl = intent.getStringExtra("browser_fallback_url");
//                            view?.loadUrl(fallbackUrl!!);

                            // or call external broswer
                            try {
                                val browserIntent =  Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl));
                                startActivity(browserIntent)
                            }catch (e:Exception){
                                shortToast("Something went wrong")
                            }
                        }

                        return true;
                    }
                } catch (e: URISyntaxException) {
                    shortToast(e.message)
                }

            }

            return false
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home){
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
