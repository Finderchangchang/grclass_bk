package wai.gr.cla.ui

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.lzy.okgo.OkGo
import com.lzy.okserver.download.DownloadManager
import com.lzy.okserver.download.DownloadService
import com.tencent.connect.common.Constants
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.Call
import okhttp3.Response
import org.json.JSONObject
import wai.gr.cla.R
import wai.gr.cla.base.App
import wai.gr.cla.base.BaseActivity
import wai.gr.cla.callback.JsonCallback
import wai.gr.cla.method.Utils
import wai.gr.cla.method.common
import wai.gr.cla.model.*
import android.net.Uri
import android.text.TextUtils
import android.view.inputmethod.InputMethodManager


/**
 * 登录
 */
class LoginActivity : BaseActivity() {
    override fun setLayout(): Int {
        return R.layout.activity_login
    }

    override fun initViews() {
        main = this
        reg_tv.setOnClickListener {
            startActivity(Intent(this, RegUserActivity::class.java).putExtra("is_reg", true))
            finish()
        }
        forget_tv.setOnClickListener {
            startActivity(Intent(this, RegUserActivity::class.java).putExtra("is_reg", false))
        }
        toolbar.setLeftClick { closeThis() }
        teacher_login_tv.setOnClickListener {
            //            startActivity(Intent(MainActivity.main, WebActivity::class.java).putExtra("name", "position")
//                    .putExtra("url", url().normal + "index/login_phone")
//                    .putExtra("title", "老师登录入口"))
            val intent = Intent()
            intent.action = "android.intent.action.VIEW"
            val content_url = Uri.parse(url().normal + "index/login_phone")
            intent.data = content_url
            startActivity(intent)
        }
    }

    internal var mTencent: Tencent? = null //qq主操作对象
    var scope: String? = null //获取信息的范围参数
    var loginListener: IUiListener? = null //授权登录监听器
    private var downloadManager: DownloadManager? = null

    companion object {
        var main: LoginActivity? = null
    }

    override fun initEvents() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(tel_et.windowToken, 0);
        OkGo.post(url().public_api + "get_qq")
                .execute(object : JsonCallback<LzyResponse<String>>() {
                    override fun onSuccess(data: LzyResponse<String>, call: okhttp3.Call?, response: okhttp3.Response?) {
                        Utils.putCache("qq", data.data)
                    }
                })
        tel_et.setText(Utils.getCache(key.KEY_Tel))
        login_btn.setOnClickListener {
            val tel = tel_et.text.toString().trim()
            var psw = pwd_et.text.toString().trim()
            psw = Utils.string2MD5(psw)
            OkGo.post(url().public_api + "login")
                    .params("username", tel)// 请求方式和请求url
                    .params("password", psw)// 请求方式和请求url
                    .execute(object : JsonCallback<LzyResponse<UserModel>>() {
                        override fun onSuccess(t: LzyResponse<UserModel>, call: okhttp3.Call?, response: okhttp3.Response?) {
                            if (t.code == 0) {
                                var model = t.data
                                Utils.putCache(key.KEY_SCHOOLID, model!!.school_id)
                                Utils.putCache(key.KEY_PWd, psw)
                                Utils.putCache(key.KEY_Tel, tel)
                                Utils.putCache(key.KEY_USERID, model!!.uid)
                                Utils.putCache("tel", t.data!!.username)
                                Utils.putCache(key.KEY_USERNAME, model.nick)
                                if (!Utils.getCache(key.KEY_OLD_USERID).equals(Utils.getCache(key.KEY_USERID))) {
                                    downloadManager = DownloadService.getDownloadManager()
                                    downloadManager!!.targetFolder = main!!.filesDir.absolutePath;
                                    downloadManager!!.removeAllTask()
                                    Utils.putCache(key.KEY_OLD_USERID, Utils.getCache(key.KEY_USERID))
                                }
                                if (model.school_id.equals("") || model.school_id.equals("0")) {
                                    startActivityForResult(Intent(this@LoginActivity, PerfaceUserActivity::class.java)
                                            .putExtra("uuid", "")
                                            .putExtra("is_login", true), 0)
                                    finish()
                                } else {
                                    closeThis()
                                }
                            } else {
                                toast(t.msg.toString())
                            }
                        }

                        override fun onError(call: Call?, response: Response?, e: Exception?) {
                            toast(e!!.message!!)
                        }
                    })
        }
        initQQ()//初始化qq
        qq_login_iv.setOnClickListener {
            if (!mTencent!!.isSessionValid) {
                //开始qq授权登录
                mTencent!!.login(this@LoginActivity, scope, loginListener)
            }
        }
        wc_login_iv.setOnClickListener {
            if (App.api != null && App.api!!.isWXAppInstalled) {
                val req = SendAuth.Req()
                req.scope = "snsapi_userinfo"
                req.state = "wechat_sdk_demo_test_neng"
                App.api!!.sendReq(req)
            } else {
                Toast.makeText(this, "用户未安装微信", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        mTencent!!.logout(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        closeThis()
        mTencent!!.logout(this)
    }

    fun closeThis() {

        setResult(88, Intent().putExtra("result", true))
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 77) {
            if (data!!.getBooleanExtra("result", false)) {
                closeThis()
            }
        }
        if (requestCode == Constants.REQUEST_LOGIN) {
            Tencent.handleResultData(data, loginListener)
        }
    }

    /**
     * 初始化qq数据
     */
    private fun initQQ() {
        //初始化qq主操作对象
        mTencent = Tencent.createInstance(App.qq_id, this@LoginActivity)
        //要所有权限，不然会再次申请增量权限，这里不要设置成get_user_info,add_t
        scope = "all"
        loginListener = object : IUiListener {

            override fun onError(arg0: UiError) {
                toast("error:")
            }

            /**
             * 返回json数据样例

             * {"ret":0,"pay_token":"D3D678728DC580FBCDE15722B72E7365",
             * "pf":"desktop_m_qq-10000144-android-2002-",
             * "query_authority_cost":448,
             * "authority_cost":-136792089,
             * "openid":"015A22DED93BD15E0E6B0DDB3E59DE2D",
             * "expires_in":7776000,
             * "pfkey":"6068ea1c4a716d4141bca0ddb3df1bb9",
             * "msg":"",
             * "access_token":"A2455F491478233529D0106D2CE6EB45",
             * "login_cost":499}
             */
            override fun onComplete(value: Any?) {
                if (value == null) {
                    return
                }

                try {
                    val jo = value as JSONObject?
                    val ret = jo!!.getInt("ret")
                    if (ret == 0) {
                        val openID = jo.getString("openid")
                        var accessToken = jo.getString("access_token")
                        val expires = jo.getString("expires_in")
                        mTencent!!.openId = openID
                        mTencent!!.setAccessToken(accessToken, expires)
                        OkGo.post(url().public_api + "open_login")
                                .params("openid", openID)// 请求方式和请求url
                                .params("login_type", "qq")// 请求方式和请求url
                                .params("access_token", accessToken)
                                .execute(object : JsonCallback<LzyResponse<UserModel>>() {
                                    override fun onSuccess(t: LzyResponse<UserModel>, call: okhttp3.Call?, response: okhttp3.Response?) {
                                        if (t.code == 0) {
                                            var model = t.data
                                            Utils.putCache("tel", t.data!!.username)
                                            /**
                                             * 前后id不同，清空下载记录
                                             * */
                                            if (!Utils.getCache(key.KEY_OLD_USERID).equals(Utils.getCache(key.KEY_USERID))) {
                                                downloadManager = DownloadService.getDownloadManager()
                                                downloadManager!!.targetFolder = main!!.filesDir.absolutePath;
                                                downloadManager!!.removeAllTask()
                                                Utils.putCache(key.KEY_OLD_USERID, Utils.getCache(key.KEY_USERID))
                                            }
                                            Utils.putCache(key.KEY_QQ, openID)
                                            Utils.putCache(key.KEY_TOKEN, accessToken)
                                            //未选择学校跳转到选择学校页面
                                            if (("0").equals(model!!.school_id)) {
                                                startActivityForResult(Intent(this@LoginActivity, PerfaceUserActivity::class.java)
                                                        .putExtra("uuid", t.data!!.openid)
                                                        .putExtra("is_login", true)
                                                        .putExtra("type", 1), 0)
                                            } else {
                                                Utils.putCache(key.KEY_SCHOOLID, model.school_id)
                                                //Utils.putCache(key.KEY_QQ, accessToken)
                                                Utils.putCache(key.KEY_USERID, model.uid)
                                                Utils.putCache(key.KEY_USERNAME, model.nick)

                                                Toast.makeText(this@LoginActivity, "登录成功", Toast.LENGTH_LONG).show()
                                                finish()
                                            }
                                        } else {
                                            toast(t.msg.toString())
                                        }
                                    }

                                    override fun onError(call: Call?, response: Response?, e: Exception?) {
                                        toast(common().toast_error(e!!))
                                    }
                                })
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@LoginActivity, "error",
                            Toast.LENGTH_LONG).show()
                }

            }

            override fun onCancel() {
                Toast.makeText(this@LoginActivity, "取消了",
                        Toast.LENGTH_LONG).show()
            }
        }
    }
}
