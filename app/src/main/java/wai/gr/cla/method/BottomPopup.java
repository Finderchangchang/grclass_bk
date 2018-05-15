package wai.gr.cla.method;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import wai.gr.cla.R;

/**
 * Created by JX on 2017/5/22.
 * 底部选择拍照或相册
 */

public class BottomPopup extends PopupWindow {

    private LayoutInflater inflater;
    private View contentView;

    private Activity mContext;

    private OnSelectClickListener onSelectClickListener;

    public interface OnSelectClickListener {
        void onFirst(View v);
        void onSecond(View v);
    }

    public void setOnSelectClickListener(OnSelectClickListener onSelectClickListener) {
        this.onSelectClickListener = onSelectClickListener;
    }

    public BottomPopup(Activity context) {
        super(context);
        mContext = context;
        inflater = LayoutInflater.from(context);
        contentView = inflater.inflate(R.layout.popup_bottom, null);
        this.setContentView(contentView);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();

        contentView.findViewById(R.id.tv_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSelectClickListener != null) {
                    onSelectClickListener.onFirst(v);
                }
            }
        });
        contentView.findViewById(R.id.tv_photograph).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSelectClickListener != null) {
                    onSelectClickListener.onSecond(v);
                }
            }
        });
        contentView.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);

        // 设置弹出窗体显示时的动画，从底部向上弹出
        this.setAnimationStyle(R.style.bottom_popup_animation);
    }

    public void showPopupWindow(View parent) {
        this.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        WindowManager.LayoutParams lp= mContext.getWindow().getAttributes();
        lp.alpha = 0.5f;
        mContext.getWindow().setAttributes(lp);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        WindowManager.LayoutParams lp= mContext.getWindow().getAttributes();
        lp.alpha = 1f;
        mContext.getWindow().setAttributes(lp);
    }
}
