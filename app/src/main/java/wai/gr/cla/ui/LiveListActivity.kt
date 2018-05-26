package wai.gr.cla.ui

import android.content.Intent
import android.text.TextUtils
import android.view.View
import com.lzy.okgo.OkGo
import kotlinx.android.synthetic.main.activity_live_list.*
import okhttp3.Call
import okhttp3.Response
import wai.gr.cla.R
import wai.gr.cla.base.BaseActivity
import wai.gr.cla.callback.JsonCallback
import wai.gr.cla.method.*
import wai.gr.cla.model.*
import java.util.ArrayList

/**
 * 更多直播
 * */
class LiveListActivity : BaseActivity() {
    internal var details_adapter: CommonAdapter<CoursesModel>? = null
    var details_list = ArrayList<CoursesModel>()
    override fun setLayout(): Int {
        return R.layout.activity_live_list
    }

    var cid = 0//传递过来的cid
    var page_index: Int = 1//当前页数
    override fun initViews() {
        toolbar.setLeftClick { finish() }
        details_adapter = object : CommonAdapter<CoursesModel>(MainActivity.main, details_list, R.layout.item_sp) {
            override fun convert(holder: CommonViewHolder, model: CoursesModel, position: Int) {
                holder.setText(R.id.tag_tv, model.title)
                holder.setTopRoundImage(R.id.tag_iv, url().total + model.thumbnail)
                if (model.price.equals("0.00") || model.price.equals("0")) {
                    holder.setText(R.id.price_tv, "免费")
                } else {
                    holder.setText(R.id.price_tv, "￥" + model.price)
                }
            }
        }
        main_lv.adapter = details_adapter
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
        main_lv?.setOnItemClickListener { adapterView, view, i, l ->
            run {
                val intent = Intent(MainActivity.main, DetailPlayer::class.java)
                intent.putExtra("cid", details_list[i].id)
                intent.putExtra("is_live", true)
                startActivity(intent)
            }
        }
        main_lv.setIsValid(object : OnlyLoadGridView.OnSwipeIsValid {
            override fun setSwipeEnabledTrue() {
                main_srl.isEnabled = true
            }

            override fun setSwipeEnabledFalse() {
                main_srl.isEnabled = false
            }
        })
    }

    fun load() {
        if (page_index == 1) {
            details_list.clear()
        }
//        if (which_more == 1) {//加载更多视频

        OkGo.post(url().public_api + "get_zhibo_course")
                .params("page", page_index)
                .execute(object : JsonCallback<LzyResponse<PageModel<CoursesModel>>>() {
                    override fun onSuccess(t: LzyResponse<PageModel<CoursesModel>>, call: okhttp3.Call?, response: okhttp3.Response?) {
                        details_list.addAll(t.data!!.list as MutableList<CoursesModel>)
                        details_adapter!!.refresh(details_list)
                        main_lv?.getIndex(page_index, 20, t.data!!.count)
                        //main_srl.isRefreshing = false

                        if (details_list.size == 0) {
                            //error_ll.visibility = View.VISIBLE;
                            main_lv?.visibility = View.GONE;
                        } else {
                            //error_ll.visibility = View.GONE;
                            main_lv?.visibility = View.VISIBLE;
                        }

                    }

                    override fun onError(call: Call?, response: Response?, e: Exception?) {
                        //toast(common().toast_error(e!!))
                        //main_srl.isRefreshing = false
                    }
                })

    }

    override fun initEvents() {
//        main_sv.viewTreeObserver.addOnScrollChangedListener {
//            main_srl!!.isEnabled = main_sv.scrollY === 0
//        }
    }
}