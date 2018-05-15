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

public class NormalActivity extends AppCompatActivity {

    @BindView(R.id.et_nickName)
    EditText etNickName;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_userId)
    EditText etUserId;
    @BindView(R.id.et_token)
    EditText etToken;
    @BindView(R.id.et_alias)
    EditText etAlias;
    @BindView(R.id.et_pwd)
    EditText etPwd;

    private String name;
    private String phone;
    private String userId;
    private String token;
    private String roomAlias;
    private String pwd;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal);
        ButterKnife.bind(this);
        context = this;
    }

    @OnClick(R.id.button3)
    public void onViewClicked() {
        name = etNickName.getText().toString().trim();
        phone = etPhone.getText().toString().trim();
        userId = etUserId.getText().toString().trim();
        token = etToken.getText().toString().trim();
        roomAlias = etAlias.getText().toString().trim();
        pwd = etPwd.getText().toString().trim();

        //TODO 连接信令
        BukaRoomSDK.connect(this, "39", name, new ReceiptListener() {
            @Override
            public void onSuccess(Object o) {
                getRoomInfo();
            }

            @Override
            public void onError() {
                Toast.makeText(context, "信令连接失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getRoomInfo() {
        //TODO 根据房间alias查询房间信息
        BukaRoomSDK.selectRoomInfo(ConstantUtil.ROOM_ID,roomAlias, new Consumer<String>() {
            @Override
            public void accept(@NonNull String body) throws Exception {
                if (ResponseJudge.isIncludeErrcode(body)) {
                    BaseResult result = JSON.parseObject(body, BaseResult.class);
                    if (result.getErrorcode() != 0) {
                        ToastUtils.showToast(context, "查询房间信息失败");
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
                //TODO 进入正常房间
                BukaRoomSDK.jumpNormalRoom(context, courseEntity, phone, pwd, token);
            } else if (encryption == ConstantUtil.ROOM_MODEL_PWD) {
                BukaRoomSDK.jumpNormalRoom(context, courseEntity, phone, pwd, token);
            } else if (encryption == ConstantUtil.ROOM_MODEL_URI) {
            } else {
                ToastUtils.showToast(context, "不支持该房间类型");
            }
        }
    }
}