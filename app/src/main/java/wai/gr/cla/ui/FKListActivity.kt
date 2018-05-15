package wai.gr.cla.ui

import android.content.Intent
import android.text.TextUtils
import android.view.View
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import kotlinx.android.synthetic.main.activity_fk_list.*
import okhttp3.Call
import okhttp3.Response
import wai.gr.cla.R
import wai.gr.cla.base.BaseActivity
import wai.gr.cla.callback.JsonCallback
import wai.gr.cla.method.CommonAdapter
import wai.gr.cla.method.CommonViewHolder
import wai.gr.cla.method.OnlyLoadListView
import wai.gr.cla.method.Utils
import wai.gr.cla.model.*
import java.util.*

/**
 * 反馈管理
 * */
class FKListActivity : BaseActivity() {
    var zx_list = ArrayList<ZiXunModel>()
    internal var page_index = 1//当前页数
    internal var cid = 0
    var answer_list = ArrayList<FKModel>()
    var answer_adapter: CommonAdapter<FKModel>? = null//资讯
    override fun setLayout(): Int {
        return R.layout.activity_fk_list
    }

    var user_id = Utils.getCache(key.KEY_USERID)
    override fun initViews() {
        toolbar.center_str = "我的反馈列表"
        //控制刷新刷新冲突
        asl_llv.setIsValid(object : OnlyLoadListView.OnSwipeIsValid {
            override fun setSwipeEnabledTrue() {
                main_srl.isEnabled = true
            }

            override fun setSwipeEnabledFalse() {
                main_srl.isEnabled = false
            }
        })
        answer_adapter = object : CommonAdapter<FKModel>(this, answer_list, R.layout.item_fk) {
            override fun convert(holder: CommonViewHolder, model: FKModel, position: Int) {
                if (TextUtils.isEmpty(model.content)) {
                    holder.setText(R.id.wen_tv, "暂无文字回复")
                } else {
                    holder.setText(R.id.wen_tv, model.content)
                }
                if (TextUtils.isEmpty(model.reply_content)) {
                    holder.setText(R.id.da_tv, "无")
                } else {
                    holder.setText(R.id.da_tv, model.reply_content)
                }
            }
        }
        toolbar.setLeftClick { finish() }
        asl_llv.adapter = answer_adapter
        main_srl.setOnRefreshListener {
            page_index = 1
            loadData()
        }//刷新加载数据
        asl_llv.setInterface {
            page_index++
            loadData()
        }//上划加载更多数据
        page_index = 1
        loadData()
    }


    var s: ZhiFuPopuWindowActivity? = null
    internal var list: ArrayList<Teacher> = ArrayList<Teacher>()
    /**
     * 加载数据
     * */
    fun loadData() {
        main_srl.isRefreshing = true
        var s = OkGo.get(url().auth_api + "get_all_feedback_info")
                .params("page", page_index)
        s.execute(object : JsonCallback<LzyResponse<PageModel<FKModel>>>() {
            override fun onSuccess(t: LzyResponse<PageModel<FKModel>>, call: Call?, response: Response?) {
                if (page_index == 1) {
                    answer_list.clear()
                }
                answer_list.addAll(t.data?.list as ArrayList<FKModel>)
                answer_adapter!!.refresh(answer_list)
                asl_llv.getIndex(page_index, 5, 5 * (page_index - 1) + t.data!!.count)
                main_srl.isRefreshing = false
                if (answer_list.size == 0) {
                    error_ll.visibility = View.VISIBLE;
                } else {
                    error_ll.visibility = View.GONE;
                }
            }

            override fun onError(call: Call?, response: Response?, e: Exception?) {
                //toast(common().toast_error(e!!))
                main_srl.isRefreshing = false
            }
        })
//        }
    }


    override fun initEvents() {

    }
}