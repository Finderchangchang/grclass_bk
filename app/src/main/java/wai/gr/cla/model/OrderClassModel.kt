package wai.gr.cla.model

/**
 * 我订阅的课程
 * Created by Administrator on 2017/5/25.
 */

class OrderClassModel {

    /**
     * id : 2
     * uid : 1
     * order_id : 22
     * teacher_course_id : 4
     * expire_time : 2020-11-10 15:53:51
     * cdate : 2017-05-10 15:53:51
     * mdate : 2017-05-10 15:53:51
     * answer_unread : 2
     */

    var id: Int = 0
    var uid: Int = 0
    var order_id: Int = 0
    var teacher_course_id: Int = 0
    var expire_time: String? = null
    var cdate: String? = null
    var mdate: String? = null
    var answer_unread: Int = 0
    var teacher_course: Teacher? = null
}
