package wai.gr.cla.ui

import android.content.Intent
import android.view.View
import android.widget.LinearLayout
import com.lzy.okgo.OkGo
import kotlinx.android.synthetic.main.activity_my_order_list.*
import okhttp3.Call
import okhttp3.Response

import wai.gr.cla.R
import wai.gr.cla.base.BaseActivity
import wai.gr.cla.callback.JsonCallback
import wai.gr.cla.method.CommonAdapter
import wai.gr.cla.method.CommonViewHolder
import wai.gr.cla.method.OnlyLoadListView
import wai.gr.cla.method.common
import wai.gr.cla.model.*
import java.util.*

/**
 * 3.获取我的订单列表
 * 4.获取我的视频课程列表
 * 5.获取我的订阅课程
 * 6.获取指定id的评论列表
 * 7.获取我提交的项目列表
 * 1,2.获取我的课程、资讯的收藏
 *
 * 详情页面
 * */
class MyOrderListActivity : BaseActivity() {
    var page_index: Int = 1//当前页数
    var which: Int = 1//当前页面内容
    var IsEdit: Boolean = false
    var which_more: Int = 1//更多1,视频或2,咨询
    internal var kc1_list: MutableList<TradeModel> = ArrayList()//购买的课程列表
    var kc1_adapter: CommonAdapter<TradeModel>? = null//购买的课程
    internal var sp2_list: MutableList<CoursesModel> = ArrayList()//我的视频课程
    var sp2_adapter: CommonAdapter<CoursesModel>? = null//我的视频课程
    internal var dy3_list: MutableList<OrderClassModel> = ArrayList()//我的订阅
    var dy3_adapter: CommonAdapter<OrderClassModel>? = null//我的订阅
    internal var tw4_list: MutableList<CommonModel> = ArrayList()//评论列表
    var tw4_adapter: CommonAdapter<CommonModel>? = null//评论列表
    internal var xm5_list: MutableList<ProjectModel> = ArrayList()//我提交的项目
    var xm5_adapter: CommonAdapter<ProjectModel>? = null//我提交的项目
    internal var sc6_list: MutableList<SCModel> = ArrayList()//我的收藏
    var sc6_adapter: CommonAdapter<SCModel>? = null//我的提问
    var zx_adapter: CommonAdapter<SCModel>? = null//资讯
    override fun setLayout(): Int {
        return R.layout.activity_my_order_list
    }

    var zx_id = 0//传递过来的资讯的id
    var free = ""
    override fun initViews() {
        which = intent.getIntExtra("which", 1)
        which_more = intent.getIntExtra("which_more", 0)


        toolbar.setLeftClick { finish() }
//        order_lv_sl.viewTreeObserver.addOnScrollChangedListener {
//            main_srl!!.isEnabled = order_lv_sl.scrollY === 0
//        }
        main_lv.setIsValid(object : OnlyLoadListView.OnSwipeIsValid {
            override fun setSwipeEnabledTrue() {
                main_srl.isEnabled = true
            }

            override fun setSwipeEnabledFalse() {
                main_srl.isEnabled = false
            }
        })
        when (which) {
            3 -> {
                toolbar.setCentertv("我的订单")
                kc1_adapter = object : CommonAdapter<TradeModel>(this, kc1_list, R.layout.item_tread) {
                    override fun convert(holder: CommonViewHolder, model: TradeModel, position: Int) {
                        holder.setText(R.id.name_tv, model.name)
                        holder.setText(R.id.xd_time_tv, "下单时间：" + model.pay_success_time.toString())
                        holder.setText(R.id.num_tv, model.trade_no.toString())
                        holder.setText(R.id.price_tv, "￥" + model.price)
                        var url = url().total
                        if (model.course != null) {
                            url += model.course!!.thumbnail!!
                        } else {
                            if (model.teacher != null && model.teacher!!.teacher != null) {
                                url += model.teacher!!.teacher!!.photo!!
                            }
                        }
                        holder.setGImage(R.id.user_iv, url)
                    }
                }
                main_lv.adapter = kc1_adapter
                load3()
                main_lv.setInterface {
                    page_index++
                    load3()
                }
                main_srl.setOnRefreshListener {
                    page_index = 1
                    load3()
                }
            }//获取我的订单列表
            4 -> {

            }//获取我的视频课程
            5 -> {
                toolbar.setCentertv("我的订阅")
                dy3_adapter = object : CommonAdapter<OrderClassModel>(this, dy3_list, R.layout.item_dy) {
                    override fun convert(holder: CommonViewHolder, model: OrderClassModel, position: Int) {
                        holder.setText(R.id.name_tv, model.teacher_course!!.cname + "-" + model.teacher_course!!.teacher_name)
                        //val uu = url().total + model.teacher_course!!.teacher!!.photo
                        holder.setGlideImage(R.id.user_iv, url().total + model.teacher_course!!.teacher!!.photo)//设置图片
                        holder.setText(R.id.xd_time_tv, "截止时间：" + model.expire_time)//创建时间
                    }
                }
                main_lv.adapter = dy3_adapter
                load5()
                main_lv.setInterface {
                    page_index++
                    load5()
                }
                main_srl.setOnRefreshListener {
                    page_index = 1
                    load5()
                }
            }//获取我的订阅课程
            6 -> {
                toolbar.setCentertv("评论")
                zx_id = intent.getIntExtra("zx_id", 0)
                tw4_adapter = object : CommonAdapter<CommonModel>(this@MyOrderListActivity, tw4_list, R.layout.item_pinglun) {
                    override fun convert(holder: CommonViewHolder, model: CommonModel, position: Int) {
                        holder.setGlideImage(R.id.user_iv, url().total + model.user!!.head_img)
                        //http://pic.58pic.com/58pic/14/27/45/71r58PICmDM_1024.jpg
                        ///GlideImgManager.glideLoader(MainActivity.main, "http://p1.qzone.la/upload/3/c8e1arir.jpg", R.mipmap.cursevideo_share_qq, R.mipmap.cursevideo_share_qq, user_iv_header, 0)
                        //holder.setGlideImage(R.id.item_user_PLheader, "http://p1.qzone.la/upload/3/c8e1arir.jpg")
                        holder.setText(R.id.count_tv, model.content)
                        holder.setText(R.id.user_name_tv, model.user!!.nick)
                        holder.setText(R.id.time_tv, model.cdate)
                    }
                }
                main_lv.adapter = tw4_adapter
                load7()
                main_lv.setInterface {
                    page_index++
                    load7()
                }
                main_srl.setOnRefreshListener {
                    page_index = 1
                    load7()
                }
            }//获取我对老师的提问
            7 -> {
                xm5_adapter = object : CommonAdapter<ProjectModel>(MainActivity.main, xm5_list, R.layout.item_sp) {
                    override fun convert(holder: CommonViewHolder, model: ProjectModel, position: Int) {
                        holder.setText(R.id.tag_tv, model.subject)
                    }
                }
                main_lv.adapter = xm5_adapter
                OkGo.post(url().auth_api + "get_my_business")
                        .params("page", page_index)
                        .execute(object : JsonCallback<LzyResponse<PageModel<ProjectModel>>>() {
                            override fun onSuccess(t: LzyResponse<PageModel<ProjectModel>>, call: okhttp3.Call?, response: okhttp3.Response?) {
                                if (t.data != null) {
                                    xm5_list = t.data!!.list as MutableList<ProjectModel>
                                    xm5_adapter!!.refresh(xm5_list)
                                }
                            }

                            override fun onError(call: Call?, response: Response?, e: Exception?) {
                                toast(common().toast_error(e!!))
                            }
                        })
            }//获取我提交的项目列表
            2 -> {
                //我收藏的资讯
                toolbar.setCentertv("收藏的资讯")
                toolbar.setRighttv("编辑")
                toolbar.setRightClick {
                    if (IsEdit) {
                        IsEdit = false;
                        toolbar.setRighttv("编辑")
                    } else {
                        IsEdit = true;
                        toolbar.setRighttv("取消")
                    }
                    load6();
                }

                zx_adapter = object : CommonAdapter<SCModel>(this, sc6_list, R.layout.item_sczixun) {
                    override fun convert(holder: CommonViewHolder, kk: SCModel, position: Int) {
                        var model: ZiXunModel = kk.news!!

                        holder.setText(R.id.title_tv, model.title)
                        holder.setText(R.id.time_tv, model.show_time)
                        holder.setText(R.id.sc_tv, model.dianzan.toString())
                        holder.setText(R.id.pl_tv, model.comments.toString())
                        holder.setRoundFangImage(R.id.tag_iv, url().total + model.thumbnail)
                        if (IsEdit) {
                            holder.setVisible(R.id.sc_zixun_delete, true)
                        } else {
                            holder.setVisible(R.id.sc_zixun_delete, false)
                        }
                        //取消收藏
                        holder.setOnClickListener(R.id.sc_zixun_delete) { delete(kk.id) }

                    }
                }

                main_lv.adapter = zx_adapter
                load6()
                main_lv.setInterface {
                    page_index++
                    load6()
                }
                main_srl.setOnRefreshListener {
                    page_index = 1
                    load6()
                }
            }//课程收藏
            else -> {

            }//获取我收藏的课程,资讯
        }
        main_lv.setOnItemClickListener { parent, view, position, id ->
            val title = toolbar.center_str
            when (which) {
                3 -> {

                }
                4 -> {
                    startActivity(Intent(this, DetailPlayer::class.java).putExtra("cid", sp2_list!![position].id))
                }
                5 -> {
                    var s=""
                    startActivity(Intent(this, AskListActivity::class.java)
                            .putExtra("id", dy3_list[position].teacher_course!!.teacher!!.id)
                            .putExtra("cid", dy3_list[position].teacher_course!!.cid)
                            .putExtra("is_one", dy3_list[position].teacher_course_id)
                            .putExtra("name", dy3_list[position].teacher_course!!.cname + "-" + dy3_list[position].teacher_course!!.teacher_name)
                            .putExtra("answer_num", dy3_list[position].answer_unread))
                }
                2 -> {
                    startActivity(Intent(this, ZiXunDetailActivity::class.java)
                            .putExtra("cid", sc6_list!![position].news!!.id.toString())
                            .putExtra("title", "资讯")
                            .putExtra("is_dy", false))
                }
                else -> {
                    if (!("评论").equals(title)) {
                        startActivity(Intent(this, DetailPlayer::class.java).putExtra("cid", sc6_list!![position].course!!.id))
                    }
                }
            }
        }
        main_lv.setOnItemClickListener { parent, view, position, id ->
            val title = toolbar.center_str
            when (which) {
                3 -> {

                }
                4 -> {
                    startActivity(Intent(this, DetailPlayer::class.java).putExtra("cid", sp2_list!![position].id))
                }
                5 -> {
                    startActivity(Intent(this, AskListActivity::class.java)
                            .putExtra("id", dy3_list[position].teacher_course!!.teacher!!.id)
                            .putExtra("cid", dy3_list[position].teacher_course!!.cid)
                            .putExtra("is_one", dy3_list[position].teacher_course_id)
                            .putExtra("is_dy", true)//订阅跳转过去的
                            .putExtra("name", dy3_list[position].teacher_course!!.cname + "-" + dy3_list[position].teacher_course!!.teacher_name)
                            .putExtra("answer_num", dy3_list[position].answer_unread))
                }
                2 -> {
                    startActivity(Intent(this, ZiXunDetailActivity::class.java)
                            .putExtra("cid", sc6_list!![position].news!!.id.toString())
                            .putExtra("title", "资讯")
                            .putExtra("is_dy", false))
                }
                else -> {
                    if (!("评论").equals(title)) {
                        startActivity(Intent(this, DetailPlayer::class.java).putExtra("cid", sc6_list!![position].course!!.id))
                    }
                }
            }
        }
    }

    fun load3() {
        if (page_index == 1) {
            kc1_list.clear()
        }
        OkGo.post(url().auth_api + "get_my_orders")
                .params("page", page_index)
                .execute(object : JsonCallback<LzyResponse<PageModel<TradeModel>>>() {
                    override fun onSuccess(t: LzyResponse<PageModel<TradeModel>>, call: okhttp3.Call?, response: okhttp3.Response?) {
                        if (t.data != null) {
                            kc1_list.addAll(t.data!!.list as MutableList<TradeModel>)
                            kc1_adapter!!.refresh(kc1_list)
                            main_lv.getIndex(page_index, 10, t.data!!.count)
                            main_srl.isRefreshing = false
                            if (kc1_list!!.size == 0) {
                                error_ll.visibility = View.VISIBLE;
                                main_lv.visibility = View.GONE;
                            } else {
                                error_ll.visibility = View.GONE;
                                main_lv.visibility = View.VISIBLE;
                            }
                        }

                    }

                    override fun onError(call: Call?, response: Response?, e: Exception?) {
                        toast(common().toast_error(e!!))
                        main_srl.isRefreshing = false
                    }
                })
    }

    fun load4() {
        if (page_index == 1) {
            sp2_list.clear()
        }
        if (which_more == 1) {//加载更多视频
            free = intent.getStringExtra("free")
            OkGo.post(url().public_api + "get_phone_recommend_course_list")
                    .params("page", page_index)
                    .params("free", free)
                    .execute(object : JsonCallback<LzyResponse<PageModel<CoursesModel>>>() {
                        override fun onSuccess(t: LzyResponse<PageModel<CoursesModel>>, call: okhttp3.Call?, response: okhttp3.Response?) {
                            if (t.data != null) {
                                sp2_list = t.data!!.list as MutableList<CoursesModel>
                                sp2_adapter!!.refresh(sp2_list)
                                main_lv.getIndex(page_index, 20, sp2_list.size)
                                main_srl.isRefreshing = false

                                if (sp2_list!!.size == 0) {
                                    error_ll.visibility = View.VISIBLE;
                                    main_lv.visibility = View.GONE;
                                } else {
                                    error_ll.visibility = View.GONE;
                                    main_lv.visibility = View.VISIBLE;
                                }
                            }
                        }

                        override fun onError(call: Call?, response: Response?, e: Exception?) {
                            toast(common().toast_error(e!!))
                            main_srl.isRefreshing = false
                        }
                    })
        } else {
            OkGo.post(url().auth_api + "get_my_courses")
                    .params("page", page_index)
                    .execute(object : JsonCallback<LzyResponse<PageModel<PageCourse>>>() {
                        override fun onSuccess(t: LzyResponse<PageModel<PageCourse>>, call: okhttp3.Call?, response: okhttp3.Response?) {
                            if (t.data != null) {

                                for (list in t.data!!.list!!) {
                                    sp2_list.add(list.course!!)
                                }
                                sp2_adapter!!.refresh(sp2_list)
                                main_lv.getIndex(page_index, 20, sp2_list.size)
                                main_srl.isRefreshing = false

                                if (sp2_list!!.size == 0) {
                                    error_ll.visibility = View.VISIBLE;
                                    main_lv.visibility = View.GONE;
                                } else {
                                    error_ll.visibility = View.GONE;
                                    main_lv.visibility = View.VISIBLE;
                                }
                            }
                        }

                        override fun onError(call: Call?, response: Response?, e: Exception?) {
                            toast(common().toast_error(e!!))
                            main_srl.isRefreshing = false
                        }
                    })
        }

    }

    fun load5() {
        if (page_index == 1) {
            dy3_list.clear()
        }
        OkGo.post(url().auth_api + "get_my_teacher_courses")
                .params("page", page_index)
                .execute(object : JsonCallback<LzyResponse<PageModel<OrderClassModel>>>() {
                    override fun onSuccess(t: LzyResponse<PageModel<OrderClassModel>>, call: okhttp3.Call?, response: okhttp3.Response?) {
                        if (t.data != null) {

                            dy3_list = t.data!!.list as MutableList<OrderClassModel>
                            dy3_adapter!!.refresh(dy3_list)
                            main_lv.getIndex(page_index, 20, dy3_list.size)
                            main_srl.isRefreshing = false

                            if (dy3_list!!.size == 0) {
                                error_ll.visibility = View.VISIBLE;
                                main_lv.visibility = View.GONE;
                            } else {
                                error_ll.visibility = View.GONE;
                                main_lv.visibility = View.VISIBLE;
                            }
                        }
//                        main_lv.setOnItemClickListener { parent, view, position, id ->
//                            startActivity(Intent(this@MyOrderListActivity, AskListActivity::class.java).putExtra("id", dy3_list[position].id).putExtra("name", dy3_list[position].teacher_course!!.teacher_name))
//                        }
                    }

                    override fun onError(call: Call?, response: Response?, e: Exception?) {
                        toast(common().toast_error(e!!))
                        main_srl.isRefreshing = false
                    }
                })
    }

    fun load6() {
        if (page_index == 1) {
            sc6_list.clear()
        }
        OkGo.post(url().auth_api + "get_my_favorite")
                .params("page", page_index)
                .params("which", which)//1.收藏课程，2.收藏资讯
                .execute(object : JsonCallback<LzyResponse<PageModel<SCModel>>>() {
                    override fun onSuccess(t: LzyResponse<PageModel<SCModel>>, call: okhttp3.Call?, response: okhttp3.Response?) {
                        if (t.data != null) {

                            sc6_list = t.data!!.list as MutableList<SCModel>
                            when (which) {
                                1 -> {
                                    sc6_adapter!!.refresh(sc6_list)
                                }
                                else -> {
                                    zx_adapter!!.refresh(sc6_list)
                                }
                            }
                            main_lv.getIndex(page_index, 20, sc6_list.size)
                            main_srl.isRefreshing = false
                            if (sc6_list!!.size == 0) {
                                error_ll.visibility = View.VISIBLE;
                                main_lv.visibility = View.GONE;
                            } else {
                                error_ll.visibility = View.GONE;
                                main_lv.visibility = View.VISIBLE;
                            }
                        }
                    }

                    override fun onError(call: Call?, response: Response?, e: Exception?) {
                        toast(common().toast_error(e!!))
                        main_srl.isRefreshing = false
                    }
                })
    }

    fun delete(id: Int) {
        //删除收藏的课程，资讯
        OkGo.post(url().auth_api + "del_my_favorite")
                .params("id", id)
                .execute(object : JsonCallback<LzyResponse<String>>() {
                    override fun onSuccess(t: LzyResponse<String>, call: okhttp3.Call?, response: okhttp3.Response?) {
                        if (t.code == 0) {
                            load6()
                        } else {
                            toast("取消收藏失败")
                        }
                    }

                    override fun onError(call: Call?, response: Response?, e: Exception?) {
                        toast(common().toast_error(e!!))

                    }
                })
    }

    /**
     * 根据id加载资讯评论列表
     * */
    fun load7() {
        OkGo.post(url().public_api + "get_news_comments")
                .params("id", zx_id)
                .params("page", page_index)
                .execute(object : JsonCallback<LzyResponse<PageModel<CommonModel>>>() {
                    override fun onSuccess(t: LzyResponse<PageModel<CommonModel>>, call: okhttp3.Call?, response: okhttp3.Response?) {
                        if (t.data != null) {

                            tw4_list = t.data!!.list as MutableList<CommonModel>
                            tw4_adapter!!.refresh(tw4_list)
                            main_lv.getIndex(page_index, 20, tw4_list.size)
                            main_srl.isRefreshing = false

                            if (tw4_list!!.size == 0) {
                                error_ll.visibility = View.VISIBLE;
                                main_lv.visibility = View.GONE;
                            } else {
                                error_ll.visibility = View.GONE;
                                main_lv.visibility = View.VISIBLE;
                            }
                        }
                    }

                    override fun onError(call: Call?, response: Response?, e: Exception?) {
                        toast(common().toast_error(e!!))
                    }
                })
    }

    override fun initEvents() {

    }
}
