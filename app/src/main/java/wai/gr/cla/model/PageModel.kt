package wai.gr.cla.model

/**
 * Created by Finder丶畅畅 on 2017/5/16 21:36
 * QQ群481606175
 */

class PageModel<T> {

    /**
     * page_size : 1
     * count : 6
     * page :  * <span>«</span>  * <span>1</span> * [2](/?s=index/Api/get_phone_course_data&free=0&page=2) * [3](/?s=index/Api/get_phone_course_data&free=0&page=3) * [4](/?s=index/Api/get_phone_course_data&free=0&page=4) * [5](/?s=index/Api/get_phone_course_data&free=0&page=5) * [6](/?s=index/Api/get_phone_course_data&free=0&page=6)  * [»](/?s=index/Api/get_phone_course_data&free=0&page=2)
     * list : [{"id":11,"cid":9,"title":"模拟电子技术","summary":"","thumbnail":"/uploads/course/2017042017254614926803466238.png","num":1,"comment_num":0,"buy_num":0,"valid_year":0,"price":"100.00","status":1,"recommend":1,"dots":146,"cdate":"2017-04-20 17:26:01","mdate":"2017-05-16 09:43:46"}]
     */

    var page_size: Int = 0
    var count: Int = 0
    var page: String? = null
    var list: List<T>? = null
}
