package wai.gr.cla.ui

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.lzy.okgo.OkGo
import kotlinx.android.synthetic.main.activity_serach_word1.*
import okhttp3.Call
import okhttp3.Response
import wai.gr.cla.R
import wai.gr.cla.base.BaseActivity
import wai.gr.cla.callback.JsonCallback
import wai.gr.cla.method.CommonAdapter
import wai.gr.cla.method.CommonViewHolder
import wai.gr.cla.method.Utils
import wai.gr.cla.method.common
import wai.gr.cla.model.*
import java.util.*

/**
 * Created by JX on 2017/5/21.
 * 搜索
 */

class SearchWord1Activity : BaseActivity() {
    var tj_list = ArrayList<TuiJianModel>()//视频
    var tj_adapter: CommonAdapter<TuiJianModel>? = null//视频
    var zx_adapter: CommonAdapter<ZiXunModel>? = null//资讯
    var zx_list = ArrayList<ZiXunModel>()
    var position = 0
    override fun setLayout(): Int {
        return R.layout.activity_serach_word1
    }

    override fun initViews() {
        et_search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.length > 0) {
                    tv_cancel.text = "搜索"
                } else {
                    tv_cancel.text = "取消"
                }
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })
        position = intent.getIntExtra("position", 0)
        when (position) {
            0 -> {
                tj_adapter = object : CommonAdapter<TuiJianModel>(this, tj_list, R.layout.item_sp) {
                    override fun convert(holder: CommonViewHolder, model: TuiJianModel, position: Int) {
                        holder.setText(R.id.tag_tv, model.title)
                        holder.setTopRoundImage(R.id.tag_iv, url().total + model.thumbnail)
                        if (model.price.equals("0.00") || model.price.equals("0")) {
                            holder.setText(R.id.price_tv, "免费")
                        } else {
                            holder.setText(R.id.price_tv, "￥" + model.price)
                        }
                    }
                }
                et_search.hint = "搜索视频"
                jp_gv.visibility = View.VISIBLE
                jp_gv.adapter = tj_adapter
                jp_gv.setOnItemClickListener { adapterView, view, i, l ->
                    val intent = Intent(this, DetailPlayer::class.java)
                    intent.putExtra("cid", tj_list!![i].id)
                    startActivity(intent)
                }
                jp_gv.setIsValid(null)
                jp_gv.setInterface {
                    page_index++
                    load1(et_search.text.toString().trim())//视频
                }
                list_lv.visibility = View.GONE
            }
            else -> {
                et_search.hint = "搜索资讯"
                zx_adapter = object : CommonAdapter<ZiXunModel>(this, zx_list, R.layout.item_zixun) {
                    override fun convert(holder: CommonViewHolder, model: ZiXunModel, position: Int) {
                        holder.setGlideImage(R.id.user_iv, model.author_img)
                        holder.setText(R.id.title_tv, model.title)
                        holder.setText(R.id.data_tv, model.show_time)
                        holder.setText(R.id.sc_tv, model.dianzan.toString())
                        holder.setText(R.id.pl_tv, model.comments.toString())
                    }
                }
                jp_gv.visibility = View.GONE
                list_lv.visibility = View.VISIBLE
                list_lv.setOnItemClickListener { adapterView, view, i, l ->
                    val intent = Intent(this, ZiXunDetailActivity::class.java)
                    intent.putExtra("cid", zx_list!![i].id.toString())
                    intent.putExtra("title", "资讯")
                    startActivity(intent)
                }
                list_lv.setIsValid(null)

                list_lv.adapter = zx_adapter
                list_lv.setInterface {
                    page_index++
                    load2(et_search.text.toString().trim())//资讯
                }
            }
        }
        tv_cancel.setOnClickListener {
            var txt = tv_cancel.text.toString().trim()
            if (!txt.equals("取消")) {//输入内容以后可以进行查询
                when (position) {
                    0 -> load1(et_search.text.toString().trim())//视频
                    else -> load2(et_search.text.toString().trim())//资讯
                }
            } else {//关闭当前页面
                finish()
            }
        }

    }

    var page_index = 1
    /**
     * 根据key获得视频列表
     * */
    fun load1(key: String) {
        OkGo.post(url().public_api + "search_phone_course_data")
                .params("page", page_index)// 请求方式和请求url
                .params("title", key)// 请求方式和请求url
                .execute(object : JsonCallback<LzyResponse<PageModel<TuiJianModel>>>() {
                    override fun onSuccess(t: LzyResponse<PageModel<TuiJianModel>>, call: okhttp3.Call?, response: okhttp3.Response?) {
                        if (t.code == 0) {
                            if (page_index == 1) {
                                tj_list.clear()
                            }
                            tj_list.addAll(t.data!!.list as ArrayList<TuiJianModel>)
                            jp_gv!!.getIndex(page_index, 20, t!!.data!!.count)
                            tj_adapter!!.refresh(tj_list)
                            if (tj_list.size == 0) {
                                toast("暂无搜索数据，请换个关键词")
                                jp_gv!!.getIndex(page_index, 20, 0)
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

    /**
     * 根据key获得资讯列表
     * */
    fun load2(key: String) {
        OkGo.post(url().public_api + "get_phone_news_list")
                .params("page", page_index)// 请求方式和请求url
                .params("title", key)// 请求方式和请求url
                .execute(object : JsonCallback<LzyResponse<PageModel<ZiXunModel>>>() {
                    override fun onSuccess(t: LzyResponse<PageModel<ZiXunModel>>, call: okhttp3.Call?, response: okhttp3.Response?) {
                        if (t.code == 0) {
                            if(page_index==1){
                                zx_list= ArrayList()
                            }
                            zx_list.addAll(t.data!!.list as ArrayList<ZiXunModel>)
                            zx_adapter!!.refresh(zx_list)
                            list_lv!!.getIndex(page_index, 20, t!!.data!!.count)

                            if (zx_list.size == 0) {
                                toast("暂无搜索数据，请换个关键词")
                                list_lv!!.getIndex(page_index, 20, 0)

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


    override fun initEvents() {
        //资讯是一个列表
    }
}
