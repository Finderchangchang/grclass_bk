package wai.gr.cla.ui

import com.lzy.okgo.OkGo
import kotlinx.android.synthetic.main.activity_jifen_list.*

import okhttp3.Call
import okhttp3.Response

import java.util.ArrayList

import wai.gr.cla.R
import wai.gr.cla.base.BaseActivity
import wai.gr.cla.callback.DialogCallback
import wai.gr.cla.callback.JsonCallback
import wai.gr.cla.method.CommonAdapter
import wai.gr.cla.method.CommonViewHolder
import wai.gr.cla.method.common
import wai.gr.cla.model.*

/**
 * 积分排行列表
 * Created by Administrator on 2017/5/19.
 */

class JiFenListActivity : BaseActivity() {
    var list: List<JiFenModel>? = null
    var adapter: CommonAdapter<JiFenModel>? = null
    var model: JiFenModel? = null//顶部显示的内容
    override fun setLayout(): Int {
        return R.layout.activity_jifen_list
    }

    override fun initViews() {
        list = ArrayList<JiFenModel>()
        toolbar!!.setLeftClick { finish() }
    }

    override fun initEvents() {
        OkGo.post(url().auth_api + "get_user_level")
                .execute(object : JsonCallback<LzyResponse<String>>() {
                    override fun onSuccess(t: LzyResponse<String>, call: okhttp3.Call?, response: okhttp3.Response?) {
                        if (t.code == 0) {
                            when (t.data) {
                                "member_lv1.png" -> {
                                    gb_iv!!.setImageResource(R.mipmap.member_lv1)
                                }
                                "member_lv2.png" -> {
                                    gb_iv!!.setImageResource(R.mipmap.member_lv2)
                                }
                                "member_lv3.png" -> {
                                    gb_iv!!.setImageResource(R.mipmap.member_lv3)
                                }
                                "member_lv4.png" -> {
                                    gb_iv!!.setImageResource(R.mipmap.member_lv4)
                                }
                            }
                        }
                    }
                })
        adapter = object : CommonAdapter<JiFenModel>(this, list, R.layout.item_jifen) {
            override fun convert(holder: CommonViewHolder, model: JiFenModel, position: Int) {
                when (model.rank.toString().length) {
                    1 -> {
                        holder.setText(R.id.num_tv, "NO.0" + model.rank)
                    }
                    else -> {
                        holder.setText(R.id.num_tv, "NO." + model.rank)
                    }
                }

                holder.setGlideImageYuan(R.id.header_iv, url().total + model.head_img)
                holder.setText(R.id.name_tv, model.nick)
                holder.setText(R.id.gb_tv, model.guanbi.toString())
            }
        }
        loadData()
    }

    fun loadData() {
        OkGo.get(url().auth_api + "guanbi_rank_list")
                .execute(object : DialogCallback<LzyResponse<RankModel>>(this) {
                    override fun onSuccess(t: LzyResponse<RankModel>, call: okhttp3.Call?, response: okhttp3.Response?) {
                        if (t.code == 0) {
                            list = t.data!!.rank
                            jifen_list_lv!!.adapter = adapter
                            adapter!!.refresh(list)
                            model = t.data!!.info
                            when (model!!.rank.toString().length) {
                                1 -> {
                                    num_tv.text = "NO.0" + model!!.rank
                                }
                                else -> {
                                    num_tv.text = "NO.0" + model!!.rank
                                }
                            }
//                            GlideImgManager.glideLoader(this@JiFenListActivity, url().total + model!!.head_img, R.mipmap.error_yuan1, R.mipmap.error_yuan1, header_iv, 0)
                            name_tv.text = model!!.nick
                            gb_tv.text = model!!.guanbi.toString()
                            if(list==null||list!!.size==0){
                                toast("当前无数据")

                            }
                        } else {
                            toast(t.msg.toString())
                        }
                    }

                    override fun onError(call: Call?, response: Response?, e: Exception?) {
                        toast(common().toast_error(e!!))
                    }
                })
    }
}
