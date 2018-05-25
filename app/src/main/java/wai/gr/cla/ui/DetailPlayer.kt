package wai.gr.cla.ui

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.*
import com.alibaba.fastjson.JSON
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.shuyu.gsyvideoplayer.GSYVideoPlayer
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import kotlinx.android.synthetic.main.activity_detail_player.*
import okhttp3.Call
import okhttp3.Response
import wai.gr.cla.R
import wai.gr.cla.base.BaseActivity
import wai.gr.cla.callback.JsonCallback
import wai.gr.cla.method.VideoAdapter
import wai.gr.cla.method.common
import wai.gr.cla.video.LandLayoutVideo
import wai.gr.cla.video.listener.SampleListener
import com.lzy.okserver.download.DownloadManager
import com.lzy.okserver.download.DownloadService
import io.reactivex.annotations.NonNull
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.sample_video.view.*
import tv.buka.roomSdk.BukaRoomSDK
import tv.buka.roomSdk.entity.BaseResult
import tv.buka.roomSdk.entity.CourseEntity
import tv.buka.roomSdk.net.ResponseJudge
import tv.buka.roomSdk.util.ConstantUtil
import tv.buka.roomSdk.util.EmptyUtil
import tv.buka.roomSdk.util.ToastUtils
import tv.buka.sdk.listener.ReceiptListener
import wai.gr.cla.buka.UriActivity
import wai.gr.cla.callback.DialogCallback
import wai.gr.cla.method.Utils
import wai.gr.cla.model.*
import java.util.*


/**
 * 视频页
 * */
class DetailPlayer : BaseActivity() {

    override fun setLayout(): Int {
        main = this
        return R.layout.activity_detail_player
    }

    private var name: String? = ""
    private var role: String = "2"
    private var token: String = "guanren"
    private var roomAlias: String? = null
    private var loginId: String = Utils.getCache(key.KEY_USERID)
    var model: TuiJianModel? = null
    var is_live = false
    internal var downloadManager: DownloadManager? = null

    companion object {
        var main: DetailPlayer? = null

    }

    override fun initViews() {

        //highApiEffects(true)
        downloadManager = DownloadService.getDownloadManager()
        //Context.getFilesDir().getPath()
        downloadManager!!.targetFolder = this.filesDir.absolutePath;
        //left_iv.setOnClickListener { finish() }
        detailPlayer = findViewById<LandLayoutVideo>(R.id.detail_player)
        is_live = intent.getBooleanExtra("is_live", false)
        detailPlayer!!.setLockClickListener { view, lock ->
            if (orientationUtils != null) {
                //配合下方的onConfigurationChanged
                orientationUtils!!.isEnable = !lock
            }
        }
        detail_player!!.backButton.setOnClickListener { finish() }
        detail_player!!.userText.text = Utils.getCache("tel")
        main_tab.addTab(main_tab.newTab().setText("概述"))
        main_tab.addTab(main_tab.newTab().setText("目录"))
        ss = intent.getIntExtra("cid", 0)
        detail_player!!.down_iv.setOnClickListener {
            var user_id = Utils.getCache(key.KEY_USERID)
            if (TextUtils.isEmpty(user_id)) {
                toast("请先登录")
            } else {//检测登录
                //1.费用为0，2，i_can_play=true（满足其中一个就可以播放）
                //if (("0").equals(model!!.price) || model!!.i_can_play) {
                play_url_position = Utils.getCache("last_position").toInt()
                if (model!!.videos!!.size > 0 && model!!.videos!!.size > play_url_position) {
                    val modd = model!!.videos!![play_url_position]
                    if (modd.free == 1 || model!!.i_can_play) {
                        var url = model!!.videos!![play_url_position].url//视频地址
                        if (!TextUtils.isEmpty(url)) {
                            url = url!!.replace(".mp4", ".gr.mp4")
                        }
                        url = url().total + "/fP3m8r/t7Me1e" + url
                        if (downloadManager!!.getDownloadInfo(url) != null) {
                            Toast.makeText(applicationContext, "任务已经在下载列表中", Toast.LENGTH_SHORT).show()
                        } else {
                            val request = OkGo.get(url)
                            downloadManager!!.addTask(url, modd, request, null)
                            if (Utils.isWifiConnected(this)) {
                                toast("添加成功")
                            } else {
                                downloadManager!!.pauseAllTask()//暂停所有任务
                                toast("添加成功，当前为移动网络已暂停下载")
                            }
                        }
                    } else {//吊起支付
                        toast("请购买后学习")
                    }
                }

            }
        }
        detailPlayer!!.star_iv.setPadding(12, 15, 12, 15)
        detailPlayer!!.down_iv.setPadding(15, 15, 15, 15)
        detailPlayer!!.share_iv.setPadding(20, 15, 20, 15)
        detail_player!!.star_iv.setOnClickListener {
            if (model!!.favorite_id > 0) {
                OkGo.post(url().auth_api + "del_my_favorite")
                        .params("id", model!!.favorite_id)
                        .execute(object : JsonCallback<LzyResponse<String>>() {
                            override fun onSuccess(t: LzyResponse<String>, call: okhttp3.Call?, response: okhttp3.Response?) {
                                if (t.code == 0) {
                                    toast("取消收藏成功")
                                    model!!.favorite_id = 0
                                    detail_player!!.star_iv.setImageResource(R.mipmap.collectionbutton)

                                } else {
                                    toast("取消收藏失败，请重试")
                                }
                            }

                            override fun onError(call: Call?, response: Response?, e: Exception?) {
                                toast(common().toast_error(e!!))
                            }
                        })
            } else {
                //if (model!!.i_can_play) {
                OkGo.post(url().auth_api + "add_my_favorite")
                        .params("which", 1)
                        .params("which_id", model!!.id)
                        .execute(object : JsonCallback<LzyResponse<SCModel>>() {
                            override fun onSuccess(t: LzyResponse<SCModel>, call: okhttp3.Call?, response: okhttp3.Response?) {
                                if (t.code == 0) {
                                    toast("收藏成功")
                                    model!!.favorite_id = t.data!!.id
                                    detail_player!!.star_iv.setImageResource(R.mipmap.shipin_yishoucang)
                                } else {
                                    toast("收藏失败，请重试")
                                }
                            }

                            override fun onError(call: Call?, response: Response?, e: Exception?) {
                                toast(common().toast_error(e!!))
                            }
                        })
                /* } else {
                     if (s == null) {
                         s = ZhiFuPopuWindowActivity(this, CardDetailActivity(), this, down_iv, false, model!!.id.toString(), ArrayList(), ss)
                     }
                     s!!.showWindow()
                 }*/
            }
            //}
        }
        //分享操作
        detail_player!!.share_iv.setOnClickListener {
            SharedPopuWindowActivity(this@DetailPlayer, share_iv, model!!.id.toString(), model!!.title, model!!.summary, model!!.thumbnail, false).showWindow()
        }
        //请求视频数据
        loadData()
        car_btn.setOnClickListener {
            var user_id = Utils.getCache(key.KEY_USERID)
            name = Utils.getCache("tel")
            //跳转到直播页面
            if (car_btn.text != "加入书包") {
                var intent = Intent(main, UriActivity::class.java);
                intent.putExtra("room_id", roomAlias)
                intent.putExtra("nick_name", Utils.getCache("nickname"))
                intent.putExtra("user_id", user_id)
                startActivity(intent)
            } else {
                if (TextUtils.isEmpty(user_id)) {
                    toast("请先登录")
                } else {//验证是否登录
                    OkGo.post(url().auth_api + "add_shopcar")
                            .params("course_id", ss)
                            .execute(object : StringCallback() {
                                override fun onSuccess(model: String, call: okhttp3.Call?, response: okhttp3.Response?) {
                                    var m = Gson().fromJson(model, LzyResponse::class.java)
                                    toast(m.msg!!)
                                }

                                override fun onError(call: Call?, response: Response?, e: Exception?) {
                                    toast(common().toast_error(e!!))
                                }
                            })
                }
            }
        }
        /**
         * 购买按钮
         * */
        buy_btn.setOnClickListener {
            var user_id = Utils.getCache(key.KEY_USERID)
            if (TextUtils.isEmpty(user_id)) {
                toast("请先登录")
            } else {//验证是否登录
                var lzy = LzyResponse<String>()
                var kk_list: ArrayList<CarModel> = ArrayList()//购买的课程列表
                var car = CarModel()
                car.course_id = model!!.id
                car.course_title = model!!.title
                car.thumbnail = model!!.thumbnail
                car.price = model!!.price
                car.is_full_cut = model!!.is_full_cut
                kk_list.add(car)
                lzy.car = kk_list
                startActivity(Intent(this@DetailPlayer, ConfimOrderActivity::class.java)
                        .putExtra("model", lzy)
                        .putExtra("is_one", true))//是当个课程，有提示
//                if (s == null) {
//                    s = ZhiFuPopuWindowActivity(this@DetailPlayer, CardDetailActivity(), this, down_iv, false, model!!.id.toString(), ArrayList(), ss)
//                }
//                s!!.showWindow()
            }
        }
        new_teacher_btn.setOnClickListener {
            OkGo.post(url().public_api + "get_phone_teachers")     // 请求方式和请求url
                    .params("cid", model!!.cid)
                    .params("page", "1")
                    .execute(object : JsonCallback<LzyResponse<PageModel<Teacher>>>() {
                        override fun onSuccess(t: LzyResponse<PageModel<Teacher>>, call: okhttp3.Call?, response: okhttp3.Response?) {
                            if (t.data!!.list!!.isNotEmpty()) {
                                startActivity(Intent(this@DetailPlayer, AskListActivity::class.java)
                                        .putExtra("is_one", model!!.id)
                                        .putExtra("id", model!!.teacher_id)
                                        .putExtra("name", model!!.subject)
                                        .putExtra("cid", model!!.cid))
                            } else {
                                toast("此老师暂时未开通答疑专栏")
                            }
                        }
                    })
            //跳转到单个提问列表

        }
    }


    var ss: Int = 0
    var s: ZhiFuPopuWindowActivity? = null
    var result: Boolean = false
    var play_url_position = 0//当前播放视频的model位置
    var videos: List<VideoModel>? = null//当前视频列表
    fun loadData1(): List<VideoModel>? {
        return videos
    }

    fun loadData() {
        if (ss > 0) {
            OkGo.post(url().public_api + "get_phone_course_detail")
                    .params("id", ss)
                    .execute(object : DialogCallback<LzyResponse<TuiJianModel>>(this) {
                        override fun onSuccess(key: LzyResponse<TuiJianModel>, call: okhttp3.Call?, response: okhttp3.Response?) {
                            model = key.data
                            detail_player!!.titleTextView.text = model!!.title
                            detail_player!!.titleTextView.gravity = 4
                            detail_player!!.titleTextView.maxLines = 2//ellipsize
                            detail_player!!.titleTextView.ellipsize = TextUtils.TruncateAt.END//ellipsize
                            detail_player!!.titleTextView.textSize = 14f
                            price_tv.text = "￥" + model!!.price
                            videos = model!!.videos//获得所有视频的列表
                            if (!result) {
                                result = true
                                var mAdapter = VideoAdapter(supportFragmentManager, model!!)
                                main_vp.adapter = mAdapter
                                main_tab.setupWithViewPager(main_vp)
                            }

                            /***
                             * 1.i_can_play	如果为1代表我可以随意观看（购买了且有效期内）。
                             * 2.单个视频free	如果为1代表免费，可以随意观看。
                             */
                            if (model!!.i_can_play) {//能播放隐藏价格
                                if (is_live) {
                                    price_ll.visibility = View.VISIBLE//隐藏价格
                                    car_btn.visibility = View.VISIBLE
                                    buy_btn.visibility = View.GONE
                                    car_btn.text = "加入直播"
                                } else {
                                    price_ll.visibility = View.GONE//隐藏价格
                                }
                            } else {
                                price_ll.visibility = View.VISIBLE//显示价格
                            }
                            if (is_live) {
                                roomAlias = model?.room_id
//
                            }
                            GSYVideoPlayer.is_live = is_live
                            Utils.putCache("last_position", model!!.last_play_num.toString())
                            play_url_position = model!!.last_play_num//设置一下最后播放的位置
                            //if (("0").equals(model!!.price) || model!!.i_can_play) {
                            if (model!!.i_can_play && model!!.videos!!.isNotEmpty() && model!!.videos!![0].free == 1) {
                                if (videos!!.size > model!!.last_play_num) {//解决超出索引的问题
                                    play(videos!![model!!.last_play_num].url!!, model!!.videos!![model!!.last_play_num].thumbnail!!, videos!![model!!.last_play_num].name!!)
                                } else {
                                    if (videos?.size!! > 0 && TextUtils.isEmpty(model?.videos?.get(0)?.name)) {
                                        play(videos!![0].url!!, model!!.videos!![0].thumbnail!!, model!!.videos!![0].name!!)
                                    }
                                }
                            } else {
                                if (videos?.size!! > 0 && !TextUtils.isEmpty(model?.videos?.get(0)?.name)) {
                                    play(videos?.get(0)?.url!!, model?.videos?.get(0)?.thumbnail!!, model?.videos?.get(0)?.name!!)
                                }
                            }

                            //}
                            if (model!!.favorite_id > 0) {
                                star_iv.setImageResource(R.mipmap.shipin_yishoucang)
                            }
                        }

                        override fun onError(call: Call?, response: Response?, e: Exception?) {
                            toast(common().toast_error(e!!))
                        }
                    })
        }
    }

    fun play(position: Int): Boolean {
        return model!!.videos!![position].free == 1 || model!!.i_can_play
    }

    fun loadsp(url: String) {
        val imageView = ImageView(this)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        //imageView.setImageResource(R.mipmap.xxx1)
        if (main != null) {
            Glide.with(this)
                    .load(url().total + url)
                    .into(imageView)
            detailPlayer!!.setThumbImageView(imageView)
            resolveNormalVideoUI()
        }

    }

    /**
     * 根据url播放视频
     * */
    fun play(url: String, img_url: String, title: String) {
        var url = url
        if (!TextUtils.isEmpty(url)) {
            url = url.replace(".mp4", ".gr.mp4")
        }
        url = url().total + "/fP3m8r/t7Me1e" + url
        Log.i("url", url)
        detailPlayer!!.setUp(url, false, null)
        loadsp(img_url)
        //外部辅助的旋转，帮助全屏
        orientationUtils = OrientationUtils(this, detailPlayer)
        //初始化不打开外部的旋转
        orientationUtils!!.isEnable = false
        detailPlayer!!.setIsTouchWiget(true)
        //detailPlayer.setIsTouchWigetFull(false);
        //关闭自动旋转
        detailPlayer!!.isRotateViewAuto = true
        detailPlayer!!.isLockLand = false
        detailPlayer!!.isShowFullAnimation = false
        detailPlayer!!.isNeedLockFull = true
        detailPlayer!!.title.text = title;
        //detailPlayer.setOpenPreView(false);
        detailPlayer!!.fullscreenButton.setOnClickListener { v ->
            //直接横屏
            orientationUtils!!.resolveByClick()
            detailPlayer!!.startWindowFullscreen(this@DetailPlayer, true, true, Utils.getCache("tel"))
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

            //highApiEffects(false)
        }
        detailPlayer!!.isNeedShowWifiTip = true
        detailPlayer!!.setStandardVideoAllCallBack(object : SampleListener() {
            override fun onPrepared(url: String, vararg objects: Any) {
                super.onPrepared(url, *objects)
                //开始播放了才能旋转和全屏
                orientationUtils!!.isEnable = true
                isPlay = true
            }

            override fun onQuitFullscreen(url: String, vararg objects: Any) {
                super.onQuitFullscreen(url, *objects)
                if (orientationUtils != null) {
                    orientationUtils!!.backToProtVideo()
                }
            }
        })
        //detailPlayer!!.startPlayLogic()
    }

    override fun initEvents() {
        var width = this.getWindowManager().getDefaultDisplay().getWidth()
        val linearParams = detailPlayer!!.getLayoutParams() as RelativeLayout.LayoutParams //取控件textView当前的布局参数
        linearParams.height = width / 16 * 9
        detailPlayer!!.layoutParams = linearParams
        top_zw_view!!.layoutParams = linearParams
    }

    //推荐使用StandardGSYVideoPlayer，功能一致
    //CustomGSYVideoPlayer部分功能处于试验阶段
    internal var detailPlayer: LandLayoutVideo? = null

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
        detailPlayer!!.onVideoPause()
    }

    override fun onResume() {
        super.onResume()
        if (s != null) {
            (s as ZhiFuPopuWindowActivity).closePop()

            isPause = false
        }
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        loadData()//支付成功以后重调
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
                if (!detailPlayer!!.isIfCurrentIsFullscreen) {
                    //highApiEffects(false)
                    //全屏
                    window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

                    detailPlayer!!.startWindowFullscreen(this@DetailPlayer, true, true, Utils.getCache("tel"))
                    //detailPlayer!!.setText("0000000")
                }
            } else {
                //新版本isIfCurrentIsFullscreen的标志位内部提前设置了，所以不会和手动点击冲突
                if (detailPlayer!!.isIfCurrentIsFullscreen) {
                    StandardGSYVideoPlayer.backFromWindowFull(this)
                }
                if (orientationUtils != null) {
                    orientationUtils!!.isEnable = true
                }
                detail_player!!.userText.text = Utils.getCache("tel")

                //window.setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            }
        }
    }

    /**
     * 状态栏显示隐藏
     * */
    private fun full(enable: Boolean) {
        if (enable) {
            val lp = window.attributes
            lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_FULLSCREEN
            window.attributes = lp
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        } else {
            val attr = window.attributes
            attr.flags = attr.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
            window.attributes = attr
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }

    private fun resolveNormalVideoUI() {
        //增加title
        //detailPlayer!!.titleTextView.visibility = View.GONE
        //detailPlayer!!.titleTextView.text = "测试视频"
        //detailPlayer!!.backButton.visibility = View.GONE
    }
}
