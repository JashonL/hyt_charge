package com.growatt.chargingpile.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.core.content.ContextCompat;

import android.text.TextUtils;
import android.view.Gravity;
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
import com.mylhyl.circledialog.CircleDialog;

import org.json.JSONObject;

import java.util.Calendar;
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
    @BindView(R.id.et_installer_email)
    EditText etInstallerEmail;
    @BindView(R.id.et_installer_phone)
    EditText etInstallerPhone;
    @BindView(R.id.et_installer_address)
    EditText etInstallerAddress;
    @BindView(R.id.tv_installer_date)
    TextView tvInstallerDate;
    @BindView(R.id.tv_serial_number)
    TextView tvSerialNumber;

    private Unbinder bind;
    private Calendar calendar = Calendar.getInstance();
    private String username;
    private String password;
    private String email;
    private String postCode;
    private String phone;
    private String installer;
    private String installerEmail;
    private String installerPhone;
    private String installerAddress;
    private String installerDate;
    private String installChargeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        bind = ButterKnife.bind(this);
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

    @OnClick({R.id.btRegister, R.id.textView4, R.id.ll_date})
    public void toRegister(View view) {
        switch (view.getId()) {
            case R.id.textView4:
                startActivity(new Intent(this, AgreementActivity.class));
                break;
            case R.id.btRegister:
                register();
                break;
            case R.id.ll_date:
                new DatePickerDialog(this, (view1, year, month, dayOfMonth) -> {
                    String sbDate = year +
                            "-" + ((month + 1) < 10 ? "0" + (month + 1) : (month + 1)) +
                            "-" + ((dayOfMonth < 10) ? "0" + dayOfMonth : dayOfMonth);
                    tvInstallerDate.setText(sbDate);
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)) {
                    @Override
                    protected void onStop() {
                    }
                }.show();
                break;
        }

    }


    private void register() {
        username = etUsername.getText().toString().trim();
        password = etPassword.getText().toString().trim();
        email = etEmail.getText().toString().trim();
        postCode = etPostCode.getText().toString().trim();
        phone = etPhone.getText().toString().trim();
        installer = etInstanller.getText().toString().trim();
        installerEmail = etInstallerEmail.getText().toString().trim();
        installerPhone = etInstallerPhone.getText().toString().trim();
        installerAddress = etInstallerAddress.getText().toString().trim();
        installerDate = tvInstallerDate.getText().toString().trim();
        installChargeId = tvSerialNumber.getText().toString().trim();

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
        if (TextUtils.isEmpty(postCode)) {
            toast(R.string.m邮政编码不能为空);
            return;
        }


        //安装者
        if (TextUtils.isEmpty(installer)) {
            toast(R.string.m安装商不能为空);
            return;
        }
        //安装者邮箱
        if (TextUtils.isEmpty(installerEmail)) {
            toast(R.string.m安装商邮箱不能为空);
            return;
        }
        //安装者电话
        if (TextUtils.isEmpty(installerPhone)) {
            toast(R.string.m安装商电话不能为空);
            return;
        }
        //安装者地址
        if (TextUtils.isEmpty(installerAddress)) {
            toast(R.string.m安装商地址不能为空);
            return;
        }
        //安装日期
        if (TextUtils.isEmpty(installerDate)) {
            toast(R.string.m安装商日期不能为空);
            return;
        }

        //充电桩序列号
        if (TextUtils.isEmpty(installChargeId)){
            toast(R.string.m充电桩序列号不能为空);
            return;
        }


        if (!checkBox.isChecked()) {
            toast(R.string.m34选择用户协议);
            return;
        }
        showDisclaimer();
    }


    public void showDisclaimer() {
        new CircleDialog.Builder()
                .setText(getString(R.string.m免责声明))
                .setGravity(Gravity.CENTER)
                .setPositive(getString(R.string.m9确定), v -> {
                    try {
                        requestRegister();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .show(getSupportFragmentManager());
    }


    private void requestRegister() {
        final String country = MyUtil.getCountryAndPhoneCodeByCountryCode(this, 1);
        Mydialog.Show(this);
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "register");//cmd  注册
            object.put("userId", username);//用户名
            object.put("roleId", "endUser");//角色
            object.put("phone", phone);
            object.put("password", password);//密码
            object.put("installer", installer);//安装者
            object.put("company", installer);//公司
            object.put("email", email);//邮箱
            object.put("installerInfo", installer);//安装商信息
            object.put("zipCode", postCode);//邮编
            object.put("country", country);//国家
            object.put("lan", getLanguage());
            object.put("installEmail", installerEmail);
            object.put("installPhone", installerPhone);
            object.put("installAddress", installerAddress);
            object.put("installDate", installerDate);
            object.put("installChargeId", installChargeId);

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
                    } else {
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
        Cons.regMap.setRegInstallEmail(installerEmail);
        Cons.regMap.setRegInstallPhone(installerPhone);
        Cons.regMap.setRegInstallAddress(installerAddress);
        Cons.regMap.setRegInstallDate(installerDate);
        Cons.regMap.setRegCity(country);
        Cons.regMap.setReInstallChargeId(installChargeId);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
