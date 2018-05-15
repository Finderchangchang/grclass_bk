package wai.gr.cla.model

import java.io.Serializable

/**
 * Created by Finder丶畅畅 on 2017/5/13 00:04
 * QQ群481606175
 */
class ZBResponse<T> : Serializable {

    var code: Int = 0
    var msg: String? = null
    var data: List<T>? = null

    companion object {
        const val serialVersionUID = 5213230387175987834L
    }
    //    public T data;

}
