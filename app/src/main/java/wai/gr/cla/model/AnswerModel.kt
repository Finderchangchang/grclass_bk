package wai.gr.cla.model

import java.io.Serializable

/**
 * Created by Finder丶畅畅 on 2017/5/30 21:14
 * QQ群481606175
 * 提问管理
 */

class AnswerModel :Serializable{

    /**
     * id : 8
     * uid : 1
     * teacher_id : 1
     * teacher_course_id : 4
     * question : Deabian是什么意思呢？
     * answer : Linux的一个发行版本。
     * answer_date : 2017-05-25 14:45:43
     * answer_unread : 0
     * cdate : 2017-05-25 14:45:18
     * mdate : 2017-05-25 14:45:43
     */

    var id: Int = 0
    var uid: Int = 0
    var teacher_id: Int = 0
    var teacher_course_id: Int = 0
    var question: String = ""
    var answer: String = ""
    var answer_date: String = ""
    var answer_unread: Int = 0
    var cdate: String = ""
    var mdate: String = ""
    var question_images: List<String>? = null
    var answer_images: List<String>? = null
}
