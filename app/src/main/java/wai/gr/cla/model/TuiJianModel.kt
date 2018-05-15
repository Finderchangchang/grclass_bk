package wai.gr.cla.model

/**
 * 视频Model
 * Created by Finder丶畅畅 on 2017/5/8 21:52
 * QQ群481606175
 */
class TuiJianModel {
    constructor(thumbnail: String, title: String, price: String, dots: Int) {
        this.thumbnail = thumbnail
        this.title = title
        this.price = price
        this.dots = dots
    }

    var id: Int = 0
    var cid: Int = 0
    var title: String = ""
    var summary: String = ""
    var thumbnail: String = ""
    var lecturer: String = ""
    var forwho: String = ""
    var num: Int = 0
    var comment_num: Int = 0
    var buy_num: Int = 0
    var valid_year: Int = 0
    var price: String = ""
    var status: Int = 0
    var recommend: Int = 0
    var dots: Int = 0
    var room_id=""
    var expire_time = ""
    var buy_max_num: Int = 0
    var cdate: String = ""
    var mdate: String = ""
    var price_show: String = ""
    var last_play_num: Int = 0
    var i_can_play: Boolean = false
    var is_full_cut = 0
    var subject: String = ""
    var favorite_id: Int = 0//收藏以后变为1
    var teacher_id: Int = 0//老师id
    var videos: List<VideoModel>? = null
}