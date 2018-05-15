package wai.gr.cla.ui


import android.content.Intent
import android.support.v4.view.ViewPager
import android.view.View
import android.view.WindowManager

import java.util.ArrayList

import kotlinx.android.synthetic.main.activity_launcher.*
import tv.buka.roomSdk.BukaRoomSDK
import wai.gr.cla.R
import wai.gr.cla.base.BaseActivity
import wai.gr.cla.buka.BukaMainActivity
import wai.gr.cla.method.LauncherPagerAdapter
import wai.gr.cla.method.Utils
import wai.gr.cla.model.key.KEY_IS_LOAD

class LauncherActivity : BaseActivity() {
    internal var images: MutableList<Int> = ArrayList()

    override fun setLayout(): Int {
        return R.layout.activity_launcher
    }

    override fun initViews() {
        main = this
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        images.add(R.mipmap.start_1)
        images.add(R.mipmap.start_2)
        images.add(R.mipmap.start_3)
        BukaRoomSDK.initBukaMedia(this); //TODO 媒体初始化
        BukaRoomSDK.getServerIp(this);//TODO 获取默认服务ip
    }

    companion object {
        var main: LauncherActivity? = null
    }

    override fun initEvents() {
        viewpager.adapter = LauncherPagerAdapter(images)
        indicator.setViewPager(viewpager)
        viewpager!!.offscreenPageLimit = 3//预加载所有
        viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                if (position == images.size - 1) {
                    go_main_btn.visibility = View.VISIBLE
                } else {
                    go_main_btn.visibility = View.GONE
                }
            }

            override fun onPageSelected(position: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        go_main_btn!!.setOnClickListener { v ->
            go_main()
            Utils.putBooleanCache(KEY_IS_LOAD, true)
        }
    }

    /**
     * 跳转到首页操作
     * */
    fun go_main() {
        finish()
        //startActivity(Intent(this, BukaMainActivity::class.java))
        startActivity(Intent(this, MainActivity::class.java))

    }
}
