package wai.gr.cla.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AlertDialog
import android.text.TextUtils

import wai.gr.cla.R
import wai.gr.cla.base.BaseActivity
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.FileCallback
import okhttp3.Call
import okhttp3.Response
import wai.gr.cla.callback.JsonCallback
import wai.gr.cla.model.*
import wai.gr.cla.method.*
import java.io.File
import android.net.Uri
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import android.support.v4.view.ViewPager

import com.yinglan.alphatabs.AlphaTabsIndicator
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.util.Util
import com.bumptech.glide.util.Util.isOnMainThread
import wai.gr.cla.DownloadUtils


class MainActivity : BaseActivity() {
    override fun initViews() {
        main = this
        check_update()
    }

    public override fun onDestroy() {
        super.onDestroy()
    }

    var tab_pager: ViewPager? = null
    var alphaIndicator: AlphaTabsIndicator? = null
    override fun initEvents() {
        var mAdapter = MainAdapter(supportFragmentManager)
        Utils.putCache(key.KEY_IS_POST, "0")//控制我要创业按钮
        tab_pager = findViewById<ViewPager>(R.id.tab_pager)
        alphaIndicator = findViewById<AlphaTabsIndicator>(R.id.alphaIndicator)
        tab_pager!!.adapter = mAdapter
        //预加载页面的个数
        tab_pager!!.offscreenPageLimit = 4
        alphaIndicator!!.setViewPager(tab_pager)
        OkGo.post(url().user_api + "get_user")//自动登录操作，检查当前是否登录账号，没登录自动执行登录操作
                .execute(object : JsonCallback<LzyResponse<UserModel>>() {
                    override fun onSuccess(t: LzyResponse<UserModel>, call: okhttp3.Call?, response: okhttp3.Response?) {
                    }

                    override fun onError(call: okhttp3.Call?, response: okhttp3.Response?, e: Exception?) {
                        val tel = Utils.getCache(key.KEY_Tel)
                        val pwd = Utils.getCache(key.KEY_PWd)
                        val login_qq = Utils.getCache(key.KEY_QQ)
                        val login_wx = Utils.getCache(key.KEY_WX)
                        if (!TextUtils.isEmpty(login_qq)) {
                            OkGo.get(url().public_api + "open_login")
                                    .params("access_token", login_qq)// 请求方式和请求url
                                    .params("login_type", "qq")// 请求方式和请求url
                                    .execute(object : JsonCallback<LzyResponse<UserModel>>() {
                                        override fun onSuccess(t: LzyResponse<UserModel>, call: okhttp3.Call?, response: okhttp3.Response?) {
                                        }
                                    })
                        } else if (!TextUtils.isEmpty(login_wx)) {
                            OkGo.get(url().public_api + "open_login")
                                    .params("openid", login_wx)
                                    .params("login_type", "wx")
                                    .execute(object : JsonCallback<LzyResponse<UserModel>>() {
                                        override fun onSuccess(t: LzyResponse<UserModel>, call: okhttp3.Call?, response: okhttp3.Response?) {
                                        }

                                        override fun onError(call: okhttp3.Call?, response: okhttp3.Response?, e: Exception?) {
                                            super.onError(call, response, e)
                                        }
                                    })
                        } else {
                            OkGo.get(url().public_api + "login")
                                    .params("username", tel)// 请求方式和请求url
                                    .params("password", pwd)// 请求方式和请求url
                                    .execute(object : JsonCallback<LzyResponse<UserModel>>() {
                                        override fun onSuccess(t: LzyResponse<UserModel>, call: okhttp3.Call?, response: okhttp3.Response?) {
                                        }
                                    })
                        }
                    }
                })
    }


    override fun setLayout(): Int {
        return R.layout.activity_main
    }

    /**
     * 检测更新
     * */
    fun check_update() {
        // 扫描功能
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED) {
            //申请CAMERA权限
            ActivityCompat.requestPermissions(this, arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE), 3)
        } else {
            check_updates()
        }

    }

    fun check_updates() {
        OkGo.post(url().public_api + "get_update_info")
                .params("app", "android")// 文字ID
                .execute(object : JsonCallback<LzyResponse<String>>() {
                    override fun onSuccess(t: LzyResponse<String>, call: okhttp3.Call?, response: okhttp3.Response?) {
                        val oldVersion = Utils.version.toDouble()//当前app版本
                        val newVersion = t.version.toDouble()//系统最新版本
                        if (newVersion > oldVersion) {//需要更新
                            val builder = AlertDialog.Builder(this@MainActivity)
                            builder.setTitle("提示")
                            builder.setMessage(t.content)
                            builder.setNegativeButton("取消", null)
                            builder.setPositiveButton("确定") { dialog, which ->
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    builder.setMessage("可在顶部状态栏，查看APP更新进度")
                                    builder.setPositiveButton("确定") { dialog, which -> }
                                    builder.show()
                                    DownloadUtils(this@MainActivity).downloadAPK(url().total + t.download, "新版本Apk.apk")
                                } else {
                                    UpdateManager(this@MainActivity).checkUpdateInfo(url().total + t.download)
                                }
//                                //执行下载操作
//                                OkGo.get(url().total + t.download)
//                                        .execute(object : FileCallback() {
//                                            override fun onSuccess(t: File?, call: Call?, response: Response?) {
//                                                val intent = Intent(Intent.ACTION_VIEW)
//                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                                                    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
//                                                    val contentUri = FileProvider.getUriForFile(this@MainActivity, "wai.gr.cla.provider", File(t!!.toString()))
//                                                    intent.setDataAndType(contentUri, "application/vnd.android.package-archive")
//                                                } else {
//                                                    intent.setDataAndType(Uri.fromFile(File(t!!.toString())), "application/vnd.android.package-archive")
//                                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                                                }
//                                                startActivity(intent)
//                                            }
//
//                                            override fun onError(call: Call?, response: Response?, e: Exception?) {
//                                                super.onError(call, response, e)
//                                            }
//
//                                        })
                            }
                            builder.show()
                        } else {

                        }
                    }

                    override fun onError(call: okhttp3.Call?, response: okhttp3.Response?, e: Exception?) {
                        //toast(common().toast_error(e!!))
                    }
                })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (3 == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                check_updates()
            } else {
                // 未授权
            }
        }
    }

    companion object {
        var main: MainActivity? = null
    }
}
