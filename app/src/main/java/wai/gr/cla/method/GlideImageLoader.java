package wai.gr.cla.method;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.youth.banner.loader.ImageLoader;

/**
 * Created by Finder丶畅畅 on 2017/3/8 20:39
 * QQ群481606175
 */

public class GlideImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        if (Util.isOnMainThread()) {
            if (context != null && imageView != null) {
                Glide.with(context).load(path).into(imageView);
            }
        }
    }
}
