package com.growatt.chargingpile.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.bean.UdpSearchBean;
import com.growatt.chargingpile.connutil.PostUtil;
import com.growatt.chargingpile.util.Cons;
import com.growatt.chargingpile.util.MyUtil;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.util.PermissionCodeUtil;
import com.growatt.chargingpile.util.SmartHomeUrlUtil;
import com.growatt.chargingpile.util.SmartHomeUtil;
import com.growatt.chargingpile.util.T;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import pub.devrel.easypermissions.EasyPermissions;

public class ConnetWiFiActivity extends BaseActivity {

    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.ivLeft)
    ImageView ivLeft;
    @BindView(R.id.headerView)
    LinearLayout headerView;
    @BindView(R.id.tv_wifi_name)
    TextView tvWifiName;
    @BindView(R.id.get_wifi)
    TextView tvGetWifi;
    @BindView(R.id.ivRight)
    ImageView ivRight;
    @BindView(R.id.relativeLayout1)
    RelativeLayout relativeLayout1;
    @BindView(R.id.tv_id)
    TextView tvId;
    @BindView(R.id.tv_current_charging)
    TextView tvCurrentCharging;
    @BindView(R.id.ll_switch_ap)
    LinearLayout llSwitchAp;
    @BindView(R.id.tv_wifi_prompt)
    TextView tvWifiPrompt;
    @BindView(R.id.ll_refresh)
    LinearLayout llRefresh;
    @BindView(R.id.linearlayout2)
    LinearLayout linearlayout2;
    @BindView(R.id.et_wifi_password)
    TextView etWifiPassword;
    @BindView(R.id.ll_setwifi)
    LinearLayout llSetwifi;
    @BindView(R.id.btnOk)
    Button btnOk;
    @BindView(R.id.iv_apicon)
    ImageView ivApicon;
    @BindView(R.id.tv_aptext)
    TextView tvAptext;
    //wifi名称
    private String currentSSID;
    public static final int SEARCH_DEVICE_START = 1;
    public static final int SEARCH_DEVICE_FINISH = 2;
    private List<UdpSearchBean> mDeviceList;
    private Dialog dialog;
    private TextView tvProgress;
    private boolean isCancel = false;
    private int second = 5;
    private String devId;
    private int online;
    public String mIP;//服务器地址
    public int mPort = 8888;//服务器端口号

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, final Intent intent) {
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                checkWifiNetworkStatus();
            }
        }
    };

    private static final int FIRSTACT_TO_WIFI = 10000;
    private Unbinder bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connet_wi_fi);
        bind=ButterKnife.bind(this);
//        mDeviceList = new ArrayList<>();
        initIntent();
        initViews();
    }


    @Override
    protected void onResume() {
        super.onResume();
        initWifi();
    }

    private void initIntent() {
        devId = getIntent().getStringExtra("sn");
        online=getIntent().getIntExtra("online",0);
    }


    /**
     * 广播接收器，接收连接wifi的广播
     */
    private void initWifi() {
        registerReceiver(mBroadcastReceiver, new IntentFilter(
                WifiManager.NETWORK_STATE_CHANGED_ACTION));
    }

    private void initViews() {
        ivLeft.setImageResource(R.drawable.back);
        tvTitle.setText(R.string.m247热点连接);
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.title_1));
        ivRight.setImageResource(R.drawable.info);
        tvGetWifi.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        tvGetWifi.getPaint().setAntiAlias(true);//抗锯齿
        if (!TextUtils.isEmpty(devId)) {
            tvId.setText(devId);
        } else tvId.setText(R.string.m106选择充电桩);

        if (online==1){//1是离线
            llSwitchAp.setVisibility(View.GONE);
        }else {
            llSwitchAp.setVisibility(View.VISIBLE);
        }
    }


    public void checkWifiNetworkStatus() {
        if (MyUtil.isNetworkAvailable(ConnetWiFiActivity.this)) {
            if (Build.VERSION.SDK_INT >= 27) {//8.1
                if (EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    currentSSID = MyUtil.getWIFISSID(this);
                } else {
                    EasyPermissions.requestPermissions(this, String.format(getString(R.string.m权限获取某权限说明), getString(R.string.m位置)), PermissionCodeUtil.PERMISSION_LOCATION_CODE, Manifest.permission.ACCESS_FINE_LOCATION);
                }
            } else {
                currentSSID = MyUtil.getWIFISSID(this);
            }
            setWiFiName();
            mIP = getServerIp();
        } else {
            currentSSID = null;
            tvWifiName.setText(R.string.m288未连接);
        }

    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        super.onPermissionsGranted(requestCode, perms);
        if (requestCode == PermissionCodeUtil.PERMISSION_LOCATION_CODE) {
            if (EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                currentSSID = MyUtil.getWIFISSID(this);
                setWiFiName();
                mIP = getServerIp();
            } else {
                currentSSID = null;
                tvWifiName.setText(R.string.m288未连接);
            }
        }
    }


    private void setWiFiName() {
        if (TextUtils.isEmpty(currentSSID))
            tvWifiName.setText(R.string.m288未连接);
        else tvWifiName.setText(currentSSID);
    }


    @Override
    protected void onDestroy() {
        unRegisterWifiReceiver();
        if (bind!=null)bind.unbind();
        super.onDestroy();
    }


    private void unRegisterWifiReceiver() {
        try {
            if (mBroadcastReceiver != null) {
                unregisterReceiver(mBroadcastReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 提示是否调整到wifi界面
     */
    public void showJumpWifiSet() {
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        startActivity(intent);
    }

    @OnClick({R.id.ll_setwifi, R.id.ivLeft, R.id.btnOk, R.id.ll_refresh, R.id.ivRight, R.id.ll_switch_ap})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_setwifi:
                showJumpWifiSet();
                break;
            case R.id.ivLeft:
                finish();
                break;
            case R.id.btnOk:
                second = 60;
                isCancel = false;
                searchDevice();
                break;
            case R.id.ll_refresh:
                checkWifiNetworkStatus();
                break;
            case R.id.ivRight:
                Intent intent = new Intent(this, WifiSetGuideActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                jumpTo(intent, false);
                break;
            case R.id.ll_switch_ap:
                apMode();
                break;
        }
    }

    private void apMode() {
        Mydialog.Show(this);
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("chargeId", devId);//测试id
        jsonMap.put("userId", Cons.userBean.getAccountName());//测试id
        jsonMap.put("lan", getLanguage());//测试id
        String json = SmartHomeUtil.mapToJsonString(jsonMap);
        PostUtil.postJson(SmartHomeUrlUtil.postRequestSwitchAp(), json, new PostUtil.postListener() {
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
                        llSwitchAp.setBackgroundResource(R.drawable.selector_circle_btn_green_gradient);
                        ivApicon.setImageResource(R.drawable.ap_off);
                        tvAptext.setTextColor(ContextCompat.getColor(ConnetWiFiActivity.this,R.color.white_background));
                        toast(R.string.m307切换成功);
                    } else {
                        llSwitchAp.setBackgroundResource(R.drawable.shape_solid_white_stroke_green);
                        ivApicon.setImageResource(R.drawable.ap_on);
                        tvAptext.setTextColor(ContextCompat.getColor(ConnetWiFiActivity.this,R.color.maincolor_1));
                        toast(R.string.m308切换失败);
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


    private void toSetWifiParams() {
        Intent intent = new Intent(this, WifiSetActivity.class);
        intent.putExtra("ip", mIP);
        intent.putExtra("port", mPort);
        intent.putExtra("devId", currentSSID);
        startActivity(intent);
    }

    private String getServerIp() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) return null;
        DhcpInfo info = wifiManager.getDhcpInfo();
        return intToIp(info.serverAddress);
    }


    private String intToIp(int i) {
        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                (i >> 24 & 0xFF);
    }


    private void searchDevice() {
        if (TextUtils.isEmpty(currentSSID)) {
            T.make(R.string.m253手机暂未连接wifi, this);
        } else {
            if (!devId.equals(currentSSID)) {
                toast(R.string.m295该电桩热点不是已选择的电桩);
                return;
            }
            toSetWifiParams();
        }

     /*   new DeviceSearchThread() {
            @Override
            public void onSearchStart() {
                Message msg = Message.obtain();
                msg.what = SEARCH_DEVICE_START;
                handler.sendMessage(msg);
            }

            @Override
            public void onSearchFinish(Set deviceSet) {
                for (Object aDeviceSet : deviceSet) {
                    mDeviceList.add((UdpSearchBean) aDeviceSet);
                }
                Message msg = Message.obtain();
                msg.what = SEARCH_DEVICE_FINISH;
                handler.sendMessage(msg);
            }
        }.start();*/
    }


    /*显示进度对话框*/
    private void showProgress() {
        if (dialog == null) {
            View view = View.inflate(this, R.layout.progress_dialog_layout, null);
            tvProgress = view.findViewById(R.id.tv_progress);
            tvProgress.setText(String.valueOf(second));
            Button btnCancel = view.findViewById(R.id.btnCancel);
            btnCancel.setOnClickListener(v -> {
                isCancel = true;
                dialog.dismiss();
            });
            dialog = new Dialog(this, R.style.myDialogStyle);
            dialog.setContentView(view);
            Window win = dialog.getWindow();
            WindowManager.LayoutParams params = win.getAttributes();
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            int cxScreen = dm.widthPixels;
            params.width = cxScreen / 9 * 7;
            params.height = WindowManager.LayoutParams.MATCH_PARENT;
            dialog.setCanceledOnTouchOutside(false);
        }
        dialog.show();
    }
}
