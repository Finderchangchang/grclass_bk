package wai.gr.cla.ui

import android.content.Intent
import android.text.TextUtils
import android.view.View
import com.lzy.okgo.OkGo
import kotlinx.android.synthetic.main.activity_ask_list.*
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
 * 提问管理
 * */
class AskListActivity : BaseActivity() {
    var zx_adapter: CommonAdapter<ZiXunModel>? = null//资讯
    var zx_list = ArrayList<ZiXunModel>()
    internal var page_index = 1//当前页数
    internal var teacher_id = 1//老师id
    internal var cid = 0
    var class_id = 0//课程编号
    var answer_list = ArrayList<AnswerModel>()
    var is_dy = false
    var answer_adapter: CommonAdapter<AnswerModel>? = null//资讯
    override fun setLayout(): Int {
        return R.layout.activity_ask_list
    }

    var user_id = Utils.getCache(key.KEY_USERID)
    override fun initViews() {
        teacher_id = intent.getIntExtra("id", 1)
        cid = intent.getIntExtra("cid", 0)
        class_id = intent.getIntExtra("is_one", 0)
        is_dy = intent.getBooleanExtra("is_dy", false)
        toolbar.center_str = intent.getStringExtra("name")
        zx_adapter = object : CommonAdapter<ZiXunModel>(this, zx_list, R.layout.item_zixun) {
            override fun convert(holder: CommonViewHolder, model: ZiXunModel, position: Int) {
                holder.setGlideImage(R.id.user_iv, model.author_img)
                holder.setText(R.id.title_tv, model.title)
                holder.setText(R.id.data_tv, model.show_time)
                //holder.setText(R.id.sc_tv, model.dots.toString())
                //holder.setText(R.id.pl_tv, model.comments.toString())
                holder.setInVisible(R.id.zixun_item_xihuan)
                holder.setInVisible(R.id.item_zixun_ll_dot)
            }
        }
        asl_llv.setIsValid(object : OnlyLoadListView.OnSwipeIsValid {
            override fun setSwipeEnabledTrue() {
                main_srl.isEnabled = true
            }

            override fun setSwipeEnabledFalse() {
                main_srl.isEnabled = false
            }
        })
        answer_adapter = object : CommonAdapter<AnswerModel>(this, answer_list, R.layout.item_wenda) {
            override fun convert(holder: CommonViewHolder, model: AnswerModel, position: Int) {
                if (TextUtils.isEmpty(model.answer)) {
                    holder.setVisible(R.id.da_time_tv, false)
                    holder.setText(R.id.da_tv, "暂无文字回复")
                } else {
                    holder.setVisible(R.id.da_time_tv, true)
                    holder.setText(R.id.da_time_tv, model.answer_date)
                    holder.setText(R.id.da_tv, model.answer)
                }
                if (TextUtils.isEmpty(model.question)) {
                    holder.setText(R.id.wen_tv, "无")
                } else {
                    holder.setText(R.id.wen_tv, model.question)
                }
                holder.setText(R.id.time_tv, model.cdate)
                if (model.answer_images!!.size > 0) {
                    holder.setVisible(R.id.da_ll, true)
                    when (model.answer_images!!.size) {
                        1 -> holder.setGImage(R.id.da1_iv, url().total + model.answer_images!![0])
                        2 -> {
                            holder.setGImage(R.id.da1_iv, url().total + model.answer_images!![0])
                            holder.setGImage(R.id.da2_iv, url().total + model.answer_images!![1])
                        }
                        3 -> {
                            holder.setGImage(R.id.da1_iv, url().total + model.answer_images!![0])
                            holder.setGImage(R.id.da2_iv, url().total + model.answer_images!![1])
                            holder.setGImage(R.id.da3_iv, url().total + model.answer_images!![2])
                        }
                    }
                    holder.setOnClickListener(R.id.da1_iv) {
                        startActivity(Intent(this@AskListActivity, ImgDetailActivity::class.java)
                                .putExtra("url", model)
                                .putExtra("position", -1)
                        )
                    }
                    holder.setOnClickListener(R.id.da2_iv) {
                        startActivity(Intent(this@AskListActivity, ImgDetailActivity::class.java)
                                .putExtra("url", model)
                                .putExtra("position", -2)
                        )
                    }
                    holder.setOnClickListener(R.id.da3_iv) {
                        startActivity(Intent(this@AskListActivity, ImgDetailActivity::class.java)
                                .putExtra("url", model)
                                .putExtra("position", -3)
                        )
                    }
                }
                if (model.question_images != null) {
                    if (model.question_images!!.size > 0) {
                        holder.setVisible(R.id.wen_ll, true)
                        when (model.question_images!!.size) {
                            1 -> holder.setGImage(R.id.wen1_iv, url().total + model.question_images!![0])
                            2 -> {
                                holder.setGImage(R.id.wen1_iv, url().total + model.question_images!![0])
                                holder.setGImage(R.id.wen2_iv, url().total + model.question_images!![1])
                            }
                            3 -> {
                                holder.setGImage(R.id.wen1_iv, url().total + model.question_images!![0])
                                holder.setGImage(R.id.wen2_iv, url().total + model.question_images!![1])
                                holder.setGImage(R.id.wen3_iv, url().total + model.question_images!![2])
                            }
                        }
                        holder.setOnClickListener(R.id.wen1_iv) {
                            startActivity(Intent(this@AskListActivity, ImgDetailActivity::class.java)
                                    .putExtra("url", model)
                                    .putExtra("position", 1)
                            )
                        }
                        holder.setOnClickListener(R.id.wen2_iv) {
                            startActivity(Intent(this@AskListActivity, ImgDetailActivity::class.java)
                                    .putExtra("url", model)
                                    .putExtra("position", 2)
                            )
                        }
                        holder.setOnClickListener(R.id.wen3_iv) {
                            startActivity(Intent(this@AskListActivity, ImgDetailActivity::class.java)
                                    .putExtra("url", model)
                                    .putExtra("position", 3)
                            )
                        }
                    }

                }
            }
        }
        toolbar.setLeftClick { finish() }
//        if (class_id == 0) {
//            asl_llv.adapter = zx_adapter
//            asl_llv.setOnItemClickListener { adapterView, view, i, l ->
//                val intent = Intent(MainActivity.main, ZiXunDetailActivity::class.java)
//                intent.putExtra("cid", zx_list[i].id.toString())
//                intent.putExtra("title", "资讯")
//                intent.putExtra("is_dy", true)
//                startActivity(intent)
//            }
//        } else {
        asl_llv.adapter = answer_adapter
//        }
        main_srl.setOnRefreshListener { loadData(1) }//刷新加载数据
        //asl_llv.emptyView = error_ll
        asl_llv.setInterface { loadData(page_index++) }//上划加载更多数据
        ask_btn.setOnClickListener {
            skip(true)
        }
        hf_rl.setOnClickListener {
            skip(false)
        }
        loadData(1)
        val num = intent.getIntExtra("answer_num", 0)
        if (num > 0) {
            answer_num_tv.visibility = View.VISIBLE
            answer_num_tv.text = num.toString()
        }
        user_can_ask()
    }

    /**
     * 跳页处理
     * @param to_ask true:提问。false：回复页
     * */
    fun skip(to_ask: Boolean) {
        if (TextUtils.isEmpty(user_id)) {
            toast("请先登录")
        } else if (can_ask) {//跳转到添加提问页面
            if (to_ask) {
                startActivityForResult(Intent(this@AskListActivity, AddTeacherAskActivity::class.java)
                        .putExtra("teacher_id", price_list!![0].teacher_course_id), 0)
            } else {
                startActivityForResult(Intent(this@AskListActivity, HFActivity::class.java)
                        .putExtra("teacher_id", price_list!![0].teacher_course_id), 0)
            }
        } else {
            if (price_list!!.isNotEmpty()) {
                if (s == null) {
                    s = ZhiFuPopuWindowActivity(null, null, this,
                            ask_btn, true, price_list!![0].teacher_course_id.toString(), price_list!!, 0)
                }
                s!!.showWindow()
                s!!.setOnDismissListener { user_can_ask() }//13091201967

            } else {
                toast("此老师暂时未开通答疑专栏")
            }
        }
    }

    var s: ZhiFuPopuWindowActivity? = null
    internal var list: ArrayList<Teacher> = ArrayList<Teacher>()
    /**
     * 加载数据
     * */
    fun loadData(index: Int) {
        page_index = index//获得当前要加载页面id
        main_srl.isRefreshing = true
//        if (class_id == 0) {
//            OkGo.get(url().public_api + "get_phone_teacher_news_list")
//                    .params("teacher_id", teacher_id)
//                    .params("page", page_index)
//                    .execute(object : JsonCallback<LzyResponse<PageModel<ZiXunModel>>>() {
//                        override fun onSuccess(t: LzyResponse<PageModel<ZiXunModel>>, call: okhttp3.Call?, response: okhttp3.Response?) {
//                            zx_list = t.data!!.list as ArrayList<ZiXunModel>
//                            zx_adapter!!.refresh(zx_list)
//                            asl_llv.getIndex(page_index, 20, zx_list.size)
//                            main_srl.isRefreshing = false
//                            if (zx_list.size == 0) {
//                                error_ll.visibility = View.VISIBLE;
//                            } else {
//                                error_ll.visibility = View.GONE;
//                            }
//                        }
//
//                        override fun onError(call: Call?, response: Response?, e: Exception?) {
//                            //toast(common().toast_error(e!!))
//                            main_srl.isRefreshing = false
//                        }
//                    })
//        } else {
        var s = OkGo.get(url().public_api + "get_course_answer_list")
        var url = url().public_api + "get_course_answer_list"
        if (is_dy) {
            s = OkGo.get(url().public_api + "get_course_answer_list_by_teacher_course_id")
            s.params("teacher_course_id", class_id)
        } else {
            s.params("id", class_id)
        }

        s.execute(object : JsonCallback<LzyResponse<List<AnswerModel>>>() {
            override fun onSuccess(t: LzyResponse<List<AnswerModel>>, call: okhttp3.Call?, response: okhttp3.Response?) {
                answer_list = t.data!! as ArrayList<AnswerModel>
                answer_adapter!!.refresh(answer_list)
                asl_llv.getIndex(page_index, 20, answer_list.size)
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

    var can_ask = false
    var teacher: Teacher? = null
    var price_list: List<PriceModel>? = ArrayList<PriceModel>()
    fun user_can_ask() {

        if (TextUtils.isEmpty(user_id)) {
            toast("请先登录")
        } else {
            OkGo.get(url().auth_api + "get_phone_one_teacher")
                    .params("cid", cid)
                    .execute(object : JsonCallback<LzyResponse<Teacher>>() {
                        override fun onSuccess(t: LzyResponse<Teacher>, call: okhttp3.Call?, response: okhttp3.Response?) {
                            if (t.code == 0) {
                                teacher = t.data
                                can_ask = t.data!!.i_can_ask
                                price_list = t.data!!.price_list
                            }
                        }

                        override fun onError(call: Call?, response: Response?, e: Exception?) {
                            //toast(common().toast_error(e!!))
                        }
                    })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 88) {
            skip(false)
        }
        user_can_ask()
    }

    override fun onResume() {
        super.onResume()
        user_can_ask()
    }

    override fun initEvents() {

    }
}