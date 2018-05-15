package wai.gr.cla.ui

import android.content.Intent
import android.text.TextUtils
import com.lzy.okgo.OkGo
import kotlinx.android.synthetic.main.activity_hf.*
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
 * 查看回复
 * */
class HFActivity : BaseActivity() {
    internal var kc1_list: MutableList<AnswerModel> = ArrayList()
    var kc1_adapter: CommonAdapter<AnswerModel>? = null
    override fun setLayout(): Int {
        return R.layout.activity_hf
    }

    override fun initViews() {
        toolbar.setLeftClick { finish() }
        id = intent.getIntExtra("teacher_id", 0)
        kc1_adapter = object : CommonAdapter<AnswerModel>(this, kc1_list, R.layout.item_wenda) {
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
                        startActivity(Intent(this@HFActivity, ImgDetailActivity::class.java)
                                .putExtra("url", model)
                                .putExtra("position", -1)
                        )
                    }
                    holder.setOnClickListener(R.id.da2_iv) {
                        startActivity(Intent(this@HFActivity, ImgDetailActivity::class.java)
                                .putExtra("url", model)
                                .putExtra("position", -2)
                        )
                    }
                    holder.setOnClickListener(R.id.da3_iv) {
                        startActivity(Intent(this@HFActivity, ImgDetailActivity::class.java)
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
                            startActivity(Intent(this@HFActivity, ImgDetailActivity::class.java)
                                    .putExtra("url", model)
                                    .putExtra("position", 1)
                            )
                        }
                        holder.setOnClickListener(R.id.wen2_iv) {
                            startActivity(Intent(this@HFActivity, ImgDetailActivity::class.java)
                                    .putExtra("url", model)
                                    .putExtra("position", 2)
                            )
                        }
                        holder.setOnClickListener(R.id.wen3_iv) {
                            startActivity(Intent(this@HFActivity, ImgDetailActivity::class.java)
                                    .putExtra("url", model)
                                    .putExtra("position", 3)
                            )
                        }
                    }

                }
            }
        }
        main_lv.adapter = kc1_adapter
        main_srl.setOnRefreshListener {
            load()
        }
        main_lv.setInterface {
            page_index++
            load()
        }//上划加载更多数据
        main_lv.setIsValid(object : OnlyLoadListView.OnSwipeIsValid {
            override fun setSwipeEnabledTrue() {
                main_srl.isEnabled = true
            }

            override fun setSwipeEnabledFalse() {
                main_srl.isEnabled = false
            }
        })
    }

    var id = 0
    var page_index = 1
    override fun initEvents() {
        load()
    }

    fun load() {
        OkGo.post(url().auth_api + "get_my_course_quiz")
                .params("teacher_course_id", id)
                .params("page", page_index)
                .execute(object : JsonCallback<LzyResponse<PageModel<AnswerModel>>>() {
                    override fun onSuccess(t: LzyResponse<PageModel<AnswerModel>>, call: okhttp3.Call?, response: okhttp3.Response?) {
                        if (page_index == 1) {
                            kc1_list = ArrayList()
                        }
                        kc1_list.addAll(t.data!!.list as MutableList<AnswerModel>)
                        kc1_adapter!!.refresh(kc1_list)
                        main_srl.isRefreshing = false
                        main_lv.getIndex(page_index, 20, kc1_list.size)

                    }

                    override fun onError(call: Call?, response: Response?, e: Exception?) {
                        toast(common().toast_error(e!!))
                        main_srl.isRefreshing = false
                    }
                })
    }
}
