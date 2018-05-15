package wai.gr.cla.ui

import android.view.View
import com.lzy.okgo.OkGo
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.footermore.*
import okhttp3.Call
import okhttp3.Response

import wai.gr.cla.R
import wai.gr.cla.base.BaseActivity
import wai.gr.cla.callback.JsonCallback
import wai.gr.cla.method.CommonAdapter
import wai.gr.cla.method.CommonViewHolder
import wai.gr.cla.method.common
import wai.gr.cla.model.LzyResponse
import wai.gr.cla.model.TuiJianModel
import wai.gr.cla.model.ZB
import wai.gr.cla.model.url
import java.util.*

class Main2Activity : BaseActivity() {
    override fun setLayout(): Int {
        return R.layout.activity_main2
    }

    var tj_list = ArrayList<TuiJianModel>()//推荐
    var tj_adapter: CommonAdapter<TuiJianModel>? = null
    override fun initViews() {
        tj_adapter = object : CommonAdapter<TuiJianModel>(this, tj_list, R.layout.item_sp) {
            override fun convert(holder: CommonViewHolder, model: TuiJianModel, position: Int) {
                holder.setText(R.id.tag_tv, model.title + "\n" + model.title)
                holder.setVisible(R.id.price_tv, false)
            }
        }
        class_gv.adapter = tj_adapter
        main_sl.setScrollBottomListener {
            toast("到底了")
            if (tj_list.size < 15) {
                tj_list.add(tj_list[0])
                tj_list.add(tj_list[1])
                tj_adapter!!.refresh(tj_list)
                pb.visibility = View.VISIBLE
            } else {
                load_tv.text = "到底了..."
                pb.visibility = View.GONE
            }
        }
        refresh()
    }

    fun refresh() {
        val ok = OkGo.post(url().public_api + "get_phone_course_data")     // 请求方式和请求url
                .params("page", "1")
        ok.execute(object : JsonCallback<LzyResponse<ZB>>() {
            override fun onSuccess(t: LzyResponse<ZB>, call: okhttp3.Call?, response: okhttp3.Response?) {
                for (i in 0..2) {
                    tj_list.addAll(t.data!!.free_course!!)
                }
                tj_adapter!!.refresh(tj_list)
            }

            override fun onError(call: Call?, response: Response?, e: Exception?) {
                toast(common().toast_error(e!!))
            }
        })
    }

    override fun initEvents() {

    }
}
