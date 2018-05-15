package wai.gr.cla.ui

import android.app.Activity
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import com.tencent.connect.share.QQShare
import com.tencent.connect.share.QzoneShare
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError

import wai.gr.cla.R
import wai.gr.cla.base.App
import android.R.id.message
import android.R.attr.description
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXTextObject
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.umeng.socialize.ShareAction
import com.umeng.socialize.ShareContent
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import com.umeng.socialize.media.UMWeb
import java.io.BufferedOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream


/**
 * 分享popuwindow直接New使用
 * Created by Administrator on 2017/5/15.
 */

class SharedPopuWindowActivity//view可随便传
(private val context: Activity, private val canView: View, id: String, t: String?, content: String?, img_url: String?, is_zx: Boolean) : PopupWindow(context) {
    private val myView: View
    private var alpha = 1f
    internal var mHandler: Handler
    internal var mTencent: Tencent? = null //qq主操作对象
    var api: IWXAPI? = null
    var zx_url = wai.gr.cla.model.url().normal + "information/information_detail_phone&from=groupmessage&id="
    var sp_url = wai.gr.cla.model.url().normal + "course/course_phone_view&id="
    var url = ""
    var content = content
    var title = t
    var img = ""
    var img_bitmap: Bitmap? = null

    /**
     * 初始化数据
     * */
    init {
        if (is_zx) {
            url = zx_url + id
        } else {
            url = sp_url + id
        }
        title = t
        api = WXAPIFactory.createWXAPI(context, App.wx_id, false)
        api!!.registerApp(App.wx_id)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        myView = inflater.inflate(R.layout.shared_popup_window, null)
        contentView = myView
        this.width = ViewGroup.LayoutParams.MATCH_PARENT
        this.height = ViewGroup.LayoutParams.WRAP_CONTENT
        mHandler = object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    1 -> backgroundAlpha(msg.obj as Float)
                }
            }
        }
        if (TextUtils.isEmpty(img_url)) {
            img = "http://test.guanrenjiaoyu.com/images/touxiang.png"
        } else {
            img = wai.gr.cla.model.url().total + img_url
        }
        mTencent = Tencent.createInstance(App.qq_id, context)
        Thread(Runnable {
//            img_bitmap = Glide.with(context)
//                    .load(img)
//                    .asBitmap() //必须
//                    .centerCrop()
//                    .into(200, 200)
//                    .get()
        }).start()
        showWindow()
    }

    fun compressImage(image: Bitmap): Bitmap {

        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        var options = 100
        while (baos.toByteArray().size / 1024 > 1024 / 10) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset()//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos)//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10//每次都减少10
        }
        val isBm = ByteArrayInputStream(baos.toByteArray())//把压缩后的数据baos存放到ByteArrayInputStream中
        val bitmap = BitmapFactory.decodeStream(isBm, null, null)//把ByteArrayInputStream数据生成图片
        return bitmap
    }

    //popuwindow
    internal fun bottomwindow() {

        if (this != null && this.isShowing) {
            return
        }
        //点击空白处时，隐藏掉pop窗口
        this.isFocusable = true
        this.setBackgroundDrawable(BitmapDrawable())
        //添加弹出、弹入的动画
        this.animationStyle = R.style.Popupwindow
        val location = IntArray(2)
        canView.getLocationOnScreen(location)
        this.showAtLocation(canView, Gravity.LEFT or Gravity.BOTTOM, 0, 0)
        //添加按键事件监听
        setButtonListeners()
        //添加pop窗口关闭事件，主要是实现关闭时改变背景的透明度
        this.setOnDismissListener(poponDismissListener())
        backgroundAlpha(1f)
    }


    private fun qq(flag: Boolean) {
        if (mTencent!!.isSessionValid && mTencent!!.openId == null) {
            Toast.makeText(context, "您还未安装QQ", Toast.LENGTH_SHORT).show()
        }
        val params = Bundle()
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);//点击跳转的url
        //params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_AUDIO)
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title)
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, content)
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, img)
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, context.getString(R.string.app_name))
        if (flag) {
            params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN)
        } else {
            params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE)
        }
        mTencent!!.shareToQQ(context, params, object : IUiListener {
            override fun onComplete(o: Any) {

            }

            override fun onError(uiError: UiError) {

            }

            override fun onCancel() {

            }
        })
    }

    /**
     * 返回或者点击空白位置的时候将背景透明度改回来
     */
    internal inner class poponDismissListener : PopupWindow.OnDismissListener {

        override fun onDismiss() {
            // TODO Auto-generated method stub
            Thread(Runnable {
                //此处while的条件alpha不能<= 否则会出现黑屏
                while (alpha < 1f) {
                    try {
                        Thread.sleep(4)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }

                    Log.d("HeadPortrait", "alpha:" + alpha)
                    val msg = mHandler.obtainMessage()
                    msg.what = 1
                    println("alpha:" + alpha)
                    alpha += 0.01f
                    msg.obj = alpha
                    mHandler.sendMessage(msg)
                }
            }).start()
        }
    }

    //在调用弹出的方法后，开启一个子线程
    fun showWindow() {

        Thread(Runnable {
            while (alpha > 0.4f) {
                try {
                    //4是根据弹出动画时间和减少的透明度计算
                    Thread.sleep(4)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                val msg = mHandler.obtainMessage()
                msg.what = 1
                //每次减少0.01，精度越高，变暗的效果越流畅
                alpha -= 0.01f
                println("-----alpha:" + alpha)
                msg.obj = alpha
                mHandler.sendMessage(msg)
            }
        }).start()
        bottomwindow()
    }

    /**
     * 设置添加屏幕的背景透明度

     * @param bgAlpha
     */
    fun backgroundAlpha(bgAlpha: Float) {
        val lp = context.window.attributes
        lp.alpha = bgAlpha //0.0-1.0
        context.window.attributes = lp
        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }


    //给界面添加点击事件
    fun setButtonListeners() {
        val ivqq = myView.findViewById<ImageView>(R.id.share_iv_qq) as ImageView
        val ivqqkong = myView.findViewById<ImageView>(R.id.share_iv_qqkong) as ImageView
        val ivwei = myView.findViewById<ImageView>(R.id.share_iv_weixin) as ImageView
        val ivpeng = myView.findViewById<ImageView>(R.id.share_iv_peng) as ImageView
        val cancel = myView.findViewById<TextView>(R.id.share_btn_cancel) as TextView
        /**
         * 分享到qq聊天
         * */
        ivqq.setOnClickListener {
            qq(false)
        }
        /**
         * 分享到qq空间
         * */
        ivqqkong.setOnClickListener {
            //shareToQzone()
            qq(true)
        }
        /**
         * 分享到微信聊天
         * */
        ivwei.setOnClickListener {
            //方法中设置asBitmap可以设置回调类型
            var web = WXWebpageObject()
            web.webpageUrl = url
            var msg = WXMediaMessage(web)
            msg.title = title
            msg.description = content
            msg.setThumbImage(img_bitmap)
            var req = SendMessageToWX.Req()
            req.transaction = buildTransaction("webpage")
            req.message = msg
            req.scene = SendMessageToWX.Req.WXSceneSession
            api!!.sendReq(req)
            //msg.thumbData = bmpToByteArray(BitmapFactory.decodeResource(context.resources, R.mipmap.icon_font))
        }
        /**
         * 分享到微信朋友圈
         * */
        ivpeng.setOnClickListener {
            //分享到微信朋友圈
            var web = WXWebpageObject()
            web.webpageUrl = url
            var msg = WXMediaMessage(web)
            msg.title = title
            msg.description = content
            msg.setThumbImage(img_bitmap)
            var req = SendMessageToWX.Req()
            req.transaction = buildTransaction("webpage")
            req.message = msg
            req.scene = SendMessageToWX.Req.WXSceneTimeline
            api!!.sendReq(req)
        }
        /**
         * 点击取消关闭popwindow
         * */
        cancel.setOnClickListener {
            if (this@SharedPopuWindowActivity != null && this@SharedPopuWindowActivity.isShowing) {
                this@SharedPopuWindowActivity.dismiss()
            }
        }
    }

    /**
     * 构建一个唯一标志

     * @param type 分享的类型分字符串
     * *
     * @return 返回唯一字符串
     */
    fun buildTransaction(type: String?): String {
        return if (type == null) System.currentTimeMillis().toString() else type + System.currentTimeMillis()
    }
}
