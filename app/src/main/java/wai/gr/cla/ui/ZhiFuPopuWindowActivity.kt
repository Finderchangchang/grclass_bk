package wai.gr.cla.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.opengl.Visibility
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import com.google.gson.Gson

import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.request.PostRequest
import okhttp3.Call
import okhttp3.Response

import wai.gr.cla.R
import wai.gr.cla.base.BaseActivity
import wai.gr.cla.callback.JsonCallback
import wai.gr.cla.method.WxUtil
import wai.gr.cla.model.*
import com.tsy.sdk.pay.weixin.WXPay
import wai.gr.cla.base.App
import com.tsy.sdk.pay.alipay.Alipay
import wai.gr.cla.method.common


/**
 * 支付popuwindow
 * Created by Administrator on 2017/5/15.
 * type-
 */
class ZhiFuPopuWindowActivity
(val det: DetailPlayer?, val card: CardDetailActivity?, cons: Context, var canView: View,
 type: Boolean, course: String, price_list: List<PriceModel>, cid: Int) : PopupWindow(cons) {
    private var myView: View? = null
    private var alpha = 1f//透明度
    private var SelectPosition = 1//默认选中第二个6个月
    private var Type = true//true为显示选择月份，false为隐藏
    private var courseid = ""
    internal var mHandler: Handler? = null
    var cid: Int = 0
    private var price_list: List<PriceModel>? = null
    var check_item = 2//1：冠币，2:微信，3：支付宝
    var conte: Context? = null

    companion object {}

    /**
     * 初始化数据
     * */
    init {
        this.courseid = course
        this.Type = type
        this.price_list = price_list
        this.cid = cid
        val inflater = cons.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        myView = inflater.inflate(R.layout.zhifu_popuwindow, null)
        var gbline = myView!!.findViewById<TextView>(R.id.tv_linejifen) as TextView
        var gb = myView!!.findViewById<LinearLayout>(R.id.zhifu_ll_gb) as LinearLayout
        conte = cons
        if (Type) {//是否允许冠币支付（允许）
            gbline.visibility = View.VISIBLE
            gb.visibility = View.VISIBLE
        } else {
            gbline.visibility = View.VISIBLE
            gb.visibility = View.VISIBLE
        }

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
        //showWindow()
    }

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
                    val msg = mHandler!!.obtainMessage()
                    msg.what = 1
                    alpha += 0.01f
                    msg.obj = alpha
                    mHandler!!.sendMessage(msg)
                }
            }).start()
        }
        backgroundAlpha(1f)
    }

    /**
     * 在调用弹出的方法后，开启一个子线程
     * */
    fun showWindow() {

        Thread(Runnable {
            while (alpha > 0.4f) {
                try {
                    //4是根据弹出动画时间和减少的透明度计算
                    Thread.sleep(4)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                val msg = mHandler!!.obtainMessage()
                msg.what = 1
                //每次减少0.01，精度越高，变暗的效果越流畅
                alpha -= 0.01f
                msg.obj = alpha
                mHandler!!.sendMessage(msg)
            }
        }).start()
        bottomwindow()
    }

    /**
     * 设置添加屏幕的背景透明度

     * @param bgAlpha
     */
    fun backgroundAlpha(bgAlpha: Float) {
        if (card != null && det != null) {
            if (Type) {
                val lp = card!!.window.attributes
                lp.alpha = bgAlpha //0.0-1.0
                card!!.window.attributes = lp
            } else {
                val lp = det!!.window.attributes
                lp.alpha = bgAlpha //0.0-1.0
                det.window.attributes = lp
            }
        }
        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    var ok: PostRequest? = null
    /**
     * 页面初始化页面赋值
     * */
    fun setButtonListeners() {
        val ivClose = myView!!.findViewById<ImageView>(R.id.zhifu_iv_close) as ImageView
        val btnSave = myView!!.findViewById<Button>(R.id.zhifu_btn_save) as Button
        val llwei = myView!!.findViewById<LinearLayout>(R.id.zhifu_ll_weixin) as LinearLayout
        val llzhifubao = myView!!.findViewById<LinearLayout>(R.id.zhifu_ll_bao) as LinearLayout
        val ivwei = myView!!.findViewById<ImageView>(R.id.zhifu_iv_wei) as ImageView
        val llgb = myView!!.findViewById<LinearLayout>(R.id.zhifu_ll_gb) as LinearLayout
        val ivgb = myView!!.findViewById<ImageView>(R.id.zhifu_iv_gb) as ImageView
        val ivbao = myView!!.findViewById<ImageView>(R.id.zhifu_iv_bao) as ImageView
        val llleft = myView!!.findViewById<LinearLayout>(R.id.zhifu_ll_left) as LinearLayout
        val tvlefttop = myView!!.findViewById<TextView>(R.id.left_tv_top) as TextView
        val tvleftbottom = myView!!.findViewById<TextView>(R.id.left_tv_botton) as TextView
        val llcenter = myView!!.findViewById<LinearLayout>(R.id.zhifu_ll_center) as LinearLayout
        val tv_center_top = myView!!.findViewById<TextView>(R.id.tv_center_top) as TextView
        val tv_center_bottom = myView!!.findViewById<TextView>(R.id.tv_center_bottom) as TextView
        val tv_right_top = myView!!.findViewById<TextView>(R.id.tv_right_top) as TextView
        val tv_right_bottom = myView!!.findViewById<TextView>(R.id.tv_right_bottom) as TextView
        val price_ll = myView!!.findViewById<LinearLayout>(R.id.price_ll) as LinearLayout
        val zhifu_ll_right = myView!!.findViewById<LinearLayout>(R.id.zhifu_ll_right) as LinearLayout

        /**
         * 关闭按钮点击事件
         * */
        ivClose.setOnClickListener { closePop() }
        /**
         * 页面赋值，选择订阅的月份
         * */
        if (price_list!!.size > 2) {
            zhifu_ll_right.visibility = View.VISIBLE
            tv_right_top.text = price_list!![2].months.toString() + "个月"
            tv_right_bottom.text = "￥" + price_list!![2].price.toString()
        }
        if (price_list!!.size > 1) {
            tv_center_top.text = price_list!![1].months.toString() + "个月"
            tv_center_bottom.text = "￥" + price_list!![1].price.toString()
        }
        if (price_list!!.size > 0) {
            tvlefttop.text = price_list!![0].months.toString() + "个月"
            tvleftbottom.text = "￥" + price_list!![0].price.toString()
        }
        /**
         * 选择微信支付
         * */
        llwei.setOnClickListener {
            check_item = 2
            ivwei.setImageResource(R.mipmap.purchasecourse_selected)
            ivbao.setImageResource(R.mipmap.purchasecourse_choice)
            ivgb.setImageResource(R.mipmap.purchasecourse_choice)
        }
        /**
         * 选择支付宝支付
         * */
        llzhifubao.setOnClickListener {
            check_item = 3
            ivbao.setImageResource(R.mipmap.purchasecourse_selected)
            ivwei.setImageResource(R.mipmap.purchasecourse_choice)
            ivgb.setImageResource(R.mipmap.purchasecourse_choice)
        }
        /**
         * 选择冠币支付
         * */
        llgb.setOnClickListener {
            check_item = 1
            ivbao.setImageResource(R.mipmap.purchasecourse_choice)
            ivwei.setImageResource(R.mipmap.purchasecourse_choice)
            ivgb.setImageResource(R.mipmap.purchasecourse_selected)
            Toast.makeText(conte, "冠币支付是普通金额的10倍", Toast.LENGTH_SHORT).show()
        }
        /**
         *type为true，显示价格内容。false-直接调起支付
         * */
        if (Type) {
            price_ll.visibility = View.VISIBLE
            /**
             * 第一个点击事件
             * */
            llleft.setOnClickListener {
                //切换选中状态
                changeTextWhite(llleft, tvlefttop, tvleftbottom)
                when (SelectPosition) {
                    2 -> changeTextCai(llcenter, tv_center_top, tv_center_bottom)
                    3 -> changeTextCai(zhifu_ll_right, tv_right_top, tv_right_bottom)
                }
                SelectPosition = 1
            }
            /**
             * 第二个点击事件
             * */
            llcenter.setOnClickListener {
                //切换选中状态
                changeTextWhite(llcenter, tv_center_top, tv_center_bottom)
                when (SelectPosition) {
                    1 -> changeTextCai(llleft, tvlefttop, tvleftbottom)
                    3 -> changeTextCai(zhifu_ll_right, tv_right_top, tv_right_bottom)
                }
                SelectPosition = 2
            }
            /**
             * 第三个点击事件
             * */
            zhifu_ll_right.setOnClickListener {
                //切换选中状态
                changeTextWhite(zhifu_ll_right, tv_right_top, tv_right_bottom)
                when (SelectPosition) {
                    1 -> changeTextCai(llleft, tvlefttop, tvleftbottom)
                    2 -> changeTextCai(llcenter, tv_center_top, tv_center_bottom)
                }
                SelectPosition = 3
            }
        } else {
            price_ll.visibility = View.GONE
        }
        /**
         * 确认支付按钮，1.生成订单，2.根据订单id返回支付需要请求的参数
         * */
        btnSave.setOnClickListener {
            /**
             * true:订阅老师，false：购买视频
             * */
            if (check_item == 1) {
                var dialog = AlertDialog.Builder(conte)
                dialog.setTitle("提示")
                dialog.setMessage("确定要支付"+price_list!![SelectPosition - 1].price!!.toDouble()*10+"冠币")
                dialog.setPositiveButton("取消") { dialog, which ->

                }
                dialog.setNegativeButton("确定") { dialog, which ->
                    pay()
                }
                dialog.show()
            }else{
                pay()
            }

        }
    }

    fun pay() {
        if (Type) {
            ok = OkGo.post(url().auth_api + "create_teacher_course_order")
                    .params("teacher_course_id", courseid)
                    .params("months", price_list!![SelectPosition - 1].months)
        } else {
            ok = OkGo.post(url().auth_api + "create_course_order").params("course_id", courseid)
        }
        ok!!.execute(object : JsonCallback<LzyResponse<TradeModel>>() {
            override fun onSuccess(model: LzyResponse<TradeModel>, call: okhttp3.Call?, response: okhttp3.Response?) {
                if (model.code == 0) {
                    save_pay(model.data!!.id)
                } else {
                    if (Type) {
                        Toast.makeText(conte, "生成预订单失败", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(conte, "生成预订单失败", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onError(call: Call?, response: Response?, e: Exception?) {
                if (Type) {
                    Toast.makeText(conte, common().toast_error(e!!), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(conte, common().toast_error(e!!), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    /**
     * 关闭当前页面
     * */
    fun closePop() {
        if (this@ZhiFuPopuWindowActivity != null && this@ZhiFuPopuWindowActivity.isShowing) {
            this@ZhiFuPopuWindowActivity.dismiss()
        }
    }

    /**
     * 根据订单id生成预订单
     * */
    fun save_pay(id: Int) {
        /**
         * 调起微信支付
         * */
        when (check_item) {
            1 -> {
//                if (Type) {
//                    card!!.removeFirstObjectInAdapter()//刷新数据
//                } else {
//                    det!!.loadData()//刷新页面数据
//                }
//                closePop()//关闭当前popwindow
                OkGo.post(url().pay_api + "pay_use_guanbi")
                        .params("id", id)
                        .execute(object : JsonCallback<LzyResponse<PayModel>>() {
                            override fun onSuccess(model: LzyResponse<PayModel>, call: okhttp3.Call?, response: okhttp3.Response?) {
                                if (model.code == 0) {
                                    if (card != null && det != null) {
                                        if (Type) {//不用刷新卡片
                                            card!!.loadData()//刷新数据
                                        } else {
                                            det!!.loadData()//刷新页面数据
                                        }
                                    }
                                    closePop()//关闭当前popwindow
                                }
                                Toast.makeText(conte, model.msg, Toast.LENGTH_SHORT).show()
                            }

                            override fun onError(call: Call?, response: Response?, e: Exception?) {
                                Toast.makeText(conte, common().toast_error(e!!), Toast.LENGTH_SHORT).show()
                            }
                        })
            }
            2 ->
                OkGo.post(url().wx_api + "unifiedorder")
                        .params("id", id)
                        .execute(object : JsonCallback<LzyResponse<PayModel>>() {
                            override fun onSuccess(model: LzyResponse<PayModel>, call: okhttp3.Call?, response: okhttp3.Response?) {
                                if (model.code == 0) {
                                    val data = Gson().toJson(model.data)
                                    val wx_appid = App.wx_id     //替换为自己的 appid
                                    WXPay.init(conte, wx_appid)      //要在支付前调用
                                    WXPay.getInstance().doPay(data, object : WXPay.WXPayResultCallBack {
                                        override fun onSuccess() {
                                            check_pay_status(id)
                                        }

                                        override fun onError(error_code: Int) {
                                            when (error_code) {
                                                WXPay.NO_OR_LOW_WX -> Toast.makeText(conte, "未安装微信或微信版本过低", Toast.LENGTH_SHORT).show()

                                                WXPay.ERROR_PAY_PARAM -> Toast.makeText(conte, "参数错误", Toast.LENGTH_SHORT).show()

                                                WXPay.ERROR_PAY -> Toast.makeText(conte, "支付失败", Toast.LENGTH_SHORT).show()
                                            }
                                        }

                                        override fun onCancel() {
                                            Toast.makeText(conte, "支付取消", Toast.LENGTH_SHORT).show()
                                        }
                                    })
                                } else {
                                    Toast.makeText(conte, "生成预订单失败", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onError(call: Call?, response: Response?, e: Exception?) {
                                Toast.makeText(conte, common().toast_error(e!!), Toast.LENGTH_SHORT).show()
                            }
                        })
            3 ->
                /**
                 * 调起支付宝支付
                 * */
                OkGo.post(url().normal + "alipay/create_response")
                        .params("id", id)
                        .execute(object : JsonCallback<LzyResponse<String>>() {
                            override fun onSuccess(model: LzyResponse<String>, call: okhttp3.Call?, response: okhttp3.Response?) {
                                if (model.code == 0) {
                                    Alipay(conte, model.data, object : Alipay.AlipayResultCallBack {
                                        override fun onSuccess() {
                                            check_pay_status(id)
                                        }

                                        override fun onDealing() {
                                            Toast.makeText(conte, "支付处理中...", Toast.LENGTH_SHORT).show()
                                        }

                                        override fun onError(error_code: Int) {
                                            when (error_code) {
                                                Alipay.ERROR_RESULT -> Toast.makeText(conte, "支付失败:支付结果解析错误", Toast.LENGTH_SHORT).show()

                                                Alipay.ERROR_NETWORK -> Toast.makeText(conte, "支付失败:网络连接错误", Toast.LENGTH_SHORT).show()

                                                Alipay.ERROR_PAY -> Toast.makeText(conte, "支付错误:支付码支付失败", Toast.LENGTH_SHORT).show()

                                                else -> Toast.makeText(conte, "支付错误", Toast.LENGTH_SHORT).show()
                                            }

                                        }

                                        override fun onCancel() {
                                            Toast.makeText(conte, "支付取消", Toast.LENGTH_SHORT).show()
                                        }
                                    }).doPay()
                                } else {
                                    Toast.makeText(conte, "生成预订单失败", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onError(call: Call?, response: Response?, e: Exception?) {
                                Toast.makeText(conte, common().toast_error(e!!), Toast.LENGTH_SHORT).show()
                            }
                        })
        }

    }

    /**
     * 检测是否支付成功 id：订单状态
     * */
    fun check_pay_status(id: Int) {
        OkGo.post(url().normal + "pay/get_pay_status")
                .params("id", id)
                .execute(object : JsonCallback<LzyResponse<String>>() {
                    override fun onSuccess(model: LzyResponse<String>, call: okhttp3.Call?, response: okhttp3.Response?) {
                        if (model.code == 0) {
                            Toast.makeText(conte, "支付成功", Toast.LENGTH_SHORT).show()
                            if (card != null && det != null) {
                                if (Type) {
                                    card!!.loadData()//刷新数据
                                } else {
                                    det!!.loadData()//刷新页面数据
                                }
                                check_can_draw()
                            }
                            //closePop()//关闭当前popwindow
                        } else {
                            Toast.makeText(conte, model.msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                })
    }

    /**
     * 修改item点击事件
     * */
    fun changeTextWhite(ll: LinearLayout, tv1: TextView, tv2: TextView) {
        tv1.setTextColor(conte!!.resources.getColor(R.color.white))
        tv2.setTextColor(conte!!.resources.getColor(R.color.white))
        ll.background = conte!!.resources.getDrawable(R.drawable.bg_fang_blue)
    }

    /**
     * 修改item点击事件
     * */
    fun changeTextCai(ll: LinearLayout, tv1: TextView, tv2: TextView) {
        tv1.setTextColor(conte!!.resources.getColor(R.color.hei))
        tv2.setTextColor(conte!!.resources.getColor(R.color.huang_bg))
        ll.background = conte!!.resources.getDrawable(R.drawable.bg_card_hui)
    }

    //判断是否能进入抽奖页面
    fun check_can_draw() {
        OkGo.post(url().auth_api + "i_can_draw")
                .execute(object : JsonCallback<LzyResponse<String>>() {
                    override fun onSuccess(model: LzyResponse<String>, call: okhttp3.Call?, response: okhttp3.Response?) {
                        if (model.code == 0) {//抽奖动画
                            conte!!.startActivity(Intent(conte, WebActivity::class.java)
                                    .putExtra("title", "抽奖")
                                    .putExtra("url", url().normal + "course/luckydraw"))
                        }
                    }
                })
    }
}
