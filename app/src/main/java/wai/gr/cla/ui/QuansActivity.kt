package wai.gr.cla.ui

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.View
import com.lzy.okgo.OkGo
import kotlinx.android.synthetic.main.activity_quan_list.*
import okhttp3.Call
import okhttp3.Response
import wai.gr.cla.R
import wai.gr.cla.base.BaseActivity
import wai.gr.cla.callback.JsonCallback
import wai.gr.cla.method.CommonAdapter
import wai.gr.cla.method.CommonViewHolder
import wai.gr.cla.method.common
import wai.gr.cla.model.QuanModel
import wai.gr.cla.model.LzyResponse
import wai.gr.cla.model.url
import java.util.*
import android.content.Context.CLIPBOARD_SERVICE
import android.text.ClipboardManager


class QuansActivity : BaseActivity() {
    override fun setLayout(): Int {
        return R.layout.activity_quan_list
    }

    internal var kc1_list: ArrayList<QuanModel> = ArrayList()//购买的课程列表
    var kc1_adapter: CommonAdapter<QuanModel>? = null//购买的课程
    override fun initViews() {
        context = this
        toolbar.setLeftClick { finish() }
        kc1_adapter = object : CommonAdapter<QuanModel>(this, kc1_list, R.layout.item_coupon) {
            override fun convert(holder: CommonViewHolder, model: QuanModel, position: Int) {
                holder.setText(R.id.price_tv, "￥" + model.coupon_price)
                holder.setText(R.id.time_tv, "有效时间："+model.expiration_date)
                holder.setText(R.id.code_tv,model.coupon_code)
                if(model.coupon_price.toDouble()>=50.0){
                    holder.setBG(R.id.main_rl, R.mipmap.bg_kuaiguoqi)
                }else{
                    holder.setBG(R.id.main_rl, R.mipmap.bg_weishiyong)
                }
                when (model.expiration_status) {
                    0 -> {//0   未过期
                        holder.setVisible(R.id.tag_iv, false)
                    }
                    1 -> {//快过期
                        holder.setImageResource(R.id.tag_iv, R.mipmap.kuaidaoqi)
                    }
                    2 -> {//已过期
                        holder.setImageResource(R.id.tag_iv, R.mipmap.yiguoqi)
                    }
                    3 -> {//已使用
                        holder.setImageResource(R.id.tag_iv, R.mipmap.yishiyong)
                        holder.setBG(R.id.main_rl, R.mipmap.bg_yishiyong)
                    }
                }

            }
        }
        main_lv.adapter = kc1_adapter
        main_srl.setOnRefreshListener {
            load_data()
        }
        main_lv.setOnItemClickListener { adapterView, view, i, l ->
            val cm = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            // 将文本内容放到系统剪贴板里。
            cm.setText(kc1_list[i].coupon_code)
            toast("复制成功")
        }
        load_data()
    }

    companion object {
        var context: QuansActivity? = null
    }

    fun load_data() {
        OkGo.post(url().auth_api + "get_coupon_list")
                .execute(object : JsonCallback<LzyResponse<ArrayList<QuanModel>>>() {
                    override fun onSuccess(model: LzyResponse<ArrayList<QuanModel>>, call: Call?, response: Response?) {
                        kc1_list.clear()
                        kc1_list.addAll(model.data!!)
                        kc1_adapter!!.refresh(kc1_list)
                        main_srl.isRefreshing = false
                        main_lv.getIndex(1, 10, 1)
                        if (kc1_list!!.size == 0) {
                            error_ll.visibility = View.VISIBLE;
                            main_lv.visibility = View.GONE;
                        } else {
                            error_ll.visibility = View.GONE;
                            main_lv.visibility = View.VISIBLE;
                        }
                    }

                    override fun onError(call: Call?, response: Response?, e: Exception?) {
                        toast(common().toast_error(e!!))
                        main_srl.isRefreshing = false
                    }
                })
    }

    override fun initEvents() {

    }
}