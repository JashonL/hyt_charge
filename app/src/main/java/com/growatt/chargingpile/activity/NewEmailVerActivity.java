package com.growatt.chargingpile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.connutil.PostUtil;
import com.growatt.chargingpile.connutil.Urlsutil;
import com.growatt.chargingpile.listener.OnCirclerDialogListener;
import com.growatt.chargingpile.util.Cons;
import com.growatt.chargingpile.util.DialogUtil;
import com.growatt.chargingpile.util.MyUtil;
import com.growatt.chargingpile.util.Mydialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.util.LogUtil;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

;

public class NewEmailVerActivity extends BaseActivity {
    @BindView(R.id.headerView)
    View headerView;
    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etVCode)
    EditText etVCode;
    @BindView(R.id.btnSendCode)
    Button btnSendCode;
    private String email;
    private String vCode;
    private final int TOTAL_TIME = 180;
    private int TIME_COUNT = TOTAL_TIME;
    //跳转而来的信息
    private String jumpEmail;
    private int type;//跳转过来的类型:100:代表server验证

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_email_ver);
        ButterKnife.bind(this);
        initIntent();
        initHeaderView();
        initView();
    }

    private void initIntent() {
        Intent intent = getIntent();
        jumpEmail = intent.getStringExtra("email");
        type = intent.getIntExtra("type", 100);
        etEmail.setText(jumpEmail);
    }

    private void initView() {
        // 新建一个属性对象,设置文字的大小
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan((int) getResources().getDimension(R.dimen.xa23), false);
// 新建一个可以添加属性的文本对象
        //国家编码
        SpannableString ss1 = new SpannableString(getString(R.string.m国家区号));
        //手机号
        SpannableString ss2 = new SpannableString(getString(R.string.m61输入邮箱地址));
        //验证码
        SpannableString ss3 = new SpannableString(getString(R.string.m72请输入短信验证码));
        ss1.setSpan(ass, 0, ss1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss2.setSpan(ass, 0, ss2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        etEmail.setHint(new SpannedString(ss2)); // 一定要进行转换,否则属性会消失
        ss3.setSpan(ass, 0, ss3.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        etVCode.setHint(new SpannedString(ss3)); // 一定要进行转换,否则属性会消失

        //获取焦点显示输入法
        etEmail.requestFocus();
        etEmail.setFocusableInTouchMode(true);
        etEmail.requestFocus();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
                           public void run() {
                               MyUtil.showKeyboard(etEmail);
                           }
                       },
                200);
    }


    private void initHeaderView() {
        setHeaderImage(headerView, R.drawable.back, Position.LEFT, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setHeaderTitle(headerView, getString(R.string.m70验证用户邮箱), R.color.title_1, false);
    }

    @OnClick({R.id.btnSendCode, R.id.btnOk})
    public void onClickListener(View view) {
        switch (view.getId()) {
            case R.id.btnSendCode:
                sendSms();
                break;
            case R.id.btnOk:
                toUpdata();
                break;
        }

    }

    private void sendSms() {
        vCode = "";
        if (isEmpty(etEmail)) return;
        email = etEmail.getText().toString().trim();
        //发送后对button做处理
        //先获取服务器地址
        showAfterButton();
        PostUtil.post(new Urlsutil().postValPhoneOrEmail, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {
                params.put("type", "0");
                params.put("content", email);
            }

            @Override
            public void success(String json) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int result = jsonObject.getInt("result");
                    if (result == 1) {
                        vCode = jsonObject.getJSONObject("obj").getString("validate");
                        toast(R.string.m成功);
                    } else {
                        mHandler.sendEmptyMessage(result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void LoginError(String str) {
                LogUtil.i("error: " + str);
            }
        });
    }

    //显示灰色button
    private void showAfterButton() {
        if (btnSendCode.isEnabled() == true) {
            btnSendCode.setEnabled(false);
            btnSendCode.setBackgroundColor(getResources().getColor(R.color.white_background_click));
        }
        //显示文本
        btnSendCode.setText(TIME_COUNT + getString(R.string.m秒));
        //发送消息
        handler.sendEmptyMessageDelayed(102, 1000);
    }

    //显示正常button
    private void showBeforeButton() {
        btnSendCode.setEnabled(true);
        btnSendCode.setBackgroundColor(getResources().getColor(R.color.green_2));
        TIME_COUNT = TOTAL_TIME;
        //显示文本
        btnSendCode.setText(R.string.m75发送验证码);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String errorCode = (String) msg.obj;
            switch (msg.what) {
                case 101://发送验证码
                    if ("501".equals(errorCode)) {
                        toast(R.string.m78发送短信验证码不成功);
                    } else if ("502".equals(errorCode)) {
                        toast(R.string.m77手机号格式错误);
                    } else if ("503".equals(errorCode)) {
                        toast(R.string.m79该手机号没有注册用户);
                    }
                    break;
                case 102://发送验证码后倒计时
                    TIME_COUNT--;
                    if (TIME_COUNT <= 0) {
                        showBeforeButton();
                    } else {
                        showAfterButton();
                    }
                    break;
            }
        }
    };

    public void toUpdata() {
        String code = etVCode.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            toast(R.string.m61输入邮箱地址);
            return;
        }
        if (TextUtils.isEmpty(code)) {
            toast(R.string.m72请输入短信验证码);
            return;
        }
        if (code.equals(vCode)) {
            //更新用户验证信息
            updateUserEmail();
        } else {
            toast(R.string.m74验证码错误);
        }
    }

    private void updateUserEmail() {
        Mydialog.Show(this);
        PostUtil.post(new Urlsutil().postUpdateVal, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {
                params.put("type", "0");
                params.put("userName", Cons.userBean != null ? Cons.userBean.getAccountName() : "");
                params.put("content", email);
            }

            @Override
            public void success(String json) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int result = jsonObject.getInt("result");
                    if (result == 1) {
                        Cons.isValiEmail = true;
                        Cons.userBean.setEmail(email);
                        DialogUtil.circlerDialog(NewEmailVerActivity.this, getString(R.string.m成功), result, false, new OnCirclerDialogListener() {
                            @Override
                            public void onCirclerPositive() {
                                setResult(RESULT_OK);
                                finish();
                            }
                        });
                    } else {
                        mHandler.sendEmptyMessage(result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Mydialog.Dismiss();
                }
            }

            @Override
            public void LoginError(String str) {
            }
        });
    }

    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

            }
            DialogUtil.circlerDialog(NewEmailVerActivity.this, getString(R.string.m失败), msg.what, false, null);
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
