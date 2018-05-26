package wai.gr.cla.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.lzy.okgo.OkGo
import kotlinx.android.synthetic.main.activity_my_order_gv_list.*
import okhttp3.Call
import okhttp3.Response
import wai.gr.cla.R
import wai.gr.cla.base.BaseFragment
import wai.gr.cla.callback.JsonCallback
import wai.gr.cla.method.*
import wai.gr.cla.model.*
import java.util.ArrayList

/**
 * 直播页面
 * Created by Finder丶畅畅 on 2017/5/7 22:11
 * QQ群481606175
 */

class LiveFragment : BaseFragment() {
    internal var title_adapter: CommonAdapter<ClassTag>? = null
    var title_list = ArrayList<ClassTag>()
    internal var details_adapter: CommonAdapter<CoursesModel>? = null
    var details_list = ArrayList<CoursesModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title_adapter = object : CommonAdapter<ClassTag>(MainActivity.main, title_list, R.layout.item_tab) {
            override fun convert(holder: CommonViewHolder, model: ClassTag, index: Int) {
                holder.setText(R.id.name_tv, model.name)
                if (model.click) {
                    holder.setBG(R.id.name_tv, R.drawable.tag_click_bg)
                } else {
                    holder.setBG(R.id.name_tv, R.drawable.tag_bg)
                }
            }
        }

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

    }

    var is_click: Boolean = false//右侧按钮是否点击
    var title_gv: GridView? = null
    var detail_gv: OnlyLoadGridView? = null
    var cid = "";
    //internal var main_srl: SwipeRefreshLayout? = null
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.frag_live, container, false)
        title_gv = view.findViewById<GridView>(R.id.title_gv) as GridView

        detail_gv = view.findViewById<GridView>(R.id.detail_gv) as OnlyLoadGridView
        title_gv!!.setOnItemClickListener { adapterView, view, i, l ->
            if (!title_list[i].click) {//没点击的才执行
                for (model in title_list) {
                    model.click = false
                }
                title_list[i].click = !title_list[i].click
                title_adapter!!.refresh(title_list)
                //查询全部的情况
                if (i == 0) {
                    cid = ""
                } else {
                    cid = title_list[i].cid.toString()
                }
                click_refresh()
            }
        }
        detail_gv?.setOnItemClickListener { adapterView, view, i, l ->
            run {
                val intent = Intent(MainActivity.main, DetailPlayer::class.java)
                intent.putExtra("cid", details_list[i].id)
                intent.putExtra("is_live", true)
                startActivity(intent)
            }
        }
        detail_gv?.setInterface {
            page_index++
            click_refresh()
        }
        title_gv?.adapter = title_adapter
        detail_gv?.adapter = details_adapter
        initData()
        click_refresh()
        //main_srl = view.findViewById<SwipeRefreshLayout>(R.id.main_srl) as SwipeRefreshLayout

        return view
    }

    override fun onFragmentFirstVisible() {
        super.onFragmentFirstVisible()

    }

    var page_index = 1
    fun click_refresh() {
        if (page_index == 1) {
            details_list.clear()
        }
//        if (which_more == 1) {//加载更多视频

        OkGo.post(url().public_api + "get_zhibo_course")
                .params("page", page_index)
                .params("cid", cid)
                .execute(object : JsonCallback<LzyResponse<PageModel<CoursesModel>>>() {
                    override fun onSuccess(t: LzyResponse<PageModel<CoursesModel>>, call: okhttp3.Call?, response: okhttp3.Response?) {
                        details_list.addAll(t.data!!.list as MutableList<CoursesModel>)
                        details_adapter!!.refresh(details_list)
                        detail_gv?.getIndex(page_index, 20, t.data!!.count)
                        //main_srl.isRefreshing = false

                        if (details_list.size == 0) {
                            //error_ll.visibility = View.VISIBLE;
                            detail_gv?.visibility = View.GONE;
                        } else {
                            //error_ll.visibility = View.GONE;
                            detail_gv?.visibility = View.VISIBLE;
                        }

                    }

                    override fun onError(call: Call?, response: Response?, e: Exception?) {
                        //toast(common().toast_error(e!!))
                        //main_srl.isRefreshing = false
                    }
                })

    }

    fun initData() {
        OkGo.post(url().public_api + "get_zhibo_cate")
                .execute(object : JsonCallback<LzyResponse<JJ>>() {
                    override fun onSuccess(t: LzyResponse<JJ>, call: Call?, response: Response?) {
                        if (t.code == 0) {
                            title_list.add(ClassTag("全部", true))
                            t.data?.`_$138`
                                    ?.flatMap { it.children }
                                    ?.forEach { title_list.add(it.info!!) }
                            title_adapter!!.refresh(title_list)
                        } else {
                            MainActivity.main!!.toast(t.msg.toString())
                        }
                        //main_srl!!.isRefreshing = false
                    }

                    override fun onError(call: Call?, response: Response?, e: Exception?) {
                        Toast.makeText(context, common().toast_error(e!!), Toast.LENGTH_SHORT).show()
                        //main_srl!!.isRefreshing = false
                    }
                })
    }
}