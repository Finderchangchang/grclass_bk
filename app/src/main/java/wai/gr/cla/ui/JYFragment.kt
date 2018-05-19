@file:Suppress("DEPRECATION")

package wai.gr.cla.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.lzy.okgo.OkGo
import okhttp3.Call
import okhttp3.Response

import wai.gr.cla.R
import wai.gr.cla.base.BaseFragment
import wai.gr.cla.callback.JsonCallback
import wai.gr.cla.method.*
import wai.gr.cla.model.*
import java.util.*

/**
 * 就业创业
 * Created by Finder丶畅畅 on 2017/5/7 22:11
 * QQ群481606175
 */

class JYFragment : BaseFragment() {
    internal var title_adapter: CommonAdapter<ClassTag>? = null
    var title_list = ArrayList<ClassTag>()
    internal var tj_adapter: CommonAdapter<CoursesModel>? = null
    var tj_list = ArrayList<CoursesModel>()
    var position = 5//cid=5就业，cid=4创业
    var title_gv: GridView? = null

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
        tj_adapter = object : CommonAdapter<CoursesModel>(MainActivity.main, tj_list, R.layout.item_sp) {
            override fun convert(holder: CommonViewHolder, model: CoursesModel, index: Int) {
                holder.setText(R.id.tag_tv, model.title)
                holder.setTopRoundImage(R.id.tag_iv, url().total + model.thumbnail)
                if (model.price.equals("0.00") || model.price.equals("0")) {
                    holder.setText(R.id.price_tv, "直播")
                } else {
                    holder.setText(R.id.price_tv, "￥" + model.price)
                }
            }
        }

    }

    var cid = ""
    var is_click: Boolean = false//右侧按钮是否点击
    var main_lv: LoadGridView? = null
    var main_sl: ScrollView? = null
    internal var main_srl: SwipeRefreshLayout? = null
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.frag_job, container, false)
        val jy_btn = view.findViewById<TextView>(R.id.jy_btn) as TextView
        val cy_btn = view.findViewById<TextView>(R.id.cy_btn) as TextView
        main_srl = view.findViewById<SwipeRefreshLayout>(R.id.main_srl) as SwipeRefreshLayout
        val have_project_ll = view.findViewById<LinearLayout>(R.id.have_project_ll) as LinearLayout
        main_lv = view.findViewById<LoadGridView>(R.id.main_lv) as LoadGridView
        val input_btn = view.findViewById<Button>(R.id.input_btn) as Button
        main_sl = view.findViewById<ScrollView>(R.id.main_sl) as ScrollView
        title_gv = view.findViewById<GridView>(R.id.title_gv) as GridView

        jy_btn.setOnClickListener {
            jy_btn.setBackgroundResource(R.drawable.title_tg_click)
            jy_btn.setTextColor(resources.getColor(R.color.white))
            cy_btn.setTextColor(resources.getColor(R.color.login_bg))
            cy_btn.setBackgroundColor(resources.getColor(R.color.white))
            have_project_ll.visibility = View.GONE
            position = 5
            page_index = 1
            title_gv!!.visibility = View.GONE
            click_refresh()
        }
        cy_btn.setOnClickListener {
            cy_btn.setBackgroundResource(R.drawable.title_tg_click)
            cy_btn.setTextColor(resources.getColor(R.color.white))
            jy_btn.setTextColor(resources.getColor(R.color.login_bg))
            jy_btn.setBackgroundColor(resources.getColor(R.color.white))
//            if (("0").equals(Utils.getCache(key.KEY_IS_POST))) {
//                have_project_ll.visibility = View.VISIBLE
//            }
            title_gv!!.visibility = View.GONE
            position = 4
            page_index = 1
            initData()
        }
        main_lv!!.adapter = tj_adapter
        main_lv!!.setInterface {
            page_index++
            initData()
        }
        //填写创业信息
        input_btn.setOnClickListener {
            AddChuangYePopuwindow(MainActivity.main!!, have_project_ll)
        }
        //解决下拉刷新和scrollview冲突的问题
        main_sl!!.viewTreeObserver.addOnScrollChangedListener {
            main_srl!!.isEnabled = main_sl!!.scrollY === 0
        }
        main_srl!!.setOnRefreshListener {
            page_index = 1
            if (position == 5) {
                click_refresh()
            } else {
                initData()
            }
        }
        main_lv!!.setOnItemClickListener { _, _, i, _ ->
            run {
                val intent = Intent(MainActivity.main, DetailPlayer::class.java)
                intent.putExtra("cid", tj_list[i].id)
                intent.putExtra("is_live", true)
                startActivity(intent)
            }
        }
        title_gv?.adapter = title_adapter
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
                page_index = 1
                click_refresh()
            }
        }
        initTitle()
        return view
    }

    fun initTitle() {
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

    fun click_refresh() {
        if (page_index == 1) {
            tj_list.clear()
        }
//        if (which_more == 1) {//加载更多视频

        OkGo.post(url().public_api + "get_zhibo_course")
                .params("page", page_index)
                .params("cid", cid)
                .execute(object : JsonCallback<LzyResponse<PageModel<CoursesModel>>>() {
                    override fun onSuccess(t: LzyResponse<PageModel<CoursesModel>>, call: okhttp3.Call?, response: okhttp3.Response?) {
                        tj_list.addAll(t.data!!.list as MutableList<CoursesModel>)
                        tj_adapter!!.refresh(tj_list)
                        main_lv?.getIndex(page_index, 20, t.data!!.count)
                        //main_srl.isRefreshing = false

                        if (tj_list.size == 0) {
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

    override fun onFragmentFirstVisible() {
        super.onFragmentFirstVisible()
        if (position == 5) {
            click_refresh()
        } else {
            initData()
        }
    }

    internal var page_index = 1
    fun initData() {
        OkGo.post(url().public_api + "get_phone_recommend_course_list")
                .params("page", page_index)//当前页数
                .params("free", "1")//当前页数
                .execute(object : JsonCallback<LzyResponse<PageModel<CoursesModel>>>() {
                    override fun onSuccess(t: LzyResponse<PageModel<CoursesModel>>, call: okhttp3.Call?, response: okhttp3.Response?) {
                        if (t.code == 0) {
                            if (page_index == 1) {
                                tj_list.clear()
                            }
                            tj_list.addAll(t.data!!.list!!)
                            main_lv!!.getIndex(page_index, 20, tj_list.size)
                            tj_adapter!!.refresh(tj_list)
                            main_sl!!.fullScroll(ScrollView.FOCUS_UP)
                        } else {
                            MainActivity.main!!.toast(t.msg.toString())
                        }
                        main_srl!!.isRefreshing = false
                    }

                    override fun onError(call: Call?, response: Response?, e: Exception?) {
                        Toast.makeText(context, common().toast_error(e!!), Toast.LENGTH_SHORT).show()
                        main_srl!!.isRefreshing = false
                    }
                })
    }
}
