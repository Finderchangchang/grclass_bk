package wai.gr.cla.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.lzy.okgo.OkGo
import kotlinx.android.synthetic.main.activity_card_detail.*
import kotlinx.android.synthetic.main.activity_detail_player.*
import kotlinx.android.synthetic.main.card_new_item.*
import okhttp3.Call
import okhttp3.Response

import java.util.ArrayList

import wai.gr.cla.R
import wai.gr.cla.base.BaseActivity
import wai.gr.cla.callback.JsonCallback
import wai.gr.cla.flingswipe.SwipeFlingAdapterView
import wai.gr.cla.method.CommonAdapter
import wai.gr.cla.method.CommonViewHolder
import wai.gr.cla.method.common
import wai.gr.cla.model.*

/**
 * 卡片详情
 * Created by Administrator on 2017/5/12.
 */

class CardDetailActivity : BaseActivity(), SwipeFlingAdapterView.onFlingListener, SwipeFlingAdapterView.OnItemClickListener, View.OnClickListener {
    override fun setLayout(): Int {
        return R.layout.activity_card_detail
    }

    override fun initViews() {
        classTag = intent.getSerializableExtra("id") as ClassTag?
        list = ArrayList<Teacher>()
        toolbar.setLeftClick { finish() }
        //重置卡片
        toolbar.setRightClick {
            //if (list.size == 0) {
            page = 1
            loadData()
            /* } else {
                 toast("查看完老师才可重新加载")
             }*/
        }
        adapter = object : CommonAdapter<Teacher>(this, list, R.layout.card_new_item) {
            override fun convert(holder: CommonViewHolder, o: Teacher, ccc: Int) {
                val model = o.teacher
                holder.setGlideImage(R.id.portrait, url().total + model!!.photo)
                //holder.setText(R.id.card_sex, "教授性别：" + GetSex(model.sex))
                holder.setText(R.id.card_name, "教授姓名：" + model.name)
                holder.setText(R.id.card_type, "专业领域：" + o.cname)
                holder.setText(R.id.card_content, model.introduce)
                holder.setVisible(R.id.dy_iv, o.i_can_ask)
                holder.setOnClickListener(R.id.btn_dingyue) {
                    //订阅操作
                    if (s == null) {
                        s = ZhiFuPopuWindowActivity(DetailPlayer(), this@CardDetailActivity, this@CardDetailActivity,
                                btn_dingyue, true, o.id.toString(), list[ccc].price_list!!, 0)
                    }
                    s!!.showWindow()
                }
            }
        }
        initView()
    }

    var s: ZhiFuPopuWindowActivity? = null
    override fun onResume() {
        super.onResume()
        if (s != null) {
            (s as ZhiFuPopuWindowActivity).closePop()
        }
        loadData()//支付成功以后重调
    }

    var classTag: ClassTag? = null
    override fun initEvents() {
        toolbar.setCentertv(classTag!!.name)
    }

    private var swipeView: SwipeFlingAdapterView? = null
    private var adapter: CommonAdapter<Teacher>? = null
    internal var list: ArrayList<Teacher> = ArrayList<Teacher>()
    //备份数据，用于向左划加载数据
    internal var listbei: ArrayList<Teacher> = ArrayList<Teacher>()
    //当前卡片 Index private var cardIndex: Int = 0;
    private var cardWidth: Int = 0
    private var cardHeight: Int = 0
    private var page = 1

    fun loadData() {

        OkGo.post(url().public_api + "get_phone_teachers")     // 请求方式和请求url
                .params("cid", classTag!!.cid)
                .params("page", page.toString())
                .execute(object : JsonCallback<LzyResponse<PageModel<Teacher>>>() {
                    override fun onSuccess(t: LzyResponse<PageModel<Teacher>>, call: okhttp3.Call?, response: okhttp3.Response?) {

                        list.clear()

                        for (teacher in list) {
                            list.removeAt(0)
                            adapter!!.notifyDataSetChanged()
                        }

                        list.addAll(t.data!!.list as MutableList<Teacher>)
                        listbei.removeAll(listbei)
                        if (list.size > 0) {
                            for (i in 0..(list.size - 1)) {
                                listbei.add(list[list.size - 1 - i])
                            }
                        }

                        if (list.size == 0) {
                            toast("没有更多数据")
                        } else {
                            swipeView!!.removeAllViewsInLayout()
                            adapter!!.refresh(list)
                        }
                    }

                    override fun onError(call: Call?, response: Response?, e: Exception?) {
                        toast(common().toast_error(e!!))
                    }
                })
    }

    fun initView() {
        val dm = resources.displayMetrics
        val density = dm.density
        cardWidth = (dm.widthPixels - 2f * 18f * density).toInt()
        cardHeight = (dm.heightPixels - 338 * density).toInt()
        swipeView = findViewById<SwipeFlingAdapterView>(R.id.swipe_view)
        swipeView!!.setIsNeedSwipe(true)
        swipeView!!.setFlingListener(this)
        swipeView!!.setOnItemClickListener(this)
        swipeView!!.adapter = adapter
        swipeView!!.setOnItemClickListener { event, v, dataObject ->
            var tea = dataObject as Teacher
            if (tea.i_can_ask) {
                startActivity(Intent(this@CardDetailActivity, AskListActivity::class.java)
                        .putExtra("id", tea.teacher!!.id)
                        .putExtra("cid", tea.id)
                        .putExtra("name", classTag!!.name + "-" + tea.teacher!!.name)
                        .putExtra("answer_num", 0))
            }
        }
    }


    override fun onItemClicked(event: MotionEvent, v: View, dataObject: Any) {

    }

    override fun removeFirstObjectInAdapter() {


    }

    fun refresh() {
        list[0].i_can_ask = true
        adapter!!.notifyDataSetChanged()
    }

    override fun onLeftCardExit(dataObject: Any) {
        if (list.size > 0) {
            list.removeAt(0)
            adapter!!.notifyDataSetChanged()
        } else {
            toast("没有更多卡片信息，请重新加载")
        }

    }

    fun GetSex(date: Int): String {
        if (date == 0) {
            return "男"
        } else {
            return "女"
        }
    }

    override fun onRightCardExit(dataObject: Any) {
        if (list.size > 0) {
            list.removeAt(0)
            adapter!!.notifyDataSetChanged()
        } else {
            toast("没有更多卡片信息，请重新加载")
        }
    }

    override fun onAdapterAboutToEmpty(itemsInAdapter: Int) {

    }

    override fun onScroll(progress: Float, scrollXProgress: Float) {

    }

    override fun onClick(v: View) {

    }
}
