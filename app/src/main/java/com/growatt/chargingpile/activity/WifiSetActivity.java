package com.growatt.chargingpile.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.adapter.WifiSetAdapter;
import com.growatt.chargingpile.application.MyApplication;
import com.growatt.chargingpile.bean.WiFiRequestMsgBean;
import com.growatt.chargingpile.bean.WifiSetBean;
import com.growatt.chargingpile.util.MyUtil;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.util.SmartHomeUtil;
import com.growatt.chargingpile.util.SocketClientUtil;
import com.growatt.chargingpile.util.T;
import com.growatt.chargingpile.util.WiFiMsgConstant;
import com.mylhyl.circledialog.CircleDialog;

import org.xutils.common.util.LogUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiSetActivity extends BaseActivity {

    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.ivLeft)
    ImageView ivLeft;
    @BindView(R.id.headerView)
    LinearLayout headerView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.srl_pull)
    SwipeRefreshLayout srlPull;

    private WifiSetAdapter mAdapter;
    private List<WifiSetBean> list = new ArrayList<>();
    private String[] keys;
    private String[] lanArray;
    private String[] rcdArray;
    private String[] modeArray;

    private String ip;
    private int port;
    private String devId;
    private boolean isAllowed = false;//是否允许进入

    private byte devType;//交流直流
    private byte encryption;//加密类型
    private String startTime;
    private String endTime;

    //信息参数相关
    private byte[] idByte;
    private byte[] lanByte;
    private byte[] cardByte;
    private byte[] rcdByte;
    //网络相关设置
    private byte[] ipByte;
    private byte[] gatewayByte;
    private byte[] maskByte;
    private byte[] macByte;
    private byte[] dnsByte;
    //wifi相关设置
    private byte[] ssidByte;
    private byte[] wifiKeyByte;
    private byte[] bltNameByte;
    private byte[] bltPwdByte;
    private byte[] name4GByte;
    private byte[] pwd4GByte;
    private byte[] apn4GByte;
    //url相关配置
    private byte[] urlByte;
    private byte[] hskeyByte;
    private byte[] heatByte;
    private byte[] pingByte;
    private byte[] intervalByte;
    //充电参数
    private byte[] modeByte;
    private byte[] maxCurrentByte;
    private byte[] rateByte;
    private byte[] tempByte;
    private byte[] powerByte;
    private byte[] timeByte;

    //加密密钥
    private byte[] oldKey;
    private byte[] newKey;


    //是否已经连接
    private boolean isConnected = false;


    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            String text = "";
            int what = msg.what;
            switch (what) {
                case SocketClientUtil.SOCKET_EXCETION_CLOSE:
                    String message = (String) msg.obj;
                    text = "异常退出：" + message;
                    Log.d("liaojinsha", text);
                    break;
                case SocketClientUtil.SOCKET_CLOSE:
                    text = "连接关闭";
                    Log.d("liaojinsha", text);
                    break;
                case SocketClientUtil.SOCKET_OPEN:
                    text = "连接成功";
                    isConnected = true;
                    Log.d("liaojinsha", text);
                    srlPull.setEnabled(true);
                    break;
                case SocketClientUtil.SOCKET_SEND_MSG:
                    text = "发送消息";
                    Log.d("liaojinsha", text);
                    break;
                case SocketClientUtil.SOCKET_RECEIVE_MSG:
                    text = "回应字符串消息";
                    String receiString = (String) msg.obj;
                    Log.d("liaojinsha", text + receiString);
                    break;

                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    text = "回应字节消息";
                    byte[] receiByte = (byte[]) msg.obj;

                    parseReceivData(receiByte);
                    Log.d("liaojinsha", text);
                    break;
                case SocketClientUtil.SOCKET_CONNECT:
                    text = "socket已连接";
                    Log.d("liaojinsha", text);
                    break;
                case SocketClientUtil.SOCKET_SEND:
                    Log.d("liaojinsha", "socket已连接发送消息");
                    this.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sendCmdConnect();
                        }
                    }, 3500);
                    break;
                case 100://恢复按钮点击

                    break;
                case 101:
                    connectSendMsg();
                    break;
                default:

                    break;
            }
            srlPull.setRefreshing(false);
        }
    };
    private String tips;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_set);
        ButterKnife.bind(this);
        initIntent();
        initHeaderView();
        initResource();
        initRecyclerView();
        connectSendMsg();
        setOnclickListener();
        createNewKey();
    }

    private void createNewKey() {
        oldKey = SmartHomeUtil.commonkeys;
        newKey = SmartHomeUtil.createKey();
    }

    private void initIntent() {
        ip = getIntent().getStringExtra("ip");
        port = getIntent().getIntExtra("port", -1);
        devId = getIntent().getStringExtra("devId");
    }

    private void initHeaderView() {
        ivLeft.setImageResource(R.drawable.back);
        tvTitle.setText(getString(R.string.m105桩体设置));
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.title_1));
        srlPull.setColorSchemeColors(ContextCompat.getColor(this, R.color.maincolor_1));
        srlPull.setOnRefreshListener(this::refresh);
        srlPull.setEnabled(false);
    }


    private void initRecyclerView() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new WifiSetAdapter(list);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(mAdapter);
    }


    private void initResource() {
        keys = new String[]{
                getString(R.string.m255设备信息参数设置), getString(R.string.m146充电桩ID), getString(R.string.m260语言), getString(R.string.m264读卡器秘钥), getString(R.string.m265RCD保护值),
                getString(R.string.m256设备以太网参数设置), getString(R.string.m156充电桩IP), getString(R.string.m157网关), getString(R.string.m158子网掩码), getString(R.string.m159网络MAC地址), getString(R.string.m161DNS地址),
                getString(R.string.m257设备账号密码参数设置), getString(R.string.m266Wifi名称), getString(R.string.m267Wifi密码), getString(R.string.m268蓝牙名称), getString(R.string.m269蓝牙密码), getString(R.string.m2704G用户名), getString(R.string.m2714G密码), getString(R.string.m2724GAPN),
                getString(R.string.m258设备服务器参数设置), getString(R.string.m160服务器URL), getString(R.string.m273握手登录授权秘钥), getString(R.string.m274心跳间隔时间), getString(R.string.m275PING间隔时间), getString(R.string.m276表计上传间隔时间),
                getString(R.string.m259设备充电参数设置), getString(R.string.m154充电模式), getString(R.string.m277电桩最大输出电流), getString(R.string.m152充电费率), getString(R.string.m278保护温度), getString(R.string.m279外部监测最大输入功率), getString(R.string.m280允许充电时间)
        };

        for (int i = 0; i < keys.length; i++) {
            WifiSetBean bean = new WifiSetBean();
            if (i == 0 || i == 5 || i == 11 || i == 19 || i == 25) {
                bean.setTitle(keys[i]);
                bean.setType(WifiSetBean.PARAM_TITILE);
            } else if (i == 1) {
                bean.setType(WifiSetBean.PARAM_ITEM_CANT_CLICK);
                bean.setKey(keys[i]);
                bean.setValue("");
            } else {
                bean.setType(WifiSetBean.PARAM_ITEM);
                bean.setKey(keys[i]);
                bean.setValue("");
            }
            list.add(bean);
        }

        lanArray = new String[]{getString(R.string.m263英文), getString(R.string.m262泰文), getString(R.string.m261中文)};
        rcdArray = new String[9];
        for (int i = 0; i < 9; i++) {
            int rcdValue = (i + 1);
            rcdArray[i] = String.valueOf(rcdValue) + getString(R.string.m287级);
        }
        modeArray = new String[]{getString(R.string.m217扫码刷卡), getString(R.string.m218仅刷卡充电), getString(R.string.m219插枪充电)};
    }


    /*建立TCP连接*/
    private void connectSendMsg() {
        Mydialog.Show(this);
        connectServer();
    }


    private void refresh() {
        if (isConnected) connectSendMsg();
        else getDeviceInfo(WiFiMsgConstant.CONSTANT_MSG_01);
    }


    //连接对象
    private SocketClientUtil mClientUtil;

    private void connectServer() {
        mClientUtil = SocketClientUtil.newInstance();
        if (mClientUtil != null) {
            mClientUtil.connect(mHandler, ip, port);
        }
    }

    private void setOnclickListener() {
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (!isAllowed) return;
            int itemType = mAdapter.getData().get(position).getItemType();
            if (itemType != WifiSetBean.PARAM_ITEM) return;
            WifiSetBean bean = mAdapter.getData().get(position);
            switch (position) {
                case 2:
                    setLanguage();
                    break;
                case 4:
                    setRcd();
                    break;
                case 26:
                    setMode();
                    break;
                case 31:
                    showTimePickView(false);
                    break;
                default:
                    inputEdit(position, String.valueOf(bean.getValue()));
                    break;
            }
        });
    }


    /**
     * 弹框输入修改内容
     * item 修改项
     */
    private void inputEdit(final int key, final String value) {
        tips = "";
        switch (key) {
            case 22:
            case 23:
            case 24://心跳间隔时间、PING间隔时间、表记上传时间
                tips = "5~300(s)";
                break;
            case 27://输出最大电流
                tips = getString(R.string.m291设定值不能小于);
                break;
            case 29://保护温度
                tips = "65~85(℃)";
                break;
            case 30://外部检测最大输入功率
                tips = getString(R.string.m291设定值不能小于);
                break;
        }
        new CircleDialog.Builder()
                .setWidth(0.8f)
                .setTitle(this.getString(R.string.m27温馨提示))
                .setInputHint(tips)
                .setInputText(value)
                .setNegative(this.getString(R.string.m7取消), null)
                .setPositiveInput(this.getString(R.string.m9确定), (text, v) -> {
                    if (TextUtils.isEmpty(text)) {
                        toast(R.string.m140不能为空);
                        return;
                    }
                    byte[] bytes = text.trim().getBytes();
                    switch (key) {
                        case 3:
                            boolean letterDigit = MyUtil.isLetterDigit2(text);
                            if (!letterDigit) {
                                T.make(getString(R.string.m177输入格式不正确), this);
                                return;
                            }
                            if (bytes.length > 6) {
                                T.make(getString(R.string.m286输入值超出规定长度), this);
                                return;
                            }
                            cardByte = new byte[6];
                            System.arraycopy(bytes, 0, cardByte, 0, bytes.length);
                            setInfo();
                            break;
                        case 6:
                            boolean b = MyUtil.isboolIp(text);
                            if (!b) {
                                toast(R.string.m177输入格式不正确);
                                return;
                            }
                            if (bytes.length > 15) {
                                T.make(getString(R.string.m286输入值超出规定长度), this);
                                return;
                            }
                            ipByte = new byte[15];
                            System.arraycopy(bytes, 0, ipByte, 0, bytes.length);
                            setInternt();
                            break;
                        case 7:
                            boolean b1 = MyUtil.isboolIp(text);
                            if (!b1) {
                                toast(R.string.m177输入格式不正确);
                                return;
                            }
                            if (bytes.length > 15) {
                                T.make(getString(R.string.m286输入值超出规定长度), this);
                                return;
                            }
                            gatewayByte = new byte[15];
                            System.arraycopy(bytes, 0, gatewayByte, 0, bytes.length);
                            setInternt();
                            break;
                        case 8:
                            boolean b2 = MyUtil.isboolIp(text);
                            if (!b2) {
                                toast(R.string.m177输入格式不正确);
                                return;
                            }
                            if (bytes.length > 15) {
                                T.make(getString(R.string.m286输入值超出规定长度), this);
                                return;
                            }
                            maskByte = new byte[15];
                            System.arraycopy(bytes, 0, maskByte, 0, bytes.length);
                            setInternt();
                            break;
                        case 9:
                            boolean macLetterDigit = MyUtil.isLetterDigit2(text);
                            if (!macLetterDigit) {
                                T.make(getString(R.string.m177输入格式不正确), this);
                                return;
                            }
                            if (bytes.length > 17) {
                                T.make(getString(R.string.m286输入值超出规定长度), this);
                                return;
                            }
                            macByte = new byte[17];
                            System.arraycopy(bytes, 0, macByte, 0, bytes.length);
                            setInternt();
                            break;
                        case 10:
                            boolean b3 = MyUtil.isboolIp(text);
                            if (!b3) {
                                toast(R.string.m177输入格式不正确);
                                return;
                            }
                            if (bytes.length > 15) {
                                T.make(getString(R.string.m286输入值超出规定长度), this);
                                return;
                            }
                            dnsByte = new byte[15];
                            System.arraycopy(bytes, 0, dnsByte, 0, bytes.length);
                            setInternt();
                            break;
                        case 12:
                            boolean letterDigit1 = MyUtil.isLetterDigit2(text);
                            if (!letterDigit1) {
                                T.make(getString(R.string.m177输入格式不正确), this);
                                return;
                            }

                            if (bytes.length > 16) {
                                T.make(getString(R.string.m286输入值超出规定长度), this);
                                return;
                            }
                            ssidByte = new byte[16];
                            System.arraycopy(bytes, 0, ssidByte, 0, bytes.length);
                            setWifi();
                            break;
                        case 13:
                            boolean letterDigit2 = MyUtil.isLetterDigit2(text);
                            if (!letterDigit2) {
                                T.make(getString(R.string.m177输入格式不正确), this);
                                return;
                            }
                            if (bytes.length > 16) {
                                T.make(getString(R.string.m286输入值超出规定长度), this);
                                return;
                            }
                            wifiKeyByte = new byte[16];
                            System.arraycopy(bytes, 0, wifiKeyByte, 0, bytes.length);
                            setWifi();
                            break;

                        case 14:
                            boolean letterDigit3 = MyUtil.isLetterDigit2(text);
                            if (!letterDigit3) {
                                T.make(getString(R.string.m177输入格式不正确), this);
                                return;
                            }
                            if (bytes.length > 16) {
                                T.make(getString(R.string.m286输入值超出规定长度), this);
                                return;
                            }
                            bltNameByte = new byte[16];
                            System.arraycopy(bytes, 0, bltNameByte, 0, bytes.length);
                            setWifi();
                            break;
                        case 15:
                            boolean letterDigit4 = MyUtil.isLetterDigit2(text);
                            if (!letterDigit4) {
                                T.make(getString(R.string.m177输入格式不正确), this);
                                return;
                            }
                            if (bytes.length > 16) {
                                T.make(getString(R.string.m286输入值超出规定长度), this);
                                return;
                            }
                            bltPwdByte = new byte[16];
                            System.arraycopy(bytes, 0, bltPwdByte, 0, bytes.length);
                            setWifi();
                            break;
                        case 16:
                            boolean letterDigit5 = MyUtil.isLetterDigit2(text);
                            if (!letterDigit5) {
                                T.make(getString(R.string.m177输入格式不正确), this);
                                return;
                            }
                            if (bytes.length > 16) {
                                T.make(getString(R.string.m286输入值超出规定长度), this);
                                return;
                            }
                            name4GByte = new byte[16];
                            System.arraycopy(bytes, 0, name4GByte, 0, bytes.length);
                            setWifi();
                            break;
                        case 17:
                            boolean letterDigit6 = MyUtil.isLetterDigit2(text);
                            if (!letterDigit6) {
                                T.make(getString(R.string.m177输入格式不正确), this);
                                return;
                            }

                            if (bytes.length > 16) {
                                T.make(getString(R.string.m286输入值超出规定长度), this);
                                return;
                            }
                            pwd4GByte = new byte[16];
                            System.arraycopy(bytes, 0, pwd4GByte, 0, bytes.length);
                            setWifi();
                            break;
                        case 18:
                            boolean letterDigit7 = MyUtil.isLetterDigit2(text);
                            if (!letterDigit7) {
                                T.make(getString(R.string.m177输入格式不正确), this);
                                return;
                            }

                            if (bytes.length > 16) {
                                T.make(getString(R.string.m286输入值超出规定长度), this);
                                return;
                            }
                            apn4GByte = new byte[16];
                            System.arraycopy(bytes, 0, apn4GByte, 0, bytes.length);
                            setWifi();
                            break;

                        case 20:
                            if (bytes.length > 70) {
                                T.make(getString(R.string.m286输入值超出规定长度), this);
                                return;
                            }
                            urlByte = new byte[70];
                            System.arraycopy(bytes, 0, urlByte, 0, bytes.length);
                            setUrl();
                            break;
                        case 21:
                            boolean letterDigit8 = MyUtil.isLetterDigit2(text);
                            if (!letterDigit8) {
                                T.make(getString(R.string.m177输入格式不正确), this);
                                return;
                            }
                            if (bytes.length > 20) {
                                T.make(getString(R.string.m286输入值超出规定长度), this);
                                return;
                            }
                            hskeyByte = new byte[20];
                            System.arraycopy(bytes, 0, hskeyByte, 0, bytes.length);
                            setUrl();
                            break;
                        case 22:
                            boolean numeric = MyUtil.isNumberiZidai(text);
                            if (!numeric) {
                                T.make(getString(R.string.m177输入格式不正确), this);
                                return;
                            }

                            if (Integer.parseInt(text) < 5 || Integer.parseInt(text) > 300) {
                                T.make(getString(R.string.m290超出设置范围) + tips, WifiSetActivity.this);
                                return;
                            }

                            if (bytes.length > 4) {
                                T.make(getString(R.string.m286输入值超出规定长度), this);
                                return;
                            }
                            heatByte = new byte[4];
                            System.arraycopy(bytes, 0, heatByte, 0, bytes.length);
                            setUrl();
                            break;
                        case 23:
                            boolean numeric1 = MyUtil.isNumberiZidai(text);
                            if (!numeric1) {
                                T.make(getString(R.string.m177输入格式不正确), this);
                                return;
                            }

                            if (Integer.parseInt(text) < 5 || Integer.parseInt(text) > 300) {
                                T.make(getString(R.string.m290超出设置范围) + tips, WifiSetActivity.this);
                                return;
                            }

                            if (bytes.length > 4) {
                                T.make(getString(R.string.m286输入值超出规定长度), this);
                                return;
                            }
                            pingByte = new byte[4];
                            System.arraycopy(bytes, 0, pingByte, 0, bytes.length);
                            setUrl();
                            break;
                        case 24:
                            boolean numeric2 = MyUtil.isNumberiZidai(text);
                            if (!numeric2) {
                                T.make(getString(R.string.m177输入格式不正确), this);
                                return;
                            }

                            if (Integer.parseInt(text) < 5 || Integer.parseInt(text) > 300) {
                                T.make(getString(R.string.m290超出设置范围) + tips, WifiSetActivity.this);
                                return;
                            }

                            if (bytes.length > 4) {
                                T.make(getString(R.string.m286输入值超出规定长度), this);
                                return;
                            }
                            intervalByte = new byte[4];
                            System.arraycopy(bytes, 0, intervalByte, 0, bytes.length);
                            setUrl();
                            break;

                        case 27:
                            boolean numeric3 = MyUtil.isNumberiZidai(text);
                            if (!numeric3) {
                                T.make(getString(R.string.m177输入格式不正确), this);
                                return;
                            }

                            if (Integer.parseInt(text) < 3) {
                                T.make(getString(R.string.m291设定值不能小于), this);
                                return;
                            }

                            if (bytes.length > 2) {
                                T.make(getString(R.string.m286输入值超出规定长度), this);
                                return;
                            }
                            maxCurrentByte = new byte[2];
                            System.arraycopy(bytes, 0, maxCurrentByte, 0, bytes.length);
                            setCharging();
                            break;
                        case 28:
                            boolean numeric4 = MyUtil.isNumeric(text);
                            if (!numeric4) {
                                T.make(getString(R.string.m177输入格式不正确), this);
                                return;
                            }
                            if (bytes.length > 5) {
                                T.make(getString(R.string.m286输入值超出规定长度), this);
                                return;
                            }
                            rateByte = new byte[5];
                            System.arraycopy(bytes, 0, rateByte, 0, bytes.length);
                            setCharging();
                            break;
                        case 29:
                            boolean numeric5 = MyUtil.isNumberiZidai(text);
                            if (!numeric5) {
                                T.make(getString(R.string.m177输入格式不正确), this);
                                return;
                            }

                            if (Integer.parseInt(text) < 65 || Integer.parseInt(text) > 85) {
                                T.make(getString(R.string.m290超出设置范围) + tips, WifiSetActivity.this);
                                return;
                            }

                            if (bytes.length > 3) {
                                T.make(getString(R.string.m286输入值超出规定长度), this);
                                return;
                            }
                            tempByte = new byte[3];
                            System.arraycopy(bytes, 0, tempByte, 0, bytes.length);
                            setCharging();
                            break;
                        case 30:
                            boolean numeric6 = MyUtil.isNumberiZidai(text);
                            if (!numeric6) {
                                T.make(getString(R.string.m177输入格式不正确), this);
                                return;
                            }
                            if (Integer.parseInt(text) < 3) {
                                T.make(getString(R.string.m291设定值不能小于), this);
                                return;
                            }
                            if (bytes.length > 2) {
                                T.make(getString(R.string.m286输入值超出规定长度), this);
                                return;
                            }
                            powerByte = new byte[2];
                            System.arraycopy(bytes, 0, powerByte, 0, bytes.length);
                            setCharging();
                            break;
                    }
                })
                .show(this.getSupportFragmentManager());
    }

    /*发送连接命令*/
    private void sendCmdConnect() {
        //头1
        byte frame1 = WiFiMsgConstant.FRAME_1;
        //头2
        byte frame2 = WiFiMsgConstant.FRAME_2;
        //交流直流
        byte devType = WiFiMsgConstant.CONSTANT_MSG_10;
        //加密方式
        byte encryption = WiFiMsgConstant.CONSTANT_MSG_01;
        this.encryption = encryption;
        //指令
        byte cmd = WiFiMsgConstant.CMD_A0;

        /*****有效数据*****/
        byte len = (byte) 38;
        byte[] prayload = new byte[38];

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String time = simpleDateFormat.format(new Date());
        byte[] timeBytes = time.getBytes();
        System.arraycopy(timeBytes, 0, prayload, 0, timeBytes.length);

        byte[] idBytes = new byte[20];
        byte[] idBytesReal = devId.getBytes();
        System.arraycopy(idBytesReal, 0, idBytes, idBytes.length - idBytesReal.length, idBytesReal.length);
        System.arraycopy(idBytes, 0, prayload, timeBytes.length, idBytes.length);
        System.arraycopy(newKey, 0, prayload, timeBytes.length + idBytes.length, newKey.length);

        byte[] encryptedData = SmartHomeUtil.decodeKey(prayload, oldKey);

        byte end = WiFiMsgConstant.BLT_MSG_END;

        byte[] start = WiFiRequestMsgBean.Builder.newInstance()
                .setFrame_1(frame1)
                .setFrame_2(frame2)
                .setDevType(devType)
                .setEncryption(encryption)
                .setCmd(cmd)
                .setDataLen(len)
                .setPrayload(encryptedData)
                .setMsgEnd(end)
                .create();

        mClientUtil.sendMsg(start);

        LogUtil.i("连接命令：" + SmartHomeUtil.bytesToHexString(start));
        LogUtil.i("时间：" + time);
        LogUtil.i("时间转成16进制：" + SmartHomeUtil.bytesToHexString(timeBytes));
        LogUtil.i("id：" + SmartHomeUtil.bytesToHexString(timeBytes));
        LogUtil.i("idBytes：" + SmartHomeUtil.bytesToHexString(idBytes));
        LogUtil.i("key：" + SmartHomeUtil.bytesToHexString(timeBytes));
        LogUtil.i("keyBytes：" + SmartHomeUtil.bytesToHexString(newKey));
    }


    /*退出命令*/
    private void sendCmdExit() {
        //头1
        byte frame1 = WiFiMsgConstant.FRAME_1;
        //头2
        byte frame2 = WiFiMsgConstant.FRAME_2;
        //交流直流
        byte devType = this.devType;
        //加密方式
        byte encryption = this.encryption;
        //指令
        byte cmd = WiFiMsgConstant.CMD_A1;

        /*****有效数据*****/
        byte len = (byte) 14;
        byte[] prayload = new byte[14];

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String time = simpleDateFormat.format(new Date());
        byte[] timeBytes = time.getBytes();
        System.arraycopy(timeBytes, 0, prayload, 0, timeBytes.length);

        byte[] encryptedData = SmartHomeUtil.decodeKey(prayload, newKey);

        byte end = WiFiMsgConstant.BLT_MSG_END;

        byte[] exit = WiFiRequestMsgBean.Builder.newInstance()
                .setFrame_1(frame1)
                .setFrame_2(frame2)
                .setDevType(devType)
                .setEncryption(encryption)
                .setCmd(cmd)
                .setDataLen(len)
                .setPrayload(encryptedData)
                .setMsgEnd(end)
                .create();

        mClientUtil.sendMsg(exit);
    }


    /**
     * 获取设备相关信息参数
     * cmd = 0x01 获取设备信息参数
     * cmd = 0x02 获取设备以太网参数
     * cmd =0x03  获取设备账号密码参数
     * cmd =0x04  获取服务器参数
     */

    private void getDeviceInfo(byte cmd) {
        //头1
        byte frame1 = WiFiMsgConstant.FRAME_1;
        //头2
        byte frame2 = WiFiMsgConstant.FRAME_2;
        //交流直流
        byte devType = this.devType;
        //加密方式
        byte encryption = this.encryption;
        //指令
        byte getCmd = cmd;

        /*****有效数据*****/
        byte len = (byte) 1;
        byte[] prayload = new byte[1];
        byte[] getInfo = "1".getBytes();
        System.arraycopy(getInfo, 0, prayload, 0, getInfo.length);
        byte[] encryptedData = SmartHomeUtil.decodeKey(prayload, newKey);

        byte end = WiFiMsgConstant.BLT_MSG_END;

        byte[] infoBytes = WiFiRequestMsgBean.Builder.newInstance()
                .setFrame_1(frame1)
                .setFrame_2(frame2)
                .setDevType(devType)
                .setEncryption(encryption)
                .setCmd(cmd)
                .setDataLen(len)
                .setPrayload(encryptedData)
                .setMsgEnd(end)
                .create();

        mClientUtil.sendMsg(infoBytes);

        LogUtil.i("连接命令：" + SmartHomeUtil.bytesToHexString(infoBytes));
        LogUtil.i("转成16进制：" + SmartHomeUtil.bytesToHexString(getInfo));
    }


    /**************************************设置充电桩*****************************************/

    //信息参数设置
    private void setInfo() {
        //头1
        byte frame1 = WiFiMsgConstant.FRAME_1;
        //头2
        byte frame2 = WiFiMsgConstant.FRAME_2;
        //交流直流
        byte devType = this.devType;
        //加密方式
        byte encryption = this.encryption;
        //指令
        byte cmd = WiFiMsgConstant.CONSTANT_MSG_11;

        /*****有效数据*****/
        byte len = (byte) 28;
        byte[] prayload = new byte[28];


        if (idByte == null || lanByte == null || cardByte == null || rcdByte == null) {
            T.make(R.string.m244设置失败, this);
            return;
        }
        //id
        System.arraycopy(idByte, 0, prayload, 0, idByte.length);
        //语言
        System.arraycopy(lanByte, 0, prayload, idByte.length, lanByte.length);
        //card
        System.arraycopy(cardByte, 0, prayload, idByte.length + lanByte.length, cardByte.length);
        //rcd
        System.arraycopy(rcdByte, 0, prayload, idByte.length + lanByte.length + cardByte.length, rcdByte.length);


        byte[] encryptedData = SmartHomeUtil.decodeKey(prayload, newKey);

        byte end = WiFiMsgConstant.BLT_MSG_END;

        byte[] setInfo = WiFiRequestMsgBean.Builder.newInstance()
                .setFrame_1(frame1)
                .setFrame_2(frame2)
                .setDevType(devType)
                .setEncryption(encryption)
                .setCmd(cmd)
                .setDataLen(len)
                .setPrayload(encryptedData)
                .setMsgEnd(end)
                .create();

        mClientUtil.sendMsg(setInfo);
        LogUtil.i("设置id：" + SmartHomeUtil.bytesToHexString(setInfo));
    }


    //网络设置
    private void setInternt() {
        //头1
        byte frame1 = WiFiMsgConstant.FRAME_1;
        //头2
        byte frame2 = WiFiMsgConstant.FRAME_2;
        //交流直流
        byte devType = this.devType;
        //加密方式
        byte encryption = this.encryption;
        //指令
        byte cmd = WiFiMsgConstant.CONSTANT_MSG_12;

        /*****有效数据*****/
        byte len = (byte) 77;
        byte[] prayload = new byte[77];

        if (ipByte == null || gatewayByte == null || maskByte == null || macByte == null || dnsByte == null) {
            T.make(R.string.m244设置失败, this);
            return;
        }

        //ip
        System.arraycopy(ipByte, 0, prayload, 0, ipByte.length);
        //网关
        System.arraycopy(gatewayByte, 0, prayload, ipByte.length, gatewayByte.length);
        //掩码
        System.arraycopy(maskByte, 0, prayload, ipByte.length + gatewayByte.length, maskByte.length);
        //mac
        System.arraycopy(macByte, 0, prayload, ipByte.length + gatewayByte.length + maskByte.length, macByte.length);
        //dns
        System.arraycopy(dnsByte, 0, prayload, ipByte.length + gatewayByte.length + maskByte.length + macByte.length, dnsByte.length);


        byte[] encryptedData = SmartHomeUtil.decodeKey(prayload, newKey);

        byte end = WiFiMsgConstant.BLT_MSG_END;

        byte[] setInterNet = WiFiRequestMsgBean.Builder.newInstance()
                .setFrame_1(frame1)
                .setFrame_2(frame2)
                .setDevType(devType)
                .setEncryption(encryption)
                .setCmd(cmd)
                .setDataLen(len)
                .setPrayload(encryptedData)
                .setMsgEnd(end)
                .create();

        mClientUtil.sendMsg(setInterNet);
        LogUtil.i("设置ip：" + SmartHomeUtil.bytesToHexString(setInterNet));
    }

    //设置wifi
    private void setWifi() {
        //头1
        byte frame1 = WiFiMsgConstant.FRAME_1;
        //头2
        byte frame2 = WiFiMsgConstant.FRAME_2;
        //交流直流
        byte devType = this.devType;
        //加密方式
        byte encryption = this.encryption;
        //指令
        byte cmd = WiFiMsgConstant.CONSTANT_MSG_13;

        /*****有效数据*****/
        byte len = (byte) 112;
        byte[] prayload = new byte[112];

        if (ssidByte == null || wifiKeyByte == null || bltNameByte == null || bltPwdByte == null || name4GByte == null || pwd4GByte == null || apn4GByte == null) {
            T.make(R.string.m244设置失败, this);
            return;
        }

        //ssid
        System.arraycopy(ssidByte, 0, prayload, 0, ssidByte.length);
        //key
        System.arraycopy(wifiKeyByte, 0, prayload, ssidByte.length, wifiKeyByte.length);
        //蓝牙名称
        System.arraycopy(bltNameByte, 0, prayload, ssidByte.length + wifiKeyByte.length, bltNameByte.length);
        //蓝牙密码
        System.arraycopy(bltPwdByte, 0, prayload, ssidByte.length + wifiKeyByte.length + bltNameByte.length, bltPwdByte.length);
        //4G用户名
        System.arraycopy(name4GByte, 0, prayload, ssidByte.length + wifiKeyByte.length + bltNameByte.length + bltPwdByte.length, name4GByte.length);
        //4G密码
        System.arraycopy(pwd4GByte, 0, prayload, ssidByte.length + wifiKeyByte.length + bltNameByte.length + bltPwdByte.length + name4GByte.length, pwd4GByte.length);
        //4GAPN
        System.arraycopy(apn4GByte, 0, prayload, ssidByte.length + wifiKeyByte.length + bltNameByte.length + bltPwdByte.length + name4GByte.length + pwd4GByte.length, apn4GByte.length);


        byte[] encryptedData = SmartHomeUtil.decodeKey(prayload, newKey);

        byte end = WiFiMsgConstant.BLT_MSG_END;

        byte[] setWifi = WiFiRequestMsgBean.Builder.newInstance()
                .setFrame_1(frame1)
                .setFrame_2(frame2)
                .setDevType(devType)
                .setEncryption(encryption)
                .setCmd(cmd)
                .setDataLen(len)
                .setPrayload(encryptedData)
                .setMsgEnd(end)
                .create();

        mClientUtil.sendMsg(setWifi);
    }


    //设置url
    private void setUrl() {
        //头1
        byte frame1 = WiFiMsgConstant.FRAME_1;
        //头2
        byte frame2 = WiFiMsgConstant.FRAME_2;
        //交流直流
        byte devType = this.devType;
        //加密方式
        byte encryption = this.encryption;
        //指令
        byte cmd = WiFiMsgConstant.CONSTANT_MSG_14;

        /*****有效数据*****/
        byte len = (byte) 102;
        byte[] prayload = new byte[102];


        if (urlByte == null || hskeyByte == null || heatByte == null || pingByte == null || intervalByte == null) {
            T.make(R.string.m244设置失败, this);
            return;
        }


        //url
        System.arraycopy(urlByte, 0, prayload, 0, urlByte.length);
        //key
        System.arraycopy(hskeyByte, 0, prayload, 70, hskeyByte.length);
        //蓝牙名称
        System.arraycopy(heatByte, 0, prayload, 90, heatByte.length);
        //蓝牙密码
        System.arraycopy(pingByte, 0, prayload, 94, pingByte.length);
        //4G用户名
        System.arraycopy(intervalByte, 0, prayload, 98, intervalByte.length);


        byte[] encryptedData = SmartHomeUtil.decodeKey(prayload, newKey);

        byte end = WiFiMsgConstant.BLT_MSG_END;

        byte[] setUrl = WiFiRequestMsgBean.Builder.newInstance()
                .setFrame_1(frame1)
                .setFrame_2(frame2)
                .setDevType(devType)
                .setEncryption(encryption)
                .setCmd(cmd)
                .setDataLen(len)
                .setPrayload(encryptedData)
                .setMsgEnd(end)
                .create();

        mClientUtil.sendMsg(setUrl);
    }


    //设置充电参数
    private void setCharging() {
        //头1
        byte frame1 = WiFiMsgConstant.FRAME_1;
        //头2
        byte frame2 = WiFiMsgConstant.FRAME_2;
        //交流直流
        byte devType = this.devType;
        //加密方式
        byte encryption = this.encryption;
        //指令
        byte cmd = WiFiMsgConstant.CONSTANT_MSG_15;

        /*****有效数据*****/
        byte len = (byte) 44;
        byte[] prayload = new byte[44];


        if (modeByte == null || maxCurrentByte == null || rateByte == null || tempByte == null || powerByte == null || timeByte == null) {
            T.make(R.string.m244设置失败, this);
            return;
        }

        //模式
        System.arraycopy(modeByte, 0, prayload, 0, modeByte.length);
        //电流
        System.arraycopy(maxCurrentByte, 0, prayload, 1, maxCurrentByte.length);
        //充电费率
        System.arraycopy(rateByte, 0, prayload, 3, rateByte.length);
        //保护温度
        System.arraycopy(tempByte, 0, prayload, 8, tempByte.length);
        //最大电流
        System.arraycopy(powerByte, 0, prayload, 11, powerByte.length);
        //允许充电时间
        System.arraycopy(timeByte, 0, prayload, 13, timeByte.length);


        byte[] encryptedData = SmartHomeUtil.decodeKey(prayload, newKey);

        byte end = WiFiMsgConstant.BLT_MSG_END;

        byte[] setCharging = WiFiRequestMsgBean.Builder.newInstance()
                .setFrame_1(frame1)
                .setFrame_2(frame2)
                .setDevType(devType)
                .setEncryption(encryption)
                .setCmd(cmd)
                .setDataLen(len)
                .setPrayload(encryptedData)
                .setMsgEnd(end)
                .create();

        mClientUtil.sendMsg(setCharging);
    }

    /**********************************解析数据************************************/

    private void parseReceivData(byte[] data) {
        if (data == null) return;
        int length = data.length;
        byte frame1 = data[0];
        byte frame2 = data[1];
        byte end = data[length-1];
        if (length > 4 && frame1 == WiFiMsgConstant.FRAME_1 && frame2 == WiFiMsgConstant.FRAME_2&&end==WiFiMsgConstant.BLT_MSG_END) {
            byte cmd = data[4];//指令类型
            //校验位
            byte sum = data[length - 2];
            byte checkSum = SmartHomeUtil.getCheckSum(data);
            if (checkSum != sum) {
                LogUtil.d("数据校验失败-->" + "返回校验数据：" + sum + "真实数据校验:" + checkSum);
                return;
            }
            int len = (int) data[5];
            //有效数据
            byte[] prayload = new byte[len];
            System.arraycopy(data, 6, prayload, 0, prayload.length);
            if (WifiSetActivity.this.encryption == WiFiMsgConstant.CONSTANT_MSG_01) {//解密
                if (cmd == WiFiMsgConstant.CMD_A0)
                    prayload = SmartHomeUtil.decodeKey(prayload, oldKey);
                else prayload = SmartHomeUtil.decodeKey(prayload, newKey);
            }
            Log.d("liaojinsha", SmartHomeUtil.bytesToHexString(prayload));
            switch (cmd) {
                case WiFiMsgConstant.CMD_A0://连接命令
                    //电桩类型，直流或者交流
                    devType = data[2];
                    //是否允许进入
                    byte allow = prayload[0];
                    Mydialog.Dismiss();
                    if ((int) allow == 0) {
                        isAllowed = false;
                        T.make(getString(R.string.m254连接失败), WifiSetActivity.this);
                    } else {
                        isAllowed = true;
                        T.make(getString(R.string.m169连接成功), WifiSetActivity.this);
                        getDeviceInfo(WiFiMsgConstant.CONSTANT_MSG_01);
                    }
                    break;

                case WiFiMsgConstant.CMD_A1:
                    byte exit = prayload[0];
                    if ((int) exit == 1) {
                        T.make(getString(R.string.m281电桩断开), WifiSetActivity.this);
                    }
                    SocketClientUtil.close(mClientUtil);
                    break;

                case WiFiMsgConstant.CONSTANT_MSG_01://获取信息参数
                    idByte = new byte[20];
                    System.arraycopy(prayload, 0, idByte, 0, 20);
                    String devId = MyUtil.ByteToString(idByte);
                    mAdapter.getData().get(1).setValue(devId);

                    lanByte = new byte[1];
                    System.arraycopy(prayload, 20, lanByte, 0, 1);
                    String lan = MyUtil.ByteToString(lanByte);
                    if ("1".equals(lan)) {
                        lan = lanArray[0];
                    } else if ("2".equals(lan)) {
                        lan = lanArray[1];
                    } else {
                        lan = lanArray[2];
                    }
                    mAdapter.getData().get(2).setValue(lan);

                    cardByte = new byte[6];
                    System.arraycopy(prayload, 21, cardByte, 0, 6);
                    String card = MyUtil.ByteToString(cardByte);
                    mAdapter.getData().get(3).setValue(card);


                    rcdByte = new byte[1];
                    System.arraycopy(prayload, 27, rcdByte, 0, 1);
                    String rcd = MyUtil.ByteToString(rcdByte);
                    int i = Integer.parseInt(rcd);
                    if (i <= 0) i = 1;
                    String s = rcdArray[i - 1];
                    mAdapter.getData().get(4).setValue(s);


                    mAdapter.notifyDataSetChanged();

                    getDeviceInfo(WiFiMsgConstant.CONSTANT_MSG_02);
                    break;
                case WiFiMsgConstant.CONSTANT_MSG_02://获取以太网参数
                    ipByte = new byte[15];
                    System.arraycopy(prayload, 0, ipByte, 0, 15);
                    String devIp = MyUtil.ByteToString(ipByte);
                    mAdapter.getData().get(6).setValue(devIp);

                    gatewayByte = new byte[15];
                    System.arraycopy(prayload, 15, gatewayByte, 0, 15);
                    String gateway = MyUtil.ByteToString(gatewayByte);
                    mAdapter.getData().get(7).setValue(gateway);


                    maskByte = new byte[15];
                    System.arraycopy(prayload, 30, maskByte, 0, 15);
                    String mask = MyUtil.ByteToString(maskByte);
                    mAdapter.getData().get(8).setValue(mask);


                    macByte = new byte[17];
                    System.arraycopy(prayload, 45, macByte, 0, 17);
                    String mac = MyUtil.ByteToString(macByte);
                    mAdapter.getData().get(9).setValue(mac);

                    dnsByte = new byte[15];
                    System.arraycopy(prayload, 62, dnsByte, 0, 15);
                    String dns = MyUtil.ByteToString(dnsByte);
                    mAdapter.getData().get(10).setValue(dns);

                    mAdapter.notifyDataSetChanged();
                    getDeviceInfo(WiFiMsgConstant.CONSTANT_MSG_03);
                    break;
                case WiFiMsgConstant.CONSTANT_MSG_03://获取设备帐号密码参数
                    ssidByte = new byte[16];
                    System.arraycopy(prayload, 0, ssidByte, 0, 16);
                    String ssid = MyUtil.ByteToString(ssidByte);
                    mAdapter.getData().get(12).setValue(ssid);

                    wifiKeyByte = new byte[16];
                    System.arraycopy(prayload, 16, wifiKeyByte, 0, 16);
                    String wifikey = MyUtil.ByteToString(wifiKeyByte);
                    mAdapter.getData().get(13).setValue(wifikey);


                    bltNameByte = new byte[16];
                    System.arraycopy(prayload, 32, bltNameByte, 0, 16);
                    String bltName = MyUtil.ByteToString(bltNameByte);
                    mAdapter.getData().get(14).setValue(bltName);

                    bltPwdByte = new byte[16];
                    System.arraycopy(prayload, 48, bltPwdByte, 0, 16);
                    String bltPwd = MyUtil.ByteToString(bltPwdByte);
                    mAdapter.getData().get(15).setValue(bltPwd);

                    name4GByte = new byte[16];
                    System.arraycopy(prayload, 64, name4GByte, 0, 16);
                    String name4G = MyUtil.ByteToString(name4GByte);
                    mAdapter.getData().get(16).setValue(name4G);

                    pwd4GByte = new byte[16];
                    System.arraycopy(prayload, 80, pwd4GByte, 0, 16);
                    String pwd4G = MyUtil.ByteToString(pwd4GByte);
                    mAdapter.getData().get(17).setValue(pwd4G);

                    apn4GByte = new byte[16];
                    System.arraycopy(prayload, 96, apn4GByte, 0, 16);
                    String apn4G = MyUtil.ByteToString(apn4GByte);
                    mAdapter.getData().get(18).setValue(apn4G);

                    mAdapter.notifyDataSetChanged();
                    getDeviceInfo(WiFiMsgConstant.CONSTANT_MSG_04);
                    break;
                case WiFiMsgConstant.CONSTANT_MSG_04://获取服务器参数
                    urlByte = new byte[70];
                    System.arraycopy(prayload, 0, urlByte, 0, 70);
                    String url = MyUtil.ByteToString(urlByte);
                    mAdapter.getData().get(20).setValue(url);

                    hskeyByte = new byte[20];
                    System.arraycopy(prayload, 70, hskeyByte, 0, 20);
                    String hskey = MyUtil.ByteToString(hskeyByte);
                    mAdapter.getData().get(21).setValue(hskey);

                    heatByte = new byte[4];
                    System.arraycopy(prayload, 90, heatByte, 0, 4);
                    String heat = MyUtil.ByteToString(heatByte);
                    mAdapter.getData().get(22).setValue(heat);

                    pingByte = new byte[4];
                    System.arraycopy(prayload, 94, pingByte, 0, 4);
                    String ping = MyUtil.ByteToString(pingByte);
                    mAdapter.getData().get(23).setValue(ping);

                    intervalByte = new byte[4];
                    System.arraycopy(prayload, 98, intervalByte, 0, 4);
                    String interval = MyUtil.ByteToString(intervalByte);
                    mAdapter.getData().get(24).setValue(interval);

                    mAdapter.notifyDataSetChanged();
                    getDeviceInfo(WiFiMsgConstant.CONSTANT_MSG_05);
                    break;

                case WiFiMsgConstant.CONSTANT_MSG_05://获取充电参数
                    modeByte = new byte[1];
                    System.arraycopy(prayload, 0, modeByte, 0, 1);
                    String mode = MyUtil.ByteToString(modeByte);
                    int modeSet = Integer.parseInt(mode);
                    if (modeSet <= 0) modeSet = 1;
                    String modeValue = modeArray[modeSet - 1];
                    mAdapter.getData().get(26).setValue(modeValue);

                    maxCurrentByte = new byte[2];
                    System.arraycopy(prayload, 1, maxCurrentByte, 0, 2);
                    String maxCurrent = MyUtil.ByteToString(maxCurrentByte);
                    mAdapter.getData().get(27).setValue(maxCurrent);

                    rateByte = new byte[5];
                    System.arraycopy(prayload, 3, rateByte, 0, 5);
                    String rate = MyUtil.ByteToString(rateByte);
                    mAdapter.getData().get(28).setValue(rate);

                    tempByte = new byte[3];
                    System.arraycopy(prayload, 8, tempByte, 0, 3);
                    String temp = MyUtil.ByteToString(tempByte);
                    mAdapter.getData().get(29).setValue(temp);

                    powerByte = new byte[2];
                    System.arraycopy(prayload, 11, powerByte, 0, 2);
                    String power = MyUtil.ByteToString(powerByte);
                    mAdapter.getData().get(30).setValue(power);

                    timeByte = new byte[11];
                    System.arraycopy(prayload, 13, timeByte, 0, 11);
                    String time = MyUtil.ByteToString(timeByte);
                    mAdapter.getData().get(31).setValue(time);

                    mAdapter.notifyDataSetChanged();
                    break;
                //设置回应
                case WiFiMsgConstant.CONSTANT_MSG_11:
                case WiFiMsgConstant.CONSTANT_MSG_12:
                case WiFiMsgConstant.CONSTANT_MSG_13:
                case WiFiMsgConstant.CONSTANT_MSG_14:
                case WiFiMsgConstant.CONSTANT_MSG_15:
                    byte result = prayload[0];
                    if ((int) result == 1) {
                        getDeviceInfo(WiFiMsgConstant.CONSTANT_MSG_01);
                        T.make(getString(R.string.m243设置成功), WifiSetActivity.this);
                    } else {
                        T.make(getString(R.string.m244设置失败), WifiSetActivity.this);
                    }
                    break;
                default:
                    break;
            }
        }


    }


    @OnClick(R.id.ivLeft)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivLeft:
                sendCmdExit();
                finish();
                break;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            sendCmdExit();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /*设置语言*/
    private void setLanguage() {
        List<String> list = Arrays.asList(lanArray);
        OptionsPickerView<String> pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                final String tx = list.get(options1);
                String pos = String.valueOf(options1 + 1);
                byte[] bytes = pos.trim().getBytes();
                if (bytes.length > 1) {
                    T.make(getString(R.string.m286输入值超出规定长度), WifiSetActivity.this);
                    return;
                }
                lanByte = new byte[1];
                System.arraycopy(bytes, 0, lanByte, 0, bytes.length);
                setInfo();
            }
        })
                .setTitleText(getString(R.string.m260语言))
                .setTitleBgColor(0xffffffff)
                .setTitleColor(0xff333333)
                .setSubmitColor(0xff333333)
                .setCancelColor(0xff999999)
                .setBgColor(0xffffffff)
                .setTitleSize(22)
                .setTextColorCenter(0xff333333)
                .build();
        pvOptions.setPicker(list);
        pvOptions.show();
    }


    /*设置rcd*/
    private void setRcd() {
        List<String> list = Arrays.asList(rcdArray);
        OptionsPickerView<String> pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                final String tx = list.get(options1);
                String pos = String.valueOf(options1 + 1);
                byte[] bytes = pos.trim().getBytes();
                if (bytes.length > 1) {
                    T.make(getString(R.string.m286输入值超出规定长度), WifiSetActivity.this);
                    return;
                }
                rcdByte = new byte[1];
                System.arraycopy(bytes, 0, rcdByte, 0, bytes.length);
                setInfo();
            }
        })
                .setTitleText(getString(R.string.m265RCD保护值))
                .setTitleBgColor(0xffffffff)
                .setTitleColor(0xff333333)
                .setSubmitColor(0xff333333)
                .setCancelColor(0xff999999)
                .setBgColor(0xffffffff)
                .setTitleSize(22)
                .setTextColorCenter(0xff333333)
                .build();
        pvOptions.setPicker(list);
        pvOptions.show();
    }


    /*设置模式*/
    private void setMode() {
        List<String> list = Arrays.asList(modeArray);
        OptionsPickerView<String> pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                final String tx = list.get(options1);
                String pos = String.valueOf(options1 + 1);
                byte[] bytes = pos.trim().getBytes();
                if (bytes.length > 1) {
                    T.make(getString(R.string.m286输入值超出规定长度), WifiSetActivity.this);
                    return;
                }
                modeByte = new byte[1];
                System.arraycopy(bytes, 0, modeByte, 0, bytes.length);
                setCharging();
            }
        })
                .setTitleText(getString(R.string.m154充电模式))
                .setTitleBgColor(0xffffffff)
                .setTitleColor(0xff333333)
                .setSubmitColor(0xff333333)
                .setCancelColor(0xff999999)
                .setBgColor(0xffffffff)
                .setTitleSize(22)
                .setTextColorCenter(0xff333333)
                .build();
        pvOptions.setPicker(list);
        pvOptions.show();
    }


    /**
     * 弹出时间选择器
     */
    public void showTimePickView(boolean isEnd) {
        Calendar selectedDate = Calendar.getInstance();//系统当前时间
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        String tittleText;
        if (isEnd) {
            tittleText = getString(R.string.m282结束时间);
        } else {
            tittleText = getString(R.string.m283选择时间);
        }
        TimePickerView pvCustomTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", getResources().getConfiguration().locale);
                String time = sdf.format(date);
                if (isEnd) {
                    endTime = time;
                    String[] start = startTime.split(":");
                    String[] end = endTime.split(":");
                    int statValue = Integer.parseInt(start[0]) * 60 + Integer.parseInt(start[1]);
                    int endValue = Integer.parseInt(end[0]) * 60 + Integer.parseInt(end[1]);
                   /* if (statValue >= endValue) {
                        T.make(getString(R.string.m285开始时间不能大于结束时间), WifiSetActivity.this);
                    } else {*/
                    String chargingTime = startTime + "-" + endTime;
                    byte[] bytes = chargingTime.trim().getBytes();
                    if (bytes.length > 11) {
                        T.make(getString(R.string.m286输入值超出规定长度), WifiSetActivity.this);
                        return;
                    }
                    timeByte = new byte[11];
                    System.arraycopy(bytes, 0, timeByte, 0, bytes.length);
                    setCharging();
//                    }
                } else {
                    startTime = time;
                    showTimePickView(true);
                }
            }
        })
                .setType(new boolean[]{false, false, false, true, true, false})// 默认全部显示
                .setCancelText(getString(R.string.m7取消))//取消按钮文字
                .setSubmitText(getString(R.string.m9确定))//确认按钮文字
                .setContentTextSize(18)
                .setTitleSize(20)//标题文字大小
                .setTitleText(tittleText)//标题文字
                .setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(true)//是否循环滚动
                .setTitleColor(0xff333333)//标题文字颜色
                .setSubmitColor(0xff333333)//确定按钮文字颜色
                .setCancelColor(0xff999999)//取消按钮文字颜色
                .setTitleBgColor(0xffffffff)//标题背景颜色 Night mode
                .setBgColor(0xffffffff)//滚轮背景颜色 Night mode
                .setTextColorCenter(0xff333333)
                .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
                .setRangDate(startDate, endDate)//起始终止年月日设定
                .setLabel("", "", "", "时", "分", "")//默认设置为年月日时分秒
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(false)//是否显示为对话框样式
                .build();
        pvCustomTime.show();
    }

}
