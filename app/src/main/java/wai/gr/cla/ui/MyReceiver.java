package wai.gr.cla.ui;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;

import cn.jpush.android.api.JPushInterface;
import wai.gr.cla.model.Jss;

/**
 * Created by Administrator on 2018/3/6.
 */

public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "MyReceiver";

    private NotificationManager nm;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null == nm) {
            nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        Bundle bundle = intent.getExtras();

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            Log.d(TAG, "JPush用户注册成功");

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "接受到推送下来的自定义消息");

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "接受到推送下来的通知");

            receivingNotification(context, bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "用户点击打开了通知");
            receivingNotification(context, bundle);
            Intent i = new Intent(context, ZiXunDetailActivity.class);
//            if (data !!.banner !![position - 1].url_id != null){
//                intent.putExtra("cid", data !!.banner !![position - 1].url_id)
//                intent.putExtra("title", "资讯")
//                startActivity(intent)
//            }
            i.putExtra("title", title);
            i.putExtra("cid", cid);
            i.putExtra("is_tui", true);
//            i.putExtras(bundle);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
//            openNotification(context, bundle);

        } else {
            Log.d(TAG, "Unhandled intent - " + intent.getAction());
        }
    }

    String title = "";
    String cid = "";

    private void receivingNotification(Context context, Bundle bundle) {
        title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
        String message = bundle.getString(JPushInterface.EXTRA_ALERT);
        Object extras = bundle.get(JPushInterface.EXTRA_EXTRA);
        Jss gson = new Gson().fromJson(extras.toString(), Jss.class);
        cid = gson.getNews_id();
    }

}
