package wai.gr.cla.ui

import android.text.TextUtils
import com.lzy.okgo.OkGo
import kotlinx.android.synthetic.main.activity_update_password.*
import okhttp3.Call
import okhttp3.Response
import wai.gr.cla.R
import wai.gr.cla.base.BaseActivity
import wai.gr.cla.callback.JsonCallback
import wai.gr.cla.method.Utils
import wai.gr.cla.method.common
import wai.gr.cla.model.LzyResponse
import wai.gr.cla.model.UserModel
import wai.gr.cla.model.key
import wai.gr.cla.model.url

/**
 * 修改密码
 * Created by Administrator on 2017/5/17.
 */

class UpdatePassWordActivity : BaseActivity() {

    override fun setLayout(): Int {
        return R.layout.activity_update_password
    }

    override fun initViews() {

    }

    override fun initEvents() {
        toolbar.setLeftClick { finish() }
        psw_btn_save!!.setOnClickListener {
            if (Yan()) {
                var old_pwd = updatepsw_et_psw.text.toString().trim()
                var new_pwd = updatepsw_et_newpsw.text.toString().trim()
                old_pwd = Utils.string2MD5(old_pwd)
                new_pwd = Utils.string2MD5(new_pwd)
                OkGo.get(url().user_api + "modify_pass")
                        .params("old_pass", old_pwd)
                        .params("password", new_pwd)
                        .execute(object : JsonCallback<LzyResponse<UserModel>>() {
                            override fun onSuccess(t: LzyResponse<UserModel>, call: okhttp3.Call?, response: okhttp3.Response?) {
                                if (t.code == 0) {
                                    Utils.putCache(key.KEY_PWd, new_pwd)
                                    finish()
                                    toast("修改成功")
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
    }

    fun Yan(): Boolean {
        var old_pwd = updatepsw_et_psw.text.toString().trim()
        var new_pwd = updatepsw_et_newpsw.text.toString().trim()
        val old = Utils.getCache(key.KEY_PWd)
        old_pwd = Utils.string2MD5(old_pwd)
        new_pwd = Utils.string2MD5(new_pwd)
        if (TextUtils.isEmpty(old_pwd) || !old_pwd.equals(Utils.getCache(key.KEY_PWd))) {
            toast("旧密码不正确，请重新输入")
            return false
        } else if (TextUtils.isEmpty(new_pwd)) {
            toast("请输入新密码")
            return false
        } else if (!Utils.string2MD5(updatepsw_et_newpsw2.text.toString().trim()).equals(new_pwd)) {
            toast("前后密码不一致，请重新输入")
            return false
        }
        return true
    }
}
