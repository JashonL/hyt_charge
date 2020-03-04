package com.growatt.chargingpile.activity;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.connutil.PostUtil;
import com.growatt.chargingpile.util.Cons;
import com.growatt.chargingpile.util.LoginUtil;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.util.SmartHomeUrlUtil;
import com.growatt.chargingpile.util.SmartHomeUtil;
import com.mylhyl.circledialog.CircleDialog;

import org.json.JSONObject;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class UserActivity extends BaseActivity {

    @BindView(R.id.headerView)
    View headerView;
    @BindView(R.id.textView3)
    TextView textView3;
    @BindView(R.id.textView5)
    TextView textView5;
    private Unbinder bind;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        bind = ButterKnife.bind(this);
        initHeaderView();
        initViews();
    }

    private void initViews() {
        String phone = Cons.userBean.getPhone();
        if (!TextUtils.isEmpty(phone)) {
            textView3.setText(phone);
        }
        String email = Cons.userBean.getEmail();
        if (!TextUtils.isEmpty(email)) {
            textView5.setText(email);
        }
    }


    private void initHeaderView() {
        setHeaderTitle(headerView, getString(R.string.m51账号管理), R.color.title_1, false);
        setHeaderImage(headerView, R.drawable.back, Position.LEFT, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @OnClick({R.id.rl_edit_password, R.id.rl_edit_phone, R.id.rl_edit_email, R.id.logout})
    public void onClickListners(View view) {
        switch (view.getId()) {
            case R.id.rl_edit_password:
                startActivity(new Intent(UserActivity.this, UpdatepwdActivity.class));
                break;
            case R.id.rl_edit_phone:
                toUpdate(1);
                break;
            case R.id.rl_edit_email:
                toUpdate(2);
                break;
            case R.id.logout:
                LogoutUser();
                break;
        }
    }

    private void LogoutUser() {
        new CircleDialog.Builder()
                .setWidth(0.75f)
                .setTitle(getString(R.string.m318是否注销账户))
                .setText(getString(R.string.m319注销后账户将被删除))
                .configText(params -> {
                    params.textSize = 30;
                })
                .setGravity(Gravity.CENTER).setPositive(getString(R.string.m9确定), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUser();
            }
        })
                .setNegative(getString(R.string.m7取消), null)
                .show(getSupportFragmentManager());

    }


    private void deleteUser() {
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "deleteUser");
            object.put("userId", SmartHomeUtil.getUserName());
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
                        LoginUtil.logout(UserActivity.this);
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


    private void toUpdate(int type) {
        Intent intent = new Intent(UserActivity.this, AmendsActivity.class);
        Bundle bundle = new Bundle();
        if (type == 1) {
            bundle.putString("type", "1");
        } else if (type == 2) {
            bundle.putString("type", "2");
        }
        bundle.putString("PhoneNum", Cons.userBean.getPhone());
        bundle.putString("email", Cons.userBean.getEmail());
        intent.putExtras(bundle);
        startActivityForResult(intent, 103);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 103:
                    String type = data.getStringExtra("type");
                    String result = data.getStringExtra("result");
                    if (!TextUtils.isEmpty(result)){
                        if ("1".equals(type)){
                            Cons.userBean.setPhone(result);
                            textView3.setText(result);
                        }else {
                            Cons.userBean.setEmail(result);
                            textView5.setText(result);
                        }
                    }
                    break;

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
