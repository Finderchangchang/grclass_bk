package wai.gr.cla.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.text.format.Formatter
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.lzy.okserver.download.DownloadInfo
import com.lzy.okserver.download.DownloadManager
import com.lzy.okserver.download.DownloadService
import com.lzy.okserver.listener.DownloadListener
import com.lzy.okserver.task.ExecutorWithListener
import kotlinx.android.synthetic.main.activity_down_manage.*

import wai.gr.cla.R
import wai.gr.cla.method.NumberProgressBar
import wai.gr.cla.model.VideoModel
import wai.gr.cla.model.url
import java.util.*
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import wai.gr.cla.method.Utils


/**
 * 下载管理页
 * */
class DownManageActivity : AppCompatActivity(), ExecutorWithListener.OnAllTaskEndListener {
    private var allTask: ArrayList<DownloadInfo>? = null
    override fun onAllTaskEnd() {
        for (downloadInfo in allTask!!) {
            if (downloadInfo.state != DownloadManager.FINISH) {
                return
            }
        }
        Toast.makeText(this, "所有下载任务完成", Toast.LENGTH_SHORT).show()
    }

    private var downloadManager: DownloadManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_down_manage)
        val REQUEST_EXTERNAL_STORAGE = 1
        val PERMISSIONS_STORAGE = arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permission = ActivityCompat.checkSelfPermission(this@DownManageActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this@DownManageActivity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            )
        }
        toolbar.setLeftClick { finish() }
        downloadManager = DownloadService.getDownloadManager()
        downloadManager!!.targetFolder = this.filesDir.absolutePath;
        allTask = downloadManager!!.allTask as ArrayList<DownloadInfo>?
        if (allTask == null || allTask!!.size == 0) {
            error_ll.visibility = View.VISIBLE;
            main_lv.visibility = View.GONE;
        }
        adapter = MyAdapter()
        main_lv.adapter = adapter
        downloadManager!!.threadPool.executor.addOnAllTaskEndListener(this)
        del_btn.setOnClickListener {
            downloadManager!!.removeAllTask()
            adapter!!.notifyDataSetChanged()  //移除的时候需要调用
        }
        stop_btn.setOnClickListener {
            when (stop_btn.text) {
                "全部暂停" -> {
                    downloadManager!!.pauseAllTask()
                    stop_btn.text = "全部开始"
                }
                else -> {//全部开始
                    downloadManager!!.startAllTask()
                    stop_btn.text = "全部暂停"
                }
            }
        }
        main_lv.setOnItemClickListener { parent, view, position, id ->
            if (allTask != null) {
                var path = allTask!![position].targetPath
                var name = allTask!![position].fileName
                if (TextUtils.isEmpty(path)) {
                    path = ""
                }
                startActivity(Intent(this@DownManageActivity, OnlyDetailPlayer::class.java)
                        .putExtra("name", allTask!!.get(position).fileName)
                        .putExtra("url", path))
            } else {
                Toast.makeText(this, "播放地址有问题，请重新下载", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //记得移除，否者会回调多次
        downloadManager!!.threadPool.executor.removeOnAllTaskEndListener(this)
    }

    private inner class MyAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return allTask!!.size
        }

        override fun getItem(position: Int): DownloadInfo {
            return allTask!!.get(position)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            val downloadInfo = getItem(position)
            val holder: ViewHolder
            if (convertView == null) {
                convertView = View.inflate(this@DownManageActivity, R.layout.item_download_manager, null)
                holder = ViewHolder(convertView)
                convertView!!.tag = holder
            } else {
                holder = convertView.tag as ViewHolder
            }
            val apk = downloadInfo.data as VideoModel
            Glide.with(this@DownManageActivity).load(url().total + apk.thumbnail).into(holder.icon)
            holder.name.text = apk.name
            holder.refresh(downloadInfo)
            //对于非进度更新的ui放在这里，对于实时更新的进度ui，放在holder中
            holder.download.setOnClickListener(holder)
            holder.remove.setOnClickListener(holder)
            holder.restart.setOnClickListener(holder)
            val downloadListener = MyDownloadListener()
            downloadListener.userTag = holder
            downloadInfo.listener = downloadListener
            return convertView
        }
    }

    private inner class ViewHolder(convertView: View) : View.OnClickListener {
        var downloadInfo: DownloadInfo? = null
        val icon: ImageView
        var name: TextView
        val downloadSize: TextView
        val tvProgress: TextView
        val netSpeed: TextView
        val pbProgress: NumberProgressBar
        val download: ImageView
        val remove: ImageView
        val restart: Button

        init {
            icon = convertView.findViewById<ImageView>(R.id.icon) as ImageView
            name = convertView.findViewById<TextView>(R.id.name) as TextView
            downloadSize = convertView.findViewById<TextView>(R.id.downloadSize) as TextView
            tvProgress = convertView.findViewById<TextView>(R.id.tvProgress) as TextView
            netSpeed = convertView.findViewById<TextView>(R.id.netSpeed) as TextView
            pbProgress = convertView.findViewById<NumberProgressBar>(R.id.pbProgress) as NumberProgressBar
            download = convertView.findViewById<ImageView>(R.id.start) as ImageView
            remove = convertView.findViewById<ImageView>(R.id.remove) as ImageView
            restart = convertView.findViewById<Button>(R.id.restart) as Button
        }

        fun refresh(downloadInfo: DownloadInfo) {
            this.downloadInfo = downloadInfo
            refresh()
        }

        //对于实时更新的进度ui，放在这里，例如进度的显示，而图片加载等，不要放在这，会不停的重复回调
        //也会导致内存泄漏
        fun refresh() {
            pbProgress.visibility = View.VISIBLE
            val downloadLength = Formatter.formatFileSize(this@DownManageActivity, downloadInfo!!.downloadLength)
            val totalLength = Formatter.formatFileSize(this@DownManageActivity, downloadInfo!!.totalLength)
            downloadSize.text = downloadLength + "/" + totalLength
            if (downloadInfo!!.state == DownloadManager.NONE) {
                netSpeed.text = "停止"
                download.setImageResource(R.mipmap.shipin_start)
                //download.text = "下载"
            } else if (downloadInfo!!.state == DownloadManager.PAUSE) {
                netSpeed.text = "暂停中"
                download.setImageResource(R.mipmap.shipin_start)
                //download.text = "继续"
            } else if (downloadInfo!!.state == DownloadManager.ERROR) {
                netSpeed.text = "下载出错"
                download.setImageResource(R.mipmap.shipin_start)
                // download.text = "出错"
            } else if (downloadInfo!!.state == DownloadManager.WAITING) {
                netSpeed.text = "等待中"
                download.visibility = View.INVISIBLE
                //download.text = "等待"
            } else if (downloadInfo!!.state == DownloadManager.FINISH) {
//                if (ApkUtils.isAvailable(this@DownManageActivity, File(downloadInfo!!.targetPath))) {
//                    download.text = "卸载"
//                } else {
//
//                }
                // download.text = "播放"
                //download.setImageResource(R.mipmap.shipin_bofanglv)
                netSpeed.text = "下载完成"
                download.visibility = View.INVISIBLE
                pbProgress.visibility = View.GONE
            } else if (downloadInfo!!.state == DownloadManager.DOWNLOADING) {
                val networkSpeed = Formatter.formatFileSize(this@DownManageActivity, downloadInfo!!.networkSpeed)
                netSpeed.text = networkSpeed + "/s"
                //download.text = "暂停"
                download.setImageResource(R.mipmap.shipin_zanting)
            }
            //println("totalprogress"+downloadInfo!!.totalLength+"--"+ downloadInfo!!.downloadLength);
            //println("progress:"+downloadInfo!!.progress +":...:"+(Math.round(downloadInfo!!.progress * 10000) * 1.0f / 100).toString());
            tvProgress.text = (Math.round(downloadInfo!!.progress * 10000) * 1.0f / 100).toString() + "%"
            //tvProgress.visibility=View.GONE
            var total = downloadInfo!!.totalLength.toString().replace(" MB", "")
            var now_length = downloadInfo!!.downloadLength.toString().replace(" MB", "")

            pbProgress.max = total.toInt()
            pbProgress.progress = now_length.toInt()
            pbProgress.visibility = View.GONE
        }

        override fun onClick(v: View) {
            if (v.id == download.id) {
                when (downloadInfo!!.state) {
                    DownloadManager.PAUSE, DownloadManager.NONE, DownloadManager.ERROR ->
                        if (Utils.isWifiConnected(this@DownManageActivity)) {
                            downloadManager!!.addTask(downloadInfo!!.url, downloadInfo!!.request, downloadInfo!!.listener)
                        } else {
                            var builder = AlertDialog.Builder(this@DownManageActivity);
                            builder.setTitle("提示");
                            builder.setMessage("当前为移动网络，确定要开始下载吗？");
                            builder.setNegativeButton("取消", null);
                            builder.setPositiveButton("确定") { dialogInterface, i ->
                                downloadManager!!.addTask(downloadInfo!!.url, downloadInfo!!.request, downloadInfo!!.listener)
                            }
                            builder.show()
                        }

                    DownloadManager.DOWNLOADING -> downloadManager!!.pauseTask(downloadInfo!!.url)
                    DownloadManager.FINISH -> {//下载完成后执行的操作
                        val model = downloadInfo!!.data as VideoModel
                        startActivity(Intent(this@DownManageActivity, VideoActivity::class.java)
                                .putExtra("name", model.name)
                                .putExtra("url", downloadInfo!!.targetPath))
                    }
                }
                refresh()
            } else if (v.id == remove.id) {
                downloadManager!!.removeTask(downloadInfo!!.url)
                adapter!!.notifyDataSetChanged()
            } else if (v.id == restart.id) {
                if (Utils.isWifiConnected(this@DownManageActivity)) {
                    downloadManager!!.restartTask(downloadInfo!!.url)
                } else {
                    var builder = AlertDialog.Builder(this@DownManageActivity);
                    builder.setTitle("提示");
                    builder.setMessage("当前为移动网络，确定要开始下载吗？");
                    builder.setNegativeButton("取消", null);
                    builder.setPositiveButton("确定") { dialogInterface, i ->
                        downloadManager!!.restartTask(downloadInfo!!.url)
                    }
                    builder.show()
                }
            }
        }
    }

    private var adapter: MyAdapter? = null

    private inner class MyDownloadListener : DownloadListener() {

        override fun onProgress(downloadInfo: DownloadInfo) {
            if (userTag == null) return
            val holder = userTag as ViewHolder
            holder.refresh()  //这里不能使用传递进来的 DownloadInfo，否者会出现条目错乱的问题
        }

        override fun onFinish(downloadInfo: DownloadInfo) {
            Toast.makeText(this@DownManageActivity, "下载完成:" + downloadInfo.targetPath, Toast.LENGTH_SHORT).show()
        }

        override fun onError(downloadInfo: DownloadInfo, errorMsg: String?, e: Exception) {
            if (errorMsg != null) Toast.makeText(this@DownManageActivity, errorMsg, Toast.LENGTH_SHORT).show()
        }
    }

}

