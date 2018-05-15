package wai.gr.cla.ui

import android.content.Intent
import android.os.Handler
import android.text.TextUtils
import android.view.View

import com.lzy.okgo.OkGo
import kotlinx.android.synthetic.main.activity_zi_xun_detail.*
import okhttp3.Call
import okhttp3.Response

import wai.gr.cla.R
import wai.gr.cla.base.BaseActivity
import wai.gr.cla.callback.JsonCallback
import wai.gr.cla.method.Utils
import wai.gr.cla.method.common
import wai.gr.cla.model.*

/**
 * Created by lenovo on 2017/5/20.
 */

class ZiXunDetailActivity : BaseActivity() {
    var model: ZiXunModel? = null
    var is_dy: Boolean = false//true：当前页面为答疑专栏跳转过来的
    override fun setLayout(): Int {
        return R.layout.activity_zi_xun_detail
    }

    private var handler: Handler? = null

    override fun initViews() {
        toolbar.setCentertv(intent.getStringExtra("title"))
        toolbar.setLeftClick { finish() }
        is_tui = intent.getBooleanExtra("is_tui", false)
        if (is_tui) {
            pl_rl.visibility = View.GONE
            zixun_tv_zuozhe.visibility = View.GONE
            zixun_tv_ctime.visibility = View.GONE

        }
        /*  handler = object : Handler() {
              override fun handleMessage(msg: Message) {
                  if (msg.what == 0x101) {
                      zixun_tv_content.text = msg.obj as CharSequence

                  }
                  super.handleMessage(msg)
              }
          }*/
        //留言
        ly_ll.setOnClickListener {
            var user_id = Utils.getCache(key.KEY_USERID)
            if (TextUtils.isEmpty(user_id)) {
                toast("请先登录")
            } else {
                startActivityForResult(Intent(this, AddAskActivity::class.java).putExtra("news_id", model!!.id), 0)
            }
        }
        zixun_tv_pinglun.setOnClickListener {
            startActivity(Intent(MainActivity.main, MyOrderListActivity::class.java).putExtra("which", 6).putExtra("zx_id", model!!.id))
        }
        //收藏
        sc_ll.setOnClickListener {
            var user_id = Utils.getCache(key.KEY_USERID)
            if (TextUtils.isEmpty(user_id)) {
                toast("请先登录")
            } else {
                if (model!!.favorite_id > 0) {
                    OkGo.post(url().auth_api + "del_my_favorite")
                            .params("id", model!!.favorite_id)
                            .execute(object : JsonCallback<LzyResponse<String>>() {
                                override fun onSuccess(t: LzyResponse<String>, call: okhttp3.Call?, response: okhttp3.Response?) {
                                    if (t.code == 0) {
                                        toast("取消收藏成功")
                                        refresh()
                                        sc_iv.setImageResource(R.mipmap.shoucang)
                                    } else {
                                        toast("取消收藏失败，请重试")
                                    }
                                }

                                override fun onError(call: Call?, response: Response?, e: Exception?) {
                                    toast(common().toast_error(e!!))
                                }
                            })
                } else {
                    OkGo.post(url().auth_api + "add_my_favorite")
                            .params("which", 2)
                            .params("which_id", model!!.id)
                            .execute(object : JsonCallback<LzyResponse<SCModel>>() {
                                override fun onSuccess(t: LzyResponse<SCModel>, call: okhttp3.Call?, response: okhttp3.Response?) {
                                    if (t.code == 0) {
                                        toast("收藏成功")
                                        refresh()
                                        sc_iv.setImageResource(R.mipmap.star)
                                    } else {
                                        toast("收藏失败，请重试")
                                    }
                                }

                                override fun onError(call: Call?, response: Response?, e: Exception?) {
                                    toast(common().toast_error(e!!))
                                }
                            })
                }
            }
        }
        //分享
        share_ll.setOnClickListener {
            SharedPopuWindowActivity(this@ZiXunDetailActivity, share_ll, model!!.id.toString(), model!!.title, model!!.summary, model!!.thumbnail, true).showWindow()
        }
        //点赞
        dz_ll.setOnClickListener {
            OkGo.get(url().public_api + "add_news_zan")
                    .params("id", model!!.id)
                    .execute(object : JsonCallback<LzyResponse<String>>() {
                        override fun onSuccess(t: LzyResponse<String>, call: okhttp3.Call?, response: okhttp3.Response?) {
                            if (t.code == 0) {
                                toast("点赞成功")
                            } else {
                                toast("您刚点赞过了")
                            }
                        }

                        override fun onError(call: Call?, response: Response?, e: Exception?) {
                            toast(common().toast_error(e!!))
                        }
                    })
        }
        refresh()
        // 因为从网上下载图片是耗时操作 所以要开启新线程
    }

    fun setValue() {
        zixund_tv_title!!.text = model!!.title
        zixun_tv_chakan!!.text = model!!.dots.toString()
        zixun_tv_pinglun!!.text = model!!.comments.toString()
        zixun_tv_zuozhe!!.text = "文章来源：" + model!!.editor
        zixun_tv_ctime!!.text = model!!.show_time
        if (model!!.favorite_id > 0) {//1为已收藏
            sc_iv.setImageResource(R.mipmap.star)
        } else {
            sc_iv.setImageResource(R.mipmap.shoucang)
        }
        var content = model!!.content
        content = "<html><head><title></title><style>img{width:100%;}</style></head><body>" + content!!.replace(" src=\"", " src=\"" + url().total)
        web.loadDataWithBaseURL(null, content, "text/html", "utf-8", null)
        //ImageTextUtil.setImageText(zixun_tv_content,model!!.content.toString())
        if (is_dy) {
            pl_rl.visibility = View.GONE
            bottom_ll.visibility = View.GONE
            //line_tv.visibility = View.GONE
        } else {
            if (is_tui) {
                bottom_ll.visibility = View.GONE
            } else {
                bottom_ll.visibility = View.VISIBLE
            }
        }
        /*val t = Thread(object : Runnable {
            internal var msg = Message.obtain()

            override fun run() {
                */
        /**
         * 要实现图片的显示需要使用Html.fromHtml的一个重构方法：public static Spanned
         * fromHtml (String source, Html.ImageGetterimageGetter,
         * Html.TagHandler
         * tagHandler)其中Html.ImageGetter是一个接口，我们要实现此接口，在它的getDrawable
         * (String source)方法中返回图片的Drawable对象才可以。
         */
        /*
                        val imageGetter = Html.ImageGetter { source ->
                            // TODO Auto-generated method stub
                            val url: URL
                            var drawable: Drawable? = null
                            var dm = DisplayMetrics()
                            dm = MainActivity.main!!.application.getResources().getDisplayMetrics()
                            val screenWidth = dm.widthPixels

                            try {
                                url = URL(url().total + source)
                                drawable = Drawable.createFromStream(
                                        url.openStream(), null)
                                val w = drawable!!.intrinsicWidth

                                val h = drawable!!.intrinsicHeight * (screenWidth / w)

                                drawable!!.setBounds(0, 0, screenWidth, h)
                            } catch (e: MalformedURLException) {
                                e.printStackTrace()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }

                            drawable
                        }
                        val test = Html.fromHtml(model!!.content.toString(), imageGetter, null)
                        msg.what = 0x101
                        msg.obj = test
                        handler!!.sendMessage(msg)
                    }
                })
                t.start()*/
        /*if (model!!.thumbnail.equals("")) {
            zixun_iv_tu!!.visibility = View.GONE
        } else {
            Glide.with(this@ZiXunDetailActivity).load(url().auth_api + model!!.author_img).error(R.mipmap.error_img_big).into(zixun_iv_tu)
        }*/
        main_sv.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        refresh()
    }

    override fun onDestroy() {
        super.onDestroy()
        OkGo.getInstance().cancelAll()//取消所有网络请求
    }

    var is_tui = false
    fun refresh() {
        val cid = intent.getStringExtra("cid")
        is_dy = intent.getBooleanExtra("is_dy", false)
        OkGo.post(url().public_api + "get_phone_news_detail")
                .params("id", cid)// 文字ID
                .execute(object : JsonCallback<LzyResponse<ZiXunModel>>() {
                    override fun onSuccess(t: LzyResponse<ZiXunModel>, call: okhttp3.Call?, response: okhttp3.Response?) {
                        if (t.code == 0) {
                            model = t.data
                            setValue()
                            total_rl.visibility = View.VISIBLE
                        } else {
                            toast(t.msg.toString())
                        }
                    }

                    override fun onError(call: okhttp3.Call?, response: okhttp3.Response?, e: Exception?) {
                        toast(common().toast_error(e!!))
                    }
                })
    }

    override fun initEvents() {

    }
}
