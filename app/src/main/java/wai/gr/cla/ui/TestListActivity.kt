package wai.gr.cla.ui

import android.content.Intent
import android.text.TextUtils
import android.view.View
import com.lzy.okgo.OkGo
import kotlinx.android.synthetic.main.activity_test_list.*
import okhttp3.Call
import okhttp3.Response

import wai.gr.cla.R
import wai.gr.cla.base.BaseActivity
import wai.gr.cla.callback.JsonCallback
import wai.gr.cla.method.*
import wai.gr.cla.model.*
import java.util.*

/**
 * 根据cid查询出指定的考试题
 * */
class TestListActivity : BaseActivity() {
    internal var test_list: MutableList<TestModel> = ArrayList()//购买的课程列表
    var test_adapter: CommonAdapter<TestModel>? = null//购买的课程
    override fun setLayout(): Int {
        return R.layout.activity_test_list
    }

    var cid = 0//传递过来的cid
    var page_index: Int = 1//当前页数
    override fun initViews() {
        toolbar.setLeftClick { finish() }
        cid = intent.getIntExtra("cid", 0)
        var title = intent.getStringExtra("title")
        if (!TextUtils.isEmpty(title)) {
            toolbar.setCentertv(title)
        } else {
            when (cid) {
                3 -> {
                    title = "高等数学"
                }
                4 -> {
                    title = "英语"
                }
                5 -> {
                    title = "政治"
                }
                else -> {
                    title = "更多试题"
                }
            }
            toolbar.setCentertv(title)
        }
        test_adapter = object : CommonAdapter<TestModel>(this, test_list, R.layout.item_test) {
            override fun convert(holder: CommonViewHolder, model: TestModel, position: Int) {
                holder.setText(R.id.title_tv, model.title)
                holder.setText(R.id.data_tv, "有" + model.done_num + "人参考")
            }
        }
        main_lv.adapter = test_adapter
        load()//加载数据
        //刷新操作
        main_srl.setOnRefreshListener {
            page_index = 1
            load()
        }
        //加载更多
        main_lv.setInterface {
            page_index++
            load()
        }
        main_lv.setIsValid(object : OnlyLoadListView.OnSwipeIsValid {
            override fun setSwipeEnabledTrue() {
                main_srl.isEnabled=true
            }

            override fun setSwipeEnabledFalse() {
                main_srl.isEnabled=false
            }
        })
        main_lv.setOnItemClickListener { parent, view, position, id ->
            ///判断是否登录
            if (position < test_list.size) {
                var user_id = Utils.getCache(key.KEY_USERID)
                if (TextUtils.isEmpty(user_id)) {
                    MainActivity.main!!.toast("请先登录")
                } else {
                    startActivity(Intent(MainActivity.main, WebActivity::class.java).putExtra("name", "position")
                            .putExtra("url", url().normal + "analog/analog_phone_detail&id=" + test_list!![position].id + "&uid=" + user_id)
                            .putExtra("title", "考试详情"))
                }
            }
        }
    }

    fun load() {
        OkGo.post(url().public_api + "self_testing_list")
                .params("page", page_index)
                .params("cid", cid)
                .execute(object : JsonCallback<LzyResponse<PageModel<TestModel>>>() {
                    override fun onSuccess(t: LzyResponse<PageModel<TestModel>>, call: okhttp3.Call?, response: okhttp3.Response?) {
                        if(page_index==1){
                            test_list=ArrayList()
                        }
                        test_list.addAll(t.data!!.list as MutableList<TestModel>)
                        if (test_list.size > 0) {
                            test_adapter!!.refresh(test_list)
                            main_lv.getIndex(page_index, 20, t.data!!.count)
                            main_srl.isRefreshing = false
                            myText.visibility = View.GONE
                            main_lv.visibility = View.VISIBLE
                        } else {
                            myText.visibility = View.VISIBLE
                            main_lv.visibility = View.GONE
                        }

                    }

                    override fun onError(call: Call?, response: Response?, e: Exception?) {
                        toast(common().toast_error(e!!))
                        main_srl.isRefreshing = false
                    }
                })
    }

    override fun initEvents() {
//        main_sv.viewTreeObserver.addOnScrollChangedListener {
//            main_srl!!.isEnabled = main_sv.scrollY === 0
//        }
    }
}
