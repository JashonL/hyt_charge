package com.growatt.chargingpile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.connutil.PostUtil;
import com.growatt.chargingpile.scan.activity.MyCaptureActivity;
import com.growatt.chargingpile.util.Cons;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.util.PermissionCodeUtil;
import com.growatt.chargingpile.util.SmartHomeUrlUtil;
import com.growatt.chargingpile.util.SmartHomeUtil;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.util.LogUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

public class AddChargingActivity extends BaseActivity {

    @BindView(R.id.headerView)
    LinearLayout headerView;

    @BindView(R.id.et_input_sn)
    EditText etSn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_charging);
        ButterKnife.bind(this);
        initHeaderView();
    }

    private void initHeaderView() {
        setHeaderImage(headerView, R.drawable.back, Position.LEFT, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backPileActivity(Cons.mCurrentPile.getChargeId());
            }
        });
        setHeaderTitle(headerView, getString(R.string.m138添加充电桩), R.color.title_1, true);
    }

    @OnClick({R.id.btConfirm, R.id.ll_scan_add})
    public void addDevice(View view) {
        switch (view.getId()) {
            case R.id.btConfirm:
                addBySncode();
                break;
            case R.id.ll_scan_add:
                //请求拍照权限
                if (EasyPermissions.hasPermissions(this, PermissionCodeUtil.PERMISSION_CAMERA)) {
                    addByScan();
                } else {
                    EasyPermissions.requestPermissions(this, String.format(getString(R.string.m权限获取某权限说明), getString(R.string.m相机)), PermissionCodeUtil.PERMISSION_CAMERA_CODE, PermissionCodeUtil.PERMISSION_CAMERA);
                }

                break;
        }
    }

    private void addByScan() {
        Intent intent = new Intent(this, MyCaptureActivity.class);
                                /*ZxingConfig是配置类
                                 *可以设置是否显示底部布局，闪光灯，相册，
                                 * 是否播放提示音  震动
                                 * 设置扫描框颜色等
                                 * 也可以不传这个参数
                                 * */
        ZxingConfig config = new ZxingConfig();
        config.setPlayBeep(true);//是否播放扫描声音 默认为true
        config.setShake(true);//是否震动  默认为true
        config.setFullScreenScan(true);//是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
        config.setShowbottomLayout(false);
        intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
        startActivityForResult(intent, 105);

    }

    private void addBySncode() {
        String sn = etSn.getText().toString().trim();
        addCharging(sn);
    }


    private void addCharging(final String sn) {
        String userId = Cons.userId;
        if (TextUtils.isEmpty(sn)) {
            toast(getString(R.string.m136请输入充电桩ID));
            return;
        }
        Mydialog.Show(this);
        Map<String, Object> jsonMap = new LinkedHashMap<String, Object>();
        jsonMap.put("userId", Cons.userBean.getId());
        jsonMap.put("sn", sn);
        jsonMap.put("lan",getLanguage());//测试id
        String json = SmartHomeUtil.mapToJsonString(jsonMap);
        LogUtil.i(json);
        PostUtil.postJson(SmartHomeUrlUtil.ADD_CHARGING, json, new PostUtil.postListener() {
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
                        backPileActivity(sn);
                    }
                    toast(object.getString("data"));
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
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        switch (requestCode) {
            case PermissionCodeUtil.PERMISSION_CAMERA_CODE:
                if (EasyPermissions.hasPermissions(this, PermissionCodeUtil.PERMISSION_CAMERA)) {
                    addByScan();
                }
                break;
        }
    }



    private void backPileActivity(String sn) {
        Intent intent = new Intent();
        intent.putExtra("chargingId",sn);
        setResult(RESULT_OK, intent);
        finish();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        backPileActivity(Cons.mCurrentPile.getChargeId());
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 105) {//扫码返回结果
            // 扫描二维码/条码回传
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    String sn = data.getStringExtra(Constant.CODED_CONTENT);
                    if (TextUtils.isEmpty(sn)) {
                        return;
                    }
                    addCharging(sn);
                }
            }
        }
    }
}
