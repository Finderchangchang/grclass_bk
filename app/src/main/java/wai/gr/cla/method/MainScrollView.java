package wai.gr.cla.method;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by Administrator on 2017/5/20.
 */

public class MainScrollView extends ScrollView {

    private ScrollBottomListener scrollBottomListener;

    public MainScrollView(Context context) {
        super(context);
    }

    public MainScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MainScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (t + getHeight() >= computeVerticalScrollRange()) {
            //ScrollView滑动到底部了
            scrollBottomListener.scrollBottom();
        }
    }

    public void setScrollBottomListener(ScrollBottomListener scrollBottomListener) {
        this.scrollBottomListener = scrollBottomListener;
    }

    public interface ScrollBottomListener {
        void scrollBottom();
    }

}