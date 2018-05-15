package wai.gr.cla.model

import java.io.Serializable

/**
 * 用户登录表
 * Created by lenovo on 2017/5/17.
 */

class UserModel : Serializable {
    var username: String = ""
    var school_id: String? = null
    val login_count: String? = null
    var uid: String? = null
    val login_key: String? = null
    val last_login: String = ""
    val nick: String? = null
    val wxid: String? = null
    val qqid: String? = null
    val cdate: String? = null
    val mdate: String? = null
    var guanbi: Double = 0.0
    var head_img: String? = null
    var forbidden: Int = 0
    var school_start_year = ""
    var openid = ""
    var type = ""
    var complete_smscode_val = ""//验证码
    var schoolInfo:ShopInfoModel?=null
}
