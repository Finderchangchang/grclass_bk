package wai.gr.cla.model

/**
 * 通用返回的内容
 * Created by Finder丶畅畅 on 2017/4/22 21:00
 * QQ群481606175
 */

class NormalRequest<T> {
    val code: Int = 0
    val msg: T? = null
    val data: NormalModel? = null
}
