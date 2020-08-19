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
        setHeaderTitle(headerView,getString(R.string.m57修改密码),R.color.title_1,false);
    }


    @OnClick({R.id.btnOk})
    public void onClicklistener(View view){
        switch (view.getId()){
            case R.id.btnOk:
                valPhoneOrEmail();
                break;
        }

    }

    private void initIntent() {
        Bundle bundle=getIntent().getExtras();
        assert bundle != null;
        PhoneNum=bundle.getString("PhoneNum");
        email=bundle.getString("email");
        type=bundle.getString("type");
    }


    private void initViews() {
        if(type.equals("1")){
            tvTip.setText(R.string.m58修改手机号);
            setHeaderTitle(headerView,getString(R.string.m58修改手机号));
            etContent.setHint(R.string.m104输入电话号码);
            etContent.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_CLASS_PHONE);
            if (!TextUtils.isEmpty(PhoneNum)){
                etContent.setText(PhoneNum);
            }
        }else{
            tvTip.setText(R.string.m59修改邮箱);
            setHeaderTitle(headerView, getString(R.string.m59修改邮箱));
            etContent.setHint(R.string.m61输入邮箱地址);
            if (!TextUtils.isEmpty(email)) {
                etContent.setText(email);
            }
        }
    }

    /**
     * 修改手机号或者邮箱
     */
    public void valPhoneOrEmail() {
        String upContent = etContent.getText().toString().trim();
        String password = etUserPwd.getText().toString().trim();
        if (TextUtils.isEmpty(upContent)){
            toast(R.string.m140不能为空);
            return;
        }

        if ("2".equals(type)){
            if (!MyUtil.regexCheckEmail(upContent)) {
                toast(R.string.m35请输入正确邮箱格式);
                return;
            }
        }

        if (TextUtils.isEmpty(password)){
            toast(R.string.m26请输入密码);
            return;
        }

        Mydialog.Show(this);
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "updateUser");//cmd  注册
            object.put("userId", SmartHomeUtil.getUserName());//用户名
            object.put("password", password);//密码
            if ("1".equals(type)){
                object.put("phone", upContent);//密码
            }else {
                object.put("email", upContent);//密码
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
                    String data=object.optString("data");
                    if (code == 0) {
                        DialogUtil.circlerDialog(AmendsActivity.this, data, code,false, () -> {
                            Intent intent=new Intent();
                            intent.putExtra("type",type);
                            intent.putExtra("result", upContent);
                            setResult(RESULT_OK,intent);
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
  /*      if (type.equals("1")) {
            String phone = etText.getText().toString().trim();
            Intent intent = new Intent(this, NewPhoneVerActivity.class);
            intent.putExtra("phone", phone);
            intent.putExtra("type", 101);
            startActivityForResult(intent, 1001);
        } else {
            String email = etText.getText().toString().trim();
            Intent intent = new Intent(this, NewEmailVerActivity.class);
            intent.putExtra("email", email);
            intent.putExtra("type", 101);
            startActivityForResult(intent, 1002);
        }*/
    }




/*    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1001) {//验证手机通过
            save(Cons.userBean.getPhone());
        }
        if (resultCode == RESULT_OK && requestCode == 1002) {//验证邮箱通过
            save(Cons.userBean.getEmail());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void save(final String valStr) {
        Mydialog.Show(AmendsActivity.this, "");
        PostUtil.post(new Urlsutil().updateUser, new PostUtil.postListener() {

            @Override
            public void success(String json) {
                Mydialog.Dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    if (jsonObject.get("success").toString().equals("true")) {
                        Intent intent = new Intent(AmendsActivity.this, UserActivity.class);
                        Bundle bundle = new Bundle();
                        if (type.equals("1")) {
                            bundle.putString("PhoneNum", valStr);
                            bundle.putString("email", email);
                            intent.putExtras(bundle);
                            setResult(1, intent);
                        } else if (type.equals("2")) {
                            bundle.putString("PhoneNum", PhoneNum);
                            bundle.putString("email", valStr);
                            intent.putExtras(bundle);
                            setResult(2, intent);
                        }
                        toast(R.string.m9确定);
                        finish();
                    } else if ("701".equals(jsonObject.get("msg").toString())) {
                        toast(R.string.m66你的账号没有操作权限);
                    } else {
                        toast(R.string.m修改失败);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Mydialog.Dismiss();
                }
            }

            @Override
            public void Params(Map<String, String> params) {
                params.put("accountName", SmartHomeUtil.getUserName());
                if (type.equals("1")) {
                    params.put("phoneNum", valStr);
                    params.put("email", email);
                } else {
                    params.put("phoneNum", PhoneNum);
                    params.put("email", valStr);
                }
            }

            @Override
            public void LoginError(String str) {
            }
        });

    }*/



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
