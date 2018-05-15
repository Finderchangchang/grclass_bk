package wai.gr.cla.ui

import android.content.Intent
import android.view.WindowManager

import wai.gr.cla.R
import wai.gr.cla.base.BaseActivity
import wai.gr.cla.method.Utils
import wai.gr.cla.model.key.KEY_IS_LOAD

class StartActivity : BaseActivity() {
    override fun setLayout(): Int {
        return R.layout.activity_start
    }

    override fun initViews() {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (!Utils.getBooleanCache(KEY_IS_LOAD)) {//跳转到轮播页
            startActivity(Intent(this@StartActivity, LauncherActivity::class.java))
            finish()
        } else {
            //延迟3秒跳转到首页
            startActivity(Intent(this@StartActivity, MainActivity::class.java))
            finish()
        }
    }

    override fun initEvents() {

    }
}
