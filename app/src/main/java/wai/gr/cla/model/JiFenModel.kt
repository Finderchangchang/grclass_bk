package wai.gr.cla.model

import java.io.Serializable

/**
 * 积分模型
 * Created by Administrator on 2017/5/19.
 */

class JiFenModel : Serializable {

    /**
     * uid : 1
     * nick : 测试用户
     * username : 15354220940
     * guanbi : 788
     * head_img : /uploads/user/1_2017052410390114955935410979.png
     * rank : 1
     */

    var uid: Int = 0
    var nick: String = ""
    var username = ""
    var guanbi: Double = 0.0
    var head_img = ""
    var rank: Int = 0
}
