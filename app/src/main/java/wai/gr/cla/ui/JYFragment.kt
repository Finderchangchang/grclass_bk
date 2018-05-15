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
    internal var tj_adapter: CommonAdapter<ZiXunModel>? = null
    var tj_list = ArrayList<ZiXunModel>()
    var position = 5//cid=5就业，cid=4创业
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tj_adapter = object : CommonAdapter<ZiXunModel>(MainActivity.main, tj_list, R.layout.item_jiuye) {
            override fun convert(holder: CommonViewHolder, model: ZiXunModel, index: Int) {
                var time=model.show_time
                if(time!!.length>=10){
                    time=time.substring(0,10)
                }
                holder.setText(R.id.item_jiuye_tiem, time)
                holder.setText(R.id.jiuyt_tv_title, model.title)
                holder.setRoundImage(R.id.jiuye_iv_image, url().total + model.thumbnail)
                holder.setText(R.id.jiuye_tv_shoucang, model.dianzan.toString())
                holder.setText(R.id.jiuyt_tv_pinglun, model.comments.toString())
                if (position == 5) {
                    holder.setVisible(R.id.tag_ll, true)
                } else {
                    holder.setInVisible(R.id.tag_ll)
                }
                holder.setVisible(R.id.tag1_tv, false)
                holder.setVisible(R.id.tag2_tv, false)
                holder.setVisible(R.id.tag3_tv, false)
                when (model.tags!!.size) {
                    1 -> holder.setText(R.id.tag1_tv, model.tags!![0])
                    2 -> {
                        holder.setText(R.id.tag1_tv, model.tags!![0])
                        holder.setText(R.id.tag2_tv, model.tags!![1])
                    }
                    3 -> {
                        holder.setText(R.id.tag1_tv, model.tags!![0])
                        holder.setText(R.id.tag2_tv, model.tags!![1])
                        holder.setText(R.id.tag3_tv, model.tags!![2])
                    }
                }
            }
        }
    }

    var is_click: Boolean = false//右侧按钮是否点击
    var main_lv: LoadListView? = null
    var main_sl: ScrollView? = null
    internal var main_srl: SwipeRefreshLayout? = null
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.frag_job, container, false)
        val jy_btn = view.findViewById<TextView>(R.id.jy_btn) as TextView
        val cy_btn = view.findViewById<TextView>(R.id.cy_btn) as TextView
        main_srl = view.findViewById<SwipeRefreshLayout>(R.id.main_srl) as SwipeRefreshLayout
        val have_project_ll = view.findViewById<LinearLayout>(R.id.have_project_ll) as LinearLayout
        main_lv = view.findViewById<LoadListView>(R.id.main_lv) as LoadListView
        val input_btn = view.findViewById<Button>(R.id.input_btn) as Button
        main_sl = view.findViewById<ScrollView>(R.id.main_sl) as ScrollView
        jy_btn.setOnClickListener {
            jy_btn.setBackgroundResource(R.drawable.title_tg_click)
            jy_btn.setTextColor(resources.getColor(R.color.white))
            cy_btn.setTextColor(resources.getColor(R.color.login_bg))
            cy_btn.setBackgroundColor(resources.getColor(R.color.white))
            have_project_ll.visibility = View.GONE
            position = 5
            page_index = 1
            initData()
        }
        cy_btn.setOnClickListener {
            cy_btn.setBackgroundResource(R.drawable.title_tg_click)
            cy_btn.setTextColor(resources.getColor(R.color.white))
            jy_btn.setTextColor(resources.getColor(R.color.login_bg))
            jy_btn.setBackgroundColor(resources.getColor(R.color.white))
            if (("0").equals(Utils.getCache(key.KEY_IS_POST))) {
                have_project_ll.visibility = View.VISIBLE
            }
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
            initData()
        }
        main_lv!!.setOnItemClickListener { adapterView, view, i, l ->
            run {
                if (i < tj_list.size) {
                    val intent = Intent(MainActivity.main, ZiXunDetailActivity::class.java)
                    intent.putExtra("cid", tj_list[i].id.toString())
                    when(position){
                        4->intent.putExtra("title", "就业创业")
                        5->intent.putExtra("title", "就业直通车")
                    }

                    startActivity(intent)
                }
            }
        }
        return view
    }

    override fun onFragmentFirstVisible() {
        super.onFragmentFirstVisible()
        initData()
    }

    internal var page_index = 1
    fun initData() {
        OkGo.post(url().public_api + "get_phone_news_list")
                .params("page", page_index)//当前页数
                .params("cid", position)//当前页数
                .execute(object : JsonCallback<LzyResponse<PageModel<ZiXunModel>>>() {
                    override fun onSuccess(t: LzyResponse<PageModel<ZiXunModel>>, call: okhttp3.Call?, response: okhttp3.Response?) {
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
