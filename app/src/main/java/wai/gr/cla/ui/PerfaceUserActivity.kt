package wai.gr.cla.ui


import android.content.Intent
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import com.google.gson.Gson
import com.lzy.okgo.OkGo
import com.zaaach.toprightmenu.MenuItem
import com.zaaach.toprightmenu.TopRightMenu
import kotlinx.android.synthetic.main.activity_perface_user.*
import okhttp3.Call
import okhttp3.Response
import wai.gr.cla.R
import wai.gr.cla.base.BaseActivity
import wai.gr.cla.callback.JsonCallback
import wai.gr.cla.method.Utils
import wai.gr.cla.method.common
import wai.gr.cla.model.*
import wai.gr.cla.model.key.KEY_SCHOOLID
import java.util.*
import org.feezu.liuli.timeselector.TimeSelector
import java.text.SimpleDateFormat


/**
 * 完善个人信息页面 选择学校界面
 * */
class PerfaceUserActivity : BaseActivity() {
    var list_sheng: MutableList<CodeModel>? = ArrayList()
    var list_shi: MutableList<CodeModel>? = ArrayList()

    override fun setLayout(): Int {
        return R.layout.activity_perface_user
    }

    var myslist: MutableList<SchoolModel>? = null
    internal var num = 60
    val timer = Timer()
    var task: TimerTask? = null
    // var token=""
    var uuid: String? = null
    var is_login = false//跳转过来的是登录操作
    var pw: SchoolPopuWindows? = null
    var login_type = 0//0,手机登录。1,qq。2，微信
    var school_id = 0
    override fun initViews() {
        uuid = intent.getStringExtra("uuid")
        if (uuid == null) {
            uuid = ""
        }
        login_type = intent.getIntExtra("type", 0)
        //token=intent.getStringExtra("token")
        is_login = intent.getBooleanExtra("is_login", false)
        if (!is_login) {
            OkGo.post(url().auth_api + "get_user_info")
                    .execute(object : JsonCallback<LzyResponse<UserModel>>() {
                        override fun onSuccess(t: LzyResponse<UserModel>, call: okhttp3.Call?, response: okhttp3.Response?) {
                            if (t.code == 0) {
                                school_tv1.text = t.data!!.schoolInfo!!.parent_code.toString();
                                school_tv2.text = t.data!!.schoolInfo!!.region_code.toString();
                                province_tv.text = t.data!!.schoolInfo!!.province_name
                                city_tv.text = t.data!!.schoolInfo!!.city_name
                                school_tv.text = t.data!!.schoolInfo!!.name
                                school_id = t.data!!.schoolInfo!!.id
                                time_tv.text = t.data!!.school_start_year
                            }
                        }

                        override fun onError(call: Call?, response: Response?, e: Exception?) {
                            super.onError(call, response, e)
                        }
                    })
            toolbar.center_str = "更改学校"
        }
        if (is_login && login_type != 0) {
            is_san = true
            code_ll.visibility = View.VISIBLE
            time_ll.visibility = View.VISIBLE

            gr_tv.visibility = View.GONE
        } else if (is_login) {
            time_ll.visibility = View.VISIBLE
            kefuma_ll.visibility = View.VISIBLE
        }
        toolbar.setLeftClick {
            main_back()//执行退出登录操作
            finish()
        }
        send_code_btn.setOnClickListener {
            num = 60
            send_code()//发送验证码
            task = object : TimerTask() {
                override fun run() {
                    if (num >= 0) {
                        val message = Message()
                        message.what = 1
                        handler.sendMessage(message)
                    }
                }
            }
            timer.schedule(task, 1000 * 1, 1000 * 1)
        }
        val df = SimpleDateFormat("yyyy-MM-dd")//设置日期格式
        val now = df.format(Date())// new Date()为获取当前系统时间
        //time_tv.text = now.substring(0, 4)
        perface_iv_sheng.setOnClickListener {
            OkGo.post(url().public_api + "get_provinces")
                    .execute(object : JsonCallback<ZBResponse<CodeModel>>() {
                        override fun onSuccess(t: ZBResponse<CodeModel>, call: okhttp3.Call?, response: okhttp3.Response?) {
                            if (t.code == 0) {
                                list_sheng = t.data as MutableList<CodeModel>?
                                pw = null
                                school_id = 0
                                city_tv.text = "请选择您的学校的市"
                                school_tv.text = "请选择您所在学校"
                                item_click = 1
                                val menuItems = list_sheng!!.mapTo(ArrayList<MenuItem>()) { MenuItem(it.region_name) };
                                show(perface_iv_sheng, menuItems)
                                //var pw = SchoolPopuWindows(this@PerfaceUserActivity, perface_iv_sheng, toSchoolList(list), province_tv, school_tv1, perface_btn_save, false)
                            } else {
                                toast(t.msg.toString())
                            }
                        }

                        override fun onError(call: Call?, response: Response?, e: Exception?) {
                            toast(common().toast_error(e!!))
                        }
                    })
        }

        perface_iv_shi.setOnClickListener {
            var s = school_tv1.text.toString().trim()
            if (s.equals("")) {
                toast("请先选择省")
            } else {
                OkGo.post(url().public_api + "get_cities_by_region_code")
                        .params("region_code", school_tv1.text.toString().trim())// 请求方式和请求url
                        .execute(object : JsonCallback<ZBResponse<CodeModel>>() {
                            override fun onSuccess(t: ZBResponse<CodeModel>, call: okhttp3.Call?, response: okhttp3.Response?) {
                                if (t.code == 0) {
                                    list_shi = t.data as MutableList<CodeModel>?
                                    pw = null
                                    school_id = 0
                                    school_tv.text = "请选择您所在学校"
                                    item_click = 2
                                    val menuItems = list_shi!!.mapTo(ArrayList<MenuItem>()) { MenuItem(it.region_name) };
                                    show(perface_iv_shi, menuItems)
                                    //var pw = SchoolPopuWindows(this@PerfaceUserActivity, perface_iv_shi, toSchoolList(list), city_tv, school_tv2, perface_btn_save, false)
                                } else {
                                    if (is_login) {
                                        toast(t.msg.toString())
                                    } else {
                                        toast("修改成功！")
                                    }
                                }
                            }

                            override fun onError(call: Call?, response: Response?, e: Exception?) {
                                toast(common().toast_error(e!!))
                            }
                        })
            }
        }
        perface_iv_school.setOnClickListener {
            if (school_tv2.text.toString().trim().equals("")) {
                toast("请先选择市")
            } else {
                OkGo.post(url().public_api + "get_schools_by_region_code")
                        .params("region_code", school_tv2.text.toString().trim())// 请求方式和请求url
                        .execute(object : JsonCallback<ZBResponse<SchoolModel>>() {
                            override fun onSuccess(t: ZBResponse<SchoolModel>, call: okhttp3.Call?, response: okhttp3.Response?) {
                                if (t.code == 0) {
                                    myslist = t.data as MutableList<SchoolModel>?
                                    school_id = 0
                                    item_click = 3
                                    var menuItems = ArrayList<MenuItem>();
                                    for (model in myslist!!) {
                                        menuItems.add(MenuItem(model.name));
                                    }
                                    show(perface_iv_school, menuItems)

                                    //pw = SchoolPopuWindows(this@PerfaceUserActivity, perface_iv_school, myslist, school_tv, school_tv3, perface_btn_save, true)
                                } else {
                                    toast(t.msg.toString())
                                }
                            }

                            override fun onError(call: Call?, response: Response?, e: Exception?) {
                                toast(common().toast_error(e!!))
                            }
                        })
            }
        }
        time_ll.setOnClickListener {
            val df = SimpleDateFormat("yyyy-MM-dd HH:mm")//设置日期格式
            val now = df.format(Date())// new Date()为获取当前系统时间
            val timeSelector = TimeSelector(this, TimeSelector.ResultHandler { time ->
                time_tv.text = time.replace(" 00:00", "")
                perface_btn_save.isEnabled = true
                perface_btn_save.setBackgroundResource(R.drawable.login_btn_bg)
            }, "2000-9-1 00:00", now)
            timeSelector.setMode(TimeSelector.MODE.YMD)//只显示 年月日
            timeSelector.show()
        }
    }

    var item_click = 1//1.省。2市。3学校
    fun show(iv: ImageView, menuItems: ArrayList<MenuItem>) {
        var mTopRightMenu = TopRightMenu(this);
        //添加菜单项
//        var menuItems = ArrayList<MenuItem>();
//        for (model in item) {
//            menuItems.add(MenuItem(model.region_name));
//        }
//        menuItems.add(MenuItem("   视频"));
//        menuItems.add(MenuItem("   资讯"));
        var height = menuItems.size * 140
        if (height > 870) {
            height = 870
        }
        var py = -75
        if (item_click == 3) {
            py = -400
        }
        mTopRightMenu
                .setHeight(height)     //默认高度480
                //.setWidth(180)      //默认宽度wrap_content
                .showIcon(false)     //显示菜单图标，默认为true
                .dimBackground(true)        //背景变暗，默认为true
                .needAnimationStyle(true)   //显示动画，默认为true
                .setAnimationStyle(R.style.TRM_ANIM_STYLE)
                .addMenuList(menuItems)
                .setOnMenuItemClickListener { position ->
                    when (item_click) {
                        1 -> {
                            province_tv.text = menuItems[position].text
                            perface_btn_save.setEnabled(false)
                            perface_btn_save.setBackgroundResource(R.drawable.btn_bg_hui)
                            school_tv1.text = list_sheng!![position].region_code
                        }
                        2 -> {
                            city_tv.text = menuItems[position].text
                            perface_btn_save.setEnabled(false)
                            perface_btn_save.setBackgroundResource(R.drawable.btn_bg_hui)
                            school_tv2.text = list_shi!![position].region_code
                        }
                        3 -> {
                            school_tv.text = menuItems[position].text
                            perface_btn_save.setEnabled(true)
                            perface_btn_save.setBackgroundResource(R.drawable.login_btn_bg)
                            school_id = myslist!![position].id!!.toInt()
                        }
                    }

                }
                .showAsDropDown(iv, py, -30);    //带偏移量
    }

    /**
     * 发送验证码
     * */
    var code = ""

    fun send_code() {
        var send_tel = tel_et.text.toString().trim()
        perface_btn_save.isEnabled = true
        var type = "wx"
        when (login_type) {
            1 -> type = "qq"
            2 -> type = "wx"
        }
        OkGo.get(url().public_api + "check_username_is_complete")//验证当前手机号是否注册
                .params("username", send_tel)// 请求方式和请求url
                .params("type", type)
                .execute(object : JsonCallback<LzyResponse<Void>>() {
                    override fun onSuccess(t: LzyResponse<Void>, call: okhttp3.Call?, response: okhttp3.Response?) {
                        if (t.code == 0) {
                            OkGo.get(url().public_api + "get_tel_code")
                                    .params("tel", send_tel)// 请求方式和请求url
                                    .execute(object : JsonCallback<LzyResponse<DataBean>>() {
                                        override fun onSuccess(t: LzyResponse<DataBean>, call: okhttp3.Call?, response: okhttp3.Response?) {
                                            if (t.code == 0) {
                                                //code = t.data!!.code//获得验证码
                                                //code_et.setText(code)
                                                toast("发送成功")
                                            } else {
                                                no_internet()
                                            }
                                        }

                                        override fun onError(call: Call?, response: Response?, e: Exception?) {
                                            toast(common().toast_error(e!!))
                                            no_internet()
                                        }
                                    })
                        } else {
                            toast(t.msg.toString())
                            no_internet()
                        }
                    }

                    override fun onError(call: Call?, response: Response?, e: Exception?) {
                        toast("该用户已存在")
                        no_internet()
                    }
                })
    }

    var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (num > 0) {
                send_code_btn.isClickable = false
                send_code_btn.text = num.toString() + "s"
                num--
            } else {
                no_internet()
            }
        }
    }

    /**
     * 无网络，error处理
     * */
    fun no_internet() {
        send_code_btn.isClickable = true
        send_code_btn.text = "重新发送"
        num = 0
    }

    var model: EditUserModel? = null
    var is_san = false//true,第三方登录
    override fun initEvents() {
        perface_btn_save.setOnClickListener {
            //提交到服务器
            model = EditUserModel()
            //model!!.uid = Utils.getCache(KEY_USERID)
            val time = time_tv.text.toString()
            if (school_id == 0) {
                toast("请选择您所在学校")
            } else if ("点击选择入学时间".equals(time)) {
                toast("请选择入学时间")
            } else {
//                if (school_id != 0) {
                model!!.school_id = school_id.toString()
//                } else {
//                    model!!.school_id = pw!!.position
//                }
                model!!.school_start_year = time.split("-")[0]
                //toast(is_san.toString() + "is_san" + is_login)
                if (is_san && is_login) {//第三方登录判断
                    var tel_str = tel_et.text.toString().trim()
                    var code_et = code_et.text.toString().trim()
                    if (TextUtils.isEmpty(tel_str)) {
                        toast("手机号码不能为空")
                    } else if (TextUtils.isEmpty(code_et)) {
                        toast("验证码不能为空")
                    } else {
                        model!!.kefuma = kefuma_tv.text.toString().trim()
                        model!!.username = tel_str
                        model!!.complete_smscode_val = code_et
                        putData()
                    }
                } else {
                    putData()
                }
            }
        }
    }

    fun putData() {
        var data = ""
        var go = OkGo.post(url().user_api + "modify_user")
        if ((login_type == 1 || login_type == 2) && is_login) {
            go = OkGo.post(url().normal + "index/save_third_user")
            when (login_type) {
                1 -> {
                    model!!.type = "qq"
                    model!!.openid = uuid!!
                }
                else -> {
                    model!!.type = "wx"
                    model!!.openid = uuid!!
                }
            }
            data = Gson().toJson(model)
        } else {
            go = OkGo.post(url().user_api + "modify_user")
            //model!!.username = Utils.getCache(key.KEY_Tel)
            data = "{\"school_id\":\"" + model!!.school_id + "\"}"
        }
        go.params("data", data)// 请求方式和请求url
                .execute(object : JsonCallback<LzyResponse<UserModel>>() {
                    override fun onSuccess(t: LzyResponse<UserModel>, call: okhttp3.Call?, response: okhttp3.Response?) {
                        if (t.code == 0) {
                            Utils.putCache(KEY_SCHOOLID, model!!.school_id)
                            if (!TextUtils.isEmpty(uuid)) {
                                when (login_type) {
                                    1 -> {
                                        //Utils.putCache(key.KEY_QQ, uuid)
                                    }
                                    else -> {
                                        Utils.putCache(key.KEY_WX, uuid)
                                    }
                                }
                                Utils.putCache(key.KEY_USERID, t.data!!.uid)
//                                if(is_login) {
//                                    startActivity(Intent(this@PerfaceUserActivity, MainActivity::class.java))
//                                }
                                setResult(77, Intent().putExtra("result", true))
                            }
                            //uuid为null，只是执行修改操作
                            if (is_login) {
                                toast(t.msg.toString())
                            } else {
                                toast("修改成功！")
                            }
                            finish()

                        } else {
                            toast(t.msg.toString())
                        }
                    }

                    override fun onError(call: Call?, response: Response?, e: Exception?) {
                        toast(e.toString().split(":")[1])
                    }
                })
    }

    override fun onBackPressed() {
        main_back()
        super.onBackPressed()
    }

    fun main_back() {
        if (is_login) {
            OkGo.post(url().public_api + "logout")
                    .execute(object : JsonCallback<String>() {
                        override fun onSuccess(t: String, call: okhttp3.Call?, response: okhttp3.Response?) {

                        }
                    })
        }
    }

    fun toSchoolList(list: List<CodeModel>?): ArrayList<SchoolModel>? {
        var zx_list = ArrayList<SchoolModel>()
        for (model in list!!) {
            var school: SchoolModel = SchoolModel()
            school.name = model.region_name
            school.id = model.region_code
            zx_list!!.add(school)
        }
        return zx_list;
    }
}
