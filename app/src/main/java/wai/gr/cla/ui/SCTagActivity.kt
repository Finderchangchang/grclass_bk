package wai.gr.cla.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_sctag.*

import wai.gr.cla.R
import wai.gr.cla.base.BaseActivity

/**
 * 有2个收藏标签
 * */
class SCTagActivity : BaseActivity() {
    override fun setLayout(): Int {
        return R.layout.activity_sctag
    }

    override fun initViews() {
        toolbar.setLeftClick { finish() }
        sc_kc.setOnClickListener { startActivity(Intent(this, MyOrderGVListActivity::class.java).putExtra("free","").putExtra("which", 1)) }
        sc_zx.setOnClickListener { startActivity(Intent(this, MyOrderListActivity::class.java).putExtra("which", 2)) }
    }

    override fun initEvents() {

    }
}
