package wai.gr.cla.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_add_teacher_ask.*
import me.iwf.photopicker.PhotoPicker
import me.iwf.photopicker.PhotoPreview

import wai.gr.cla.R
import wai.gr.cla.base.BaseActivity
import java.util.*
import me.iwf.photopicker.PhotoPickerActivity
import me.iwf.photopicker.PhotoPicker.KEY_SELECTED_PHOTOS
import android.R.attr.data
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.TextUtils
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.kaopiz.kprogresshud.KProgressHUD
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import me.iwf.photopicker.PhotoPicker.REQUEST_CODE
import okhttp3.Call
import okhttp3.Response
import wai.gr.cla.callback.JsonCallback
import wai.gr.cla.method.common
import wai.gr.cla.model.*
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException


/**
 * 对老师进行提问
 * */
class AddTeacherAskActivity : BaseActivity() {
    override fun setLayout(): Int {
        return R.layout.activity_add_teacher_ask
    }

    var course_id: Int = 0
    var img_list = ArrayList<String>()
    var src_list = ArrayList<String>()

    var kp_dialog: KProgressHUD? = null
    override fun initViews() {
        toolbar.setLeftClick { finish() }
        main = this
        kp_dialog = KProgressHUD.create(this)
                .setStyle(com.kaopiz.kprogresshud.KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
        /**
         * 提交数据
         * */
        toolbar.setRightClick {
            course_id = intent.getIntExtra("teacher_id", 0)
            val txt = ask_et.text.toString()
            if (TextUtils.isEmpty(txt) && img_list.size == 0) {
                toast("请输入点内容吧")
            } else {//执行保存操作
                toolbar.setRightCanClick(false)
                var bArray = arrayOfNulls<String>(img_list.size)
                for (s in 0..img_list.size - 1) {
                    bArray[s] = img_list[s]
                }
                OkGo.post(url().auth_api + "save_user_question")
                        .params("teacher_course_id", course_id)
                        .params("question", txt)
                        .params("question_images", Gson().toJson(bArray))
                        .execute(object : JsonCallback<LzyResponse<PageModel<ZiXunModel>>>() {
                            override fun onSuccess(t: LzyResponse<PageModel<ZiXunModel>>, call: okhttp3.Call?, response: okhttp3.Response?) {
                                if (t.code == 0) {
                                    setResult(88)
                                    finish()
                                    toast("提问成功")
                                } else {
                                    toast("提交失败，请重新提交")
                                    toolbar.setRightCanClick(true)
                                }
                            }

                            override fun onError(call: Call?, response: Response?, e: Exception?) {
                                toast(common().toast_error(e!!))
                                toolbar.setRightCanClick(true)
                            }
                        })
//                }
            }
        }
    }

    override fun onBackPressed() {
        if (kp_dialog!!.isShowing) {
            kp_dialog!!.dismiss()
            OkGo.getInstance().cancelAll()
        } else {
            finish()
        }
        super.onBackPressed()
    }

    override fun initEvents() {
        del1_iv.setOnClickListener {
            img_list.removeAt(0)
            src_list.removeAt(0)
            refresh()
        }
        del2_iv.setOnClickListener {
            img_list.removeAt(1)
            src_list.removeAt(1)
            refresh()
        }
        del3_iv.setOnClickListener {
            img_list.removeAt(2)
            src_list.removeAt(2)
            refresh()
        }
        select_img1.setOnClickListener {
            if (img_list.size == 0) {
                PhotoPicker.builder()
                        .setPhotoCount(1)
                        .setPreviewEnabled(false)
                        //.setSelected(img_list)
                        .start(this, PhotoPicker.REQUEST_CODE)
            } else {
                PhotoPreview.builder()
                        .setPhotos(src_list)
                        .setCurrentItem(0)
                        .setShowDeleteButton(false)
                        .start(this)
            }
        }
        select_img2.setOnClickListener {
            if (img_list.size == 1) {
                PhotoPicker.builder()
                        .setPhotoCount(1)
                        .setPreviewEnabled(false)
                        //.setSelected(img_list)
                        .start(this, PhotoPicker.REQUEST_CODE)
            } else if (img_list.size in 2..2) {
                PhotoPreview.builder()
                        .setPhotos(src_list)
                        .setCurrentItem(1)
                        .setShowDeleteButton(false)
                        .start(this)
            }
        }
        select_img3.setOnClickListener {
            if (img_list.size == 2) {
                PhotoPicker.builder()
                        .setPhotoCount(1)
                        .setPreviewEnabled(false)
                        //.setSelected(img_list)
                        .start(this, PhotoPicker.REQUEST_CODE)
            } else if (img_list.size in 3..3) {
                PhotoPreview.builder()
                        .setPhotos(src_list)
                        .setCurrentItem(2)
                        .setShowDeleteButton(false)
                        .start(this)
            }
        }
    }

    /**
     * 刷新当前页面图片显示内容
     * */
    fun refresh() {
        del1_iv.visibility = View.VISIBLE
        del2_iv.visibility = View.VISIBLE
        del3_iv.visibility = View.VISIBLE
        for (i in 0..2) {
            when (i) {
                0 -> setErrorOrImg(img_list, select_img1, i)
                1 -> setErrorOrImg(img_list, select_img2, i)
                2 -> setErrorOrImg(img_list, select_img3, i)
            }
        }
        /**
         * 删除按钮显示隐藏
         * */
        when (img_list.size) {
            0 -> {
                del1_iv.visibility = View.GONE
                del2_iv.visibility = View.GONE
                del3_iv.visibility = View.GONE
                card2_rl.visibility = View.INVISIBLE
                card3_rl.visibility = View.INVISIBLE
            }
            1 -> {
                del2_iv.visibility = View.GONE
                del3_iv.visibility = View.GONE
                card2_rl.visibility = View.VISIBLE
                card3_rl.visibility = View.INVISIBLE
            }
            2 -> {
                del3_iv.visibility = View.GONE
                card2_rl.visibility = View.VISIBLE
                card3_rl.visibility = View.VISIBLE
            }
        }
    }

//    var bitmap: Bitmap? = null
    /**
     * 设置error图片或者显示选择的图片
     * */
    fun setErrorOrImg(url: ArrayList<String>, iv: ImageView, position: Int) {
        if (url.size > position) {
            if (TextUtils.isEmpty(url[position])) {
                iv.setImageDrawable(resources.getDrawable(R.mipmap.sc))
            } else {
//                if (bitmap != null) {
//                    if (!bitmap!!.isRecycled) bitmap!!.recycle()//回收bitmap
//                }
                Glide.with(main!!)
                        .load(url().total + url[position]).into(iv)
//                bitmap = getLoacalBitmap(src_list[position])
//                iv.setImageBitmap(bitmap)
            }
        } else {
            iv.setImageDrawable(resources.getDrawable(R.mipmap.sc))
        }
    }

    companion object {
        var main: AddTeacherAskActivity? = null
    }

    /**
     * 加载本地图片
     * @param url
     * @return
     */
    fun getLoacalBitmap(url: String): Bitmap? {
        try {
            val fis = FileInputStream(url)
            return BitmapFactory.decodeStream(fis)  ///把流转化为Bitmap图片
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode === Activity.RESULT_OK && requestCode === REQUEST_CODE) {
            if (data != null) {
                var list = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS)
                if (list.size > 0) {
                    kp_dialog!!.show()
                    for (i in 0..list.size - 1) {
                        OkGo.post(url().auth_api + "upload_thumbnail")
                                .params("file", File(list[i]))// 请求方式和请求url
                                .execute(object : JsonCallback<LzyResponse<ApkModel>>() {
                                    override fun onSuccess(t: LzyResponse<ApkModel>, call: okhttp3.Call?, response: okhttp3.Response?) {
                                        if (t.code == 0) {
                                            //bArray[i] = t.data!!.src
                                            img_list.add(t.data!!.src)
                                            src_list.add(list[0])
                                            if (i == list.size - 1) {
                                                toast("添加成功")
                                                kp_dialog!!.dismiss()
                                                refresh()
                                            }
                                        }
                                    }
                                })
                    }
                }

            }
        }
    }
}
