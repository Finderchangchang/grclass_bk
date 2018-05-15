package wai.gr.cla.wxapi

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.tencent.mm.sdk.constants.ConstantsAPI
import com.tencent.mm.sdk.modelbase.BaseReq
import com.tencent.mm.sdk.modelbase.BaseResp
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler
import com.tsy.sdk.pay.weixin.WXPay
import wai.gr.cla.R
import wai.gr.cla.base.App
import wai.gr.cla.base.BaseActivity

class WXPayEntryActivity : BaseActivity(), IWXAPIEventHandler {
    override fun onResp(resp: com.tencent.mm.sdk.modelbase.BaseResp?) {
        if (resp!!.getType() == com.tencent.mm.sdk.constants.ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (WXPay.getInstance() != null) {
                if (resp.errStr != null) {
                    Log.e("wxpay", "errstr=" + resp.errStr)
                }

                WXPay.getInstance().onResp(resp.errCode)
                finish()
            }
        }
    }

    override fun onReq(p0: com.tencent.mm.sdk.modelbase.BaseReq?) {

    }


    override fun setLayout(): Int {
        return R.layout.activity_wxpay_entry
    }

    override fun initViews() {
        if (WXPay.getInstance() != null) {
            WXPay.getInstance().wxApi.handleIntent(intent, this)
        } else {
            finish()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (WXPay.getInstance() != null) {
            WXPay.getInstance().wxApi.handleIntent(intent, this)
        }
    }
    override fun initEvents() {

    }

}
