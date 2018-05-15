package wai.gr.cla.model

import java.io.Serializable

/**
 * Created by Administrator on 2017/5/17.
 */

class CodeModel : Serializable {
    var region_code: String? = null
    var region_name: String? = null
    var region_level: String? = null
    var parent_region: String? = null
    var jianpin: String? = null
    var quanpin: String? = null
    var first_char: String? = null
}
