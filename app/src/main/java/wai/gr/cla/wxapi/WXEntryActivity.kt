package wai.gr.cla.wxapi


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.lzy.okgo.OkGo

import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.umeng.socialize.weixin.view.WXCallbackActivity
import okhttp3.Call
import okhttp3.Response
import wai.gr.cla.R

import wai.gr.cla.base.App
import wai.gr.cla.callback.JsonCallback
import wai.gr.cla.method.Utils
import wai.gr.cla.model.*
import wai.gr.cla.ui.LoginActivity
import wai.gr.cla.ui.PerfaceUserActivity
import com.lzy.okserver.download.DownloadManager
import com.lzy.okserver.download.DownloadService


class WXEntryActivity : WXCallbackActivity(), IWXAPIEventHandler {
    var api: IWXAPI? = null
    private var downloadManager: DownloadManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pay_result)
        //如果没回调onResp，八成是这句没有写
        api = WXAPIFactory.createWXAPI(this, App.wx_id);
        api!!.handleIntent(intent, this)
    }


    // 微信发送请求到第三方应用时，会回调到该方法
    override fun onReq(req: BaseReq) {

    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    //app发送消息给微信，处理返回消息的回调
    override fun onResp(resp: BaseResp?) {
        when (resp!!.errCode) {
            BaseResp.ErrCode.ERR_AUTH_DENIED, BaseResp.ErrCode.ERR_USER_CANCEL -> {
                when (resp.type) {
                    RETURN_MSG_TYPE_LOGIN -> {
                        Toast.makeText(this@WXEntryActivity, "登录取消", Toast.LENGTH_SHORT).show()
                    }
                    RETURN_MSG_TYPE_SHARE -> {
                        Toast.makeText(this@WXEntryActivity, "分享失败", Toast.LENGTH_SHORT).show()
                    }
                }
                finish()//如果返回错误则关闭当前页面
            }
            BaseResp.ErrCode.ERR_OK -> when (resp.type) {
                RETURN_MSG_TYPE_LOGIN -> {
                    //拿到了微信返回的code,立马再去请求access_token
                    val code = (resp as SendAuth.Resp).code
                    OkGo.get("https://api.weixin.qq.com/sns/oauth2/access_token?appid=" +
                            App.wx_id + "&secret=" + App.wx_secret + "&code=" + code + "&grant_type=authorization_code")
                            .execute(object : JsonCallback<LzyResponse<String>>() {
                                override fun onSuccess(t: LzyResponse<String>?, call: Call?, response: Response?) {
                                    val path = "https://api.weixin.qq.com/sns/userinfo?access_token=" +
                                            t!!.access_token + "&openid=" + App.wx_id
                                    val token = t!!.access_token
                                    OkGo.get(path)
                                            .execute(object : JsonCallback<LzyResponse<String>>() {
                                                override fun onSuccess(t: LzyResponse<String>?, call: Call?, response: Response?) {
                                                    //登录成功，跳转到注册学校页面(必须选择完学校以后才算是注册完成)
                                                    if (t!!.nickname != null) {
                                                        var uuid = t.unionid
                                                        OkGo.post(url().public_api + "open_login")
                                                                .params("openid", uuid)// 请求方式和请求url
                                                                .params("login_type", "wx")// 请求方式和请求url
                                                                .params("access_token", token)
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
                                                                                downloadManager!!.targetFolder = this@WXEntryActivity.filesDir.absolutePath;
                                                                                downloadManager!!.removeAllTask()
                                                                                Utils.putCache(key.KEY_OLD_USERID, Utils.getCache(key.KEY_USERID))
                                                                            }
                                                                            //未选择学校跳转到选择学校页面
                                                                            if (("0").equals(model!!.school_id)) {
                                                                                finish()//关闭回调页面
                                                                                LoginActivity.main!!.finish()//关闭login页面
                                                                                startActivityForResult(Intent(this@WXEntryActivity, PerfaceUserActivity::class.java)
                                                                                        .putExtra("uuid", uuid)
                                                                                        .putExtra("type", 2)
                                                                                        .putExtra("is_login", true), 0)
                                                                            } else {
                                                                                Utils.putCache(key.KEY_SCHOOLID, model.school_id)
                                                                                Utils.putCache(key.KEY_WX, uuid)
                                                                                Utils.putCache(key.KEY_TOKEN, token)
                                                                                Utils.putCache(key.KEY_USERID, model.uid)
                                                                                Utils.putCache(key.KEY_USERNAME, model.nick)
                                                                                finish()
                                                                                LoginActivity.main!!.finish()//关闭login页面
                                                                                Toast.makeText(this@WXEntryActivity, "登录成功", Toast.LENGTH_LONG).show()
                                                                            }
                                                                        } else {
                                                                            Toast.makeText(this@WXEntryActivity, t.msg.toString(), Toast.LENGTH_SHORT).show()
                                                                        }
                                                                    }

                                                                    override fun onError(call: Call?, response: Response?, e: Exception?) {
                                                                        var mes = e.toString().split(":")
                                                                    }
                                                                })
//                                                        finish()//关闭当前页面
                                                    }
                                                }
                                            })
                                }
                            })
                }

                RETURN_MSG_TYPE_SHARE -> {
                    Toast.makeText(this@WXEntryActivity, "分享成功", Toast.LENGTH_SHORT).show()
                    finish()
                }

            }//就在这个地方，用网络库什么的或者自己封的网络api，发请求去咯，注意是get请求
        }//                if (RETURN_MSG_TYPE_SHARE == resp.getType()) UIUtils.showToast("分享失败");
        //                else UIUtils.showToast("登录失败");
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 77) {
            Toast.makeText(this@WXEntryActivity, "分享成功", Toast.LENGTH_SHORT).show()
            if (data!!.getBooleanExtra("result", false)) {
                finish()
                LoginActivity!!.main!!.finish()
            }
        }
    }

    companion object {
        private val RETURN_MSG_TYPE_LOGIN = 1
        private val RETURN_MSG_TYPE_SHARE = 2
    }
}
