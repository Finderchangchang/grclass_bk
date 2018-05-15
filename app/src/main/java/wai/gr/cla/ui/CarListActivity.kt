package wai.gr.cla.ui

import android.content.Intent
import android.text.TextUtils
import android.view.View
import com.lzy.okgo.OkGo
import kotlinx.android.synthetic.main.activity_car_list.*
import okhttp3.Call
import okhttp3.Response

import wai.gr.cla.R
import wai.gr.cla.base.BaseActivity
import wai.gr.cla.callback.JsonCallback
import wai.gr.cla.method.*
import wai.gr.cla.model.*
import java.util.*

class CarListActivity : BaseActivity() {
    override fun setLayout(): Int {
        return R.layout.activity_car_list
    }

    internal var kc1_list: ArrayList<CarModel> = ArrayList()//购买的课程列表
    var kc1_adapter: CommonAdapter<CarModel>? = null//购买的课程
    override fun initViews() {
        toolbar.setLeftClick { finish() }
        qq_tv.text="如有任何疑问，请咨询客服QQ"+ Utils.getCache("qq")
        context = this
        kc1_adapter = object : CommonAdapter<CarModel>(this, kc1_list, R.layout.item_car) {
            override fun convert(holder: CommonViewHolder, model: CarModel, position: Int) {
                holder.setText(R.id.title_tv, model.course_title)
                holder.setText(R.id.price_tv, "￥" + model.price)
                holder.setGImage(R.id.total_iv, url().total + model.thumbnail)
                if (model.isChecked) {
                    holder.setImageResource(R.id.check_iv, R.mipmap.check_s)
                } else {
                    holder.setImageResource(R.id.check_iv, R.mipmap.check_n)
                }
                if (model.is_full_cut == 1) {
                    holder.setVisible(R.id.jm_tv, false)
                }else{
                    holder.setVisible(R.id.jm_tv, true)
                }
                holder.setOnClickListener(R.id.check_iv) {
                    model.isChecked = !model.isChecked
                    if (model.isChecked) {
                        holder.setImageResource(R.id.check_iv, R.mipmap.check_s)
                    } else {
                        holder.setImageResource(R.id.check_iv, R.mipmap.check_n)
                    }
                    refresh_data()
                }
            }
        }
        main_lv.adapter = kc1_adapter
        toolbar.setRightClick {
            tool_click = !tool_click
            if (tool_click) {//编辑模式
                toolbar.setRighttv("完成")
                refresh_data()
            } else {//完成模式
                toolbar.setRighttv("编辑")
                refresh_data()
            }
        }
        load_data()
        var del_id = ""
        check_all_rb.setOnClickListener {
            check_all = !check_all
            check_all_rb.isChecked = check_all

            for (model in kc1_list) {
                model.isChecked = check_all
                del_id += model.course_id.toString() + ","
            }
            refresh_data()
        }
        pay_tv.setOnClickListener {
            when (pay_tv.text) {
                "删除" -> {
                    for (model in kc1_list) {
                        if (model.isChecked) {
                            del_id += model.id.toString() + ","
                        }
                    }
                    del_id(del_id.substring(0, del_id.length - 1))
                }
                "去结算" -> {

                }
                else -> {
                    var lzy = LzyResponse<String>()
                    var kk_list: ArrayList<CarModel> = ArrayList()//购买的课程列表
                    for (model in kc1_list) {
                        if (model.isChecked) {
                            kk_list.add(model)
                        }
                    }
                    lzy.car = kk_list
                    startActivity(Intent(this@CarListActivity, ConfimOrderActivity::class.java).putExtra("model", lzy))
                }
            }
        }
    }

    companion object {
        var context: CarListActivity? = null
    }

    var tool_click = false//true:点击false：完成
    var check_all = false//true:选中全部
    var checked_size = 0
    var checked_price = 0.0
    fun del_id(id: String) {
        OkGo.post(url().auth_api + "delete_shopcar")
                .params("id", id)
                .execute(object : JsonCallback<LzyResponse<String>>() {
                    override fun onSuccess(model: LzyResponse<String>, call: okhttp3.Call?, response: okhttp3.Response?) {
                        if (model.code == 0) {
                            toast("删除成功")
                            load_data()
                        }
                    }

                    override fun onError(call: Call?, response: Response?, e: Exception?) {
                        toast(common().toast_error(e!!))
                    }
                })
    }

    //刷新价格
    fun refresh_data() {
        checked_size = 0
        checked_price = 0.0
        for (model in kc1_list) {
            if (model.isChecked) {
                checked_size++
                if (!TextUtils.isEmpty(model.price))
                    checked_price += model.price.toDouble()
            }
        }
        if (kc1_list.size > 0) {
            check_all = checked_size == kc1_list.size
            check_all_rb.isChecked = check_all
        } else {
            check_all_rb.isChecked = false
        }
        if (tool_click) {
            pay_tv.text = "删除"
            hj_tv.visibility = View.GONE
            total_price_tv.visibility = View.GONE
        } else {
            if (checked_size == 0) {
                pay_tv.text = "去结算"
            } else {
                pay_tv.text = "去结算($checked_size)"
            }
            hj_tv.visibility = View.VISIBLE
            total_price_tv.visibility = View.VISIBLE
            var price = convert(checked_price)
            total_price_tv.text = "￥$price"
            if(price==0.0){
                yh_price_tv.text="暂无消费金额"
            }else {
                man_jian(price)
            }
        }

        kc1_adapter!!.refresh(kc1_list)
    }

    fun convert(value: Double): Double {
        val l1 = Math.round(value * 100)   //四舍五入
        return l1 / 100.0
    }

    fun load_data() {
        OkGo.post(url().auth_api + "get_shopcar_infos")
                .execute(object : JsonCallback<LzyResponse<ArrayList<CarModel>>>() {
                    override fun onSuccess(model: LzyResponse<ArrayList<CarModel>>, call: okhttp3.Call?, response: okhttp3.Response?) {
                        kc1_list.clear()
                        kc1_list.addAll(model.data!!)
                        kc1_adapter!!.refresh(kc1_list)
                        main_lv.getIndex(1, 10, 1)
                        if (kc1_list!!.size == 0) {
                            error_ll.visibility = View.VISIBLE;
                            main_lv.visibility = View.GONE;
                        } else {
                            error_ll.visibility = View.GONE;
                            main_lv.visibility = View.VISIBLE;
                        }
                        refresh_data()
                    }

                    override fun onError(call: Call?, response: Response?, e: Exception?) {
                        toast(common().toast_error(e!!))
                    }
                })
    }
    fun man_jian(price:Double){
        OkGo.post(url().auth_api + "get_subtract_price")
                .params("price", price)
                .execute(object : JsonCallback<LzyResponse<QuanModel>>() {
                    override fun onSuccess(model: LzyResponse<QuanModel>, call: okhttp3.Call?, response: okhttp3.Response?) {
                        if (model.code == 0) {
                            yh_price_tv.text="优惠：满"+model.data!!.amount+"减"+model.data!!.reduce_amount
                        } else {
                            yh_price_tv.text="暂无减免"
                        }
                    }

                    override fun onError(call: Call?, response: Response?, e: Exception?) {
                        yh_price_tv.text="暂无减免"
                    }
                })
    }
    override fun initEvents() {
    }
}
