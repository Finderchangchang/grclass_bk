package wai.gr.cla.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import wai.gr.cla.R;

/**
 * 支付详情
 * Created by Administrator on 2017/5/15.
 */

public class ZhiFuDetailPopuWindow extends PopupWindow {
    private View myView;
    private Activity context;
    private float alpha = 1f;
    private View canView;
    private String myUser;
    private String myMoney;

    //view可随便传
    public ZhiFuDetailPopuWindow(Activity context, View view, String user, String money) {
        super(context);
        this.context = context;
        canView = view;
        this.myUser = user;
        this.myMoney = money;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        myView = inflater.inflate(R.layout.zhifu_detail_popuwidow, null);
        setContentView(myView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        showWindow();
    }

    //popuwindow
    void bottomwindow() {
        if (this != null && this.isShowing()) {
            return;
        }
        //点击空白处时，隐藏掉pop窗口
        this.setFocusable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        //添加弹出、弹入的动画
        this.setAnimationStyle(R.style.Popupwindow);
        int[] location = new int[2];
        canView.getLocationOnScreen(location);
        this.showAtLocation(canView, Gravity.LEFT | Gravity.BOTTOM, 0, 0);
        //添加按键事件监听
        setButtonListeners();
        //添加pop窗口关闭事件，主要是实现关闭时改变背景的透明度
        this.setOnDismissListener(new ZhiFuDetailPopuWindow.poponDismissListener());
        backgroundAlpha(1f);
    }

    /**
     * 返回或者点击空白位置的时候将背景透明度改回来
     */
    class poponDismissListener implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            // TODO Auto-generated method stub
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //此处while的条件alpha不能<= 否则会出现黑屏
                    while (alpha < 1f) {
                        try {
                            Thread.sleep(4);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.d("HeadPortrait", "alpha:" + alpha);
                        Message msg = mHandler.obtainMessage();
                        msg.what = 1;
                        System.out.println("alpha:" + alpha);
                        alpha += 0.01f;
                        msg.obj = alpha;
                        mHandler.sendMessage(msg);
                    }
                }
            }).start();
        }
    }

    //在调用弹出的方法后，开启一个子线程
    public void showWindow() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (alpha > 0.4f) {
                    try {
                        //4是根据弹出动画时间和减少的透明度计算
                        Thread.sleep(4);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message msg = mHandler.obtainMessage();
                    msg.what = 1;
                    //每次减少0.01，精度越高，变暗的效果越流畅
                    alpha -= 0.01f;
                    System.out.println("-----alpha:" + alpha);
                    msg.obj = alpha;
                    mHandler.sendMessage(msg);
                }
            }

        }).start();
        bottomwindow();
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {

        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        context.getWindow().setAttributes(lp);
        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    //给界面添加点击事件
    private void setButtonListeners() {
        ImageView ivclose = (ImageView) myView.findViewById(R.id.zifudetail_iv_close);
        TextView tvuser = (TextView) myView.findViewById(R.id.zhifudetail_tv_user);
        TextView tvmoney = (TextView) myView.findViewById(R.id.zhifudetail_tv_money);
        tvuser.setText(myUser);
        tvmoney.setText(myMoney);
        Button btnSave = (Button) myView.findViewById(R.id.zhifudetail_btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //确认支付
            }
        });


        ivclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ZhiFuDetailPopuWindow.this != null && ZhiFuDetailPopuWindow.this.isShowing()) {
                    ZhiFuDetailPopuWindow.this.dismiss();
                }
            }
        });
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    backgroundAlpha((float) msg.obj);
                    break;
            }
        }
    };

}
