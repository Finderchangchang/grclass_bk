package wai.gr.cla.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.ActionBarOverlayLayout;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.youth.banner.Banner;

import wai.gr.cla.R;
import wai.gr.cla.method.RichText;

/**
 * Created by Administrator on 2017/5/12.
 */

public class HuTest extends Activity {

    private Button btnCrd;
    private Button btnPopu;
    private Button btn_player;
    private PopupMenu popupMenu;
    private PopupWindow popupWindow;
    private float alpha = 1f;

    void time(){

        CountDownTimer timer = new CountDownTimer(1 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

            }
        };
        timer.start();// 开始计时
        EditText editText=new EditText(this);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    String body1="<p><img src=\"/uploads/news/2017/07/20170722184039825656.jpg\" title=\"SS:网站文件：20170722184039825656.jpg；原始文件：1的副本.JPG\" alt=\"1的副本.JPG\"/>心情好的时候<br/></p>";
    private RichText mRichText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntent().getStringExtra("");
        setContentView(R.layout.activity_hutest);
        btnCrd = (Button) findViewById(R.id.btn_card);
        btnPopu = (Button) findViewById(R.id.btn_popu);
        btn_player = (Button) findViewById(R.id.btn_player);
        mRichText = (RichText) findViewById(R.id.tv_news_detail_body);
        mRichText.setRichText(body1);
//给页面赋值
        //   GlideImgManager.glideLoader(this, foodModel.getImgPath(), R.mipmap.moren, R.mipmap.moren, add_food_img, 0);

        btnCrd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HuTest.this, CardDetailActivity.class));

            }
        });
        btnPopu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击按钮就创建并显示一个popupMenu
                //showPopmenu(btn_player);
                //showWindow();
                //SharedPopuWindowActivity popu = new SharedPopuWindowActivity(HuTest.this, btn_player);
                //popu.showWindow();
            }
        });
        btn_player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HuTest.this, DetailPlayer.class));
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

    //popuwindow
    void bottomwindow(View view) {
        if (popupWindow != null && popupWindow.isShowing()) {
            return;
        }
        LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.shared_popup_window, null);
        popupWindow = new PopupWindow(layout,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);


        //点击空白处时，隐藏掉pop窗口
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        //添加弹出、弹入的动画
        popupWindow.setAnimationStyle(R.style.Popupwindow);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        popupWindow.showAtLocation(view, Gravity.LEFT | Gravity.BOTTOM, location[0]-popupWindow.getWidth(), location[1]);
        //添加按键事件监听
        setButtonListeners(layout);
        //添加pop窗口关闭事件，主要是实现关闭时改变背景的透明度
        popupWindow.setOnDismissListener(new poponDismissListener());
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
        bottomwindow(btn_player);
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    private void setButtonListeners(LinearLayout layout) {
        // Button camera = (Button) layout.findViewById(R.id.camera);
        // Button gallery = (Button) layout.findViewById(R.id.gallery);
        // Button savepicture = (Button) layout.findViewById(R.id.savepicture);
        Button cancel = (Button) layout.findViewById(R.id.cancel);


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });
    }

    //显示popumenu
    private void showPopmenu(View view) {
        popupMenu = new PopupMenu(HuTest.this, view);
        popupMenu.getMenuInflater().inflate(R.menu.sharedmenu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.camera:
                        Toast.makeText(HuTest.this, "Click camera", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.gallery:
                        Toast.makeText(HuTest.this, "Click gallery", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.cancel:
                        Toast.makeText(HuTest.this, "Click cancel", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }
}
