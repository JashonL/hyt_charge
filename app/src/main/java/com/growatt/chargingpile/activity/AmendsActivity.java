package com.growatt.chargingpile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.connutil.PostUtil;
import com.growatt.chargingpile.util.Cons;
import com.growatt.chargingpile.util.DialogUtil;
import com.growatt.chargingpile.util.MyUtil;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.util.SmartHomeUrlUtil;
import com.growatt.chargingpile.util.SmartHomeUtil;

import org.json.JSONObject;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AmendsActivity extends BaseActivity {
    @BindView(R.id.headerView)
    View headerView;
    @BindView(R.id.tvTip)
    TextView tvTip;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.et_user_pwd)
    EditText etUserPwd;


    private String type;
    private String PhoneNum;
    private String email;

    private String installEmail;
    private String installPhone;
    private String installAddress;
    private String installer;

    private Unbinder bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amendaddress);
        bind = ButterKnife.bind(this);
        initHeaderView();
        initIntent();
        initViews();
    }


    private void initHeaderView() {
        setHeaderImage(headerView, R.drawable.back, Position.LEFT, v -> finish());
        setHeaderTitle(headerView, getString(R.string.m57修改密码), R.color.title_1, false);
    }


    @OnClick({R.id.btnOk})
    public void onClicklistener(View view) {
        switch (view.getId()) {
            case R.id.btnOk:
                valPhoneOrEmail();
                break;
        }

    }

    private void initIntent() {
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        PhoneNum = bundle.getString("PhoneNum");
        email = bundle.getString("email");
        installEmail = bundle.getString("installEmail");
        installPhone = bundle.getString("installPhone");
        installAddress = bundle.getString("installAddress");
        type = bundle.getString("type");
    }


    private void initViews() {
        if ("1".equals(type)) {
            tvTip.setText(R.string.m58修改手机号);
            setHeaderTitle(headerView, getString(R.string.m58修改手机号));
            etContent.setHint(R.string.m104输入电话号码);
            etContent.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_PHONE);
            if (!TextUtils.isEmpty(PhoneNum)) {
                etContent.setText(PhoneNum);
            }
        } else if ("2".equals(type)) {
            tvTip.setText(R.string.m59修改邮箱);
            setHeaderTitle(headerView, getString(R.string.m59修改邮箱));
            etContent.setHint(R.string.m61输入邮箱地址);
            if (!TextUtils.isEmpty(email)) {
                etContent.setText(email);
            }
        } else if ("3".equals(type)) {
            tvTip.setText(R.string.m修改安装人员邮箱);
            setHeaderTitle(headerView, getString(R.string.m修改安装人员邮箱));
            etContent.setHint(R.string.m安装人员Email);
            if (!TextUtils.isEmpty(installEmail)) {
                etContent.setText(installEmail);
            }

        } else if ("4".equals(type)) {
            tvTip.setText(R.string.m修改安装人员电话);
            setHeaderTitle(headerView, getString(R.string.m修改安装人员电话));
            etContent.setHint(R.string.m安装人员电话);
            if (!TextUtils.isEmpty(installPhone)) {
                etContent.setText(installPhone);
            }
        } else {
            tvTip.setText(R.string.m修改安装人员地址);
            setHeaderTitle(headerView, getString(R.string.m修改安装人员地址));
            etContent.setHint(R.string.m安装人员地址);
            if (!TextUtils.isEmpty(installAddress)) {
                etContent.setText(installAddress);
            }
        }

        installer = Cons.userBean.getInstaller();
        if (TextUtils.isEmpty(installer)) {
            installer = "";
        }
    }

    /**
     * 修改手机号或者邮箱
     */
    public void valPhoneOrEmail() {
        String upContent = etContent.getText().toString().trim();
        String password = etUserPwd.getText().toString().trim();
        if (TextUtils.isEmpty(upContent)) {
            toast(R.string.m140不能为空);
            return;
        }

        if ("2".equals(type) || "3".equals(type)) {
            if (!MyUtil.regexCheckEmail(upContent)) {
                toast(R.string.m35请输入正确邮箱格式);
                return;
            }
        }


        if (TextUtils.isEmpty(password)) {
            toast(R.string.m26请输入密码);
            return;
        }

        Mydialog.Show(this);
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "updateUser");//cmd  注册
            object.put("userId", SmartHomeUtil.getUserName());//用户名
            object.put("password", password);//密码
            if ("1".equals(type)) {
                object.put("phone", upContent);//密码
            } else if ("2".equals(type)) {
                object.put("email", upContent);//密码
            } else if ("3".equals(type)) {
                object.put("installAddress", installAddress);
                object.put("installPhone", installPhone);
                object.put("installEmail", upContent);
                object.put("installer", installer);
            } else if ("4".equals(type)) {
                object.put("installAddress", installAddress);
                object.put("installPhone", upContent);
                object.put("installEmail", installEmail);
                object.put("installer", installer);
            } else {
                object.put("installAddress", upContent);
                object.put("installPhone", installPhone);
                object.put("installEmail", installEmail);
                object.put("installer", installer);
            }
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
                    String data = object.optString("data");
                    if (code == 0) {
                        DialogUtil.circlerDialog(AmendsActivity.this, data, code, false, () -> {
                            Intent intent = new Intent();
                            intent.putExtra("type", type);
                            intent.putExtra("result", upContent);
                            setResult(RESULT_OK, intent);
                            finish();
                        });
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


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
