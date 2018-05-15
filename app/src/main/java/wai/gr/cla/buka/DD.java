package wai.gr.cla.buka;

import android.content.Context;
import android.widget.Toast;

import tv.buka.roomSdk.BukaRoomSDK;
import tv.buka.sdk.listener.ReceiptListener;

/**
 * Created by Finder丶畅畅 on 2018/4/29 21:29
 * QQ群481606175
 */

public class DD {
    interface CC {
        void load();
    }

    public void ff(final Context context, String loginId, String name, final CC cc) {
        BukaRoomSDK.connect(context, loginId, name, new ReceiptListener() {
            @Override
            public void onSuccess(Object o) {
                cc.load();
            }

            @Override
            public void onError() {
                Toast.makeText(context, "信令连接失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
