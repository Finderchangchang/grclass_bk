package wai.gr.cla.method;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.zip.Inflater;

import wai.gr.cla.R;
import wai.gr.cla.model.url;
import wai.gr.cla.ui.ImgDetailActivity;
import wai.gr.cla.ui.LauncherActivity;

public class LauncherPagerAdapter extends PagerAdapter {
    List<Integer> urls;

    public LauncherPagerAdapter(List<Integer> url) {
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
        textView.setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(LauncherActivity.Companion.getMain()).load(urls.get(position)).into(textView);
        view.addView(textView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        return textView;
    }
}