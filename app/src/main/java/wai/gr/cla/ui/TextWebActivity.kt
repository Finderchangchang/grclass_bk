package wai.gr.cla.ui

import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView

import com.lzy.okgo.OkGo
import kotlinx.android.synthetic.main.activity_text_web.*
import kotlinx.android.synthetic.main.activity_zi_xun_detail.*
import org.sufficientlysecure.htmltextview.HtmlResImageGetter

import java.io.IOException
import java.net.MalformedURLException
import java.net.URL

import wai.gr.cla.R
import wai.gr.cla.base.BaseActivity
import wai.gr.cla.callback.JsonCallback
import wai.gr.cla.model.LzyResponse
import wai.gr.cla.model.url

class TextWebActivity : BaseActivity() {

    private var handler: Handler? = null
    private var html: String? = null
    private var tv: TextView? = null
    private var bar: ProgressBar? = null

    override fun setLayout(): Int {
        return R.layout.activity_text_web
    }

    var url = ""
    override fun initViews() {
        titleBar.setLeftClick { finish() }
        when (intent.getStringExtra("name")) {
            "关于" -> {
                titleBar.setCentertv("冠人教育介绍")
                url = url().public_api + "get_about_info"
            }
            "协议" -> {
                titleBar.setCentertv("冠人教育用户协议")
                url = url().public_api + "get_agreement_info"
            }
        }

        tv = this.findViewById<TextView>(R.id.id)
        bar = this.findViewById<ProgressBar>(R.id.id_bar)
        tv!!.movementMethod = ScrollingMovementMethod.getInstance()// 滚动
        id.movementMethod = LinkMovementMethod.getInstance();//设置超链接可以打开网页
        handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                // TODO Auto-generated method stub
                if (msg.what == 0x101) {
                    bar!!.visibility = View.GONE
                    //id.text = msg.obj as CharSequence
                    var text=msg.obj as CharSequence
                   // html_text.setHtml(text.toString(), HtmlResImageGetter(html_text))
                }
                super.handleMessage(msg)
            }
        }

        // 因为从网上下载图片是耗时操作 所以要开启新线程
        val t = Thread(object : Runnable {
            internal var msg = Message.obtain()

            override fun run() {
                // TODO Auto-generated method stub
                bar!!.visibility = View.VISIBLE
                /**
                 * 要实现图片的显示需要使用Html.fromHtml的一个重构方法：public static Spanned
                 * fromHtml (String source, Html.ImageGetterimageGetter,
                 * Html.TagHandler
                 * tagHandler)其中Html.ImageGetter是一个接口，我们要实现此接口，在它的getDrawable
                 * (String source)方法中返回图片的Drawable对象才可以。
                 */
                val imageGetter = Html.ImageGetter { source ->
                    // TODO Auto-generated method stub
                    val url: URL
                    var drawable: Drawable? = null
                    try {
                        url = URL(source)
                        drawable = Drawable.createFromStream(
                                url.openStream(), null)
                        drawable!!.setBounds(0, 0,
                                drawable.intrinsicWidth,
                                drawable.intrinsicHeight)
                    } catch (e: MalformedURLException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    drawable
                }

                OkGo.post(url)
                        .execute(object : JsonCallback<LzyResponse<String>>() {
                            override fun onSuccess(t: LzyResponse<String>, call: okhttp3.Call?, response: okhttp3.Response?) {
                                if (t.code == 0) {
                                    html_text.setHtml(t.data.toString(), HtmlResImageGetter(html_text))
                                    //val test = Html.fromHtml(model!!.content.toString(), imageGetter, null)
                                    val test = Html.fromHtml(t.data, imageGetter, null)
//                                    msg.what = 0x101
//                                    msg.obj = test
//                                    handler!!.sendMessage(msg)
                                   // id.text=test
                                    msg.what = 0x101
                                    msg.obj = test
                                    handler!!.sendMessage(msg)
                                }
                            }
                        })

            }
        })
        t.start()
    }

    override fun initEvents() {

    }
}
