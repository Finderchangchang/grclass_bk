package wai.gr.cla.model

import com.google.gson.annotations.SerializedName

/**
 * 支付model
 * Created by Administrator on 2017/6/8.
 */
class PayModel {
    /**
     * appid : wxc281dccd97667c78
     * partnerid : 1480178942
     * prepayid : wx201706081110140b5e029a370499237728
     * package : Sign=WXPay
     * noncestr : DFQQSLAFE0LLUCPSS2CG9KVPFPJBSTKV
     * timestamp : 1496891414
     * sign : F48EE09A5A4106F2A9EA7CA3203EB735
     */
    var appid: String? = null
    var partnerid: String? = null
    var prepayid: String? = null
    @SerializedName("package")
    var packageX: String? = null
    var noncestr: String? = null
    var timestamp: Int = 0
    var sign: String? = null
}
