package com.growatt.chargingpile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.application.MyApplication;
import com.growatt.chargingpile.listener.OnViewEnableListener;
import com.growatt.chargingpile.sqlite.SqliteUtil;
import com.growatt.chargingpile.util.Cons;
import com.growatt.chargingpile.util.Constant;
import com.growatt.chargingpile.util.LoginUtil;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.util.SharedPreferencesUnit;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Administrator on 2018/10/16.
 */

public class LoginActivity extends BaseActivity {
    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.bt_login)
    Button btLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initUser();
        AutoLogin();
    }

    private void initUser() {
        Map<String, Object> inquirylogin = SqliteUtil.inquirylogin();
        if (inquirylogin.size()>0){
            etUsername.setText(inquirylogin.get("name").toString());
            etPassword.setText(inquirylogin.get("pwd").toString());
            etUsername.setSelection(inquirylogin.get("name").toString().length());
            etPassword.setSelection(inquirylogin.get("pwd").toString().length());
        }
    }


    /**
     * 自动登录
     */
    private void AutoLogin() {
        final Map<String, Object> map = SqliteUtil.inquirylogin();
        int autoLoginNum = SharedPreferencesUnit.getInstance(this).getInt(Constant.AUTO_LOGIN);
        if(autoLoginNum == 0 || map.size()==0){
            return;
        }
        SqliteUtil.time((System.currentTimeMillis()+500000)+"");
        Mydialog.Show(LoginActivity.this,"");
        //oss登录
        int autoLoginType = SharedPreferencesUnit.getInstance(this).getInt(Constant.AUTO_LOGIN_TYPE);
        switch (autoLoginType){
            case 0://oss登录
                break;
            case 1://server登录
                String url=SqliteUtil.inquiryurl();
                if(TextUtils.isEmpty(url)){
                    LoginUtil.autoLogin(mContext,map.get("name").toString().trim(), map.get("pwd").toString().trim());
                }else {
                    LoginUtil.serverLogin(mContext,url,map.get("name").toString().trim(), map.get("pwd").toString().trim(), new OnViewEnableListener(){});
                }
                break;
        }
    }

    @OnClick({R.id.tvRight, R.id.bt_login, R.id.tv_foget})
    public void onClickListeners(View view) {
        switch (view.getId()) {
            case R.id.tvRight:
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
        }
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
        //正式登录
        LoginUtil.ossErrAutoLogin(mContext, etUsername.getText().toString().trim(), etPassword.getText().toString().trim(), new OnViewEnableListener() {
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
            Cons.isExit = true;
            MyApplication.getInstance().exit();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}