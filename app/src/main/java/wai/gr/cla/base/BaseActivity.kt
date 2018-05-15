package wai.gr.cla.base

import android.app.ProgressDialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast

//import butterknife.ButterKnife
import wai.gr.cla.method.AndroidBug5497Workaround

/**
 * BaseActivity声明相关通用方法
 *
 *
 * Created by LiuWeiJie on 2015/7/22 0022.
 * Email:1031066280@qq.com
 */
abstract class BaseActivity : AppCompatActivity() {
    internal var layoutid: Int = 0
    internal var dialog: ProgressDialog? = null

    abstract fun setLayout(): Int

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        layoutid = setLayout()
        if (layoutid != 0) {
            setContentView(layoutid)
            AndroidBug5497Workaround.assistActivity(this);
        }
        //ButterKnife.bind(this)
        initViews()
        initEvents()
    }

    abstract fun initViews()

    abstract fun initEvents()

    private var toast: Toast? = null

    fun toast(msg: String) {
        if (toast == null) {
            toast = Toast.makeText(App.context, msg, Toast.LENGTH_SHORT)
        } else {
            toast!!.setText(msg)
        }
        toast!!.show()
    }
}
