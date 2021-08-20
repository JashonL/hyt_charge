package com.growatt.chargingpile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.application.MyApplication;
import com.growatt.chargingpile.connutil.PostUtil;
import com.growatt.chargingpile.listener.OnViewEnableListener;
import com.growatt.chargingpile.sqlite.SqliteUtil;
import com.growatt.chargingpile.util.Cons;
import com.growatt.chargingpile.util.Constant;
import com.growatt.chargingpile.util.LoginUtil;
import com.growatt.chargingpile.util.SharedPreferencesUnit;
import com.growatt.chargingpile.util.SmartHomeUrlUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.jpush.android.api.JPushInterface;


/**
 * 登录
 * Created by Administrator on 2018/10/16.
 */

public class LoginActivity extends BaseActivity {
    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.bt_login)
    Button btLogin;
    private Unbinder bind;
    private boolean mIsShowPassword=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bind=ButterKnife.bind(this);
        JPushInterface.init(getApplicationContext());
        initUser();
        AutoLogin();
    }

    private void initUser() {
        Map<String, Object> inquirylogin = SqliteUtil.inquirylogin();
        if (inquirylogin.size() > 0) {
            String name = inquirylogin.get("name").toString();
            String pwd = inquirylogin.get("pwd").toString();
//            if (!Cons.isflagId.equals(name)) {
            etUsername.setText(name);
            etPassword.setText(pwd);
            etUsername.setSelection(name.length());
            etPassword.setSelection(pwd.length());
//            }
        }
    }


    /**
     * 自动登录
     */
    private void AutoLogin() {
        final Map<String, Object> map = SqliteUtil.inquirylogin();
        int autoLoginNum = SharedPreferencesUnit.getInstance(this).getInt(Constant.AUTO_LOGIN);
        if (autoLoginNum == 0 || map.size() == 0) {
            return;
        }
        //oss登录
        LoginUtil.login(mContext, etUsername.getText().toString().trim(), etPassword.getText().toString().trim(), new OnViewEnableListener() {
            @Override
            public void onViewEnable() {

            }
        });

     /*
     int autoLoginType = SharedPreferencesUnit.getInstance(this).getInt(Constant.AUTO_LOGIN_TYPE);
     switch (autoLoginType) {
            case 0://oss登录
                break;
            case 1://server登录
                String url = SqliteUtil.inquiryurl();
                if (TextUtils.isEmpty(url)) {
                    LoginUtil.autoLogin(mContext, map.get("name").toString().trim(), map.get("pwd").toString().trim());
                } else {
                    LoginUtil.serverLogin(mContext, url, map.get("name").toString().trim(), map.get("pwd").toString().trim(), new OnViewEnableListener() {
                    });
                }
                break;
        }*/
    }

    @OnClick({R.id.tv_register, R.id.bt_login, R.id.tv_foget,R.id.iv_switch})
    public void onClickListeners(View view) {
        switch (view.getId()) {
            case R.id.tv_register:
                Intent intent = new Intent();
                intent.setClass(this, RegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("activity", "LoginActivity");
                startActivityForResult(intent, 105);
                break;
            case R.id.bt_login:
                btnLogin();
                break;
            case R.id.tv_foget:
                jumpTo(ForgotPasswordActivity.class, false);
                break;
            case R.id.iv_switch:
                //loginDemo();
                if (!mIsShowPassword){
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else {
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                mIsShowPassword=!mIsShowPassword;
                break;
        }
    }


    /**
     * demo登录
     */

    private void loginDemo() {
        PostUtil.postJson(SmartHomeUrlUtil.postGetDemoUser(), "", new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                try {
                    JSONObject object = new JSONObject(json);
                    int code = object.getInt("code");
                    if (code == 0) {
                        JSONObject demoUserObject = object.getJSONObject("data");
                        String password = demoUserObject.getString("password");
                        String userId = demoUserObject.getString("userId");
                        Cons.isflagId = userId;
                        LoginUtil.demoLogin(mContext, userId, password);
                    } else {
                        toast(R.string.m37服务器错误);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void LoginError(String str) {

            }
        });

    }


    /**
     * 登录
     */
    public void btnLogin() {
        String userName = String.valueOf(etUsername.getText()).trim();
        if (TextUtils.isEmpty(userName)) {
            toast(R.string.m21用户名密码为空);
            return;
        }
        String pwd = String.valueOf(etPassword.getText()).trim();
        if (TextUtils.isEmpty(pwd)) {
            toast(R.string.m21用户名密码为空);
            return;
        }
        btLogin.setEnabled(false);
     /*   //正式登录
        LoginUtil.ossErrAutoLogin(mContext, etUsername.getText().toString().trim(), etPassword.getText().toString().trim(), new OnViewEnableListener() {
            @Override
            public void onViewEnable() {
                if (!btLogin.isEnabled()) {
                    btLogin.setEnabled(true);
                }
            }
        });*/
        LoginUtil.login(mContext, etUsername.getText().toString().trim(), etPassword.getText().toString().trim(), new OnViewEnableListener() {
            @Override
            public void onViewEnable() {
                if (!btLogin.isEnabled()) {
                    btLogin.setEnabled(true);
                }
            }
        });

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            MyApplication.getInstance().exit();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bind!=null)bind.unbind();
    }
}