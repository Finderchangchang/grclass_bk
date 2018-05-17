package wai.gr.cla.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v4.widget.SwipeRefreshLayout
import android.text.TextUtils
import android.widget.*
import com.lzy.okgo.OkGo
import com.youth.banner.Banner
import com.zaaach.toprightmenu.MenuItem
import com.zaaach.toprightmenu.TopRightMenu
import kotlinx.android.synthetic.main.frag_main.*
import net.tsz.afinal.view.TotalScrollView
import okhttp3.Call
import okhttp3.Response
import wai.gr.cla.R
import wai.gr.cla.base.BaseFragment
import wai.gr.cla.callback.JsonCallback
import wai.gr.cla.method.*
import wai.gr.cla.model.*
import java.util.*
import android.view.*


/**
 * Created by Finder丶畅畅 on 2017/5/7 22:11
 * QQ群481606175
 */

class MainFragment : BaseFragment() {
    var zx_adapter: CommonAdapter<DataBean.NewsBean>? = null//资讯
    var zx_list: MutableList<DataBean.NewsBean>? = ArrayList<DataBean.NewsBean>()
    var tj_adapter: CommonAdapter<TuiJianModel>? = null//推荐
    var tj_list: MutableList<TuiJianModel>? = ArrayList()//收费

    var mf_adapter: CommonAdapter<TuiJianModel>? = null//推荐
    var mf_list: MutableList<TuiJianModel>? = ArrayList()//收费

    var zb_adapter: CommonAdapter<LiveModel>? = null//推荐
    var zb_list: MutableList<LiveModel>? = ArrayList()//值班

    var dy_adapter: CommonAdapter<TuiJianModel>? = null//资格证考试
    var dy_list: MutableList<TuiJianModel>? = ArrayList()//资格证
    var ks_adapter: CommonAdapter<DataBean.ExamsBean>? = null//考试
    var ks_list: MutableList<DataBean.ExamsBean>? = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (MainActivity.main != null) {
            zx_adapter = object : CommonAdapter<DataBean.NewsBean>(MainActivity.main, zx_list, R.layout.item_zx) {
                override fun convert(holder: CommonViewHolder, model: DataBean.NewsBean, position: Int) {
                    if (model.cname!!.length == 2) {
                        val name = StringBuffer(model.cname)
                        model.cname = name.insert(1, "    ").toString()
                    }
                    //holder.setBGColor(R.id.tag_tv, resources.getColor(R.color.login_bg))
                    holder.setText(R.id.tag_tv, model.cname)
                    holder.setText(R.id.content_tv, model.title)
                }
            }
            ks_adapter = object : CommonAdapter<DataBean.ExamsBean>(MainActivity.main, ks_list, R.layout.item_zx) {
                override fun convert(holder: CommonViewHolder, model: DataBean.ExamsBean, position: Int) {
                    holder.setText(R.id.content_tv, model.title)
                    // holder.setBGColor(R.id.tag_tv, resources.getColor(R.color.tag_hui))
                }
            }
            tj_adapter = object : CommonAdapter<TuiJianModel>(MainActivity.main, tj_list, R.layout.item_sp) {
                override fun convert(holder: CommonViewHolder, model: TuiJianModel, position: Int) {
                    holder.setText(R.id.tag_tv, model.title)
                    holder.setTopRoundImage(R.id.tag_iv, url().total + model.thumbnail)
                    if (model.price == "0.00" || model.price == "0") {
                        holder.setText(R.id.price_tv, "直播")
                    } else {
                        holder.setText(R.id.price_tv, "￥" + model.price)
                    }
                }
            }
            mf_adapter = object : CommonAdapter<TuiJianModel>(MainActivity.main, mf_list, R.layout.item_sp) {
                override fun convert(holder: CommonViewHolder, model: TuiJianModel, position: Int) {
                    holder.setText(R.id.tag_tv, model.title)
                    holder.setTopRoundImage(R.id.tag_iv, url().total + model.thumbnail)
                    if (model.price.equals("0.00") || model.price.equals("0")) {
                        holder.setText(R.id.price_tv, "直播")
                    } else {
                        holder.setText(R.id.price_tv, "￥" + model.price)
                    }
                }
            }
            zb_adapter = object : CommonAdapter<LiveModel>(MainActivity.main, zb_list, R.layout.item_sp) {
                override fun convert(holder: CommonViewHolder, model: LiveModel, position: Int) {
                    holder.setText(R.id.tag_tv, model.title)
                    holder.setTopRoundImage(R.id.tag_iv, url().total + model.thumbnail)
                    if (model.price.equals("0.00") || model.price.equals("0")) {
                        holder.setText(R.id.price_tv, "直播")
                    } else {
                        holder.setText(R.id.price_tv, "￥" + model.price)
                    }
                }
            }
            //答疑专栏
            dy_adapter = object : CommonAdapter<TuiJianModel>(MainActivity.main, dy_list, R.layout.item_sp) {
                override fun convert(holder: CommonViewHolder, model: TuiJianModel, position: Int) {
                    holder.setText(R.id.tag_tv, model.title)
                    holder.setTopRoundImage(R.id.tag_iv, url().total + model.thumbnail)
                    holder.setText(R.id.price_tv, "￥" + model.price)
                }
            }
        }
    }

    override fun onFragmentFirstVisible() {
        super.onFragmentFirstVisible()
        initData()
    }

    fun initAdapter() {
        zx_adapter = object : CommonAdapter<DataBean.NewsBean>(MainActivity.main, zx_list, R.layout.item_zx) {
            override fun convert(holder: CommonViewHolder, model: DataBean.NewsBean, position: Int) {
                if (model.cname!!.length == 2) {
                    val name = StringBuffer(model.cname)
                    model.cname = name.insert(1, "    ").toString()
                }
                holder.setBGColor(R.id.tag_tv, resources.getColor(R.color.login_bg))
                holder.setText(R.id.tag_tv, model.cname)
                holder.setText(R.id.content_tv, model.title)
            }
        }
        zu_lv.adapter = zx_adapter
    }

    fun initData() {
        val u = url().public_api + "get_phone_index_data"
        main_ban!!.setImageLoader(GlideImageLoader())
        var data: DataBean? = null
        OkGo.post(u)     // 请求方式和请求url
                .execute(object : JsonCallback<LzyResponse<DataBean>>() {
                    override fun onSuccess(t: LzyResponse<DataBean>?, call: okhttp3.Call?, response: okhttp3.Response?) {
                        data = t!!.data
                        images = ArrayList<String>()
                        data!!.banner!!.mapTo(images) { url().total + it.src }
                        main_ban!!.setImages(images)
                        main_ban!!.start()

                        zx_list = data!!.news as MutableList<DataBean.NewsBean>?
                        if (zx_adapter == null) {
                            initAdapter()
                        }
                        zx_adapter!!.refresh(zx_list!!)
                        tj_list = data!!.courses as MutableList<TuiJianModel>?
                        if (tj_list != null && tj_adapter != null) {
                            tj_adapter!!.refresh(tj_list)
                        }
                        mf_list = data!!.free_courses as MutableList<TuiJianModel>?
                        if (mf_list!!.size > 0) {
                            jz_ll!!.visibility = View.VISIBLE
                        } else {
                            jz_ll!!.visibility = View.GONE
                        }
                        mf_adapter!!.refresh(mf_list)

                        zb_list = data!!.zhibo_courses as MutableList<LiveModel>?
                        if (zb_list!!.size > 0) {
                            live_ll!!.visibility = View.VISIBLE
                        } else {
                            live_ll!!.visibility = View.GONE
                        }
                        zb_adapter!!.refresh(zb_list)
                        dy_list = data!!.certificate_courses as MutableList<TuiJianModel>?
                        dy_adapter!!.refresh(dy_list)
                        ks_list = data!!.exams as MutableList<DataBean.ExamsBean>?
                        ks_adapter!!.refresh(ks_list)
                        if (tj_list!!.size > 0) {
                            jp_ll!!.visibility = View.VISIBLE
                        } else {
                            jp_ll!!.visibility = View.GONE
                        }
                        if (dy_list!!.size > 0) {
                            dy_ll!!.visibility = View.VISIBLE
                        } else {
                            dy_ll!!.visibility = View.GONE
                        }
                        main_sl.fullScroll(ScrollView.FOCUS_UP)
                    }

                    override fun onError(call: Call?, response: Response?, e: Exception?) {
                        Toast.makeText(context, common().toast_error(e!!), Toast.LENGTH_SHORT).show()
                    }
                })
        main_ban!!.setOnBannerClickListener { position: Int ->
            val intent = Intent(MainActivity.main, ZiXunDetailActivity::class.java)
            if (data!!.banner!!.size >= position) {
                if (data!!.banner!![position - 1].url_id != null) {
                    intent.putExtra("cid", data!!.banner!![position - 1].url_id)
                    intent.putExtra("title", "资讯")
                    startActivity(intent)
                }
            }
        }
    }

    internal var main_ban: Banner? = null
    internal var main_srl: SwipeRefreshLayout? = null
    internal var images = ArrayList<String>()
    var jz_ll: LinearLayout? = null
    var jp_ll: LinearLayout? = null
    var dy_ll: LinearLayout? = null
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.frag_main, container, false)
        main_ban = view.findViewById<Banner>(R.id.main_title_ban) as Banner
        jz_ll = view.findViewById<LinearLayout>(R.id.jz_ll) as LinearLayout
        jp_ll = view.findViewById<LinearLayout>(R.id.jp_ll) as LinearLayout
        dy_ll = view.findViewById<LinearLayout>(R.id.dy_ll) as LinearLayout
        main_ban!!.setDelayTime(5000)
        main_ban!!.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
        main_srl = view.findViewById<SwipeRefreshLayout>(R.id.main_srl) as SwipeRefreshLayout
        var title_rl = view.findViewById<RelativeLayout>(R.id.title_rl) as RelativeLayout
        var right_iv = view.findViewById<ImageView>(R.id.right_iv) as ImageView
        var main_tv = view.findViewById<TextView>(R.id.main_tv) as TextView
        var search_rl = view.findViewById<RelativeLayout>(R.id.search_rl) as RelativeLayout
        var zu_lv = view.findViewById<OnlyMeasureListView>(R.id.zu_lv) as OnlyMeasureListView
        var text_lv = view.findViewById<OnlyMeasureListView>(R.id.text_lv) as OnlyMeasureListView
        var class_gv = view.findViewById<OnlyMeasureListView>(R.id.class_gv) as OnlyMeasureGridView
        var free_gv = view.findViewById<OnlyMeasureListView>(R.id.free_gv) as OnlyMeasureGridView
        var live_ll = view.findViewById<LinearLayout>(R.id.live_ll) as LinearLayout
        var live_more_tv = view.findViewById<TextView>(R.id.live_more_tv) as TextView
        var live_gv = view.findViewById<OnlyMeasureListView>(R.id.live_gv) as OnlyMeasureGridView

        var question_lv = view.findViewById<OnlyMeasureListView>(R.id.question_lv) as OnlyMeasureGridView
        var main_sl = view.findViewById<TotalScrollView>(R.id.main_sl) as TotalScrollView
        var tj_more_tv = view.findViewById<TextView>(R.id.tj_more_tv) as TextView
        var free_more_tv = view.findViewById<TextView>(R.id.free_more_tv) as TextView
        var dy_tv = view.findViewById<TextView>(R.id.dy_tv) as TextView
        var ks_more_tv = view.findViewById<TextView>(R.id.ks_more_tv) as TextView
        zu_lv.adapter = zx_adapter
        text_lv.adapter = ks_adapter
        class_gv.adapter = tj_adapter
        free_gv.adapter = mf_adapter
        question_lv.adapter = dy_adapter
        live_gv.adapter = zb_adapter
        main_sl.setTitleView(main_tv, title_rl)//设置需要显示隐藏的view
        search_rl.setOnClickListener { }

        main_sl.viewTreeObserver.addOnScrollChangedListener {
            main_srl!!.isEnabled = main_sl.scrollY === 0
        }
        main_srl!!.setOnRefreshListener {
            main_srl!!.isRefreshing = false
            initData()
        }
        live_more_tv.setOnClickListener{
            startActivity(Intent(MainActivity.main, LiveListActivity::class.java))
        }
        val mLists = ArrayList<SchoolModel>()
        right_iv.setOnClickListener {
            //            mLists.clear()
//            mLists.add(SchoolModel("视频"))
//            mLists.add(SchoolModel("资讯"))
//            CodePopuWindow(MainActivity.main, right_iv, mLists).setClick { position ->
//                startActivity(Intent(MainActivity.main, SearchWord1Activity::class.java).putExtra("position", position))
//            }
            show()
        }
        text_lv.setOnItemClickListener { parent, view, position, id ->
            var user_id = Utils.getCache(key.KEY_USERID)
            if (TextUtils.isEmpty(user_id)) {
                MainActivity.main!!.toast("请先登录")
            } else {
                startActivity(Intent(MainActivity.main, WebActivity::class.java).putExtra("name", "position")
                        .putExtra("url", url().normal + "analog/analog_phone_detail&id=" + ks_list!![position].id + "&uid=" + user_id)
                        .putExtra("title", "考试详情"))
            }
        }
        tj_more_tv.setOnClickListener {
            //更多视频列表
            startActivity(Intent(MainActivity.main, MyOrderGVListActivity::class.java).putExtra("which_more", 1).putExtra("which", 4).putExtra("free", "0"))//加载更多视频
        }
        //更多资质证
        dy_tv.setOnClickListener {
            startActivity(Intent(MainActivity.main, MyOrderGVListActivity::class.java)
                    .putExtra("which_more", 1)
                    .putExtra("which", 4)
                    .putExtra("free", "2"))
        }
        free_more_tv.setOnClickListener {
            //更多视频列表
            startActivity(Intent(MainActivity.main, MyOrderGVListActivity::class.java).putExtra("which_more", 1).putExtra("which", 4).putExtra("free", "1"))//加载更多视频
        }
        //更多试题
        ks_more_tv.setOnClickListener {
            startActivity(Intent(MainActivity.main, TestListActivity::class.java).putExtra("cid", 0))
        }
        class_gv.setOnItemClickListener { parent, view, position, id ->
            run {
                val intent = Intent(MainActivity.main, DetailPlayer::class.java)
                intent.putExtra("cid", tj_list!![position].id)
                startActivity(intent)
            }
        }
        live_gv.setOnItemClickListener { parent, view, position, id ->
            run {
                val intent = Intent(MainActivity.main, DetailPlayer::class.java)
                intent.putExtra("cid", zb_list!![position].id)
                intent.putExtra("is_live", true)
                startActivity(intent)
            }
        }
        free_gv.setOnItemClickListener { parent, view, position, id ->
            run {
                val intent = Intent(MainActivity.main, DetailPlayer::class.java)
                intent.putExtra("cid", mf_list!![position].id)
                startActivity(intent)
            }
        }
        question_lv.setOnItemClickListener { adapterView, view, i, l ->
            run {
                val intent = Intent(MainActivity.main, DetailPlayer::class.java)
                intent.putExtra("cid", dy_list!![i].id)
                startActivity(intent)
            }
        }
        zu_lv.setOnItemClickListener { adapterView, view, i, l ->
            run {
                val intent = Intent(MainActivity.main, ZiXunDetailActivity::class.java)
                intent.putExtra("cid", zx_list!![i].id.toString())
                intent.putExtra("title", "资讯")
                startActivity(intent)
            }
        }
        return view
    }

    fun show() {
        var mTopRightMenu = TopRightMenu(MainActivity.main);
        //添加菜单项
        var menuItems = ArrayList<MenuItem>();
        menuItems.add(MenuItem("   视频"));
        menuItems.add(MenuItem("   资讯"));
        val wm = context
                .getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val height = wm.defaultDisplay.height
        mTopRightMenu
                .setHeight(height / 7)     //默认高度480
                .setWidth(180)      //默认宽度wrap_content
                .showIcon(false)     //显示菜单图标，默认为true
                .dimBackground(true)        //背景变暗，默认为true
                .needAnimationStyle(true)   //显示动画，默认为true
                .setAnimationStyle(R.style.TRM_ANIM_STYLE)
                .addMenuList(menuItems)
                .setOnMenuItemClickListener { position ->
                    startActivity(Intent(MainActivity.main, SearchWord1Activity::class.java).putExtra("position", position))
                }
                .showAsDropDown(right_iv, -75, -30);    //带偏移量
    }
}
