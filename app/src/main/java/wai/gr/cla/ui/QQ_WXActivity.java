package wai.gr.cla.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONObject;
import wai.gr.cla.R;
import wai.gr.cla.base.App;
import wai.gr.cla.base.BaseActivity;
import wai.gr.cla.wxapi.WXEntryActivity;

public class QQ_WXActivity extends BaseActivity {

    Button qqLoginBtn;
    Button qqShareBtn;
    Button wxLoginBtn;
    Button wxShareBtn;
    /******QQ*****/
    private static final String APPID = "1105736392";

    private Tencent mTencent; //qq主操作对象
    private IUiListener loginListener; //授权登录监听器
    private IUiListener userInfoListener; //获取用户信息监听器
    private String scope; //获取信息的范围参数
    private UserInfo userInfo; //qq用户信息

    /***************************/
    @Override
    public int setLayout() {
        return R.layout.activity_qq__wx;
    }

    @Override
    public void initViews() {
        qqLoginBtn= (Button) findViewById(R.id.qq_login_btn);
        qqShareBtn= (Button) findViewById(R.id.qq_share_btn);
        wxLoginBtn= (Button) findViewById(R.id.wx_login_btn);
        wxShareBtn= (Button) findViewById(R.id.wx_share_btn);

        initQQ();
        qqShareBtn.setOnClickListener(v -> {
//                qq_share();
            qq(true);
        });
        qqLoginBtn.setOnClickListener(v -> QQlogin());
        wxLoginBtn.setOnClickListener(v -> {
            if (App.Companion.getApi() != null && App.Companion.getApi().isWXAppInstalled()) {
                SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";
                req.state = "wechat_sdk_demo_test_neng";
                App.Companion.getApi().sendReq(req);
            } else
                Toast.makeText(this, "用户未安装微信", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void initEvents() {

    }

    /**
     * 初始化qq数据
     */
    private void initQQ() {
        //初始化qq主操作对象
        mTencent = Tencent.createInstance(APPID, QQ_WXActivity.this);
        //要所有权限，不然会再次申请增量权限，这里不要设置成get_user_info,add_t
        scope = "all";
        loginListener = new IUiListener() {

            @Override
            public void onError(UiError arg0) {

            }

            /**
             * 返回json数据样例
             *
             * {"ret":0,"pay_token":"D3D678728DC580FBCDE15722B72E7365",
             * "pf":"desktop_m_qq-10000144-android-2002-",
             * "query_authority_cost":448,
             * "authority_cost":-136792089,
             * "openid":"015A22DED93BD15E0E6B0DDB3E59DE2D",
             * "expires_in":7776000,
             * "pfkey":"6068ea1c4a716d4141bca0ddb3df1bb9",
             * "msg":"",
             * "access_token":"A2455F491478233529D0106D2CE6EB45",
             * "login_cost":499}
             */
            @Override
            public void onComplete(Object value) {
                if (value == null) {
                    return;
                }

                try {
                    JSONObject jo = (JSONObject) value;
                    int ret = jo.getInt("ret");
                    if (ret == 0) {
                        Toast.makeText(QQ_WXActivity.this, "登录成功",
                                Toast.LENGTH_LONG).show();
                        String openID = jo.getString("openid");
                        String accessToken = jo.getString("access_token");
                        String expires = jo.getString("expires_in");
                        mTencent.setOpenId(openID);
                        mTencent.setAccessToken(accessToken, expires);
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancel() {
                // TODO Auto-generated method stub

            }
        };

        userInfoListener = new IUiListener() {

            @Override
            public void onError(UiError arg0) {
                // TODO Auto-generated method stub

            }

            /**
             * 返回用户信息样例
             *
             * {"is_yellow_year_vip":"0","ret":0,
             * "figureurl_qq_1":"http:\/\/q.qlogo.cn\/qqapp\/1104732758\/015A22DED93BD15E0E6B0DDB3E59DE2D\/40",
             * "figureurl_qq_2":"http:\/\/q.qlogo.cn\/qqapp\/1104732758\/015A22DED93BD15E0E6B0DDB3E59DE2D\/100",
             * "nickname":"攀爬←蜗牛","yellow_vip_level":"0","is_lost":0,"msg":"",
             * "city":"黄冈","
             * figureurl_1":"http:\/\/qzapp.qlogo.cn\/qzapp\/1104732758\/015A22DED93BD15E0E6B0DDB3E59DE2D\/50",
             * "vip":"0","level":"0",
             * "figureurl_2":"http:\/\/qzapp.qlogo.cn\/qzapp\/1104732758\/015A22DED93BD15E0E6B0DDB3E59DE2D\/100",
             * "province":"湖北",
             * "is_yellow_vip":"0","gender":"男",
             * "figureurl":"http:\/\/qzapp.qlogo.cn\/qzapp\/1104732758\/015A22DED93BD15E0E6B0DDB3E59DE2D\/30"}
             */
            @Override
            public void onComplete(Object arg0) {
                // TODO Auto-generated method stub
                if (arg0 == null) {
                    return;
                }
                try {
                    JSONObject jo = (JSONObject) arg0;
                    int ret = jo.getInt("ret");
                    System.out.println("json=" + String.valueOf(jo));
                    String nickName = jo.getString("nickname");
                    String gender = jo.getString("gender");

                    Toast.makeText(QQ_WXActivity.this, "你好，" + nickName,
                            Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    // TODO: handle exception
                }


            }

            @Override
            public void onCancel() {
                // TODO Auto-generated method stub

            }
        };
    }

    private void qq_share() {
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, "要分享的标题");
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, "要分享的摘要");
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "http://www.qq.com/news/1.html");
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif");
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "测试应用222222");
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, 99);
        mTencent.shareToQQ(QQ_WXActivity.this, params, new IUiListener() {
            @Override
            public void onComplete(Object o) {

            }

            @Override
            public void onError(UiError uiError) {

            }

            @Override
            public void onCancel() {

            }
        });
    }

    private void qq(boolean flag) {
        if (mTencent.isSessionValid() && mTencent.getOpenId() == null) {
            toast("您还未安装QQ");
        }

        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_APP);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, "要分享的标题");
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, "要分享的摘要");
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif");
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "测试应用222222");
        if (flag) {
            params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
        }

        mTencent.shareToQQ(QQ_WXActivity.this, params, new IUiListener() {
            @Override
            public void onComplete(Object o) {

            }

            @Override
            public void onError(UiError uiError) {

            }

            @Override
            public void onCancel() {

            }
        });
    }

    private void shareToQzone() {
        final Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, "标题");//必填
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, "摘要");//选填
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, "跳转URL");//必填
        //params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, "图片链接ArrayList");
        mTencent.shareToQzone(QQ_WXActivity.this, params, new IUiListener() {
            @Override
            public void onComplete(Object o) {
                toast(o.toString());
            }

            @Override
            public void onError(UiError uiError) {
                toast(uiError.errorDetail);
            }

            @Override
            public void onCancel() {

            }
        });
    }

    /**
     * 吊起qq登录
     */
    private void QQlogin() {
        //如果session无效，就开始登录
        if (!mTencent.isSessionValid()) {
            //开始qq授权登录
            mTencent.login(QQ_WXActivity.this, scope, loginListener);
        }
    }

    /**
     * 获得当前登录qq用户信息
     */
    private void getQQInfo() {
        userInfo = new UserInfo(QQ_WXActivity.this, mTencent.getQQToken());
        userInfo.getUserInfo(userInfoListener);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_API) {
            if (resultCode == Constants.REQUEST_LOGIN) {
                Tencent.handleResultData(data, loginListener);
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        if (mTencent != null) {
            //注销登录
            mTencent.logout(QQ_WXActivity.this);
        }
        super.onDestroy();
    }
}
