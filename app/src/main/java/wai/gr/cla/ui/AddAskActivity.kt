package wai.gr.cla.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import com.lzy.okgo.OkGo
import com.lzy.okgo.request.PostRequest
import kotlinx.android.synthetic.main.activity_add_ask.*
import okhttp3.Call
import okhttp3.Response

import wai.gr.cla.R
import wai.gr.cla.base.BaseActivity
import wai.gr.cla.callback.JsonCallback
import wai.gr.cla.method.common
import wai.gr.cla.model.LzyResponse
import wai.gr.cla.model.PageModel
import wai.gr.cla.model.ZiXunModel
import wai.gr.cla.model.url
import java.net.URLEncoder
import java.util.*

/**
 * 添加提问内容
 * */
class AddAskActivity : BaseActivity() {
    override fun setLayout(): Int {
        return R.layout.activity_add_ask
    }

    var news_id: Int = 0
    var teacher_id: Int = 0
    var course_id: Int = 0
    var okk: PostRequest? = null
    override fun initViews() {
        news_id = intent.getIntExtra("news_id", 0)
        teacher_id = intent.getIntExtra("teacher_id", 0)
        course_id = intent.getIntExtra("course_id", 0)
        if (news_id > 0 || course_id > 0) {
            toolbar.center_str = "我要留言"
        } else {
            toolbar.center_str = "意见反馈"
        }
        toolbar.setLeftClick { finish() }
        toolbar.setRightClick {
            val txt = ask_et.text.toString()
            if (TextUtils.isEmpty(txt)) {
                toast("请输入些内容吧")
            } else {//执行保存操作
                if (news_id > 0 || course_id > 0) {//添加新闻
                    if (news_id > 0) {//留言
                        okk = OkGo.post(url().auth_api + "news_add_comment").params("news_id", news_id)
                    } else {//对课程进行提问
                        okk = OkGo.post(url().auth_api + "course_add_comment").params("course_id", course_id)
                    }
                    okk!!.params("content", txt)
                            .execute(object : JsonCallback<LzyResponse<PageModel<ZiXunModel>>>() {
                                override fun onSuccess(t: LzyResponse<PageModel<ZiXunModel>>, call: okhttp3.Call?, response: okhttp3.Response?) {
                                    if (t.code == 0) {
                                        setResult(88)
                                        finish()
                                        toast("留言成功")
                                    } else {
                                        toast("提交失败，请重新提交")
                                    }
                                }

                                override fun onError(call: Call?, response: Response?, e: Exception?) {
                                    toast(common().toast_error(e!!))
                                }
                            })
                } else {
                    OkGo.post(url().auth_api + "add_feedback")
                            //.params("question", URLEncoder.encode(txt, "utf-8"))
                            .params("content", txt)
                            .execute(object : JsonCallback<LzyResponse<PageModel<ZiXunModel>>>() {
                                override fun onSuccess(t: LzyResponse<PageModel<ZiXunModel>>, call: okhttp3.Call?, response: okhttp3.Response?) {
                                    if (t.code == 0) {
                                        finish()
                                        toast("提交成功，感谢您的反馈")
                                    } else {
                                        toast("提交失败，请重新提交")
                                    }
                                }

                                override fun onError(call: Call?, response: Response?, e: Exception?) {
                                    toast(common().toast_error(e!!))
                                }
                            })
                }
            }
        }
        //ask_et.addTextChangedListener(object : TextWatcher {
        //    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        //   }
        //   override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        //      ask_num_tv.text = s.length.toString() + "/300"
        // }

        //  override fun afterTextChanged(s: Editable) {

        //  }
        //})//监听输入的文字显示在输入框右下角
    }

    override fun initEvents() {

    }
}
