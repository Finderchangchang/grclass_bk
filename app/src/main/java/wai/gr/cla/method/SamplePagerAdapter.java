package wai.gr.cla.method;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Random;

import wai.gr.cla.R;
import wai.gr.cla.model.url;
import wai.gr.cla.ui.ImgDetailActivity;

public class SamplePagerAdapter extends PagerAdapter {
    List<String> urls;

    public SamplePagerAdapter(List<String> url) {
        urls = url;
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup view, int position, Object object) {
        view.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        ImageView textView = new ImageView(view.getContext());
        Glide.with(ImgDetailActivity.Companion.getMain()).load(new url().getTotal() + urls.get(position)).into(textView);
        view.addView(textView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        return textView;
    }
}