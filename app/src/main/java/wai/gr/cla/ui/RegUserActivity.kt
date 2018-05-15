package wai.gr.cla.ui

import android.content.Intent
import android.os.Handler
import android.os.Message
import android.view.View
import com.lzy.okgo.OkGo
import kotlinx.android.synthetic.main.activity_reg.*
import okhttp3.Call
import okhttp3.Response
import wai.gr.cla.R
import wai.gr.cla.base.BaseActivity
import wai.gr.cla.callback.JsonCallback
import wai.gr.cla.method.Utils
import wai.gr.cla.method.common
import wai.gr.cla.model.*
import wai.gr.cla.model.key.KEY_LOGINKEY
import wai.gr.cla.model.key.KEY_SCHOOLID
import wai.gr.cla.model.key.KEY_SESSIONID
import java.util.*


/**
 * 注册
 */
class RegUserActivity : BaseActivity() {

    override fun setLayout(): Int {
        return R.layout.activity_reg
    }

    var is_reg = false
    internal var code = ""
    internal var send_tel = ""//发送验证码的手机号
    override fun initViews() {
        is_reg = intent.getBooleanExtra("is_reg", false)
        if (is_reg) {
            toolbar.center_str = "注册"
            check_persion_ll.visibility = View.VISIBLE
        } else {
            toolbar.center_str = "忘记密码"
            reg_btn.text = "提交新密码"
            check_persion_ll.visibility = View.GONE
        }
        xy_tv.setOnClickListener {
            startActivity(Intent(this, TextWebActivity::class.java).putExtra("name", "协议"))
        }
        toolbar.setLeftClick { finish() }
        send_code_btn.setOnClickListener {
            num = 60
            send_code()//发送验证码
            timer = Timer()
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
        read_cb.setOnCheckedChangeListener { buttonView, isChecked ->
            reg_btn.isClickable = isChecked
            reg_btn.isEnabled = isChecked
        }
        reg_btn.setOnClickListener {
            var now_tel = tel_et.text.toString().trim()
            var pwd = pwd_et.text.toString().trim()
            var pwd_again = pwd_again_et.text.toString().trim()
            //当前输入手机号与发送验证码手机号不一致，验证码不正确
            if (pwd.length < 6) {
                toast("密码太短，再输入一些吧！")
            } else if (!pwd.equals(pwd_again)) {
                toast("前后密码不一致，请重新输入")
            } else {
                pwd = Utils.string2MD5(pwd)
                if (is_reg) {
                    OkGo.post(url().public_api + "register")
                            .headers("cookie", Utils.getCache(KEY_SESSIONID))
                            .params("username", now_tel)// 请求方式和请求url
                            .params("password", pwd)// 请求方式和请求url
                            .params("code", code_et.text.toString().trim())// 请求方式和请求url
                            .params("school_id", "0")
                            .execute(object : JsonCallback<LzyResponse<UserModel>>() {
                                override fun onSuccess(t: LzyResponse<UserModel>, call: okhttp3.Call?, response: okhttp3.Response?) {
                                    if (t.code == 0) {
                                        var model = t.data
                                        Utils.putCache(KEY_SCHOOLID, "0")
                                        Utils.putCache(KEY_LOGINKEY, model!!.login_key)
                                        Utils.putCache(key.KEY_PWd, pwd)
                                        Utils.putCache(key.KEY_Tel, now_tel)
                                        Utils.putCache(key.KEY_USERID,model!!.uid)
                                        startActivityForResult(Intent(this@RegUserActivity, PerfaceUserActivity::class.java)
                                                .putExtra("uuid", "")
                                                .putExtra("is_login", true), 0)
                                        finish()
                                    }
                                    toast(t.msg.toString())
                                }

                                override fun onError(call: Call?, response: Response?, e: Exception?) {
                                    toast(common().toast_error(e!!))
                                    no_internet()
                                }
                            })
                } else {
                    OkGo.post(url().public_api + "reset_pass")
                            .params("username", now_tel)// 请求方式和请求url
                            .params("password", pwd)// 请求方式和请求url
                            .params("code", code_et.text.toString().trim())// 请求方式和请求url
                            .execute(object : JsonCallback<LzyResponse<UserModel>>() {
                                override fun onSuccess(t: LzyResponse<UserModel>, call: okhttp3.Call?, response: okhttp3.Response?) {
                                    if (t.code == 0) {
                                        finish()
                                        toast("修改成功")
                                    } else {
                                        toast(t.msg.toString())
                                    }
                                }

                                override fun onError(call: Call?, response: Response?, e: Exception?) {
                                    toast(common().toast_error(e!!))
                                    no_internet()
                                }
                            })
                }
            }
        }
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
    internal var num = 60
    var timer = Timer()
    var task: TimerTask? = null

    /**
     * 发送验证码
     * */
    fun send_code() {
        send_tel = tel_et.text.toString().trim()
        if(is_reg){
            OkGo.get(url().public_api + "check_username_is_register")//验证当前手机号是否注册
                    .params("username", send_tel)// 请求方式和请求url
                    .execute(object : JsonCallback<LzyResponse<Void>>() {
                        override fun onSuccess(t: LzyResponse<Void>, call: okhttp3.Call?, response: okhttp3.Response?) {
                            if (t.code == -1) {
                                OkGo.get(url().public_api + "get_tel_code")//发送验证码操作
                                        .params("tel", send_tel)// 请求方式和请求url
                                        .execute(object : JsonCallback<LzyResponse<DataBean>>() {
                                            override fun onSuccess(t: LzyResponse<DataBean>, call: okhttp3.Call?, response: okhttp3.Response?) {
                                                if (t.code == 0) {
//                                                    code = t.data!!.code//获得验证码
//                                                    code_et.setText(code)
//                                                    send_code_btn.isClickable = true
//                                                    send_code_btn.text = "重新发送"
//                                                    num = 0
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
                            }else{
                                toast(t.msg.toString())
                                no_internet()
                            }
                        }

                        override fun onError(call: Call?, response: Response?, e: Exception?) {
                            toast("该用户已存在")
                            no_internet()
                        }
                    })
        }else{
            OkGo.get(url().public_api + "get_tel_code")//发送验证码操作
                    .params("tel", send_tel)// 请求方式和请求url
                    .execute(object : JsonCallback<LzyResponse<DataBean>>() {
                        override fun onSuccess(t: LzyResponse<DataBean>, call: okhttp3.Call?, response: okhttp3.Response?) {
                            if (t.code == 0) {
//                                code = t.data!!.code//获得验证码
//                                code_et.setText(code)
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
        }
    }

    /**
     * 无网络，error处理
     * */
    fun no_internet() {
        send_code_btn.isClickable = true
        send_code_btn.text = "重新发送"
        num = 0
        timer.cancel()
    }

    override fun initEvents() {

    }
}
