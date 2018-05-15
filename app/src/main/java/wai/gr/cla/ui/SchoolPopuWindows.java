package wai.gr.cla.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import wai.gr.cla.R;
import wai.gr.cla.base.App;
import wai.gr.cla.method.CommonAdapter;
import wai.gr.cla.method.CommonViewHolder;
import wai.gr.cla.model.SchoolModel;

/**
 * Created by Administrator on 2017/5/17.
 */

public class SchoolPopuWindows extends PopupWindow {

    private View myView;
    private Activity context;
    private float alpha = 1f;
    private View canView;
    private CommonAdapter<SchoolModel> adapter;
    private List<SchoolModel> myList;
    private TextView etName;
    private TextView tvCode;
private Button btnSave;
    private Boolean isGo;
    public SchoolPopuWindows(Activity context, View view, ArrayList<SchoolModel> list) {
        this(context, view, list, null, null,null,false);
    }

    //view可随便传
    public SchoolPopuWindows(Activity context, View view, ArrayList<SchoolModel> list, TextView et, TextView tv,Button btnsave,boolean isGo) {
        super(context);
        this.context = context;
        canView = view;
        this.myList = list;
        this.btnSave=btnsave;
        this.etName = et;
        this.tvCode = tv;
        this.isGo=isGo;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        myView = inflater.inflate(R.layout.codelist_popuwindow, null);
        setContentView(myView);
        this.setWidth(200);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        showWindow();
        EditText ets = new EditText(App.Companion.getContext());
        ets.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    String getPosition() {
        return click_positon;
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
        this.showAtLocation(canView, Gravity.NO_GRAVITY, location[0], location[1] + canView.getHeight());
        //添加按键事件监听
        setButtonListeners();
        //添加pop窗口关闭事件，主要是实现关闭时改变背景的透明度
        this.setOnDismissListener(new SchoolPopuWindows.poponDismissListener());
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

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (alpha > 0.4f) {
//                    try {
//                        //4是根据弹出动画时间和减少的透明度计算
//                        Thread.sleep(4);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    Message msg = mHandler.obtainMessage();
//                    msg.what = 1;
//                    //每次减少0.01，精度越高，变暗的效果越流畅
//                    alpha -= 0.01f;
//                    System.out.println("-----alpha:" + alpha);
//                    msg.obj = alpha;
//                    mHandler.sendMessage(msg);
//                }
//            }
//
//        }).start();
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
        ListView lv = (ListView) myView.findViewById(R.id.codelsit_lv);
        adapter = new CommonAdapter<SchoolModel>(context, myList, R.layout.item_codelist) {
            @Override
            public void convert(CommonViewHolder holder, SchoolModel codeModel, int position) {
                holder.setText(R.id.item_code_tv, codeModel.getName());
                holder.setOnClickListener(R.id.item_code_tv, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (tvCode != null) {
                            click_positon = codeModel.getId();
                            tvCode.setText(codeModel.getId());
                            etName.setText(codeModel.getName());
                            if(isGo) {
                                btnSave.setEnabled(true);
                                btnSave.setBackgroundResource(R.drawable.login_btn_bg);
                            }else{
                                btnSave.setEnabled(false);
                                btnSave.setBackgroundResource(R.drawable.btn_bg_hui);
                            }
                        } else {//item点击触发事件
                            click.click(position);
                        }
                        if (SchoolPopuWindows.this != null && SchoolPopuWindows.this.isShowing()) {
                            SchoolPopuWindows.this.dismiss();
                        }
                    }
                });
            }
        };
        lv.setAdapter(adapter);
    }

    String click_positon = "";
    onClick click;

    public void setClick(onClick click) {
        this.click = click;
    }

    public interface onClick {
        void click(int position);
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
