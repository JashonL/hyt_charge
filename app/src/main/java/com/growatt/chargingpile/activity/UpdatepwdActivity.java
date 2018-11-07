package com.growatt.chargingpile.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.connutil.PostUtil;
import com.growatt.chargingpile.connutil.Urlsutil;
import com.growatt.chargingpile.sqlite.SqliteUtil;
import com.growatt.chargingpile.util.Constant;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.util.SharedPreferencesUnit;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UpdatepwdActivity extends BaseActivity {

    @BindView(R.id.headerView)
    View headerView;
    @BindView(R.id.editText1)
    EditText et1;
    @BindView(R.id.editText2)
    EditText et2;
    @BindView(R.id.editText3)
    EditText et3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatepwd);
        ButterKnife.bind(this);
        initHeaderView();
    }


    private void initHeaderView() {
        setHeaderImage(headerView, R.drawable.back, Position.LEFT, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setHeaderTitle(headerView,getString(R.string.m57修改密码),R.color.title_1,false);

    }

    @OnClick({R.id.bt_finish})
    public void onClicklistener(View view){
        switch (view.getId()){
            case R.id.bt_finish:
                tvOK();
                break;
        }

    }


    public void tvOK() {
        String s1 = et1.getText().toString();
        String s2 = et2.getText().toString();
        String s3 = et3.getText().toString();
        if (s1.equals("") || s2.equals("") || s3.equals("")) {
            toast(R.string.m21用户名密码为空);
            return;
        }
        if (!s2.equals(s3)) {
            toast(R.string.m98请输入相同的密码);
            return;
        }
        Mydialog.Show(UpdatepwdActivity.this, "");
        PostUtil.post(new Urlsutil().updateUserPassword, new PostUtil.postListener() {

            @Override
            public void success(String json) {
                try {
                    Mydialog.Dismiss();
                    JSONObject jsonObject = new JSONObject(json);
                    if (jsonObject.get("msg").toString().equals("200")) {
                        toast(R.string.m成功);
                        //设置不自动登录
                        //设置不自动登录
                        SharedPreferencesUnit.getInstance(UpdatepwdActivity.this).putInt(Constant.AUTO_LOGIN, 0);
                        SharedPreferencesUnit.getInstance(UpdatepwdActivity.this).putInt(Constant.AUTO_LOGIN_TYPE, 0);
                        jumpTo(LoginActivity.class, true);
                    } else if (jsonObject.get("msg").toString().equals("502")) {
                        toast(R.string.m64原密码错误);
                    } else if ("701".equals(jsonObject.get("msg").toString())) {
                        toast(R.string.m66你的账号没有操作权限);
                    } else {
                        toast(R.string.m37服务器错误);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void Params(Map<String, String> params) {
                Map<String, Object> map = SqliteUtil.inquirylogin();
                params.put("accountName", map.get("name").toString());
                params.put("passwordOld", et1.getText().toString());
                params.put("passwordNew", et2.getText().toString());
            }

            @Override
            public void LoginError(String str) {
                // TODO Auto-generated method stub

            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}
