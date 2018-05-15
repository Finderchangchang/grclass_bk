package wai.gr.cla.model

/**
 * Created by Finder丶畅畅 on 2017/5/17 23:44
 * QQ群481606175
 */

class Teacher {
    /**
     * id : 4
     * teacher_id : 1
     * cid : 4
     * cdate : 2017-05-10 11:14:19
     * teacher : {"id":1,"uid":7,"name":"李振刚","sex":0,"photo":"/uploads/teacher/thumb/2017051008203314943756334187.png","savepathfilename":"[]","summary":"1999年本科毕业于西北工业大学热力发动机专业，2005年6月获西安交通大学工学博士学位，2005年11月至2007年4月在美国ANSYS公司工作，2007年6月返校从事教学科研工作至今。","introduce":"
     *
     *1999年本科毕业于西北工业大学热力发动机专业，2005年6月获西安交通大学工学博士学位，2005年11月至2007年4月在美国ANSYS公司工作，2007年6月返校从事教学科研工作至今。<\/p>
     *
     *主要研究领域为机械动态设计与故障诊断，主持了国家自然科学基金项目\u201c基于第二代小波核函数的结构特征参数辨识及预测方法研究\u201d，参加了国家自然科学基金重点项目\u201c关键设备故障预示与运行安全保障的新理论和新技术\u201d等研究工作。曾为华为、富士康、陕鼓、中船重工、西北电力设计研究院等企事业单位解决产品设计、有限元分析等方面的技术难题。<\/p>
     *
     *<br></br><\/p>","cdate":"2017-05-02 11:38:21","mdate":"2017-05-15 16:58:24"}
     * cname : 英语
     * pname : 公共课
     */

    var id: Int = 0
    var teacher_id: Int = 0
    var cid: Int = 0
    var cdate: String? = null
    var teacher: TeacherBean? = null
    var cname: String? = null
    var pname: String? = null
    var teacher_name: String? = null
    var price_list: List<PriceModel>? = null
    var i_can_ask: Boolean = false

    class TeacherBean {
        /**
         * id : 1
         * uid : 7
         * name : 李振刚
         * sex : 0
         * photo : /uploads/teacher/thumb/2017051008203314943756334187.png
         * savepathfilename : []
         * summary : 1999年本科毕业于西北工业大学热力发动机专业，2005年6月获西安交通大学工学博士学位，2005年11月至2007年4月在美国ANSYS公司工作，2007年6月返校从事教学科研工作至今。
         * introduce :
         *
         *1999年本科毕业于西北工业大学热力发动机专业，2005年6月获西安交通大学工学博士学位，2005年11月至2007年4月在美国ANSYS公司工作，2007年6月返校从事教学科研工作至今。
         *
         *主要研究领域为机械动态设计与故障诊断，主持了国家自然科学基金项目“基于第二代小波核函数的结构特征参数辨识及预测方法研究”，参加了国家自然科学基金重点项目“关键设备故障预示与运行安全保障的新理论和新技术”等研究工作。曾为华为、富士康、陕鼓、中船重工、西北电力设计研究院等企事业单位解决产品设计、有限元分析等方面的技术难题。
         *
         *<br></br>
         * cdate : 2017-05-02 11:38:21
         * mdate : 2017-05-15 16:58:24
         */

        var id: Int = 0
        var uid: Int = 0
        var name: String? = null
        var sex: Int = 0
        var photo: String? = null
        var savepathfilename: String? = null
        var summary: String? = null
        var introduce: String? = null
        var cdate: String? = null
        var mdate: String? = null
    }
}
