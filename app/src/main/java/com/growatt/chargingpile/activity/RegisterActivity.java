package com.growatt.chargingpile.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.connutil.PostUtil;
import com.growatt.chargingpile.listener.OnViewEnableListener;
import com.growatt.chargingpile.sqlite.SqliteUtil;
import com.growatt.chargingpile.util.Cons;
import com.growatt.chargingpile.util.LoginUtil;
import com.growatt.chargingpile.util.MyUtil;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.util.SmartHomeUrlUtil;

import org.json.JSONObject;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class RegisterActivity extends BaseActivity {

    @BindView(R.id.headerView)
    LinearLayout headerView;
    @BindView(R.id.tvTitle)
    TextView tvTitle;


    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.et_password_confirm)
    EditText etConfirm;
    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.checkBox)
    CheckBox checkBox;
    @BindView(R.id.textView4)
    TextView terms;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_postcode)
    EditText etPostCode;
    @BindView(R.id.et_installer)
    EditText etInstanller;

    private Unbinder bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        bind=ButterKnife.bind(this);
        initHeaderView();
        initViews();
    }
    private void initHeaderView() {
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.title_1));
        tvTitle.setText(getString(R.string.m23注册));
        //设置字体加粗
        tvTitle.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        setHeaderImage(headerView, R.drawable.back, Position.LEFT, v -> finish());
    }


    private void initViews() {
        terms.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        terms.getPaint().setAntiAlias(true);//抗锯齿
    }

    @OnClick({R.id.btRegister, R.id.textView4})
    public void toRegister(View view) {
        switch (view.getId()) {
            case R.id.textView4:
                startActivity(new Intent(this,AgreementActivity.class));
                break;
            case R.id.btRegister:
                try {
//                    registerNext();
                    register();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }

    }

/*    private void registerNext() throws UnsupportedEncodingException {
        if (!checkBox.isChecked()) {
            toast(R.string.m34选择用户协议);
            return;
        }
        if (TextUtils.isEmpty(String.valueOf(etUsername.getText()))) {
            toast(R.string.m21用户名密码为空);
            return;
        }
        if (etUsername.getText().toString().length() < 3) {
            toast(R.string.m99用户名必须大于3位);
            return;
        }
        if (TextUtils.isEmpty(String.valueOf(etPassword.getText()))) {
            toast(R.string.m21用户名密码为空);
            return;
        }
        if (TextUtils.isEmpty(String.valueOf(etConfirm.getText()))) {
            toast(R.string.m21用户名密码为空);
            return;
        }
        if (TextUtils.isEmpty(String.valueOf(etEmail.getText()))) {
            toast(R.string.m35请输入正确邮箱格式);
            return;
        }

        //校验邮箱
        if (!MyUtil.regexCheckEmail(String.valueOf(etEmail.getText()).trim())) {
            toast(R.string.m35请输入正确邮箱格式);
            return;
        }
        if (!etPassword.getText().toString().trim().equals(etConfirm.getText().toString().trim())) {
            toast(R.string.m98请输入相同的密码);
            return;
        }


        Mydialog.Show(this, "");
        final String country = MyUtil.getCountryAndPhoneCodeByCountryCode(this, 1);
        //根据国家获取服务器地址
        GetUtil.get(new Urlsutil().getServerUrl + "&country=" + URLEncoder.encode(country, "UTF-8"), new GetUtil.GetListener() {

            @Override
            public void success(String json) {
                Mydialog.Dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String success = jsonObject.get("success").toString();
                    if (success.equals("true")) {
                        String msg = jsonObject.getString("server");
                        Urlsutil.setUrl_cons(msg);
                        SqliteUtil.url(msg);
                        //注册
                        registerByCountry(country);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void error(String json) {

            }
        });
        Cons.regMap.setRegEmail(etEmail.getText().toString().trim());
        Cons.regMap.setRegPassword(etPassword.getText().toString().trim());
        Cons.regMap.setRegPhoneNumber(etPhone.getText().toString().trim());
        Cons.regMap.setRegUserName(etUsername.getText().toString().trim());
        Cons.regMap.setRegPostCode(etPostCode.getText().toString().trim());
    }


    private void registerByCountry(final String country) {
        Mydialog.Show(this, "");
        PostUtil.post(new Urlsutil().creatAccount, new PostUtil.postListener() {
            @Override
            public void success(String json) {
                Mydialog.Dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(json).getJSONObject("back");
                    String msg = jsonObject.optString("msg");
                    if (jsonObject.opt("success").toString().equals("true")) {
                        if (msg.equals("200")) {
                           LogUtil.d("注册成功"); //调用登录
                            toast(R.string.m42注册成功);
                    *//*        LoginUtil.ossErrAutoLogin(mContext, etUsername.getText().toString().trim(), etPassword.getText().toString().trim(), new OnViewEnableListener() {
                                @Override
                                public void onViewEnable() {

                                }
                            });*//*
                            LoginUtil.login(mContext, etUsername.getText().toString().trim(), etPassword.getText().toString().trim(), new OnViewEnableListener() {
                                @Override
                                public void onViewEnable() {

                                }
                            });
                        }
                    } else {
                        if (msg.equals("501")) {
                            toast(R.string.m36超出版本限制注册用户数量);
                            return;
                        }
                        if (msg.equals("502")) {
                            MyUtil.putAppErrMsg("注册:" + Cons.regMap.getRegUserName() + "-msg:" + msg, RegisterActivity.this);
                            toast(R.string.m37服务器错误);
                            return;
                        }
                        if (msg.equals("503")) {
                            toast(R.string.m40用户名已被使用);
                            return;
                        }
                        if (msg.equals("602")) {
                            MyUtil.putAppErrMsg("注册:" + Cons.regMap.getRegUserName() + "-msg:" + msg, RegisterActivity.this);
                            toast(R.string.m38注册错误);
                            return;
                        }
                        if (msg.equals("506")) {
                            toast(R.string.m39采集器序列号或校验码错误);
                            return;
                        }
                        if (msg.equals("603")) {
                            toast(R.string.m44服务器错误提示);
                            return;
                        }
                        if (msg.equals("604")) {
                            toast(R.string.m41代理商编码错误);
                            return;
                        }
                        if (msg.equals("605")) {
                            toast(R.string.m43采集器序列号已经存在);
                            return;
                        }
                        if (msg.equals("606")) {
                            toast(R.string.m45服务器错误提示2);
                            return;
                        }
                        if (msg.equals("607")) {
                            toast(R.string.m45服务器错误提示2);
                            return;
                        }

                        if (msg.equals("504")) {
                            toast(R.string.m21用户名密码为空);
                            return;
                        }
                        if (msg.equals("505")) {
                            toast(R.string.m35请输入正确邮箱格式);
                            return;
                        }
                        if (msg.equals("509")) {
                            toast(R.string.m38注册错误);
                            return;
                        }
                        if (msg.equals("608")) {
                            toast(R.string.m18注册失败);
                            return;
                        }
                        if (msg.equals("609")) {
                            toast(R.string.m18注册失败);
                            return;
                        }
                        if (msg.equals("701")) {
                            MyUtil.putAppErrMsg("注册:" + Cons.regMap.getRegUserName() + "-msg:" + msg, RegisterActivity.this);
                            toast(R.string.m18注册失败);
                            return;
                        }
                        if (msg.equals("702")) {
                            toast(R.string.m46采集器10位提示);
                            return;
                        }
                        if (msg.equals("507")) {
                            toast(R.string.m41代理商编码错误);
                            return;
                        }

                        MyUtil.putAppErrMsg("注册:" + Cons.regMap.getRegUserName() + "-msg:" + msg, RegisterActivity.this);
                        toast(msg + ":" + getString(R.string.m44服务器错误提示));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @SuppressLint("DefaultLocale")
            @Override
            public void Params(Map<String, String> params) {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String phone=etPhone.getText().toString().trim();
                params.put("regUserName", username);
                params.put("regPassword", password);
                params.put("regEmail", email);
                params.put("regDataLoggerNo", "");
                params.put("regValidateCode", "");
                params.put("regPhoneNumber", phone);
                String s = new SimpleDateFormat("Z", Locale.ENGLISH).format(new Date());
                if (s.length() > 2) {
                    s = s.substring(0, s.length() - 2);
                } else {
                    s = "8";
                }
                if (s.startsWith("+13")) {
                    s = "12";
                }
                Locale locale = getResources().getConfiguration().locale;
                String language = locale.getLanguage();
                if (language.toLowerCase().contains("zh")) {
                    language = "zh_cn";
                } else if (language.toLowerCase().contains("en")) {
                    language = "en";
                } else if (language.toLowerCase().contains("fr")) {
                    language = "fr";
                } else if (language.toLowerCase().contains("ja")) {
                    language = "ja";
                } else if (language.toLowerCase().contains("it")) {
                    language = "it";
                } else if (language.toLowerCase().contains("ho")) {
                    language = "ho";
                } else if (language.toLowerCase().contains("tk")) {
                    language = "tk";
                } else if (language.toLowerCase().contains("pl")) {
                    language = "pl";
                } else if (language.toLowerCase().contains("gk")) {
                    language = "gk";
                } else if (language.toLowerCase().contains("gm")) {
                    language = "gm";
                } else {
                    language = "en";
                }
                params.put("regTimeZone", s);
                params.put("regLanguage", language);
                params.put("regCountry", country);
                params.put("regCity", "");
                params.put("agentCode", "");
                params.put("regLng", "");
                params.put("regLat", "");

            }

            @Override
            public void LoginError(String str) {

            }
        });
    }*/


    private void register(){
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String postCode=etPostCode.getText().toString().trim();
        String phone=etPhone.getText().toString().trim();
        String installer=etInstanller.getText().toString().trim();
        final String country = MyUtil.getCountryAndPhoneCodeByCountryCode(this, 1);
        if (TextUtils.isEmpty(username)) {
            toast(R.string.m21用户名密码为空);
            return;
        }
        if (username.length() < 3) {
            toast(R.string.m99用户名必须大于3位);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            toast(R.string.m21用户名密码为空);
            return;
        }

        if (password.length() < 6) {
            toast(R.string.m100密码必须大于6位);
            return;
        }

        if (TextUtils.isEmpty(String.valueOf(etConfirm.getText()))) {
            toast(R.string.m21用户名密码为空);
            return;
        }

        if (!etPassword.getText().toString().trim().equals(etConfirm.getText().toString().trim())) {
            toast(R.string.m98请输入相同的密码);
            return;
        }

        if (TextUtils.isEmpty(email)) {
            toast(R.string.m35请输入正确邮箱格式);
            return;
        }

        //校验邮箱
        if (!MyUtil.regexCheckEmail(email)) {
            toast(R.string.m35请输入正确邮箱格式);
            return;
        }

        //邮政编码
        if (TextUtils.isEmpty(postCode)){
            toast(R.string.m邮政编码不能为空);
            return;
        }


        if (!checkBox.isChecked()) {
            toast(R.string.m34选择用户协议);
            return;
        }

        Mydialog.Show(this);
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "register");//cmd  注册
            object.put("userId", username);//用户名
            object.put("roleId", "endUser");//角色
            object.put("phone",phone);
            object.put("password", password);//密码
            object.put("installer", installer);//安装者
            object.put("company", installer);//公司
            object.put("email", email);//邮箱
            object.put("installerInfo", installer);//安装商信息
            object.put("zipCode", postCode);//邮编
            object.put("country", country);//国家
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
                        toast(R.string.m42注册成功);
                        SqliteUtil.url(SmartHomeUrlUtil.getServer());
                        LoginUtil.login(mContext, etUsername.getText().toString().trim(), etPassword.getText().toString().trim(), new OnViewEnableListener() {
                            @Override
                            public void onViewEnable() {

                            }
                        });
                    }else {
                        String errorMsg = object.optString("data");
                        toast(errorMsg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void LoginError(String str) {

            }
        });

        Cons.regMap.setRegEmail(email);
        Cons.regMap.setRegPassword(password);
        Cons.regMap.setRegPhoneNumber(phone);
        Cons.regMap.setRegUserName(username);
        Cons.regMap.setRegPostCode(postCode);
        Cons.regMap.setRegInstaller(installer);
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
