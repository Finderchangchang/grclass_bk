package wai.gr.cla.ui

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.hardware.Camera
import android.os.Build
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.view.View
import android.widget.Toast

import com.lzy.okgo.OkGo
import kotlinx.android.synthetic.main.activity_user_detail.*
import me.iwf.photopicker.PhotoPicker
import okhttp3.Call
import okhttp3.Response
import wai.gr.cla.R
import wai.gr.cla.base.BaseActivity
import wai.gr.cla.callback.JsonCallback
import wai.gr.cla.method.*
import wai.gr.cla.model.LzyResponse
import wai.gr.cla.model.UserModel
import wai.gr.cla.model.url
import java.io.File

/**
 * Created by JX on 2017/5/22.
 * 用户详情
 */

class UserDetailActivity : BaseActivity() {
    private var path: String? = null;
    private var bm: Bitmap? = null
    private var bottomPopup: BottomPopup? = null
    var name = ""
    override fun setLayout(): Int {
        return R.layout.activity_user_detail
    }

    override fun initViews() {
        bottomPopup = BottomPopup(this)
        name = intent.getStringExtra("name")
    }

    override fun initEvents() {
        toolbar!!.setLeftClick { finish() }
        rlay_head!!.setOnClickListener {
            PhotoPicker.builder()
                    .setShowCamera(false)
                    .setPhotoCount(1)
                    .setShowCamera(true)
                    .setPreviewEnabled(false)
                    .start(this@UserDetailActivity, PhotoPicker.REQUEST_CODE)
            //bottomPopup!!.showPopupWindow(toolbar)
        }
        rlay_update!!.setOnClickListener {
            val builder = UpdateNameDialog.Builder(this)
            builder.setName(name)
            builder.setOnPositiveButtonClickListener {
                content, v ->
                OkGo.post(url().user_api + "modify_user")
                        .params("data", "{\"nick\":\"$content\"}")// 请求方式和请求url
                        .execute(object : JsonCallback<LzyResponse<UserModel>>() {
                            override fun onSuccess(t: LzyResponse<UserModel>, call: okhttp3.Call?, response: okhttp3.Response?) {
                                if (t.code == 0) {
                                    toast("修改成功")
                                    tv_name.text = content
                                } else {
                                    toast(t.msg.toString())
                                }
                            }

                            override fun onError(call: Call?, response: Response?, e: Exception?) {
                                toast(common().toast_error(e!!))
                            }
                        })
            }
            builder.create().show()
        }
        tv_name.text = name
        GlideImgManager.glideLoader(this@UserDetailActivity, intent.getStringExtra("img_url"), R.mipmap.error_yuan, R.mipmap.error_yuan, iv_head, 0)
        bottomPopup!!.setOnSelectClickListener(object : BottomPopup.OnSelectClickListener {
            override fun onFirst(v: View) {
                startCamera(11);
                bottomPopup!!.dismiss()
            }

            override fun onSecond(v: View) {
                PhotoPicker.builder()
                        .setShowCamera(false)
                        .setPhotoCount(1)
                        .setShowCamera(true)
                        .setPreviewEnabled(false)
                        .start(this@UserDetailActivity, PhotoPicker.REQUEST_CODE)
                bottomPopup!!.dismiss()
            }
        })
    }

    //开启拍照
    fun startCamera(type: Int) {
        val pm = packageManager
        val hasACamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)
                || pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD
                || Camera.getNumberOfCameras() > 0
        if (hasACamera) {
            // 利用系统自带的相机应用:拍照
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            //wai.gr.cla.provider
            path = this@UserDetailActivity.getExternalFilesDir(null).path + "/header.jpg"
            val file = File(path.toString())
            val uri = FileProvider.getUriForFile(this@UserDetailActivity, "wai.gr.cla.provider", file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            startActivityForResult(intent, type)
        } else {
            Toast.makeText(this@UserDetailActivity, "请检查相机功能是否正常!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //照相机返回路径和照片，加载图片显示
//        if (requestCode == 11 && resultCode == -1) {
//            bm = BitmapFactory.decodeFile(path.toString())
//            if (bm != null) {
//                //GlideImgManager.glideLoader(this@UserDetailActivity, path.toString(), R.mipmap.error_yuan, R.mipmap.error_yuan, iv_head, 0)
//                OkGo.post(url().user_api + "modify_user_head_img")
//                        .params("file", File(path))// 请求方式和请求url
//                        .execute(object : DialogCallback<LzyResponse<UserModel>>(this) {
//                            override fun onSuccess(t: LzyResponse<UserModel>, call: okhttp3.Call?, response: okhttp3.Response?) {
//                                if (t.code == 0) {
//                                    var model = t.data
//                                    GlideImgManager.glideLoader(this@UserDetailActivity, path.toString(), R.mipmap.error_yuan, R.mipmap.error_yuan, iv_head, 0)
//                                } else {
//                                    toast(t.msg.toString())
//                                }
//                            }
//
//                            override fun onError(call: Call?, response: Response?, e: Exception?) {
//                                toast(common().toast_error(e!!))
//                            }
//                        })
//            } else {
//                GlideImgManager.glideLoader(this@UserDetailActivity, "", R.mipmap.error_yuan, R.mipmap.error_yuan, iv_head, 0)
//            }
//        }
        if (resultCode == Activity.RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                val photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS)
                val path = photos[0]
                var myfile = File(path)
                OkGo.post(url().user_api + "modify_user_head_img")
                        .params("file", File(path))// 请求方式和请求url
                        .execute(object : JsonCallback<LzyResponse<UserModel>>() {
                            override fun onSuccess(t: LzyResponse<UserModel>, call: okhttp3.Call?, response: okhttp3.Response?) {
                                var ms = t
                                if (t.code == 0) {
                                    toast("修改成功")
//                                    GlideImgManager.glideLoader(this@UserDetailActivity, photos[0], R.mipmap.error_yuan, R.mipmap.error_yuan, iv_head, 0)
                                } else {
                                    toast(t.msg.toString())
                                }
                            }

                            override fun onError(call: Call?, response: Response?, e: Exception?) {
                                toast(common().toast_error(e!!))
                            }
                        })
            }
        }
    }
}
