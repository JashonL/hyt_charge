package com.growatt.chargingpile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.connutil.PostUtil;
import com.growatt.chargingpile.connutil.Urlsutil;
import com.growatt.chargingpile.util.Cons;
import com.growatt.chargingpile.util.Mydialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AmendsActivity extends BaseActivity {
    @BindView(R.id.headerView)
    View headerView;
    @BindView(R.id.tvTip)
    TextView tvTip;
    @BindView(R.id.etText)
    EditText etText;


    private String type;
    private String PhoneNum;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amendaddress);
        ButterKnife.bind(this);
        initHeaderView();
        initIntent();
        initViews();
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
                valPhoneOrEmail();
                break;
        }

    }

    private void initIntent() {
        Bundle bundle=getIntent().getExtras();
        PhoneNum=bundle.getString("PhoneNum");
        email=bundle.getString("email");
        type=bundle.getString("type");
    }


    private void initViews() {
        if(type.equals("1")){
            etText.setText(PhoneNum);
            tvTip.setText(R.string.m58修改手机号);
            setHeaderTitle(headerView,getString(R.string.m58修改手机号));
        }else{
            etText.setText(email);
            tvTip.setText(R.string.m59修改邮箱);
            setHeaderTitle(headerView,getString(R.string.m59修改邮箱));
        }
    }

    /**
     * 验证手机号或者邮箱
     */
    public void valPhoneOrEmail() {
        if (type.equals("1")) {
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
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1001) {//验证手机通过
            save(Cons.userBean.getPhoneNum());
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
                params.put("accountName", Cons.userBean.accountName);
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

    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
