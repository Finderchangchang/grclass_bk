package wai.gr.cla.buka;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import tv.buka.roomSdk.BukaRoomSDK;
import tv.buka.roomSdk.entity.BaseResult;
import tv.buka.roomSdk.entity.CourseEntity;
import tv.buka.roomSdk.net.ResponseJudge;
import tv.buka.roomSdk.util.ConstantUtil;
import tv.buka.roomSdk.util.EmptyUtil;
import tv.buka.roomSdk.util.ToastUtils;
import tv.buka.sdk.listener.ReceiptListener;
import wai.gr.cla.R;

public class UriActivity extends AppCompatActivity {

    @BindView(R.id.et_nickName)
    EditText etNickName;
    @BindView(R.id.et_role)
    EditText etRole;
    @BindView(R.id.et_token)
    EditText etToken;
    @BindView(R.id.et_login_id)
    EditText etLoginId;
    @BindView(R.id.et_alias)
    EditText etAlias;
    private String name;
    private String role;
    private String token;
    private String roomAlias;
    private String loginId;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uri);
        ButterKnife.bind(this);
        context = this;
        onViewClicked();
        finish();
    }

    //@OnClick(R.id.button3)
    public void onViewClicked() {
        name = getIntent().getStringExtra("nick_name");
        token = "guanren";
        roomAlias = getIntent().getStringExtra("room_id");
        role = "2";
        loginId = getIntent().getStringExtra("user_id");
        try {
            //TODO 连接信令
            BukaRoomSDK.connect(this, loginId, name, new ReceiptListener() {
                @Override
                public void onSuccess(Object o) {
                    getRoomInfo();
                }

                @Override
                public void onError() {
                    Toast.makeText(context, "信令连接失败", Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            String a="";
        }

    }

    private void getRoomInfo() {
        //TODO 根据房间alias查询房间信息
        BukaRoomSDK.selectRoomInfo(ConstantUtil.ROOM_ID,roomAlias, new Consumer<String>() {
            @Override
            public void accept(@NonNull String body) throws Exception {
                if (ResponseJudge.isIncludeErrcode(body)) {
                    BaseResult result = JSON.parseObject(body, BaseResult.class);
                    if (result.getErrorcode() != 0) {
                        ToastUtils.showToast(context, result.getErrorcode()+"查询房间信息失败"+body);
                    }
                } else {
                    CourseEntity courseEntity = JSON.parseObject(body, CourseEntity.class);
                    loginRoom(courseEntity);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable error) throws Exception {
                ToastUtils.showToast(context, "查询房间信息：" + "异常：" + error.getMessage());
            }
        });
    }

    private void loginRoom(CourseEntity courseEntity) {
        if (!EmptyUtil.isEmpty(courseEntity)) {
            //todo 判断房间类型
            int encryption = courseEntity.getCourse_chapter_encryption();
            if (encryption == ConstantUtil.ROOM_MODEL_NORMAL) {
            } else if (encryption == ConstantUtil.ROOM_MODEL_PWD) {
            } else if (encryption == ConstantUtil.ROOM_MODEL_URI) {
                //TODO 进入回调验证房间
                BukaRoomSDK.jumpUriRoom(context, courseEntity, Integer.valueOf(role), loginId, token);
            } else {
                ToastUtils.showToast(context, "不支持该房间类型");
            }
        }
    }
}