package com.growatt.chargingpile.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.EventBusMsg.FreshAuthMsg;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.connutil.PostUtil;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.util.SmartHomeUrlUtil;
import com.growatt.chargingpile.util.SmartHomeUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AddAuthorizationActivity extends BaseActivity {

    @BindView(R.id.headerView)
    LinearLayout headerView;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvRight)
    TextView tvRight;

    @BindView(R.id.et_username)
    EditText etUsername;


    private String chargingId;
    private Unbinder bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_authorization);
        bind = ButterKnife.bind(this);
        initHeadView();
        initIntent();
        setViews();
    }

    private void setViews() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sign_user);
        bitmap = Bitmap.createScaledBitmap(bitmap, getResources().getDimensionPixelSize(R.dimen.xa40), getResources().getDimensionPixelSize(R.dimen.xa40), true);
        ImageSpan imageHint = new ImageSpan(this, bitmap);
        SpannableString spannableString = new SpannableString("image" + getString(R.string.m25??????????????????));
        spannableString.setSpan(imageHint, 0, "image".length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        etUsername.setHint(spannableString);
    }

    private void initIntent() {
        chargingId = getIntent().getStringExtra("sn");
    }

    private void initHeadView() {
        setHeaderImage(headerView, R.drawable.back, Position.LEFT, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.title_1));
        tvTitle.setText(getString(R.string.m162????????????));
        //??????????????????
        tvTitle.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        int dimen = getResources().getDimensionPixelSize(R.dimen.xa20);
        tvRight.setTextSize(TypedValue.COMPLEX_UNIT_PX, dimen);
        tvRight.setTextColor(ContextCompat.getColor(this, R.color.charging_text_color_2));
        tvRight.setText(getString(R.string.m164???????????????));
        tvRight.setOnClickListener(v -> gotoRegister());


    }


    /**
     * ??????????????????
     */
    private void gotoRegister() {
        Intent intent = new Intent(this, AddAuthorizeRegisterActivity.class);
        intent.putExtra("sn", chargingId);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);

    }


    @OnClick(R.id.btAdd)
    public void toAddUser(View view) {
        final String username = etUsername.getText().toString();
        if (TextUtils.isEmpty(username.trim())) {
            toast(R.string.m25??????????????????);
        }
        toAddAuthorize();

     /*
        PostUtil.post(new Urlsutil().postServerUserId, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {
                params.put("userAccount", username);
            }

            @Override
            public void success(String json) {
                try {
                    JSONObject object = new JSONObject(json);
                    int result = object.getInt("result");
                    if (result == 1) {
                        JSONObject obj = object.getJSONObject("obj");
                        String id = obj.getString("id");
                        if (id.equals("null")) {
                            toast(getString(R.string.m???????????????));
                        } else {
                            toAddAuthorize();
                        }
                    } else {
                        toast(getString(R.string.m???????????????));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void LoginError(String str) {

            }
        });*/
    }


    /**
     * ????????????
     */

    private void toAddAuthorize() {
        String userName = etUsername.getText().toString().trim();
        if (TextUtils.isEmpty(String.valueOf(etUsername.getText()))) {
            toast(R.string.m25??????????????????);
            return;
        }
        Map<String, Object> jsonMap = new LinkedHashMap<>();
        jsonMap.put("ownerId", SmartHomeUtil.getUserName());
        jsonMap.put("sn", chargingId);
        jsonMap.put("userId", userName);
        jsonMap.put("phone", "");
        jsonMap.put("userName", userName);
        jsonMap.put("lan", getLanguage());//??????id
        String json = SmartHomeUtil.mapToJsonString(jsonMap);
        Mydialog.Show(this);
        PostUtil.postJson(SmartHomeUrlUtil.postAddAuthorizationUser(), json, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                Mydialog.Dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        EventBus.getDefault().post(new FreshAuthMsg());
                        AddAuthorizationActivity.this.finish();
                    }
                    String data = jsonObject.getString("data");
                    toast(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void LoginError(String str) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
