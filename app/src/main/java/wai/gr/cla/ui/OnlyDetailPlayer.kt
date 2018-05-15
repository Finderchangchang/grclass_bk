package wai.gr.cla.ui

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.lzy.okgo.OkGo
import com.shuyu.gsyvideoplayer.GSYVideoPlayer
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import wai.gr.cla.R
import wai.gr.cla.base.BaseActivity
import wai.gr.cla.video.listener.SampleListener
import com.lzy.okserver.download.DownloadManager
import com.lzy.okserver.download.DownloadService
import kotlinx.android.synthetic.main.activity_only_detail_player.*
import wai.gr.cla.method.Utils
import wai.gr.cla.model.*


/**
 * 视频页
 * */
class OnlyDetailPlayer : BaseActivity() {

    override fun setLayout(): Int {
        main = this
        return R.layout.activity_only_detail_player
    }

    var model: TuiJianModel? = null
    internal var downloadManager: DownloadManager? = null

    companion object {
        var main: OnlyDetailPlayer? = null

    }
    var url = ""
    var name = ""
    override fun initViews() {
        url = intent.getStringExtra("url")
        name = intent.getStringExtra("name")
        downloadManager = DownloadService.getDownloadManager()
        downloadManager!!.targetFolder = this.filesDir.absolutePath;

        //downloadManager!!.targetFolder = Environment.getExternalStorageDirectory().absolutePath + "/gr/"
        //left_iv.setOnClickListener { finish() }
        detail_player!!.setLockClickListener { view, lock ->
            if (orientationUtils != null) {
                //配合下方的onConfigurationChanged
                orientationUtils!!.isEnable = !lock
            }
        }
        detail_player!!.backButton.setOnClickListener { finish() }
        detail_player!!.star_iv.setPadding(12, 15, 12, 15)
        detail_player!!.down_iv.setPadding(15, 15, 15, 15)
        detail_player!!.share_iv.setPadding(20, 15, 20, 15)
        //收藏
        var i = 0
        //请求视频数据
        detail_player!!.backButton.setOnClickListener { finish() }
        detail_player!!.titleTextView.text = name
        detail_player!!.titleTextView.gravity = 4
        detail_player!!.titleTextView.textSize = 14f
        play(url,"https://ss2.baidu.com/6ONYsjip0QIZ8tyhnq/it/u=4127380943,1004129119&fm=173&s=28912CD5567187D24209762D03007054&w=640&h=354&img.JPEG","")
    }

    var result: Boolean = false
    var play_url_position = 0//当前播放视频的model位置
    var videos: List<VideoModel>? = null//当前视频列表
    fun loadData1(): List<VideoModel>? {
        return videos
    }


    fun play(position: Int): Boolean {
        return model!!.videos!![position].free == 1 || model!!.i_can_play
    }

    fun loadsp(url: String) {
        val imageView = ImageView(this)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        //imageView.setImageResource(R.mipmap.xxx1)
        if (main != null) {
            Glide.with(this!!)
                    .load(url().total + url)
                    .into(imageView)
            detail_player!!.setThumbImageView(imageView)
            resolveNormalVideoUI()
        }

    }

    /**
     * 根据url播放视频
     * */
    fun play(url: String, img_url: String, title: String) {
        detail_player!!.setUp(url, false, null)
        //loadsp(img_url)
        //外部辅助的旋转，帮助全屏
        orientationUtils = OrientationUtils(this, detail_player)
        //初始化不打开外部的旋转
        orientationUtils!!.isEnable = false
        detail_player!!.setIsTouchWiget(true)
        //detail_player.setIsTouchWigetFull(false);
        //关闭自动旋转
        detail_player!!.isRotateViewAuto = false
        detail_player!!.isLockLand = false
        detail_player!!.isShowFullAnimation = false
        detail_player!!.isNeedLockFull = true
        //detail_player!!.title.text = title;
        //detail_player.setOpenPreView(false);
        detail_player!!.fullscreenButton.setOnClickListener { v ->
            //直接横屏
            orientationUtils!!.resolveByClick()
            //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
            detail_player!!.startWindowFullscreen(this@OnlyDetailPlayer, true, true,Utils.getCache("tel"))
        }
        detail_player!!.isNeedShowWifiTip = true
        detail_player!!.setStandardVideoAllCallBack(object : SampleListener() {
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
        detail_player!!.startPlayLogic()
    }

    override fun initEvents() {

    }

    private var isPlay: Boolean = false
    private var isPause: Boolean = false

    private var orientationUtils: OrientationUtils? = null

    override fun onBackPressed() {
        if (orientationUtils != null) {
            orientationUtils!!.backToProtVideo()
        }

        if (StandardGSYVideoPlayer.backFromWindowFull(this)) {
            return
        }
        super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        isPause = true
        detail_player!!.onVideoPause()
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoPlayer.releaseAllVideos()
        main = null;
        OkGo.getInstance().cancelAll()//取消所有网络请求
        if (orientationUtils != null)
            orientationUtils!!.releaseListener()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        //如果旋转了就全屏
        if (isPlay && !isPause) {
            if (newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_USER) {
                if (!detail_player!!.isIfCurrentIsFullscreen) {
                    detail_player!!.startWindowFullscreen(this@OnlyDetailPlayer, true, true,Utils.getCache("tel"))
                }
            } else {
                //新版本isIfCurrentIsFullscreen的标志位内部提前设置了，所以不会和手动点击冲突
                if (detail_player!!.isIfCurrentIsFullscreen) {
                    StandardGSYVideoPlayer.backFromWindowFull(this)
                }
                if (orientationUtils != null) {
                    orientationUtils!!.isEnable = true
                }
            }
        }
    }

    private fun resolveNormalVideoUI() {
        //增加title
        //detailPlayer!!.titleTextView.visibility = View.GONE
        //detailPlayer!!.titleTextView.text = "测试视频"
        //detailPlayer!!.backButton.visibility = View.GONE
    }
}
