package com.growatt.chargingpile.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.connutil.PostUtil;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.util.SmartHomeUrlUtil;
import com.growatt.chargingpile.util.SmartHomeUtil;

import org.json.JSONObject;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ForgotPasswordActivity extends BaseActivity {
    @BindView(R.id.headerView)
    LinearLayout headerView;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.et_username)
    EditText etUserName;
    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.et_phone)
    EditText etPhone;

    private Unbinder bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        bind=ButterKnife.bind(this);
        initHeaderView();
    }

    private void initHeaderView() {
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.title_1));
        tvTitle.setText(getString(R.string.m22忘记密码));
        //设置字体加粗
        tvTitle.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        setHeaderImage(headerView, R.drawable.back, Position.LEFT, v -> finish());
    }

    @OnClick(R.id.btFinish)
    public void onClickListner(View view) {
        switch (view.getId()) {
            case R.id.btFinish:
                repeatPassword();
                break;
        }

    }




    /**
     * 重置密码
     */
    public void repeatPassword() {
        String username = etUserName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        if (TextUtils.isEmpty(username)){
            toast(R.string.m25请输入用户名);
            return;
        }
        if (TextUtils.isEmpty(email)){
            toast(R.string.m35请输入正确邮箱格式);
            return;
        }
        if (TextUtils.isEmpty(phone)){
            toast(R.string.m60填入不带国家代码的手机号);
            return;
        }
        Mydialog.Show(this);
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "invalidPassword");//cmd  注册
            object.put("userId", SmartHomeUtil.getUserName());//用户名
            object.put("phone", phone);//密码
            object.put("email", email);//密码
            object.put("lan", getLanguage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        PostUtil.postJson(SmartHomeUrlUtil.postByCmd(), object.toString(), new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                Mydialog.Dismiss();
                try {
                    JSONObject object = new JSONObject(json);
                    int code = object.getInt("code");
                    if (code == 0) {
                        String a = getResources().getString(R.string.m29发送到邮箱)+email;
                        toast(a);
                    }
                    String errorMsg = object.optString("data");
                    if (!TextUtils.isEmpty(errorMsg))
                        toast(errorMsg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void LoginError(String str) {

            }
        });
    }







/*
    private void repeatPassword() {
        final String s = etUserName.getText().toString();
        if (TextUtils.isEmpty(s)) {
            toast(R.string.m25请输入用户名);
            return;
        }
        Mydialog.Show(this, "");
        PostUtil.post(new Urlsutil().postGetServerUrlByParam, new PostUtil.postListener() {

            @Override
            public void Params(Map<String, String> params) {
                params.put("type", "1");
                params.put("param", s);
            }

            @Override
            public void success(String json) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    if (jsonObject.opt("success").toString().equals("true")) {
                        String url = jsonObject.getString("msg").toString();
                        if (TextUtils.isEmpty(url)) {
                            url = Urlsutil.url_host;
                        }
                        PostUtil.post("http://" + url + "/newForgetAPI.do?op=sendResetEmailByAccount", new PostUtil.postListener() {

                            @Override
                            public void success(String json) {
                                Mydialog.Dismiss();
                                try {
                                    JSONObject jsonObject = new JSONObject(json).getJSONObject("back");
                                    if (jsonObject.opt("success").toString().equals("true")) {
                                        String str = jsonObject.getString("msg").toString();
                                        String a = getResources().getString(R.string.m29发送到邮箱);
                                        a = a.concat(str);
                                        toast(a);
                                    } else {
                                        String str = jsonObject.getString("msg").toString();
                                        if (str.equals("501")) {
                                            toast(R.string.m31发送邮件失败);
                                        }
                                        if (str.equals("502")) {
                                            toast(R.string.m65用户不存在);
                                        }
                                        if (str.equals("503")) {
                                            toast(R.string.m37服务器错误);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void Params(Map<String, String> params) {
                                String s = etUserName.getText().toString();
                                params.put("accountName", s);
                            }

                            @Override
                            public void LoginError(String str) {
                                // TODO Auto-generated method stub

                            }
                        });
                    } else {
                        String str = jsonObject.getString("msg").toString();
                        if (str.equals("501")) {
                            toast(R.string.m31发送邮件失败);
                        }
                        if (str.equals("502")) {
                            toast(R.string.m65用户不存在);
                        }
                        if (str.equals("503")) {
                            toast(R.string.m37服务器错误);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void LoginError(String str) {
                // TODO Auto-generated method stub

            }
        });

    }*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
