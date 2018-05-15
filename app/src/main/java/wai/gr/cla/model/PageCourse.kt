package wai.gr.cla.model

/**
 * 我的视频集合
 * Created by Administrator on 2017/5/25.
 */

class PageCourse {

    /**
     * id : 4
     * uid : 1
     * order_id : 0
     * course_id : 2
     * percent : 12
     * all_num : 8
     * view_num : 0
     * last_num : 0
     * expire_time : 0000-00-00 00:00:00
     * cdate : 2017-05-22 14:14:02
     * mdate : 2017-05-22 14:14:02
     * order : []
     */
    var id: Int = 0
    var uid: Int = 0
    var order_id: Int = 0
    var course_id: Int = 0
    var percent: Int = 0
    var all_num: Int = 0
    var view_num: Int = 0
    var last_num: Int = 0
    var expire_time: String? = null
    var cdate: String? = null
    var mdate: String? = null
    var course: CoursesModel? = null
}
