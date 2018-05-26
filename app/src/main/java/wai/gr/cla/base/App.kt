package wai.gr.cla.base

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.support.multidex.MultiDexApplication
import com.lzy.okgo.OkGo
import com.lzy.okgo.cookie.store.MemoryCookieStore
import com.lzy.okgo.cookie.store.PersistentCookieStore
import com.tencent.bugly.crashreport.CrashReport
import com.umeng.socialize.PlatformConfig
import com.umeng.socialize.UMShareAPI
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import android.support.multidex.MultiDex
import android.util.Log
import android.widget.Toast
import cn.jpush.android.api.TagAliasCallback
import cn.jpush.android.api.JPushInterface
import tv.buka.roomSdk.BukaRoomSDK


/**
 * Created by Finder丶畅畅 on 2017/1/14 21:25
 * QQ群481606175
 */

class App : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        BukaRoomSDK.initSDK("1", false, applicationContext)
        context = applicationContext
        CrashReport.initCrashReport(context, "dfdc0e4c04", false)
        PlatformConfig.setWeixin(wx_id, wx_secret)//初始化微信分享
        PlatformConfig.setQQZone(qq_id, "c7394704798a158208a74ab60104f0ba")//初始化qq分享
        api = WXAPIFactory.createWXAPI(this, wx_id, true)
        api!!.registerApp(wx_id)
        UMShareAPI.get(this)
        OkGo.init(this)
        OkGo.getInstance().setCookieStore(MemoryCookieStore())
        JPushInterface.setDebugMode(false)
        JPushInterface.init(this)
        (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).largeMemoryClass;
        //不随系统变化字体
        val res = super.getResources()
        val config = Configuration()
        config.setToDefaults()
        res.updateConfiguration(config, res.displayMetrics)
        //WholeCrashHandler.getInstance().init(this);
    }

    companion object {
        var context: Context? = null
        var api: IWXAPI? = null
        var wx_id: String = "wxc281dccd97667c78"//微信的id
        var wx_secret = "6992b3bac3a2594835eb7bc7e3791c78"
        var qq_id = "1106115761"
    }

    private var toast: Toast? = null
    fun toast(msg: String) {
        if (toast == null) {
            toast = Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT)
        } else {
            toast!!.setText(msg)
        }
        toast!!.show()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base);
    }
}
