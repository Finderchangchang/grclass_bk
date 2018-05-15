package wai.gr.cla.model

/**
 * 学校信息模型
 * Created by Administrator on 2017/5/17.
 */

class SchoolModel {
    var id: String? = null
    var name: String? = null

    constructor()
    constructor(name: String) {
        this.name = name
    }
}
