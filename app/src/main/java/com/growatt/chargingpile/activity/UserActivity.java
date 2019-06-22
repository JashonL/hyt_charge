package com.growatt.chargingpile.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.connutil.PostUtil;
import com.growatt.chargingpile.connutil.Urlsutil;
import com.growatt.chargingpile.util.Cons;
import com.growatt.chargingpile.util.LoginUtil;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.util.SmartHomeUrlUtil;
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
        textView3.setText(Cons.userBean.getPhoneNum());
        textView5.setText(Cons.userBean.getEmail());
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
                PostUtil.post(new Urlsutil().postLogoutUserByName, new PostUtil.postListener() {
                    @Override
                    public void Params(Map<String, String> params) {
                        params.put("accountName", Cons.userBean.getAccountName());
                    }

                    @Override
                    public void success(String json) {
                        Mydialog.Dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            String msg = jsonObject.optString("msg");
                            if (jsonObject.opt("success").toString().equals("true")) {
                                if (msg.equals("200")) {
                                    toast(R.string.m320注销成功);
                                    deleteUser();
                                }
                            } else {
                                if (msg.equals("500")) {
                                    toast(R.string.m321注销失败);
                                }
                                if (msg.equals("501")) {
                                    toast(R.string.m322系统错误);
                                }
                                if (msg.equals("502")) {
                                    toast(R.string.m323用户不存在);
                                }
                                if (msg.equals("503")) {
                                    toast(R.string.m324该用户下存在浏览账户);
                                }
                                if (msg.equals("504")) {
                                    toast(R.string.m325admin账号不允许注销);
                                }
                                if (msg.equals("701")) {
                                    toast(R.string.m321注销失败);
                                }
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
        })
                .setNegative(getString(R.string.m7取消), null)
                .show(getSupportFragmentManager());

    }


    private void deleteUser() {
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "deleteUser");
            object.put("userId", Cons.userBean.getAccountName());
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
        bundle.putString("PhoneNum", Cons.userBean.getPhoneNum());
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

        switch (requestCode) {
            case 103:
//                Map<String, Object> map = list.get(positions);
                switch (resultCode) {
                    case 1:
                        toast(R.string.m成功);
                        String PhoneNum = data.getStringExtra("PhoneNum");
//                        map.put("str", PhoneNum);
//                        adapter.notifyDataSetChanged();
                        break;
                    case 2:
                        toast(R.string.m成功);
                        String email = data.getStringExtra("email");
//                        map.put("str", email);
//                        adapter.notifyDataSetChanged();
                        break;

                    default:
                        break;
                }
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bind != null) bind.unbind();
    }
}
