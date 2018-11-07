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
import com.growatt.chargingpile.connutil.Urlsutil;
import com.growatt.chargingpile.util.Mydialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgotPasswordActivity extends BaseActivity {
    @BindView(R.id.headerView)
    LinearLayout headerView;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.et_username)
    EditText etUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);
        initHeaderView();
    }

    private void initHeaderView() {
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.title_1));
        tvTitle.setText(getString(R.string.m22忘记密码));
        //设置字体加粗
        tvTitle.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        setHeaderImage(headerView, R.drawable.back, Position.LEFT, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @OnClick(R.id.btFinish)
    public void onClickListner(View view) {
        switch (view.getId()) {
            case R.id.btFinish:
                repeatPassword();
                break;
        }

    }

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

    }

}
