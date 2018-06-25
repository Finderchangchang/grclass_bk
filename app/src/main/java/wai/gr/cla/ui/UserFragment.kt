package wai.gr.cla.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.lzy.okgo.OkGo

import net.tsz.afinal.view.MenuView
import okhttp3.Call
import okhttp3.Response
import wai.gr.cla.R
import wai.gr.cla.base.BaseFragment
import wai.gr.cla.callback.JsonCallback
import wai.gr.cla.method.GlideImgManager
import wai.gr.cla.method.Utils
import wai.gr.cla.method.common
import wai.gr.cla.model.LzyResponse
import wai.gr.cla.model.UserModel
import wai.gr.cla.model.key
import wai.gr.cla.model.url

/**
 * 个人中心
 * Created by Finder丶畅畅 on 2017/5/7 22:11
 * QQ群481606175
 */

class UserFragment : BaseFragment() {
    var user_tv_name: TextView? = null
    var user_iv_header: ImageView? = null
    /**
     * 页面初始化，以及前期准备工作
     * */
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.frag_user_center, container, false)
        initEvent(view)

        return view
    }


    /**
     * fragment只加载一次的数据
     * */
    override fun onFragmentFirstVisible() {
        super.onFragmentFirstVisible()
        loadUser()
    }

    var main_srl: SwipeRefreshLayout? = null
    var lv_iv: ImageView? = null
    var user_mv_che: MenuView? = null
    /**
     * 加载页面数据以及处理点击事件
     * */
    fun initEvent(view: View) {
        val mvjifen = view.findViewById<MenuView>(R.id.user_mv_jifen) as MenuView
        val user_mv_kecheng = view.findViewById<MenuView>(R.id.user_mv_kecheng) as MenuView
        val user_mv_huancun = view.findViewById<MenuView>(R.id.user_mv_huancun) as MenuView
        val user_mv_dingdan = view.findViewById<MenuView>(R.id.user_mv_dd) as MenuView
        val gb_mv = view.findViewById<MenuView>(R.id.gb_mv) as MenuView
        val user_mv_sc = view.findViewById<MenuView>(R.id.user_mv_sc) as MenuView
        val user_mv_dy = view.findViewById<MenuView>(R.id.user_mv_dy) as MenuView//我的留言
        val user_mv_youxi = view.findViewById<MenuView>(R.id.user_mv_youxi) as MenuView
        val user_yijian = view.findViewById<MenuView>(R.id.user_mv_yijian) as MenuView;
        val user_mv_quan = view.findViewById<MenuView>(R.id.user_mv_quan) as MenuView
        val iv_set = view.findViewById<ImageView>(R.id.iv_set) as ImageView//设置按钮
        val user_ll = view.findViewById<LinearLayout>(R.id.user_ll) as LinearLayout
        val user_mv_fk = view.findViewById<MenuView>(R.id.user_mv_fk) as MenuView
        lv_iv = view.findViewById<ImageView>(R.id.lv_iv) as ImageView
        user_iv_header = view.findViewById<ImageView>(R.id.user_iv_header) as ImageView
        main_srl = view.findViewById<MenuView>(R.id.main_srl) as SwipeRefreshLayout
        user_mv_che = view.findViewById<MenuView>(R.id.user_mv_che) as MenuView
        main_srl!!.setOnRefreshListener {
            loadUser()
        }
        user_mv_che!!.setOnClickListener {
            if (checkLogin()) {
                startActivity(Intent(MainActivity.main, CarListActivity::class.java))
            }
        }
        user_tv_name = view.findViewById<TextView>(R.id.user_tv_name) as TextView
        //个人中心
        user_iv_header!!.setOnClickListener {
            if (checkLogin()) {
                startActivityForResult(Intent(MainActivity.main, UserDetailActivity::class.java)
                        .putExtra("img_url", url)
                        .putExtra("name", nick), 0)
            }
        }
        user_ll!!.setOnClickListener {
            if (checkLogin()) {
                startActivityForResult(Intent(MainActivity.main, UserDetailActivity::class.java)
                        .putExtra("img_url", url)
                        .putExtra("name", nick), 0)
            }
        }
        //设置按钮
        iv_set.setOnClickListener {
            if (checkLogin()) {
                startActivity(Intent(MainActivity.main, SetActivity::class.java))
            }
        }
        //反馈回复列表
        user_mv_fk.setOnClickListener {
            if (checkLogin()) {
                startActivity(Intent(MainActivity.main, FKListActivity::class.java))
            }
        }
        //积分排行
        mvjifen.setOnClickListener {
            if (checkLogin()) {
                startActivity(Intent(MainActivity.main, JiFenListActivity::class.java))
            }
        }
        //我的订单
        user_mv_dingdan.setOnClickListener {
            if (checkLogin()) {
                startActivity(Intent(MainActivity.main, MyOrderListActivity::class.java).putExtra("which", 3))
            }
        }
        //我的课程
        user_mv_kecheng.setOnClickListener {
            if (checkLogin()) {
                startActivity(Intent(MainActivity.main, MyOrderGVListActivity::class.java)
                        .putExtra("free", "3")
                        .putExtra("which", 4))
            }
        }
        //我的下载
        user_mv_huancun.setOnClickListener {
            if (checkLogin()) {
                startActivity(Intent(MainActivity.main, DownManageActivity::class.java).putExtra("which", 4))
            }
        }
        //我的订阅
        user_mv_dy.setOnClickListener {
            if (checkLogin()) {
                startActivity(Intent(MainActivity.main, MyOrderListActivity::class.java).putExtra("which", 5))
            }
        }
        //我的收藏（跳转到我的课程，我的资讯）
        user_mv_sc.setOnClickListener {
            if (checkLogin()) {
                startActivity(Intent(MainActivity.main, SCTagActivity::class.java))
            }
        }
        //互动游戏
        user_mv_youxi.setOnClickListener {
            if (checkLogin()) {
                startActivity(Intent(MainActivity.main, WebActivity::class.java)
                        .putExtra("url", "http://test.guanrenjiaoyu.com/?s=index/game/game_index")
                        .putExtra("title", "互动游戏"))
            }
        }
        //我的冠币
        gb_mv.setOnClickListener {
            if (checkLogin()) {
                loadUser()
                startActivity(Intent(MainActivity.main, GBDetailActivity::class.java).putExtra("gb", gb.toString()))
            }
        }
        //意见反馈
        user_yijian.setOnClickListener {
            if (checkLogin()) {
                startActivity(Intent(MainActivity.main, AddAskActivity::class.java))
            }
        }
        //优惠券列表
        user_mv_quan.setOnClickListener {
            if (checkLogin()) {
                startActivity(Intent(MainActivity.main, QuansActivity::class.java))
            }
        }
    }

    /**
     * 检查当前账号是否登录
     * */
    fun checkLogin(): Boolean {
        val school_id = Utils.getCache(key.KEY_SCHOOLID)
        val pwd = Utils.getCache(key.KEY_PWd)
        val user_id = Utils.getCache(key.KEY_Tel)
        val key_wx = Utils.getCache(key.KEY_WX)
        val key_qq = Utils.getCache(key.KEY_QQ)
        //login_key或者用户id为空说明已退出登录
        if (TextUtils.isEmpty(pwd) && TextUtils.isEmpty(key_wx) && TextUtils.isEmpty(key_qq)) {
            GlideImgManager.glideLoader(MainActivity.main, "", R.mipmap.defult_user, R.mipmap.defult_user, user_iv_header, 0)
            user_tv_name!!.text = "未登录"
            startActivityForResult(Intent(MainActivity.main, LoginActivity::class.java), 0)
        } else if (TextUtils.isEmpty(school_id)) {//未选择学校
            startActivityForResult(Intent(MainActivity.main, PerfaceUserActivity::class.java), 1)
        } else {//因为在main已经自动登录，所以这里上面有空说明肯定没登录=
            return true
        }
        return false
    }

    /**
     * 接收返回的消息
     * */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode!!, resultCode!!, data)
        loadUser()
//        when (resultCode) {
//            88 -> {//接收登录信息，登录成功，刷新页面数据
//                val result = data!!.getBooleanExtra("result", false)
//                if (result) {//刷新
//                    loadUser()
//                }
//            }
//        }
    }

    var url = ""
    var gb = 0.0
    var nick = ""
    /**
     * 加载个人信息
     * */
    fun loadUser() {
        main_srl!!.isRefreshing = false
        OkGo.post(url().auth_api + "get_user_level")
                .execute(object : JsonCallback<LzyResponse<String>>() {
                    override fun onSuccess(t: LzyResponse<String>, call: okhttp3.Call?, response: okhttp3.Response?) {
                        if (t.code == 0) {
                            when (t.data) {
                                "member_lv1.png" -> {
                                    lv_iv!!.setImageResource(R.mipmap.member_lv1)
                                }
                                "member_lv2.png" -> {
                                    lv_iv!!.setImageResource(R.mipmap.member_lv2)
                                }
                                "member_lv3.png" -> {
                                    lv_iv!!.setImageResource(R.mipmap.member_lv3)
                                }
                                "member_lv4.png" -> {
                                    lv_iv!!.setImageResource(R.mipmap.member_lv4)
                                }
                            }
                        }
                    }
                })
        OkGo.post(url().user_api + "get_user")
                .execute(object : JsonCallback<LzyResponse<UserModel>>() {
                    override fun onSuccess(t: LzyResponse<UserModel>, call: okhttp3.Call?, response: okhttp3.Response?) {
                        if (t.code == 0) {
                            if (!TextUtils.isEmpty(Utils.getCache(key.KEY_SCHOOLID))) {
                                url = url().total + t.data!!.head_img
                                gb = t.data!!.guanbi
                                GlideImgManager.glideLoader(MainActivity.main, url().total + t.data!!.head_img, R.mipmap.defult_user, R.mipmap.defult_user, user_iv_header, 0)
                                user_tv_name!!.text = t.data!!.nick
                                Utils.putCache("nickname", t.data!!.nick)
                                Utils.putCache("tel", t.data!!.username)
                                nick = t.data!!.nick!!
                            }
                        } else {
                            user_tv_name!!.text = "未登录"
                        }
                        main_srl!!.isRefreshing = false
                    }

                    override fun onError(call: Call?, response: Response?, e: Exception?) {
                        main_srl!!.isRefreshing = false//加载个人信息失败说明已经退出登录了
                        if (!TextUtils.isEmpty(Utils.getCache(key.KEY_USERID))) {
                            Toast.makeText(MainActivity.main, common().toast_error(e!!), Toast.LENGTH_SHORT).show()
                            login()
                        }
                        Log.i("--!!-------", common().toast_error(e!!) + "***" + Utils.getCache(key.KEY_USERID))

                        Utils.putCache(key.KEY_SCHOOLID, "")
                        //Utils.putCache(key.KEY_Tel, "")
                        Utils.putCache("tel", "")
                        Utils.putCache(key.KEY_PWd, "")
                        Utils.putCache(key.KEY_WX, "")
                        Utils.putCache(key.KEY_QQ, "")
                        Utils.putCache(key.KEY_USERID, "")
                        GlideImgManager.glideLoader(MainActivity.main, "", R.mipmap.defult_user, R.mipmap.defult_user, user_iv_header, 0)
                        user_tv_name!!.text = "未登录"
                        MainActivity.main!!.toast(common().toast_error(e!!))

                    }
                })
    }

    fun login() {
        var tel = Utils.getCache(key.KEY_Tel)
        var pwd = Utils.getCache(key.KEY_PWd)
        var wx = Utils.getCache(key.KEY_WX)
        var qq = Utils.getCache(key.KEY_QQ)
        var token = Utils.getCache(key.KEY_TOKEN)
        if (!TextUtils.isEmpty(tel) && !TextUtils.isEmpty(pwd)) {
            OkGo.post(url().public_api + "login")
                    .params("username", tel)// 请求方式和请求url
                    .params("password", pwd)// 请求方式和请求url
                    .execute(object : JsonCallback<LzyResponse<UserModel>>() {
                        override fun onSuccess(t: LzyResponse<UserModel>, call: okhttp3.Call?, response: okhttp3.Response?) {
                            loadUser()
                        }
                    })
        } else if (!TextUtils.isEmpty(wx) || !TextUtils.isEmpty(qq)) {
            var open_id = ""
            if (!TextUtils.isEmpty(wx)) {
                open_id = wx
            } else if (!TextUtils.isEmpty(qq)) {
                open_id = qq
            }
            OkGo.post(url().public_api + "open_login")
                    .params("openid", open_id)// 请求方式和请求url
                    .params("login_type", "wx")// 请求方式和请求url
                    .params("access_token", token)
                    .execute(object : JsonCallback<LzyResponse<UserModel>>() {
                        override fun onSuccess(t: LzyResponse<UserModel>, call: okhttp3.Call?, response: okhttp3.Response?) {
                            loadUser()
                        }
                    })
        }
    }
}
