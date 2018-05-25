package wai.gr.cla.method

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

import wai.gr.cla.model.TuiJianModel
import wai.gr.cla.ui.JYFragment
import wai.gr.cla.ui.MainFragment
import wai.gr.cla.ui.UserFragment
import wai.gr.cla.ui.VideoFragment
import wai.gr.cla.ui.ZBFragment

/**
 * Created by Finder丶畅畅 on 2017/1/17 21:15
 * QQ群481606175
 */

class VideoAdapter(fm: FragmentManager, kk: TuiJianModel) : FragmentPagerAdapter(fm) {
    private val mo = kk
    override fun getItem(position: Int): Fragment {
        return VideoFragment(position, mo)
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "概述"
            1 -> "目录"
            else -> "评论"
        }
    }
}
