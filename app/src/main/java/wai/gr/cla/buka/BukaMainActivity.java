package wai.gr.cla.buka;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import tv.buka.roomSdk.BukaRoomSDK;
import wai.gr.cla.R;

public class BukaMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bukamain);

        /**
         * TODO 写给开发者：
         * 1. 由于roomSDK中有较多的第三方控件，需要在gradle中配置
         * 2. 项目最低兼容版本21（即android5.0）
         * 3. 众多的权限都在sdk中配置过
         * 4. 房间内有图片选择，故SDK对安卓7.0相机调用做了适配
         * 5. 安卓6.0权限申请也做了处理
         */

        BukaRoomSDK.initBukaMedia(this); //TODO 媒体初始化
        BukaRoomSDK.getServerIp(this);//TODO 获取默认服务ip

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BukaMainActivity.this, NormalActivity.class));
            }
        });
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BukaMainActivity.this, UriActivity.class));
            }
        });
    }

}
