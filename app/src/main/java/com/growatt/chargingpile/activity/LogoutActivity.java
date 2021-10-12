package com.growatt.chargingpile.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.connutil.PostUtil;
import com.growatt.chargingpile.sqlite.SqliteUtil;
import com.growatt.chargingpile.util.LoginUtil;
import com.growatt.chargingpile.util.SmartHomeUrlUtil;
import com.growatt.chargingpile.util.SmartHomeUtil;
import com.growatt.chargingpile.view.LogoutResultDialog;

import org.json.JSONObject;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LogoutActivity extends BaseActivity {
    @BindView(R.id.headerView)
    View headerView;
    @BindView(R.id.edit_password)
    EditText mEditPassWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
        ButterKnife.bind(this);
        initHeaderView();
    }

    private void initHeaderView() {
        setHeaderTitle(headerView, getString(R.string.m55注销账号), R.color.title_1, false);
        setHeaderImage(headerView, R.drawable.back, Position.LEFT, v -> finish());
    }


    @OnClick({R.id.btn_ok})
    public void onClickListener(View view) {
        if (view.getId() == R.id.btn_ok) {
            requestDelete();
        }
    }

    private void requestDelete() {
        if (TextUtils.isEmpty(mEditPassWord.getText().toString())) {
            return;
        }

        Map<String, Object> map = SqliteUtil.inquirylogin();
        if (!mEditPassWord.getText().toString().equals(map.get("pwd"))) {
            toast(getString(R.string.Incorrect_password));
            return;
        }

        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "deleteUser");
            object.put("ownerId", SmartHomeUtil.getUserName());
            object.put("userId", SmartHomeUtil.getUserName());
            object.put("password", map.get("pwd"));
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
                try {
                    JSONObject object = new JSONObject(json);
                    int code = object.getInt("code");
                    if (code == 0) {
                        LogoutResultDialog.newInstance(0, code1 -> {
                            LoginUtil.logout(LogoutActivity.this);
                            finish();
                        }).show(getSupportFragmentManager(), "");
                    }else {
                        LogoutResultDialog.newInstance(1, code1 -> {
                        }).show(getSupportFragmentManager(), "");
                        //toast(object.getString("data"));
                        Log.d("LogoutActivity", "success: " + object.getString("data"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void LoginError(String str) {

            }
        });

    }
}