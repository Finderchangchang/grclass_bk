package wai.gr.cla.ui

import com.lzy.okgo.OkGo
import kotlinx.android.synthetic.main.activity_add_gb.*
import okhttp3.Call
import okhttp3.Response
import wai.gr.cla.R
import wai.gr.cla.base.BaseActivity
import wai.gr.cla.callback.DialogCallback
import wai.gr.cla.callback.JsonCallback
import wai.gr.cla.method.CommonAdapter
import wai.gr.cla.method.CommonViewHolder
import wai.gr.cla.method.common
import wai.gr.cla.model.*
import java.util.*

/**
 * 冠币列表
 * */
class GBDetailActivity : BaseActivity() {
    internal var kc1_list: MutableList<GBModel> = ArrayList()//购买的课程列表
    var kc1_adapter: CommonAdapter<GBModel>? = null//购买的课程
    var gb = "0"
    override fun setLayout(): Int {
        return R.layout.activity_add_gb
    }

    override fun initViews() {
        toolbar.setLeftClick { finish() }
        OkGo.post(url().auth_api + "get_user_info")
                .execute(object : DialogCallback<LzyResponse<UserModel>>(this) {
                    override fun onSuccess(t: LzyResponse<UserModel>, call: okhttp3.Call?, response: okhttp3.Response?) {
                        if(t.code==0){
                            gb_num_tv.text=t.data!!.guanbi.toString()
                        }
                    }

                    override fun onError(call: Call?, response: Response?, e: Exception?) {
                        super.onError(call, response, e)
                    }
                })
        kc1_adapter = object : CommonAdapter<GBModel>(this, kc1_list, R.layout.item_guanbi) {
            override fun convert(holder: CommonViewHolder, model: GBModel, position: Int) {
                holder.setText(R.id.item_guabi_time, model.cdate)
                holder.setText(R.id.item_guanbi_title, model.comment)
                holder.setText(R.id.item_guanbi_num, model.guanbi)
            }
        }
        main_lv.adapter = kc1_adapter
        main_lv.setInterface {
            page_index++
            load()
        }
    }

    var tj_list = ArrayList<GBModel>()//视频
    var page_index = 1
    override fun initEvents() {
        load()
    }

    fun load() {
        if (page_index == 1) {
            tj_list.clear()
        }
        OkGo.post(url().auth_api + "get_my_guanbi_list")
                .params("page", page_index)// 请求方式和请求url
                .execute(object : JsonCallback<LzyResponse<PageModel<GBModel>>>() {
                    override fun onSuccess(t: LzyResponse<PageModel<GBModel>>, call: okhttp3.Call?, response: okhttp3.Response?) {
                        if (t.code == 0) {
                            if (t.data!!.list != null) {
                                if(page_index==1){
                                    tj_list= ArrayList()
                                }
                                tj_list.addAll(t.data!!.list as ArrayList<GBModel>)
                                kc1_adapter!!.refresh(tj_list)
                                main_lv.getIndex(page_index, 20, tj_list.size)
                            } else {
                                toast("没有更多数据")
                            }
                        } else {
                            toast(t.msg.toString())
                        }
                    }

                    override fun onError(call: Call?, response: Response?, e: Exception?) {
                        toast(common().toast_error(e!!))
                    }
                })
    }
}
