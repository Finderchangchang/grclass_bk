package wai.gr.cla.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.text.Html
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.lzy.okgo.OkGo
import kotlinx.android.synthetic.main.activity_main1.*
import okhttp3.Call
import okhttp3.Response
import wai.gr.cla.R
import wai.gr.cla.callback.JsonCallback
import wai.gr.cla.method.*
import wai.gr.cla.model.*
import java.util.*
import wai.gr.cla.base.App


/**
 * Created by Finder丶畅畅 on 2017/5/17 20:32
 * QQ群481606175
 */
class VideoFragment() : Fragment() {
    @SuppressLint("ValidFragment")
    constructor(position: Int, model: TuiJianModel) : this() {
        po = position
        mo = model
    }

    internal var po = 0
    internal var mo: TuiJianModel? = null
    var ml_adapter: CommonAdapter<VideoModel>? = null//视频目录
    var ml_list: MutableList<VideoModel>? = ArrayList()
    var pl_adapter: CommonAdapter<PJModel>? = null//评论列表
    var pl_list: MutableList<PJModel>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (DetailPlayer.main != null) {
            ml_adapter = object : CommonAdapter<VideoModel>(DetailPlayer.main, ml_list, R.layout.item_sp_menu) {
                override fun convert(holder: CommonViewHolder, model: VideoModel, position: Int) {

                    var name = model.name
                    if (TextUtils.isEmpty(name)) name = model.title
                    var title = ""
                    holder.setVisible(R.id.is_live_tv,false)
                    if (model.free == 1) {
                        title ="[免费]" + name
                        if (model.check) {//选中
                            holder.setVisible(R.id.yuan_iv, true)
                            holder.setTextColor(R.id.tv, R.color.colorPrimary)
                        } else {
                            holder.setVisible(R.id.yuan_iv, false)
                            holder.setTextColor(R.id.tv, R.color.text_hint)
                        }
                    } else {
                        when (model.status) {
                            1 -> {
                                title ="[回放]" + name + "(" + model.longtime + ")"
                                holder.setTextColor(R.id.tv, R.color.text_hint)
                            }
                            2 -> {
                                title ="[回放]" + name + "(" + model.zhibo_date + "已结束)"
                                holder.setTextColor(R.id.tv, R.color.text_hint)
                            }
                            3 -> {
                                holder.setTextColor(R.id.tv, R.color.black)
                                title ="[直播]" + name + "(" + model.zhibo_date + ")"
                                holder.setVisible(R.id.is_live_tv,true)
                            }
                            else -> {
                                holder.setTextColor(R.id.tv, R.color.black)
                                title ="[直播]" + name + "(" + model.zhibo_date + " " + model.start_time + "-" + model.start_time + ")"
                            }
                        }
                    }
                    holder.setText(R.id.tv, Html.fromHtml(title))
                }
            }
            pl_adapter = object : CommonAdapter<PJModel>(DetailPlayer.main, pl_list, R.layout.item_pinglun) {
                override fun convert(holder: CommonViewHolder, model: PJModel, position: Int) {
                    holder.setGlideImage(R.id.user_iv, url().total + model.user!!.head_img)
                    holder.setText(R.id.user_name_tv, model.user!!.nick)
                    holder.setText(R.id.time_tv, model.cdate)
                    holder.setText(R.id.count_tv, model.content)
                    holder.setText(R.id.reply_tv, "回复：" + model.reply_content)
                }
            }
        }
    }

    var page_index = 1
    var center_click = 0
    var ml_pl_lv: LoadListView? = null
    var jdesc_tv: TextView? = null
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.frag_sp, container, false)
        val gs_sl = view.findViewById<ScrollView>(R.id.gs_sl) as ScrollView
        ml_pl_lv = view.findViewById<LoadListView>(R.id.ml_pl_lv) as LoadListView
        jdesc_tv = view.findViewById<TextView>(R.id.jdesc_tv) as TextView
        val main_srl = view.findViewById<SwipeRefreshLayout>(R.id.main_srl) as SwipeRefreshLayout
        val center_lv = view.findViewById<ListView>(R.id.center_lv) as ListView
        val user_iv = view.findViewById<ImageView>(R.id.user_iv) as ImageView
        val teacher_name_tv = view.findViewById<TextView>(R.id.teacher_name_tv) as TextView
        val teacher_detail_tv = view.findViewById<TextView>(R.id.teacher_detail_tv) as TextView
        val desc_tv = view.findViewById<TextView>(R.id.desc_tv) as TextView
        val yxq_tv = view.findViewById<TextView>(R.id.yxq_tv) as TextView//有效期
        val ks_tv = view.findViewById<TextView>(R.id.ks_tv) as TextView//课时
        val bf_tv1 = view.findViewById<TextView>(R.id.bf_tv1) as TextView//播放次数文字
        bf_tv1.visibility = View.GONE
        val bf_tv = view.findViewById<TextView>(R.id.bf_tv) as TextView//播放次数

        val ml_lv_fl = view.findViewById<FrameLayout>(R.id.ml_lv_fl) as FrameLayout//评论列表
        val ml_error_ll = view.findViewById<LinearLayout>(R.id.ml_error_ll) as LinearLayout
        val pl_btn = view.findViewById<ImageButton>(R.id.pl_btn) as ImageButton
        val main_sl = view.findViewById<ScrollView>(R.id.main_sl) as ScrollView
        ml_pl_lv!!.visibility = View.VISIBLE
        ml_pl_lv!!.emptyView = ml_error_ll//设置listview为空数据时的显示
        pl_btn.visibility = View.GONE
        when (po) {
            0 -> {
                main_srl.visibility = View.VISIBLE
                ml_pl_lv!!.visibility = View.VISIBLE
                ml_lv_fl.visibility = View.VISIBLE
                if (DetailPlayer.main != null) {
                    GlideImgManager.glideLoader(DetailPlayer.main!!, url().total + mo!!.thumbnail, R.mipmap.error_img_sml, R.mipmap.error_img_sml, user_iv, 0)
                } else {
                    GlideImgManager.glideLoader(App.context, url().total + mo!!.thumbnail, R.mipmap.error_img_sml, R.mipmap.error_img_sml, user_iv, 0)
                }
                var t_name = mo!!.lecturer
                if (TextUtils.isEmpty(t_name)) {
                    t_name = ""
                }
                teacher_name_tv.text = "讲师：" + t_name
                teacher_detail_tv.text = mo!!.forwho
                if (TextUtils.isEmpty(mo!!.summary)) {
                    mo!!.summary = "无"
                }
                center_lv.visibility = View.GONE
                desc_tv.text = mo!!.summary
                jdesc_tv!!.text = "适合学员：" + mo!!.forwho

                var plnum = 0
                if (pl_list != null) {
                    plnum = pl_list!!.size
                }
                //直播
                if (DetailPlayer.main!!.is_live) {
                    bf_tv1.visibility = View.VISIBLE
                    bf_tv.text = mo?.start_date + "~" + mo?.end_date
                    if (mo?.zhibo_type == 0) {//0.大班课：1.小班课
                        ks_tv.visibility = View.INVISIBLE
                    } else {
                        ks_tv.visibility = View.VISIBLE
                    }
                    ks_tv.text = "购课总名额：" + mo!!.buy_max_num + " 剩余名额：" + mo!!.shengyu_num
                    yxq_tv.visibility = View.GONE
                } else {
                    bf_tv.text = "播放：" + mo!!.dots + "次 评论：" + mo!!.comment_num + " 购买数：" + mo!!.buy_num
                    yxq_tv.text = "有效期：自购买之日起" + mo!!.valid_year + "年"
                    ks_tv.text = "课时：" + mo!!.videos!!.size + "讲"
                    yxq_tv.visibility = View.VISIBLE
                    ks_tv.visibility = View.VISIBLE
                }
                if (!DetailPlayer.main!!.is_live) {
                    OkGo.post(url().public_api + "get_course_comment_nums")
                            .params("id", mo!!.id)
                            .execute(object : JsonCallback<LzyResponse<CommentModel>>() {
                                override fun onSuccess(key: LzyResponse<CommentModel>, call: okhttp3.Call?, response: okhttp3.Response?) {
                                    if (key.code == 0) {
                                        bf_tv.text = "播放：" + mo!!.dots + "次 评论：" + key.data!!.comment_num + " 购买数：" + mo!!.buy_num
                                    }
                                }
                            })
                }
                center_lv.visibility = View.GONE
                ml_lv_fl.visibility = View.VISIBLE
                ml_pl_lv!!.visibility = View.VISIBLE
                ml_pl_lv!!.adapter = pl_adapter
                pl_btn.visibility = View.VISIBLE//显示评论按钮
                pl_btn.setOnClickListener {
                    var user_id = Utils.getCache(key.KEY_USERID)
                    if (TextUtils.isEmpty(user_id)) {
                        DetailPlayer.main!!.toast("请先登录")
                    } else {
                        if (mo!!.videos!!.isNotEmpty() || DetailPlayer!!.main!!.is_live) {
//                            startActivityForResult(Intent(DetailPlayer.main, AddAskActivity::class.java).putExtra("course_id", mo!!.videos!![center_click].course_id), 1)
                            startActivityForResult(Intent(DetailPlayer.main, AddAskActivity::class.java).putExtra("course_id", DetailPlayer!!.main?.ss), 1)

                        } else {
                            DetailPlayer.main!!.toast("当前无法评论")
                        }
                    }
                }
                main_sl.viewTreeObserver.addOnScrollChangedListener {
                    main_srl!!.isEnabled = main_sl.scrollY === 0
                }
                loadPL()
            }
            1 -> {
                ml_list = mo!!.videos as MutableList<VideoModel>?
                if (DetailPlayer().model != null) {
                    ml_list = DetailPlayer().model!!.videos as MutableList<VideoModel>?
                }
                if (ml_list!!.size > 0) {
                    ml_list!![0].check = true
                }
                center_lv.visibility = View.VISIBLE
                center_lv.adapter = ml_adapter
                ml_adapter!!.refresh(ml_list)
                main_srl.visibility = View.GONE
                ml_pl_lv!!.visibility = View.GONE
            }
            2 -> {

            }
        }
        main_srl.setOnRefreshListener {
            page_index = 1
            main_srl.isRefreshing = false
            loadPL()
        }
        ml_pl_lv!!.setInterface {
            page_index++
            loadPL()
        }
        center_lv.setOnItemClickListener { parent, view, position, id ->
            run {

                if (center_click != position) {//处理点击同一个item的问题..
//                    DetailPlayer.main!!.play(position)
//                    val ac = activity as DetailPlayer
//                    ml_list = ac.loadData1() as MutableList<VideoModel>?
                    //当前item可以播放，当前视频已购买
                    if (DetailPlayer.main!!.play(position)) {
                        var video_url = ml_list!![position].url!!
                        if (!TextUtils.isEmpty(video_url)) {
                            DetailPlayer.main!!.play(ml_list!![position].url!!, ml_list!![position].thumbnail!!, ml_list!![position].name!!)
                            ml_list!![center_click].check = false
                            center_click = position
                            ml_list!![center_click].check = true
                            ml_adapter!!.refresh(ml_list)
                            OkGo.post(url().auth_api + "update_course_percent")
                                    .params("course_id", ml_list!![position].course_id)// 请求方式和请求url
                                    .params("view_num", position + 1)// 请求方式和请求url
                                    .execute(object : JsonCallback<LzyResponse<UserModel>>() {
                                        override fun onSuccess(t: LzyResponse<UserModel>, call: okhttp3.Call?, response: okhttp3.Response?) {
                                            if (t.code == 0) {
                                                Utils.putCache("last_position", position.toString())
                                            } else {

                                            }
                                        }

                                        override fun onError(call: Call?, response: Response?, e: Exception?) {
                                            //Toast.makeText(context, "1:"+common().toast_error(e!!), Toast.LENGTH_SHORT).show()
                                        }
                                    })
                        }
                    } else {
                        DetailPlayer.main!!.toast("请先购买视频")
                    }
                }
            }

        }
        ml_pl_lv!!.setOnItemClickListener { parent, view, position, id ->
            run {
                //                val video = ml_list!![position]
//                if (0 == video!!.free) {//免费播放
//                    DetailPlayer.main!!.play(url().total + video!!.url!!, video!!.thumbnail!!)
//                } else {
//                    DetailPlayer.main!!.play("", "")
//                }
            }
        }
        return view
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == 88) {
            loadPL()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    fun loadPL() {
        if (page_index == 1) {
            pl_list = ArrayList()
        }
        OkGo.post(url().public_api + "get_course_comments")
                .params("id", mo!!.id)
                .params("page", page_index)
                .execute(object : JsonCallback<LzyResponse<PageModel<PJModel>>>() {
                    override fun onSuccess(key: LzyResponse<PageModel<PJModel>>, call: okhttp3.Call?, response: okhttp3.Response?) {
                        pl_list = key.data!!.list as MutableList<PJModel>?
                        if (pl_list != null) {
                            ml_pl_lv!!.getIndex(page_index, 20, pl_list!!.size)
                            pl_adapter!!.refresh(pl_list)
                        } else {
                            ml_pl_lv!!.getIndex(page_index, 20, 0)
                        }
                    }

                    override fun onError(call: Call?, response: Response?, e: Exception?) {
                        Toast.makeText(context, common().toast_error(e!!), Toast.LENGTH_SHORT).show()
                    }
                })
    }

    override fun onDestroy() {
        super.onDestroy()
        OkGo.getInstance().cancelAll()
    }
}