package wai.gr.cla.method

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import com.lzy.okgo.OkGo
import okhttp3.Call
import okhttp3.Response
import wai.gr.cla.callback.JsonCallback
import wai.gr.cla.model.LzyResponse
import wai.gr.cla.model.key
import wai.gr.cla.model.url
import wai.gr.cla.ui.LoginActivity
import wai.gr.cla.ui.PerfaceUserActivity

/**
 * Created by Administrator on 2017/5/23.
 */
class common {
    /**
     * 检测当前登录状态，true已登录，false未登录无法进行下面操作
     * */
    fun checkLogin(context: Activity): Boolean {
        val school_id = Utils.getCache(key.KEY_SCHOOLID)
        val login_key = Utils.getCache(key.KEY_PWd)
        val user_id = Utils.getCache(key.KEY_Tel)
        val key_wx = Utils.getCache(key.KEY_WX)
        val key_qq = Utils.getCache(key.KEY_QQ)
        //login_key或者用户id为空说明已退出登录
        if (TextUtils.isEmpty(user_id) && TextUtils.isEmpty(login_key) && TextUtils.isEmpty(key_wx) && TextUtils.isEmpty(key_qq)) {
            context.startActivityForResult(Intent(context, LoginActivity::class.java), 0)
        } else if (TextUtils.isEmpty(school_id)) {//未选择学校
            context.startActivityForResult(Intent(context, PerfaceUserActivity::class.java), 1)
        } else {//因为在main已经自动登录，所以这里上面有空说明肯定没登录
            return true
        }
        return false
    }

    /**
     * 显示错误提示
     * */
    fun toast_error(result: Exception): String {
        var mes = result.toString().split(":")
        if (mes.size > 2 && (mes[1] != "" || mes[1] != "")) {
            if (mes[2].contains("No address associated with hostname")) {
                return "请检查您的网络连接"
            }
            return mes[2]
        } else {
            return result.message!!.toString()
        }
    }

    /**
     * 快速登录
     * */
    fun quick_login() {
        OkGo.post(url().public_api + "autologin")
                .params("login_key", Utils.getCache(key.KEY_LOGINKEY))//当前页数
                .params("uid", Utils.getCache(key.KEY_USERID))//当前页数
                .execute(object : JsonCallback<LzyResponse<String>>() {
                    override fun onSuccess(t: LzyResponse<String>, call: okhttp3.Call?, response: okhttp3.Response?) {

                    }

                    override fun onError(call: Call?, response: Response?, e: Exception?) {

                    }
                })
    }
}