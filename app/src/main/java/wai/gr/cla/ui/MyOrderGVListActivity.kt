package wai.gr.cla.ui

import android.content.Intent
import android.view.View
import android.widget.LinearLayout
import com.lzy.okgo.OkGo
import kotlinx.android.synthetic.main.activity_my_order_gv_list.*
import okhttp3.Call
import okhttp3.Response

import wai.gr.cla.R
import wai.gr.cla.base.BaseActivity
import wai.gr.cla.callback.JsonCallback
import wai.gr.cla.method.CommonAdapter
import wai.gr.cla.method.CommonViewHolder
import wai.gr.cla.method.OnlyLoadGridView
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
class MyOrderGVListActivity : BaseActivity() {
    var page_index: Int = 1//当前页数
    var which: Int = 1//当前页面内容
    var IsEdit: Boolean = false
    var which_more: Int = 1//更多1,视频或2,咨询
    internal var sp2_list: MutableList<CoursesModel> = ArrayList()//我的视频课程
    var sp2_adapter: CommonAdapter<CoursesModel>? = null//我的视频课程

    internal var sc6_list: MutableList<SCModel> = ArrayList()//我的收藏
    var sc6_adapter: CommonAdapter<SCModel>? = null//我的提问
    override fun setLayout(): Int {
        return R.layout.activity_my_order_gv_list
    }

    var zx_id = 0//传递过来的资讯的id
    var free: String = ""
    override fun initViews() {
        which = intent.getIntExtra("which", 1)
        which_more = intent.getIntExtra("which_more", 0)
        free = intent!!.getStringExtra("free")


        toolbar.setLeftClick { finish() }
//        main_gv.viewTreeObserver.addOnScrollChangedListener {
//            main_srl!!.isEnabled = main_gv.scrollY === 0
//        }
        sp2_adapter = object : CommonAdapter<CoursesModel>(this, sp2_list, R.layout.item_sp) {
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
        when (which) {
            4 -> {
                if (which_more != 0) {
                    when (free) {
                        "1" -> toolbar.setCentertv("讲座直播")
                        "0" -> toolbar.setCentertv("更多精品课")
                        "2" -> toolbar.setCentertv("更多资格证考试")
                        "3" -> toolbar.setCentertv("更多直播课")
                    }
                } else {
                    toolbar.setCentertv("我的课程")
                }

                main_gv.numColumns = 3
                main_gv.adapter = sp2_adapter
                load4()
                main_gv.setInterface {
                    page_index++
                    load4()
                }
                main_srl.setOnRefreshListener {
                    page_index = 1
                    load4()
                }
            }//获取我的视频课程
            else -> {
                toolbar.setCentertv("收藏的课程")
                toolbar.setRighttv("编辑")
                toolbar.setRightClick {
                    if (IsEdit) {
                        IsEdit = false
                        toolbar.setRighttv("编辑")
                    } else {
                        IsEdit = true
                        toolbar.setRighttv("取消")
                    }
                    load6();
                }


                sc6_adapter = object : CommonAdapter<SCModel>(this, sc6_list, R.layout.item_sp) {
                    override fun convert(holder: CommonViewHolder, kk: SCModel, position: Int) {
                        var model: ZiXunModel = kk.course!!
                        holder.setText(R.id.tag_tv, model.title)
                        holder.setGImage(R.id.tag_iv, url().total + model.thumbnail)
                        if (IsEdit) {
                            holder.setVisible(R.id.sc_delete, true)
                        } else {
                            holder.setVisible(R.id.sc_delete, false)
                        }
                        holder.setOnClickListener(R.id.sc_delete, View.OnClickListener {
                            //取消收藏
                            delete(kk.id)

                        })

//                        holder.setText(R.id.item_jiuye_tiem, model.cdate)
//                        holder.setText(R.id.jiuyt_tv_title, model.title)
//                        holder.setRoundImage(R.id.jiuye_iv_image, url().total + model.thumbnail)
//                        holder.setText(R.id.jiuye_tv_shoucang, model.dots.toString())
//                        holder.setText(R.id.jiuyt_tv_pinglun, model.comments.toString())
                    }
                }
                main_gv.numColumns = 3
                main_gv.adapter = sc6_adapter
                //order_rl.setBackgroundColor(0x0000)
                load6()
                main_gv.setInterface {
                    page_index++
                    load6()
                }
                main_srl.setOnRefreshListener {
                    page_index = 1
                    load6()
                }
            }//获取我收藏的课程,资讯
        }
        main_gv.setOnItemClickListener { parent, view, position, id ->
            val title = toolbar.center_str
            //4 -> {
//         //   startActivity(Intent(this, DetailPlayer::class.java).putExtra("cid", sp2_list!![position].id).putExtra("is_live", true))
//        //}
            when (which) {
                4 -> {
                    var is_live = false
                    if (free == "3") is_live = true
                    startActivity(Intent(this, DetailPlayer::class.java)
                            .putExtra("is_live", is_live)
                            .putExtra("cid", sp2_list!![position].id))
                }
                else -> startActivity(Intent(this, DetailPlayer::class.java).putExtra("is_live", true).putExtra("cid", sc6_list!![position].course!!.id))
            }
        }
        main_gv.setIsValid(object : OnlyLoadGridView.OnSwipeIsValid {
            override fun setSwipeEnabledTrue() {
                main_srl.isEnabled = true
            }

            override fun setSwipeEnabledFalse() {
                main_srl.isEnabled = false
            }
        })
    }


    fun load4() {
        if (page_index == 1) {
            sp2_list.clear()
        }
        if (which_more == 1) {//加载更多视频
            var okgo = OkGo.post(url().public_api + "get_phone_recommend_course_list")
                    .params("page", page_index)
                    .params("free", free)
            if (free.equals("3")) {
                okgo = OkGo.post(url().public_api + "get_zhibo_course")
                        .params("page", page_index)
            }
            okgo.execute(object : JsonCallback<LzyResponse<PageModel<CoursesModel>>>() {
                override fun onSuccess(t: LzyResponse<PageModel<CoursesModel>>, call: okhttp3.Call?, response: okhttp3.Response?) {
                    sp2_list.addAll(t.data!!.list as MutableList<CoursesModel>)
                    sp2_adapter!!.refresh(sp2_list)
                    main_gv.getIndex(page_index, 20, t.data!!.count)
                    main_srl.isRefreshing = false

                    if (sp2_list!!.size == 0) {
                        error_ll.visibility = View.VISIBLE;
                        main_gv.visibility = View.GONE;
                    } else {
                        error_ll.visibility = View.GONE;
                        main_gv.visibility = View.VISIBLE;
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
                            for (list in t.data!!.list!!) {
                                sp2_list.add(list.course!!)
                            }

                            sp2_adapter!!.refresh(sp2_list)
                            main_gv.getIndex(page_index, 20, t.data!!.count)
                            main_srl.isRefreshing = false

                            if (sp2_list!!.size == 0) {
                                error_ll.visibility = View.VISIBLE;
                                main_gv.visibility = View.GONE;
                            } else {
                                error_ll.visibility = View.GONE;
                                main_gv.visibility = View.VISIBLE;
                            }
                        }

                        override fun onError(call: Call?, response: Response?, e: Exception?) {
                            toast(common().toast_error(e!!))
                            main_srl.isRefreshing = false
                        }
                    })
        }

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
                        sc6_list.addAll(t.data!!.list as MutableList<SCModel>)
                        sc6_adapter!!.refresh(sc6_list)
                        main_gv.getIndex(page_index, 21, t.data!!.count)
                        main_srl.isRefreshing = false
                        if (sc6_list!!.size == 0) {
                            error_ll.visibility = View.VISIBLE;
                            //order_sl.visibility = View.GONE;
                        } else {
                            error_ll.visibility = View.GONE;
                            //order_sl.visibility = View.VISIBLE;
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
                            toast("取消收藏成功")
                        } else {
                            toast("取消收藏失败")
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
