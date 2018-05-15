package wai.gr.cla.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.View
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_img_detail.*

import wai.gr.cla.R
import wai.gr.cla.base.BaseActivity
import wai.gr.cla.method.SamplePagerAdapter
import wai.gr.cla.model.AnswerModel
import wai.gr.cla.model.url
import java.util.*

class ImgDetailActivity : BaseActivity() {
    internal var images: MutableList<String> = ArrayList()
    override fun setLayout(): Int {
        return R.layout.activity_img_detail
    }
    var model:AnswerModel?=null
    var position=0
    override fun initViews() {
        main=this
        model=intent.getSerializableExtra("url") as AnswerModel
        position=intent.getIntExtra("position",0)
        toolbar.setLeftClick { finish() }
        if (position>0){
            viewpager.adapter = SamplePagerAdapter(model!!.question_images)
            viewpager.currentItem = position-1
            indicator.setViewPager(viewpager)
        }else{
            viewpager.adapter = SamplePagerAdapter(model!!.answer_images)
            viewpager.currentItem = -position-1
            indicator.setViewPager(viewpager)
        }
    }
    companion object {
        var main: ImgDetailActivity? = null
    }
    override fun initEvents() {

       /* viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                if (position == images.size - 1) {
                    go_main_btn.visibility = View.VISIBLE
                } else {
                    go_main_btn.visibility = View.GONE
                }
            }

            override fun onPageSelected(position: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })*/
        //Glide.with(this).load(url().total + intent.getStringExtra("url")).error(R.mipmap.error_img_big).into(iv)
    }

}
