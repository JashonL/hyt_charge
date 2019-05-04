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
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import com.growatt.chargingpile.util.DeviceSearchThread;
import com.growatt.chargingpile.util.MyUtil;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.util.PermissionCodeUtil;
import com.growatt.chargingpile.util.T;
import com.mylhyl.circledialog.CircleDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

public class ConnetWiFiActivity extends BaseActivity {

    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.ivLeft)
    ImageView ivLeft;
    @BindView(R.id.tvRight)
    TextView tvRight;
    @BindView(R.id.relativeLayout1)
    RelativeLayout relativeLayout1;
    @BindView(R.id.headerView)
    LinearLayout headerView;
    @BindView(R.id.tv_wifi_prompt)
    TextView tvWifiPrompt;
    @BindView(R.id.tv_wifi_name)
    TextView tvWifiName;
    @BindView(R.id.linearlayout2)
    LinearLayout linearlayout2;
    @BindView(R.id.et_wifi_password)
    TextView etWifiPassword;
    @BindView(R.id.btnOk)
    Button btnOk;
    @BindView(R.id.ll_setwifi)
    LinearLayout llSetwifi;
    @BindView(R.id.get_wifi)
    TextView tvGetWifi;
    @BindView(R.id.ll_refresh)
    LinearLayout llRefresh;


    public String mIP;//服务器地址
    public int mPort = 8888;//服务器端口号
    //wifi名称
    private String currentSSID;
    public static final int SEARCH_DEVICE_START = 1;
    public static final int SEARCH_DEVICE_FINISH = 2;
    private List<UdpSearchBean> mDeviceList;
    private String devId;
    private Dialog dialog;
    private TextView tvProgress;
    private boolean isCancel = false;
    private int second = 5;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, final Intent intent) {
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                checkWifiNetworkStatus();
            }
        }
    };


//    private Handler handler = new Handler(Looper.getMainLooper()) {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case SEARCH_DEVICE_START:
//                    mDeviceList.clear();
//                    showProgress();
//                    break;
//                case SEARCH_DEVICE_FINISH:
//                    second--;
//                    tvProgress.setText(String.valueOf(second));
//                    if (mDeviceList.size() == 0) {
//                        if (!isCancel && second > 0) searchDevice();
//                        else {
//                            T.make(getString(R.string.m288级),ConnetWiFiActivity.this);
//                            dialog.dismiss();
//                        }
//                    } else {
//                        dialog.dismiss();
//                        mIP = getServerIp();
//                        devId = mDeviceList.get(0).getDevName();
//                        toSetWifiParams();
//                    }
////                    toSetWifiParams();//测试
//                    break;
//            }
//        }
//    };

    private static final int FIRSTACT_TO_WIFI = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connet_wi_fi);
        ButterKnife.bind(this);
//        mDeviceList = new ArrayList<>();
        initViews();
        initWifi();
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
        tvGetWifi.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        tvGetWifi.getPaint().setAntiAlias(true);//抗锯齿
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
        else  tvWifiName.setText(currentSSID);
    }


    @Override
    protected void onDestroy() {
        unRegisterWifiReceiver();
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

    @OnClick({R.id.ll_setwifi, R.id.ivLeft, R.id.btnOk, R.id.ll_refresh})
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
        }
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
