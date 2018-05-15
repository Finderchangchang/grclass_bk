package wai.gr.cla.ui

import android.widget.ListView

import wai.gr.cla.R
import wai.gr.cla.base.BaseActivity

/**
 * Created by JX on 2017/5/21.
 * 积分排行
 */

class PointActivity : BaseActivity() {
    private var listPoint: ListView? = null


    override fun setLayout(): Int {
        return R.layout.activity_point
    }

    override fun initViews() {
        listPoint = findViewById<ListView>(R.id.list_point)
    }

    override fun initEvents() {

    }
}
