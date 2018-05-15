package wai.gr.cla.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Finder丶畅畅 on 2017/10/24 18:04
 * QQ群481606175
 */

public class WholeCrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = "TAG";
    /***************************** 单例模式 ***************************************/
    private static WholeCrashHandler crashHandler = new WholeCrashHandler();

    private WholeCrashHandler() {

    }

    public static WholeCrashHandler getInstance() {
        return crashHandler;
    }

    /***************************************************************************/
    private static final boolean ISOPEN = true; // 是否打开日志写入和发送

    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private Context context;

    public void init(Context context) {
        //LogUtils.i(TAG, "zhuce=====");
        this.context = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
//            Activity currentActivity = ActivityUtils.Instance().getCurrentActivity();
//            LogUtils.i(TAG, "current===>>>>."+currentActivity);
//            ActivityUtils.Instance().finishAll();
//            Intent intent = new Intent();
//            intent.setClass(context, CrashHintActivity1.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            LogUtils.i(TAG, "zhuce==111===");
//            context.startActivity(intent);
//            LogUtils.i(TAG, "zhuce==2222===");
//            android.os.Process.killProcess(android.os.Process.myPid());

        }
    }

    /**
     * 异常处理
     *
     * @param ex
     *            异常信息
     * @return boolean true 已处理，false 未处理
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        if (!ISOPEN) {
            return false;
        }
        //收集设备信息

        // 写入本地文件

        //可以在这直接操作发送log日志
        return true;
    }
}
