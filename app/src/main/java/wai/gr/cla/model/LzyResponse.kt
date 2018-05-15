package wai.gr.cla.model

import java.io.Serializable

/**
 * Created by Finder丶畅畅 on 2017/5/13 00:04
 * QQ群481606175
 */
class LzyResponse<T> : Serializable {

    var code: Int = 0
    var msg: String? = null
    var data: T? = null
    var PHPSESSID: String = ""

    var access_token: String? = null
    var expires_in: Int = 0
    var refresh_token: String? = null
    var openid: String = ""
    var scope: String? = null
    var unionid: String = ""

    //--微信用户id
    var nickname: String? = null
    var sex: Int = 0
    var province: String? = null
    var city: String? = null
    var country: String? = null
    var headimgurl: String? = null
    var privilege: List<String>? = null
    var version: String = ""//版本号
    var time: String = ""//提交时间
    var content: String = ""//更新内容
    var download: String = ""//下载地址
    var car:List<CarModel>?=null

    companion object {
        const val serialVersionUID = 5213230387175987834L
    }
    //    public T data;

}