package wai.gr.cla.ui

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.media.MediaPlayer
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import wai.gr.cla.R
import android.widget.Toast
import com.shuyu.gsyvideoplayer.GSYVideoPlayer
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import kotlinx.android.synthetic.main.activity_video.*
import wai.gr.cla.base.BaseActivity
import wai.gr.cla.method.Utils
import wai.gr.cla.video.listener.SampleListener

/**
 * 加载本地视频
 * */
class VideoActivity : BaseActivity() {
    override fun initEvents() {

    }

    override fun setLayout(): Int {
        return R.layout.activity_video
    }

    override fun initViews() {
        val url = intent.getStringExtra("url")
        val name = intent.getStringExtra("name")
        detail_player.backButton.setOnClickListener { finish() }
        detail_player.titleTextView.text = name
        detail_player.titleTextView.gravity = 4
        detail_player.titleTextView.textSize = 14f
        play(url)
    }

    private var isPlay: Boolean = false
    private var isPause: Boolean = false
    private var orientationUtils: OrientationUtils? = null
    fun play(url: String) {
        detail_player.setUp(url, false, null)
        detail_player.star_iv.visibility = View.GONE
        detail_player.down_iv.visibility = View.GONE
        detail_player.share_iv.visibility = View.GONE
        //外部辅助的旋转，帮助全屏
        orientationUtils = OrientationUtils(this, detail_player)
        //初始化不打开外部的旋转
        orientationUtils!!.isEnable = false
        detail_player.setIsTouchWiget(true)
        //detail_player.setIsTouchWigetFull(false);
        //关闭自动旋转
        detail_player.isRotateViewAuto = false
        detail_player.isLockLand = false
        detail_player.isShowFullAnimation = false
        detail_player.isNeedLockFull = true
        //detail_player.setOpenPreView(false);
        detail_player.fullscreenButton.setOnClickListener { v ->
            //直接横屏
            orientationUtils!!.resolveByClick()
            //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
            detail_player.startWindowFullscreen(this@VideoActivity, true, true, Utils.getCache("tel"))
        }
        detail_player.isNeedShowWifiTip = true
        detail_player.setStandardVideoAllCallBack(object : SampleListener() {
            override fun onPrepared(url: String, vararg objects: Any) {
                super.onPrepared(url, *objects)
                //开始播放了才能旋转和全屏
                orientationUtils!!.isEnable = true
                isPlay = true
            }

            override fun onAutoComplete(url: String, vararg objects: Any) {
                super.onAutoComplete(url, *objects)
            }

            override fun onClickStartError(url: String, vararg objects: Any) {
                super.onClickStartError(url, *objects)
            }

            override fun onQuitFullscreen(url: String, vararg objects: Any) {
                super.onQuitFullscreen(url, *objects)
                if (orientationUtils != null) {
                    orientationUtils!!.backToProtVideo()
                }
            }
        })
        detail_player.startPlayLogic()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        //如果旋转了就全屏
        if (isPlay && !isPause) {
            if (newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_USER) {
                if (!detail_player.isIfCurrentIsFullscreen) {
                    detail_player.startWindowFullscreen(this, true, true, Utils.getCache("tel"))
                }
            } else {
                //新版本isIfCurrentIsFullscreen的标志位内部提前设置了，所以不会和手动点击冲突
                if (detail_player.isIfCurrentIsFullscreen) {
                    StandardGSYVideoPlayer.backFromWindowFull(this)
                }
                if (orientationUtils != null) {
                    orientationUtils!!.isEnable = true
                }
            }
        }
    }

    override fun onBackPressed() {
        if (orientationUtils != null) {
            orientationUtils!!.backToProtVideo()
        }

        if (StandardGSYVideoPlayer.backFromWindowFull(this)) {
            return
        }
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoPlayer.releaseAllVideos()
        if (orientationUtils != null)
            orientationUtils!!.releaseListener()
    }

    override fun onPause() {
        super.onPause()
        isPause = true
        detail_player.onVideoPause()
    }
}
