package wai.gr.cla.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import wai.gr.cla.R
import wai.gr.cla.base.BaseActivity

class PayResultActivity : BaseActivity() {
    override fun setLayout(): Int {
        return R.layout.activity_pay_result
    }

    override fun initViews() {
        ConfimOrderActivity.context!!.finish()
        CarListActivity.context!!.finish()
    }

    override fun initEvents() {

    }

}
