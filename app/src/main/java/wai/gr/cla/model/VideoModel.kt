package wai.gr.cla.model

import java.io.Serializable

/**
 * Created by Finder丶畅畅 on 2017/5/16 22:38
 * QQ群481606175
 */

class VideoModel : Serializable {

    /**
     * id : 20
     * course_id : 1
     * name : 概述
     * number : 1
     * url : /uploads/video/1/14944900908499.mp4
     * thumbnail : /uploads/video/1/14944900908499.jpg
     * free : 1
     * cdate : 2017-05-11 16:08:08
     * mdate : 2017-05-11 16:08:41
     */

    var id: Int = 0
    var course_id: Int = 0
    var name: String? = null
    var number: Int = 0
    var sort = 0
    var longtime = ""
    var zhibo_date = ""
    var start_time=""
    var end_time=""
    var url: String? = null
    var thumbnail: String? = null
    var free: Int = 0
    var cdate: String? = null
    var mdate: String? = null
    var check: Boolean = false//点击内容
    var title = ""
    var status: Int = 0
}
