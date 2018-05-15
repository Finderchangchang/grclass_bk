package wai.gr.cla.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_result.*

import wai.gr.cla.R
import wai.gr.cla.base.BaseActivity
import wai.gr.cla.method.CommonAdapter
import wai.gr.cla.method.CommonViewHolder
import wai.gr.cla.model.DataBean
import wai.gr.cla.model.url
import java.util.*

/**
 * 显示更多
 * */
class ResultActivity : BaseActivity() {
    var position = 0
    override fun setLayout(): Int {
        return R.layout.activity_result
    }

    var tj_adapter: CommonAdapter<DataBean.CoursesBean>? = null//推荐
    var tj_list: MutableList<DataBean.CoursesBean>? = ArrayList()
    var ks_adapter: CommonAdapter<DataBean.ExamsBean>? = null//考试
    var ks_list: MutableList<DataBean.ExamsBean>? = ArrayList()

    override fun initViews() {
        position = intent.getIntExtra("cid", 0)
        var title = ""
        when (position) {
            0 -> {
                title = "更多视频"
                result_lgv.numColumns = 3
                tj_adapter = object : CommonAdapter<DataBean.CoursesBean>(this, tj_list, R.layout.item_sp) {
                    override fun convert(holder: CommonViewHolder, model: DataBean.CoursesBean, position: Int) {
                        holder.setText(R.id.tag_tv, model.title)
                        holder.setGImage(R.id.tag_iv, url().total + model.thumbnail)
                        holder.setText(R.id.price_tv, "￥" + model.price)
                    }
                }
            }
            1 -> {
                title = "更多考试"
                result_lgv.numColumns = 1
                ks_adapter = object : CommonAdapter<DataBean.ExamsBean>(this, ks_list, R.layout.item_zx) {
                    override fun convert(holder: CommonViewHolder, model: DataBean.ExamsBean, position: Int) {
                        holder.setText(R.id.content_tv, model.title)
                        holder.setBGColor(R.id.tag_tv, R.color.tag_hui)
                    }
                }
            }
        }
        toolbar.center_str = title
    }

    override fun initEvents() {

    }

}
