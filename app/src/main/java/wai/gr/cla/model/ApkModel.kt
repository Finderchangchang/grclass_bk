package wai.gr.cla.model

import java.io.Serializable

class ApkModel : Serializable {

    var name: String? = null
    var url: String? = null
    var iconUrl: String? = null
    var src: String = ""
    var selector: String = ""

    constructor() {}

    constructor(name: String, url: String, iconUrl: String) {
        this.name = name
        this.url = url
        this.iconUrl = iconUrl
    }

    companion object {
        private const val serialVersionUID = 2072893447591548402L
    }
}
