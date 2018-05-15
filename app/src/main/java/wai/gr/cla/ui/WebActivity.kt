package wai.gr.cla.ui

import android.app.AlertDialog
import android.net.http.SslError
import android.os.Build
import android.text.TextUtils
import android.webkit.*
import kotlinx.android.synthetic.main.activity_web.*

import wai.gr.cla.R
import wai.gr.cla.base.BaseActivity
import wai.gr.cla.method.Utils
import wai.gr.cla.model.key





/**
 * 加载web的内容
 * */
class WebActivity : BaseActivity() {
    override fun setLayout(): Int {
        return R.layout.activity_web
    }

    var title = ""
    var url = ""
    override fun initViews() {
        title = intent.getStringExtra("title")
        if (!TextUtils.isEmpty(title)) {
            toolbar.setCentertv(title)
        }
        toolbar.setLeftClick { finish() }
        url = intent.getStringExtra("url")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            web.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        CookieSyncManager.createInstance(this)
        val cookieManager = CookieManager.getInstance()
        val cookieString = Utils.getCache("all_session")
        cookieManager.setAcceptCookie(true);
        cookieManager.setCookie(url, cookieString)
        CookieSyncManager.getInstance().sync()
        web.loadUrl(url)
        val webSetting = web.settings
        webSetting.javaScriptEnabled = true
        webSetting.useWideViewPort = true;
        webSetting.loadWithOverviewMode = true;
        //.headers("cookie", Utils.getCache(key.KEY_SESSIONID))
        //设置WebChromeClient
        web.setWebChromeClient(object : WebChromeClient() {
            //处理javascript中的alert
            override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
                //构建一个Builder来显示网页中的对话框
                val builder = AlertDialog.Builder(this@WebActivity)
                builder.setTitle("提示")
                builder.setMessage(message)
                builder.setPositiveButton("确定"){dialog, which->
                                result.confirm()
                            }

                builder.setCancelable(false)
                builder.create()
                builder.show()
                return true
            }

        })
        web.setWebViewClient(object : WebViewClient() {
            override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
                handler.proceed()
            }
        })
    }

    override fun initEvents() {

    }

    override fun onPause() {
        web.destroy()
        super.onPause()
    }
}
