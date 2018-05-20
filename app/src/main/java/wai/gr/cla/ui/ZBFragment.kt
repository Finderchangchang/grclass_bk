package wai.gr.cla.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v4.widget.SwipeRefreshLayout
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.lzy.okgo.OkGo
import com.yinglan.alphatabs.AlphaTabsIndicator
import kotlinx.android.synthetic.main.frag_z_b.*
import net.tsz.afinal.view.ScrollBottomScrollView
import okhttp3.Call
import okhttp3.Response

import wai.gr.cla.R
import wai.gr.cla.base.BaseFragment
import wai.gr.cla.callback.JsonCallback
import wai.gr.cla.method.*
import wai.gr.cla.model.*
import java.util.*

/**
 * 专接本
 * Created by Finder丶畅畅 on 2017/5/7 22:11
 */

class ZBFragment : BaseFragment() {
    var tj_adapter: CommonAdapter<TuiJianModel>? = null
    var jp_adapter: CommonAdapter<TuiJianModel>? = null
    var tj_list = ArrayList<TuiJianModel>()//推荐
    var jp_list = ArrayList<TuiJianModel>()//精品
    var js_adapter: CommonAdapter<DataBean.TeacherNewsBean>? = null//答疑专栏
    var js_list = ArrayList<DataBean.TeacherNewsBean>()

    var ks_adapter: CommonAdapter<TuiJianModel>? = null//考试
    var zx_adapter: CommonAdapter<ZiXunModel>? = null//资讯
    var zx_list = ArrayList<ZiXunModel>()
    var zx_adapters: CommonAdapter<ZiXunModel>? = null//资讯
    var zx_lists = ArrayList<ZiXunModel>()

    var gg_adapters: CommonAdapter<ZB>? = null//公共课
    var gg_lists = ArrayList<ZB>()
    var left_adapters: CommonAdapter<ZB>? = null//公共课
    var left_lists = ArrayList<ZB>()
    var right_adapters: CommonAdapter<ZB>? = null//公共课
    var right_lists = ArrayList<ZB>()
    override fun onFragmentFirstVisible() {
        super.onFragmentFirstVisible()
        load_tag()
    }

    var page_index: Int = 1
    var left_click: Int = 0
    var right_click: Int = 0
    var audition_tv: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (MainActivity.main != null) {
            public_class_adapter = object : CommonAdapter<ClassTag>(MainActivity.main!!, public_list, R.layout.item_tab) {
                override fun convert(holder: CommonViewHolder, model: ClassTag, position: Int) {
                    holder.setText(R.id.name_tv, model.name)
                    if (position == 0 || position == 5) {
                        holder.setTextColor(R.id.name_tv, R.color.zb_title_bg)
                        holder.setBG(R.id.name_tv, R.drawable.tag_bg)
                    } else {
                        if (model.click) {
                            holder.setBG(R.id.name_tv, R.drawable.tag_click_bg)
                        } else {
                            holder.setBG(R.id.name_tv, R.drawable.tag_bg)
                        }
                    }
                }
            }
            gg_adapters = object : CommonAdapter<ZB>(MainActivity.main!!, gg_lists, R.layout.item_gg) {
                override fun convert(holder: CommonViewHolder, model: ZB, position: Int) {
                    var mytxt = model.info!!.name;
                    if (mytxt!!.length == 2) {
                        mytxt = "    $mytxt    "
                    }
                    holder.setText(R.id.gg_tv, mytxt)
                }
            }
            left_adapters = object : CommonAdapter<ZB>(MainActivity.main!!, left_lists, R.layout.item_left) {
                override fun convert(holder: CommonViewHolder, model: ZB, position: Int) {
                    holder.setText(R.id.name_tv, model.info!!.name)
                    if (left_click == position) {
                        holder.setBGColor(R.id.name_tv, resources.getColor(R.color.white))
                    } else {
                        holder.setBGColor(R.id.name_tv, resources.getColor(R.color.zb_click))
                    }
                }
            }
            right_adapters = object : CommonAdapter<ZB>(MainActivity.main!!, right_lists, R.layout.item_right) {
                override fun convert(holder: CommonViewHolder, model: ZB, position: Int) {
                    holder.setText(R.id.gg_tv, model.info!!.name)
                    if (right_click == position) {
                        holder.setVisible(R.id.tag_right_iv, true)
                    } else {
                        holder.setInVisible(R.id.tag_right_iv)
                    }
                }
            }
            tj_adapter = object : CommonAdapter<TuiJianModel>(MainActivity.main!!, tj_list, R.layout.item_sp) {
                override fun convert(holder: CommonViewHolder, model: TuiJianModel, position: Int) {
                    holder.setText(R.id.tag_tv, model.title)
                    holder.setText(R.id.price_tv, "直播")
                    holder.setTopRoundImage(R.id.tag_iv, url().total + model.thumbnail)
                }
            }
            jp_adapter = object : CommonAdapter<TuiJianModel>(MainActivity.main!!, jp_list, R.layout.item_sp) {
                override fun convert(holder: CommonViewHolder, model: TuiJianModel, position: Int) {
                    holder.setText(R.id.tag_tv, model.title)
                    holder.setText(R.id.price_tv, "￥" + model.price)
                    holder.setTopRoundImage(R.id.tag_iv, url().total + model.thumbnail)
                }
            }
            js_adapter = object : CommonAdapter<DataBean.TeacherNewsBean>(MainActivity.main!!, js_list, R.layout.item_zl) {
                override fun convert(holder: CommonViewHolder, model: DataBean.TeacherNewsBean, position: Int) {
                    holder.setText(R.id.title_tv, model.title)
                    holder.setRoundImage(R.id.tag_iv, url().total + model.thumbnail)
                    holder.setText(R.id.time_tv, model.show_time)
                }
            }
            zx_adapter = object : CommonAdapter<ZiXunModel>(MainActivity.main!!, zx_list, R.layout.item_zixun) {
                override fun convert(holder: CommonViewHolder, model: ZiXunModel, position: Int) {
                    holder.setRoundImage(R.id.user_iv, url().total + model.author_img)
                    holder.setText(R.id.title_tv, model.title)
                    holder.setText(R.id.data_tv, model.show_time)
                    holder.setText(R.id.sc_tv, model.dianzan.toString())
                    holder.setText(R.id.pl_tv, model.comments.toString())
                }
            }
            ks_adapter = object : CommonAdapter<TuiJianModel>(MainActivity.main!!, ks_list, R.layout.item_sp) {
                override fun convert(holder: CommonViewHolder, model: TuiJianModel, position: Int) {
                    holder.setText(R.id.tag_tv, model.title)
                    holder.setText(R.id.price_tv, "￥" + model.price)
                    holder.setTopRoundImage(R.id.tag_iv, url().total + model.thumbnail)
                }
            }
        }
    }

    fun initAdapter() {
        zx_adapters = object : CommonAdapter<ZiXunModel>(MainActivity.main!!, zx_lists, R.layout.item_zixun) {
            override fun convert(holder: CommonViewHolder, model: ZiXunModel, position: Int) {
                holder.setRoundImage(R.id.user_iv, model.author_img)
                holder.setText(R.id.title_tv, "                    " + model.title)
                holder.setText(R.id.data_tv, model.show_time)
                holder.setText(R.id.sc_tv, model.dianzan.toString())
                holder.setText(R.id.pl_tv, model.comments.toString())
                holder.setText(R.id.tag_tv, model.cname.toString())
            }
        }
        zls_lv!!.adapter = zx_adapters
    }

    var main_srl: SwipeRefreshLayout? = null
    var only013_srl: SwipeRefreshLayout? = null
    var zls_lv: OnlyLoadListView? = null
    var right_tag_lv: ListView? = null
    //var only013: LinearLayout? = null
    //var zb_sv: ScrollBottomScrollView? = null
    var only013_sr: ScrollBottomScrollView? = null
//    var main_sr: ScrollView? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.frag_z_b, container, false)
        val public_class_gv = view.findViewById<MyGridView>(R.id.public_class_gv) as MyGridView
        val alphaIndicator = view.findViewById<AlphaTabsIndicator>(R.id.alphaIndicator) as AlphaTabsIndicator
        val tab1_tv = view.findViewById<TextView>(R.id.tab1_tv) as TextView
        audition_tv = view.findViewById<TextView>(R.id.audition_tv) as TextView
        val tab2_tv = view.findViewById<TextView>(R.id.tab2_tv) as TextView
        val tab3_tv = view.findViewById<TextView>(R.id.tab3_tv) as TextView
        val tab4_tv = view.findViewById<TextView>(R.id.tab4_tv) as TextView
        val main_vp = view.findViewById<ViewPager>(R.id.main_vp) as ViewPager
        val st_video_gv = view.findViewById<MyGridView>(R.id.st_video_gv) as MyGridView
        val high_video_gv = view.findViewById<MyGridView>(R.id.high_video_gv) as MyGridView
        val ks_gv = view.findViewById<MyGridView>(R.id.ks_gv) as MyGridView
        val zb3_ll = view.findViewById<LinearLayout>(R.id.zb3_ll) as LinearLayout
        val class_manage_ll = view.findViewById<LinearLayout>(R.id.class_manage_ll) as LinearLayout
        val high_quality_tv = view.findViewById<TextView>(R.id.high_quality_tv) as TextView
        val audition_tv = view.findViewById<TextView>(R.id.audition_tv) as TextView
//        val zl_lv = view.findViewById<>(R.id.zl_lv) as OnlyMeasureListView
//        val zl_ll = view.findViewById<>(R.id.zl_ll) as LinearLayout
        only013_sr = view.findViewById<ScrollBottomScrollView>(R.id.only013_sr) as ScrollBottomScrollView
        zls_lv = view.findViewById<OnlyLoadListView>(R.id.zls_lv) as OnlyLoadListView
        main_srl = view.findViewById<SwipeRefreshLayout>(R.id.main_srl) as SwipeRefreshLayout
        right_tag_lv = view.findViewById<OnlyMeasureListView>(R.id.right_tag_lv) as OnlyMeasureListView
        //only013 = view.findViewById<>(R.id.only013) as LinearLayout
        only013_srl = view.findViewById<SwipeRefreshLayout>(R.id.only013_srl) as SwipeRefreshLayout
        //zb_sv = view.findViewById<>(R.id.zb_sv) as ScrollBottomScrollView
//        main_sr = view.findViewById<>(R.id.main_sr) as ScrollView
        val left_tag_lv = view.findViewById<OnlyMeasureListView>(R.id.left_tag_lv) as OnlyMeasureListView
        val tuijian_iv = view.findViewById<ImageView>(R.id.tuijian_iv) as ImageView
        val st_ll = view.findViewById<LinearLayout>(R.id.st_ll) as LinearLayout
        val mAdapter = MainAdapter(MainActivity.main!!.supportFragmentManager)
        val gg_class_gv = view.findViewById<GridView>(R.id.gg_class_gv) as GridView
        val tv1 = view.findViewById<TextView>(R.id.tv1) as TextView
        val tv2 = view.findViewById<TextView>(R.id.tv2) as TextView
        val tv3 = view.findViewById<TextView>(R.id.tv3) as TextView
        //val main_srl = view.findViewById<>(R.id.main_srl) as SwipeRefreshLayout
        main_vp.adapter = mAdapter

        st_video_gv.adapter = tj_adapter
        st_video_gv.setOnItemClickListener { parent, view, position, id ->
            run {
                val intent = Intent(MainActivity.main!!, DetailPlayer::class.java)
                intent.putExtra("cid", tj_list[position].id)
                startActivity(intent)
            }
        }
        high_video_gv.adapter = jp_adapter
        high_video_gv.setOnItemClickListener { parent, view, position, id ->
            run {
                val intent = Intent(MainActivity.main!!, DetailPlayer::class.java)
                intent.putExtra("cid", jp_list[position].id)
                startActivity(intent)
            }
        }
        ks_gv.adapter = ks_adapter
        ks_gv.setOnItemClickListener { parent, view, position, id ->
            run {
                val intent = Intent(MainActivity.main!!, DetailPlayer::class.java)
                intent.putExtra("cid", ks_list[position].id)
                startActivity(intent)
            }
        }
//        main_srl.setOnRefreshListener {
//            main_srl.isRefreshing = false
//        }
        only013_srl!!.setOnRefreshListener {
            sale_index = 1
            load_sp()
            only013_srl!!.isRefreshing = false
        }
        zls_lv!!.setIsValid(object : OnlyLoadListView.OnSwipeIsValid {
            override fun setSwipeEnabledTrue() {
                main_srl!!.isEnabled = true
            }

            override fun setSwipeEnabledFalse() {
                main_srl!!.isEnabled = false
            }
        })
        //点击顶部tab进行切换
        main_vp.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                tab1_tv.setBackgroundColor(resources.getColor(R.color.zb_title_bg))
                tab2_tv.setBackgroundColor(resources.getColor(R.color.zb_title_bg))
                tab3_tv.setBackgroundColor(resources.getColor(R.color.zb_title_bg))
                tab4_tv.setBackgroundColor(resources.getColor(R.color.zb_title_bg))
                only013_sr!!.visibility = View.VISIBLE
                zb3_ll.visibility = View.GONE
                class_manage_ll.visibility = View.VISIBLE
                audition_tv.visibility = View.VISIBLE
                only013_srl!!.visibility = View.VISIBLE
                now_tab = position
                when (position) {
                    0 -> {//课程分类
                        if (public_list.size == 0) {
                            load_tag()
                        } else {
                            refreshTitle()
                        }
                        only013_sr!!.visibility = View.VISIBLE
                        high_quality_tv.text = "精品课"
                        high_quality_tv.visibility = View.VISIBLE
                        high_video_gv.visibility = View.VISIBLE
                        st_video_gv.visibility = View.GONE//隐藏
                        main_srl!!.visibility = View.GONE
                        public_class_gv.visibility = View.VISIBLE
                        st_ll.visibility = View.GONE
                        tuijian_iv.visibility = View.GONE
                        high_video_gv.visibility = View.VISIBLE
                        ks_gv.visibility = View.GONE
                        //zl_ll.visibility = View.GONE
                        tab1_tv.setBackgroundColor(resources.getColor(R.color.zb_click))
                        only013_sr!!.viewTreeObserver.addOnScrollChangedListener {
                            only013_srl!!.isEnabled = only013_sr!!.scrollY === 0
                        }
                        sale_index = 1
                        load_sp()
                    }
                    1 -> {//答疑专栏
                        only013_sr!!.visibility = View.VISIBLE
                        high_quality_tv.visibility = View.GONE
                        high_video_gv.visibility = View.GONE
                        ks_gv.visibility = View.VISIBLE
                        public_class_gv.visibility = View.GONE
                        //zl_lv.adapter = js_adapter
                        st_video_gv.visibility = View.GONE
                        st_ll.visibility = View.GONE
                        only013_srl!!.visibility = View.VISIBLE
                        main_srl!!.visibility = View.VISIBLE
                        refreshTitle()
                        js_list = ArrayList<DataBean.TeacherNewsBean>()
                        load_dy()
                        ks_index = 1
                        load_ks()
                        tab2_tv.setBackgroundColor(resources.getColor(R.color.zb_click))
                        only013_srl!!.setOnRefreshListener {
                            only013_srl!!.isRefreshing = false
                        }
                        only013_sr!!.viewTreeObserver.addOnScrollChangedListener {
                            only013_srl!!.isEnabled = only013_sr!!.scrollY === 0
                        }

                    }
                    2 -> {//资讯
                        only013_sr!!.visibility = View.GONE
                        st_ll.visibility = View.GONE
                        tab3_tv.setBackgroundColor(resources.getColor(R.color.zb_click))
                        public_class_gv.visibility = View.GONE
                        audition_tv.visibility = View.GONE
                        high_quality_tv.visibility = View.GONE
                        st_video_gv.visibility = View.GONE
                        high_video_gv.visibility = View.GONE
                        //zl_ll.visibility = View.GONE
                        main_srl!!.visibility = View.VISIBLE
                        zls_lv!!.adapter = zx_adapters
                        only013_srl!!.visibility = View.GONE
                        main_srl!!.setOnRefreshListener {
                            page_index = 1
                            load_zx()
                        }
                        zls_lv!!.setOnItemClickListener { parent, view, position, id ->
                            run {
                                if (position < zx_lists.size) {
                                    val intent = Intent(MainActivity.main!!, ZiXunDetailActivity::class.java)
                                    intent.putExtra("cid", zx_lists!![position].id.toString())
                                    intent.putExtra("title", "资讯")
                                    startActivity(intent)
                                }
                            }
                        }
                        load_zx()
                        zls_lv!!.setInterface {
                            page_index++
                            load_zx()
                        }
                        //解决滑动和刷新冲突的问题
//                        main_sr!!.viewTreeObserver.addOnScrollChangedListener {
//                            main_srl!!.isEnabled = main_sr!!.scrollY === 0
//                        }
                    }
                    3 -> {//模拟考试
                        if (tags.size == 0) {
                            load_tag()
                        }
                        tab4_tv.setBackgroundColor(resources.getColor(R.color.zb_click))
                        zb3_ll.visibility = View.VISIBLE
                        class_manage_ll.visibility = View.GONE
                        only013_srl!!.visibility = View.GONE
                        left_tag_lv.adapter = left_adapters
                        if (tags.size > 0) {
                            gg_class_gv.numColumns = tags[0].children!!.size
                            gg_class_gv.adapter = gg_adapters
                            right_tag_lv!!.adapter = right_adapters
                            gg_adapters!!.refresh(tags[0].children)
                            left_adapters!!.refresh(tags[1].children)
                            //默认加载第一个tag对应的子课程
                            right_lists = tags[1].children!![0].children as ArrayList<ZB>
                            right_adapters!!.refresh(right_lists)
                            //左侧点击刷新右侧内容
                            left_tag_lv.setOnItemClickListener { parent, view, position, id ->
                                left_click = position
                                left_adapters!!.refresh(tags[1].children)
                                right_lists = tags[1].children!![position].children as ArrayList<ZB>
                                right_adapters!!.refresh(right_lists)

                            }
                        }
                        //顶部点击跳转到对应的试题列表
                        gg_class_gv.setOnItemClickListener { parent, view, position, id ->
                            startActivity(Intent(MainActivity.main!!, TestListActivity::class.java).putExtra("cid", (position + 3)))
                        }
                        tv1.setOnClickListener {
                            startActivity(Intent(MainActivity.main!!, TestListActivity::class.java).putExtra("cid", 3))
                        }
                        tv2.setOnClickListener {
                            startActivity(Intent(MainActivity.main!!, TestListActivity::class.java).putExtra("cid", 4))
                        }
                        tv3.setOnClickListener {
                            startActivity(Intent(MainActivity.main!!, TestListActivity::class.java).putExtra("cid", 5))
                        }
                        //右侧点击跳转到对应的试题
                        right_tag_lv!!.setOnItemClickListener { parent, view, position, id ->
                            right_lists = tags[1].children!![left_click].children as ArrayList<ZB>
                            right_click = position
                            right_adapters!!.refresh(right_lists)
                            startActivity(Intent(MainActivity.main!!, TestListActivity::class.java).putExtra("cid", right_lists[position].info!!.cid)
                                    .putExtra("title", right_lists[position].info!!.name))

                        }
                        only013_sr!!.fullScroll(ScrollView.FOCUS_UP)
                    }
                }

            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
        alphaIndicator.setViewPager(main_vp)
        public_class_gv.adapter = public_class_adapter
        public_class_gv.setOnItemClickListener { parent, view, position, id ->
            if (position != 0 && position != 5 && position != 4) {//tag不等于4（空），或者点击当前tag不操作
                if (now_tab == 1) {
                    now_tag = position
                    when (now_position.length) {
                        1, 2 -> {//当前选中的是单个的tag
                            var list = public_list[now_position.toInt()]
                            list.click = false
                            list = public_list[position]
                            list.click = true
                            public_class_adapter!!.refresh(public_list)
                            now_position = position.toString()
                        }
                        3 -> {
                            var list = public_list[0]
                            list.click = false
                            list = public_list[5]
                            list.click = false
                            list = public_list[position]
                            list.click = true
                            now_position = position.toString()
                            public_class_adapter!!.refresh(public_list)
                        }
                    }
                    click_tag = position
                    if (now_tab == 0) {
                        sale_index = 1
                        load_sp()
                    }
                    if (now_tab == 1) {
                        var user_id = Utils.getCache(key.KEY_USERID)
                        if (TextUtils.isEmpty(user_id)) {
                            MainActivity.main!!.toast("请先登录")
                        } else {
                            startActivity(Intent(MainActivity.main!!, CardDetailActivity::class.java).putExtra("id", public_list[position]))
                        }
                    }
                } else {
                    if (!now_position.equals(position.toString())) {
                        now_tag = position
                        when (now_position.length) {
                            1, 2 -> {//当前选中的是单个的tag
                                var list = public_list[now_position.toInt()]
                                list.click = false
                                list = public_list[position]
                                list.click = true
                                public_class_adapter!!.refresh(public_list)
                                now_position = position.toString()
                            }
                            3 -> {
                                var list = public_list[0]
                                list.click = false
                                list = public_list[5]
                                list.click = false
                                list = public_list[position]
                                list.click = true
                                now_position = position.toString()
                                public_class_adapter!!.refresh(public_list)
                            }
                        }
                        click_tag = position
                        if (now_tab == 0) {
                            sale_index = 1
                            load_sp()
                        }
                        //if (now_tab == 0) {

//                        } else {
//                            js_list = ArrayList<Teacher>()
//                            load_dy()
//                        }
                        // }
                        //}
                        if (now_tab == 1) {
                            var user_id = Utils.getCache(key.KEY_USERID)
                            if (TextUtils.isEmpty(user_id)) {
                                MainActivity.main!!.toast("请先登录")
                            } else {
                                startActivity(Intent(MainActivity.main!!, CardDetailActivity::class.java).putExtra("id", public_list[position]))
                            }
                        }
                    }
                }
            }
        }
        only013_sr!!.setOnScrollBottomListener {
            if (sale_count > sale_index * 20) {
                sale_index++
                load_sp()
            }
        }
        return view
    }

    var now_tab = 0
    var now_position: String = "1"
    var now_tag: Int = 1//当前选中的顶部位置
    /**
     * 重置头部选项卡
     * */
    fun refreshTitle() {
        if (public_list.size == 0) {
            load_tag()
        } else {
            if (now_position.length != 3) {
                var list = public_list[now_position.toInt()]
                list.click = false

                if (now_tab != 1) {
                    list = public_list[1]
                    list.click = true
                    now_position = "1"
                    now_tag = 1
                } else {
                    now_tag = 0
                    now_position = "0"
                }
                //list = public_list[5]
                // list.click = true
                public_class_adapter!!.refresh(public_list)

            }

        }
    }

    fun load_zx() {
        val uu = url().public_api + "get_phone_news_list2"
        OkGo.post(uu)
                .params("page", page_index)// 请求方式和请求url
                //.params("cid", "2")
                .execute(object : JsonCallback<LzyResponse<PageModel<ZiXunModel>>>() {
                    override fun onSuccess(t: LzyResponse<PageModel<ZiXunModel>>, call: okhttp3.Call?, response: okhttp3.Response?) {
                        if (page_index == 1) {
                            zx_lists.clear()
                        }

                        zx_lists.addAll(t.data!!.list as ArrayList<ZiXunModel>)
                        zls_lv!!.getIndex(page_index, 20, t.data!!.count)
                        if (zx_adapters == null) {
                            initAdapter()//初始化adapter
                        }
                        zx_adapters!!.refresh(zx_lists)
                        main_srl!!.isRefreshing = false
                        if (zx_lists.size == 0) {
                            MainActivity.main?.toast("没有更多数据")
                        }
                    }

                    override fun onError(call: Call?, response: Response?, e: Exception?) {
                        main_srl!!.isRefreshing = false
                        Toast.makeText(context, common().toast_error(e!!), Toast.LENGTH_SHORT).show()
                    }
                })
    }

    var tags = ArrayList<ZB>()
    /**
     * 加载出所有的tag
     * */
    fun load_tag() {
        val now_url = url().public_api + "get_cates_tree"
        OkGo.post(now_url)     // 请求方式和请求url
                .execute(object : JsonCallback<ZBResponse<ZB>>() {
                    override fun onSuccess(t: ZBResponse<ZB>, call: okhttp3.Call?, response: okhttp3.Response?) {
                        tags = t.data as ArrayList<ZB>
                        var load_one=false//加载了一次就不在执行
                        for (i in 0..t.data!!.size - 1) {
                            val model = t.data!![i]
                            model.info!!.click = true
                            if (i == 0) {
                                public_list.add(ClassTag("公共课", false))
                            } else {
                                if(!load_one) {
                                    public_list.add(ClassTag("专业课", false))
                                    load_one = true
                                }
                            }

                            for (key in model.children!!) {
                                if (key.info != null) {
                                    val cc = key.info
                                    cc!!.click = false
                                    public_list.add(cc)
                                }
                            }
                            if (i == 0) {
                                public_list.add(ClassTag("", false))
                            }
                        }
                        left_tag_lv!!.adapter = public_class_adapter
                        public_class_adapter!!.refresh(public_list)
                        click_tag = 1
                        load_sp()
                        refreshTitle()
                    }

                    override fun onError(call: Call?, response: Response?, e: Exception?) {
                        super.onError(call, response, e)
                    }
                })
    }

    /**
     * 答疑专栏数据显示
     * position:点击的tag
     * */
    fun load_dy() {
        only013_sr!!.fullScroll(ScrollView.FOCUS_UP)
        val uu = url().public_api + "get_phone_tuijian_teacher_news_list"
        val ok = OkGo.post(uu)
                .params("num", "30")
        ok.execute(object : JsonCallback<ZBResponse<DataBean.TeacherNewsBean>>() {
            override fun onSuccess(t: ZBResponse<DataBean.TeacherNewsBean>, call: okhttp3.Call?, response: okhttp3.Response?) {
                js_list = t.data as ArrayList<DataBean.TeacherNewsBean>
                js_adapter!!.refresh(js_list)
            }

            override fun onError(call: Call?, response: Response?, e: Exception?) {
                super.onError(call, response, e)
            }
        })
    }

    var ks_index = 1
    var ks_list = ArrayList<TuiJianModel>()
    fun load_ks() {
        if (ks_index == 1) {
            ks_list = ArrayList()
            only013_sr!!.fullScroll(ScrollView.FOCUS_UP)
        }
        val uu = url().public_api + "get_phone_recommend_course_list"
        val ok = OkGo.post(uu)     // 请求方式和请求url
                .params("page", sale_index)
                .params("free", "2")
                .execute(object : JsonCallback<LzyResponse<PageModel<TuiJianModel>>>() {
                    override fun onSuccess(t: LzyResponse<PageModel<TuiJianModel>>, call: okhttp3.Call?, response: okhttp3.Response?) {
                        ks_list = ArrayList()
                        ks_list.addAll(t.data!!.list!!)

                        ks_adapter!!.refresh(ks_list)

                        if (ks_list!!.size == 0) {
                            MainActivity.main?.toast("没有更多数据")
                        }
                    }

                    override fun onError(call: Call?, response: Response?, e: Exception?) {
                        super.onError(call, response, e)
                    }
                })
    }

    /**
     * 根据tag获得视频列表
     * */
    fun load_sp() {
        if (sale_index == 1) {
            tj_list = ArrayList()
            jp_list = ArrayList()
            only013_sr!!.fullScroll(ScrollView.FOCUS_UP)
        }
        val uu = url().public_api + "get_phone_course_data"
        val ok = OkGo.post(uu)     // 请求方式和请求url
                .params("page", sale_index)
        if (now_position.length < 3) {
            ok.params("cid", public_list[click_tag].cid)
        }

        ok.execute(object : JsonCallback<LzyResponse<ZB>>() {
            override fun onSuccess(t: LzyResponse<ZB>, call: okhttp3.Call?, response: okhttp3.Response?) {

                tj_list.addAll(t.data!!.free_course!!)
                if (tj_list!!.size > 0) {
                    audition_tv!!.text = "讲座直播"
                } else if (tj_list!!.size == 0) {
                    audition_tv!!.text = "暂无讲座直播"
                }
                //tj_adapter!!.refresh(tj_list)
                jp_list.addAll(t.data!!.sale_course!!.list!!)
                sale_count = t.data!!.sale_course!!.count
                jp_adapter!!.refresh(jp_list)
                if (jp_list!!.size == 0 && tj_list!!.size == 0) {
                    MainActivity.main?.toast("没有更多数据")
                }
            }

            override fun onError(call: Call?, response: Response?, e: Exception?) {
                super.onError(call, response, e)
            }
        })
    }

    var click_tag = 0//当前点击的tag
    var sale_index = 1//当前页码
    var sale_count = 1//总页数

    internal var public_class_adapter: CommonAdapter<ClassTag>? = null
    internal var public_list: MutableList<ClassTag> = ArrayList()
}
