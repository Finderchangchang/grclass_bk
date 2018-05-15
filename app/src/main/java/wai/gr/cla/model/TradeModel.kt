package wai.gr.cla.model

/**
 * Created by Administrator on 2017/5/25.
 */
class TradeModel {
    var id: Int = 0
    var trade_no: String? = null
    var pay_code: Int = 0
    var pay_sn: String? = null
    var uid: Int = 0
    var course_id: String = ""
    var teacher_course_id: Int = 0
    var name: String? = null
    var expire_time: String? = null
    var pay_success_time: String? = null
    var cancel_time: String? = null
    var price: String? = null
    var months: Int = 0
    var valid_year: Int = 0
    var teacher_id: Int = 0
    var cid: Int = 0
    var status: Int = 0
    var cdate: String? = null
    var mdate: String? = null
    var teacher: Teacher? = null
    var i_can_ask:Boolean=false
    var course: CoursesModel? = null
}