package wai.gr.cla.ui

import kotlinx.android.synthetic.main.activity_see_tel.*
import wai.gr.cla.R
import wai.gr.cla.base.BaseActivity
import wai.gr.cla.method.Utils

class SeeTelActivity : BaseActivity() {
    override fun setLayout(): Int {
        return R.layout.activity_see_tel
    }

    override fun initViews() {
        toolbar.setLeftClick { finish() }
        tel_et.text="手机号："+Utils.getCache("tel")
    }

    override fun initEvents() {
        }

}
