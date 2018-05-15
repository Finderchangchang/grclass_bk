package wai.gr.cla.ui

import android.content.Intent
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import com.lzy.okgo.OkGo
import kotlinx.android.synthetic.main.activity_set.*
import okhttp3.Call
import okhttp3.Response

import wai.gr.cla.R
import wai.gr.cla.base.BaseActivity
import wai.gr.cla.callback.JsonCallback
import wai.gr.cla.method.DataCleanManager
import wai.gr.cla.method.Utils
import wai.gr.cla.method.common
import wai.gr.cla.model.LzyResponse
import wai.gr.cla.model.key
import wai.gr.cla.model.url

/**
 * Created by JX on 2017/5/22.
 * 设置
 */

class SetActivity : BaseActivity() {
    override fun setLayout(): Int {
        return R.layout.activity_set
    }

    override fun initViews() {
        toolbar.setLeftClick { finish() }
        clear_cache_rl.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("提示")
            builder.setMessage("确定清除缓存吗？")
            builder.setNegativeButton("取消", null)
            builder.setPositiveButton("确定") { dialog, which ->
                DataCleanManager.clearAllCache(this)
                toast("清除成功")
                cache_num_tv.text = DataCleanManager.getTotalCacheSize(this)
            }
            builder.show()
        }
        version_tv.text = "当前版本号：" + Utils.version.toDouble()
        about_us_rl.setOnClickListener {
            startActivity(Intent(this@SetActivity, TextWebActivity::class.java).putExtra("name", "关于"))
        }
        see_tel_rl.setOnClickListener {
            startActivity(Intent(this@SetActivity, SeeTelActivity::class.java))
        }
        //地址管理
        address_rl.setOnClickListener {
            startActivity(Intent(this@SetActivity, AddressManageActivity::class.java))
        }
        change_school_rl.setOnClickListener {
            var uuid = ""
            var login_type = 0
            if (!TextUtils.isEmpty(Utils.getCache(key.KEY_WX))) {
                login_type = 2
                uuid = Utils.getCache(key.KEY_WX)
            } else if (!TextUtils.isEmpty(Utils.getCache(key.KEY_QQ))) {
                login_type = 1
                uuid = Utils.getCache(key.KEY_QQ)
            }
            startActivity(Intent(this@SetActivity, PerfaceUserActivity::class.java)
                    .putExtra("uuid", uuid)
                    .putExtra("type", login_type)
                    .putExtra("is_login", false))

        }
        cache_num_tv.text = DataCleanManager.getTotalCacheSize(this)
        change_pwd_rl.setOnClickListener { startActivity(Intent(this@SetActivity, UpdatePassWordActivity::class.java)) }
        //退出登录操作
        exit_btn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("提示")
            builder.setMessage("确定要退出当前账号吗？")
            builder.setNegativeButton("取消", null)
            builder.setPositiveButton("确定") { dialog, which ->
                OkGo.post(url().public_api + "logout")
                        .execute(object : JsonCallback<LzyResponse<String>>() {
                            override fun onSuccess(t: LzyResponse<String>, call: okhttp3.Call?, response: okhttp3.Response?) {
                                if (t.code == 0) {
                                    Utils.putCache(key.KEY_SCHOOLID, "")
                                    //Utils.putCache(key.KEY_Tel, "")
                                    Utils.putCache("tel", "")
                                    Utils.putCache(key.KEY_PWd, "")
                                    Utils.putCache(key.KEY_WX, "")
                                    Utils.putCache(key.KEY_QQ, "")
                                    Utils.putCache(key.KEY_USERID, "")
                                    finish()
                                    MainActivity.main!!.finish()
                                    startActivity(Intent(this@SetActivity, MainActivity::class.java))
                                }
                            }

                            override fun onError(call: Call?, response: Response?, e: Exception?) {
                                toast(common().toast_error(e!!))
                            }
                        })
            }
            builder.show()
        }
    }

    override fun initEvents() {

    }
}
