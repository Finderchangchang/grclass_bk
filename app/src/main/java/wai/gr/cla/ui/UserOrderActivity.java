package wai.gr.cla.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import net.tsz.afinal.view.TitleBar;

import wai.gr.cla.R;
import wai.gr.cla.base.BaseActivity;
import wai.gr.cla.method.OnlyLoadListView;

/**
 * Created by JX on 2017/5/22.
 * 我的订单
 */

public class UserOrderActivity extends BaseActivity {
    OnlyLoadListView lv;
    private TitleBar toolbar;

    @Override
    public int setLayout() {
        return R.layout.activity_user_order;
    }

    @Override
    public void initViews() {
        toolbar = (TitleBar) findViewById(R.id.toolbar);
    }

    @Override
    public void initEvents() {
        lv.setIsValid(new OnlyLoadListView.OnSwipeIsValid() {
            @Override
            public void setSwipeEnabledTrue() {

            }

            @Override
            public void setSwipeEnabledFalse() {

            }
        });
        toolbar.setLeftClick(new TitleBar.LeftClick() {
            @Override
            public void onClick() {
                finish();
            }
        });

    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            //就像onActivityResult一样这个地方就是判断你是从哪来的。
            case 222:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    //openCamra();
                } else {
                    // Permission Denied
                    //Toast.makeText(FoodActivity.this, "很遗憾你把相机权限禁用了。请务必开启相机权限享受我们提供的服务吧。", Toast.LENGTH_SHORT)
                    //        .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
