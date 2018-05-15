package wai.gr.cla.method

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

import wai.gr.cla.model.key

/**
 * Created by Administrator on 2017/6/5.
 */

class NetWorkStateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        println("网络状态发生变化")
        //检测API是不是小于23，因为到了API23之后getNetworkInfo(int networkType)方法被弃用
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {

            //获得ConnectivityManager对象
            val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            //获取ConnectivityManager对象对应的NetworkInfo对象
            //获取WIFI连接的信息
            val wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            //获取移动数据连接的信息
            val dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
            if (wifiNetworkInfo.isConnected && dataNetworkInfo.isConnected) {
                Utils.putBooleanCache(key.KEY_IS_WIFI, true)
                //Toast.makeText(context, "WIFI已连接,移动数据已连接", Toast.LENGTH_SHORT).show();
            } else if (wifiNetworkInfo.isConnected && !dataNetworkInfo.isConnected) {
                Utils.putBooleanCache(key.KEY_IS_WIFI, true)
                //Toast.makeText(context, "WIFI已连接,移动数据已断开", Toast.LENGTH_SHORT).show();
            } else if (!wifiNetworkInfo.isConnected && dataNetworkInfo.isConnected) {
                Utils.putBooleanCache(key.KEY_IS_WIFI, false)
                //Toast.makeText(context, "WIFI已断开,移动数据已连接", Toast.LENGTH_SHORT).show();
            } else {
                Utils.putBooleanCache(key.KEY_IS_WIFI, false)
                //Toast.makeText(context, "WIFI已断开,移动数据已断开", Toast.LENGTH_SHORT).show();
            }
            //API大于23时使用下面的方式进行网络监听
        } else {

            println("API level 大于23")
            //获得ConnectivityManager对象
            val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            //获取所有网络连接的信息
            val networks = connMgr.allNetworks
            //用于存放网络连接信息
            val sb = StringBuilder()
            //通过循环将网络信息逐个取出来
            for (i in networks.indices) {
                //获取ConnectivityManager对象对应的NetworkInfo对象
                val networkInfo = connMgr.getNetworkInfo(networks[i])
                if (networkInfo != null) {
                    sb.append(networkInfo.typeName + " connect is " + networkInfo.isConnected)
                }
            }
            //Toast.makeText(context, sb.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}

