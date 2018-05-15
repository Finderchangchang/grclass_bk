package wai.gr.cla.method;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import wai.gr.cla.R;

/**
 * Created by Administrator on 2017/6/7.
 */

public class ImageListView extends LinearLayout {
    Context mContext;
    int width = 0;//屏幕宽度

    public ImageListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        width = MeasureSpec.getSize(widthMeasureSpec);//计算屏幕高度
//        setMeasuredDimension(width, (width - dip2px(10)) / 3);
//    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);//计算屏幕高度
        init();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public int dip2px(float dipValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_img_list, this);
        ViewGroup.LayoutParams linearParams = view.getLayoutParams(); //取控件textView当前的布局参数
        linearParams.height = width / 3;// 控件的高强制设成20

        linearParams.width = LayoutParams.MATCH_PARENT;// 控件的宽强制设成30
        view.setLayoutParams(linearParams);
    }


    public ImageListView(Context context) {
        this(context, null);
    }

    public ImageListView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }
}
