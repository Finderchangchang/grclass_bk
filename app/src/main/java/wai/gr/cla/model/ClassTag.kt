package wai.gr.cla.model

import java.io.Serializable

/**
 * Created by Finder丶畅畅 on 2017/5/9 22:42
 * QQ群481606175
 */
class ClassTag : Serializable {
    var click: Boolean = false
    /**
     * cid : 1
     * pid : 0
     * name : 公共课
     * cdate : 2017-04-07 11:12:50
     * mdate : 2017-04-07 14:39:29
     */

    var cid: Int = 0
    var pid: Int = 0
    var name: String? = null
    var cdate: String? = null
    var mdate: String? = null

    constructor(name: String, click: Boolean) {
        this.click = click
        this.name = name
    }
}