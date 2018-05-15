package wai.gr.cla.ui

import android.app.Activity
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.lzy.okgo.OkGo

import wai.gr.cla.R
import wai.gr.cla.callback.JsonCallback
import wai.gr.cla.model.LzyResponse
import wai.gr.cla.model.MyBussinessModel

import android.text.TextUtils
import android.widget.*
import com.umeng.socialize.utils.Log.toast
import wai.gr.cla.method.Utils
import wai.gr.cla.method.common
import wai.gr.cla.model.key
import wai.gr.cla.model.url

/**
 * Created by Administrator on 2017/5/18.
 */

class AddChuangYePopuwindow(val context: Activity, private val canView: View) : PopupWindow(context) {
    private val myView: View
    private var alpha = 1f
    internal var mHandler: Handler

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        myView = inflater.inflate(R.layout.chuangye_popuwindow, null)
        contentView = myView
        this.width = context.windowManager.defaultDisplay.width - 100
        this.height = ViewGroup.LayoutParams.WRAP_CONTENT

        mHandler = object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    1 -> backgroundAlpha(msg.obj as Float)
                }
            }
        }
        showWindow()
    }

    //popuwindow
    internal fun bottomwindow() {
        if (this.isShowing) {
            return
        }
        //点击空白处时，隐藏掉pop窗口
        this.isFocusable = true
        this.setBackgroundDrawable(BitmapDrawable())

        //添加弹出、弹入的动画
        this.animationStyle = R.style.Popupwindow
        val location = IntArray(2)
        canView.getLocationOnScreen(location)
        this.showAtLocation(canView, Gravity.CENTER or Gravity.CENTER, 0, 0)
        //添加按键事件监听
        setButtonListeners()
        //添加pop窗口关闭事件，主要是实现关闭时改变背景的透明度
        this.setOnDismissListener {
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
                    println("alpha333333333:" + alpha)
                    alpha += 0.01f
                    msg.obj = alpha
                    mHandler.sendMessage(msg)
                }
            }).start()
        }
        backgroundAlpha(1f)
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
    private fun setButtonListeners() {
        val etname = myView.findViewById<EditText>(R.id.add_chuang_name)
        val etmobile = myView.findViewById<EditText>(R.id.chuang_et_mobile)
        val etprojectname = myView.findViewById<EditText>(R.id.chaung_et_pname)
        val etremark = myView.findViewById<EditText>(R.id.chuang_et_remark)
        val btnSave = myView.findViewById<Button>(R.id.chuang_btn_save)
        var btnExit = myView.findViewById<Button>(R.id.chuang_btn_exit)
        btnExit.setOnClickListener {
            if (this@AddChuangYePopuwindow.isShowing) {
                this@AddChuangYePopuwindow.dismiss()
            }
        }
        btnSave.setOnClickListener {
            if (TextUtils.isEmpty(etname.text.toString().trim())) {
                Toast.makeText(context, "姓名不能为空", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(etmobile.text.toString().trim()) || !Utils.isMobileNo(etmobile.text.toString().trim())) {
                Toast.makeText(context, "电话格式不正确", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(etprojectname.text.toString().trim())) {
                Toast.makeText(context, "项目名称不能为空", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(etremark.text.toString().trim())) {
                Toast.makeText(context, "项目简介不能为空", Toast.LENGTH_SHORT).show()
            } else {
                OkGo.post(url().auth_api + "add_my_business")
                        .params("name", etname.text.toString().trim())// 请求方式和请求url
                        .params("tel", etmobile.text.toString().trim())// 请求方式和请求url
                        .params("subject", etprojectname.text.toString().trim())// 请求方式和请求url
                        .params("content", etremark.text.toString().trim())// 请求方式和请求url
                        .execute(object : JsonCallback<LzyResponse<MyBussinessModel>>() {
                            override fun onSuccess(t: LzyResponse<MyBussinessModel>, call: okhttp3.Call?, response: okhttp3.Response?) {
                                if (t.code == 0) {
                                    canView.visibility = View.GONE
                                    if (this@AddChuangYePopuwindow.isShowing) {
                                        this@AddChuangYePopuwindow.dismiss()
                                    }
                                    Utils.putCache(key.KEY_IS_POST, "1")
                                }
                                toast(context, t.msg.toString())
                            }

                            override fun onError(call: okhttp3.Call?, response: okhttp3.Response?, e: Exception?) {
                                toast(context, common().toast_error(e!!))
                            }
                        })
            }
        }
    }
}
