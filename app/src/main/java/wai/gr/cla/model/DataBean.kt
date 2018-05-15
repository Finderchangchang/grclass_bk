package wai.gr.cla.model

/**
 * Created by Finder丶畅畅 on 2017/5/13 00:29
 * QQ群481606175
 */
class DataBean {
    var banner: List<BannerBean>? = null
    var news: List<NewsBean>? = null
    var zhibo_courses: List<LiveModel>? = null
    var courses: List<TuiJianModel>? = null
    var free_courses: List<TuiJianModel>? = null
    var certificate_courses: List<TuiJianModel>? = null

    var teacher_news: List<TeacherNewsBean>? = null
    var exams: List<ExamsBean>? = null
    var code: String = ""
    var codemodel: List<CodeModel>? = null
    var school: List<SchoolModel>? = null;

    class BannerBean {
        /**
         * id : 4
         * src : /uploads/banner/2017051014201114943972111961.png
         * url :
         */

        var id: Int = 0
        var priority: Int = 0
        var src: String? = null
        var url: String? = null
        var url_id: String = ""
    }

    class NewsBean {
        /**
         * id : 7
         * cid : 3
         * title : 南充：46项扶持政策助大学生就业创业
         * cname : 就业
         */

        var id: Int = 0
        var cid: Int = 0
        var title: String? = null
        var cname: String? = null
    }

    class CoursesBean {
        /**
         * id : 11
         * title : 模拟电子技术
         * thumbnail : /uploads/course/2017042017254614926803466238.png
         */

        var id: Int = 0
        var title: String? = null
        var thumbnail: String? = null
        var price: String? = null
    }

    class TeacherNewsBean {
        /**
         * id : 4
         * title : 什么是刚度？什么是柔度？
         */

        var id: Int = 0
        var title: String? = null
        var thumbnail: String? = null
        var show_time: String? = null
    }

    class ExamsBean {
        /**
         * id : 4
         * title : 数字电子技术期末试卷
         */

        var id: Int = 0
        var title: String? = null
    }
}