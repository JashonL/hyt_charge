package com.growatt.chargingpile.activity;

import android.content.Intent;
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
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.adapter.ParamsSetAdapter;
import com.growatt.chargingpile.adapter.WifiSetAdapter;
import com.growatt.chargingpile.bean.LockBean;
import com.growatt.chargingpile.bean.ParamsSetBean;
import com.growatt.chargingpile.bean.SolarBean;
import com.growatt.chargingpile.bean.WiFiRequestMsgBean;
import com.growatt.chargingpile.bean.WifiParseBean;
import com.growatt.chargingpile.bean.WifiSetBean;
import com.growatt.chargingpile.util.Cons;
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
import butterknife.Unbinder;

public class WifiSetActivity extends BaseActivity {

    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.ivLeft)
    ImageView ivLeft;
    @BindView(R.id.tvRight)
    TextView tvRight;
    @BindView(R.id.headerView)
    LinearLayout headerView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.srl_pull)
    SwipeRefreshLayout srlPull;

    private WifiSetAdapter mAdapter;
    private List<MultiItemEntity> list = new ArrayList<>();
    public String[] lanArray;
    public String[] rcdArray;
    public String[] modeArray;
    public String[] enableArray;
    public String[] wiringArray;
    public String[] solarArrray;
    public String[] gunArrray;
    public String[] lockArrray;

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
    private byte[] versionByte;
    private int infoLength;
    //网络相关设置
    private byte[] ipByte;
    private byte[] gatewayByte;
    private byte[] maskByte;
    private byte[] macByte;
    private byte[] dnsByte;
    private int internetLength;
    //wifi相关设置
    private byte[] ssidByte;
    private byte[] wifiKeyByte;
    private byte[] bltNameByte;
    private byte[] bltPwdByte;
    private byte[] name4GByte;
    private byte[] pwd4GByte;
    private byte[] apn4GByte;
    private int wifiLength;
    //url相关配置
    private byte[] urlByte;
    private byte[] hskeyByte;
    private byte[] heatByte;
    private byte[] pingByte;
    private byte[] intervalByte;
    private int urlLength;
    //充电参数
    private byte[] modeByte;
    private byte[] maxCurrentByte;
    private byte[] rateByte;
    private byte[] tempByte;
    private byte[] powerByte;
    private byte[] timeByte;
    private byte[] chargingEnableByte;
    private byte[] powerdistributionByte;
    private byte[] wiringByte;
    private byte[] solarByte;
    private byte[] solarCurrentByte;
    //    private byte[] currentByte;
    private byte[] ammeterByte;
    private int chargingLength;
    //获取电子锁命令
    private byte[] lockByte;
    private int lockLength;

    //加密密钥
    private byte[] oldKey;
    private byte[] newKey;


    //是否已经连接
    private boolean isConnected = false;

    private String tips;

    private boolean isEditInfo = false;
    private boolean isEditInterNet = false;
    private boolean isEditWifi = false;
    private boolean isEditUrl = false;
    private boolean isEditCharging = false;


    private WifiParseBean initPileSetBean;

    private List<SolarBean> solarBeans;
    private List<LockBean> lockBeans;


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
                    this.postDelayed(() -> sendCmdConnect(), 3500);
                    break;
                case 100://恢复按钮点击

                    break;
                case 101:
                    connectSendMsg();
                    break;
                default:

                    break;
            }
            if (srlPull != null)
                srlPull.setRefreshing(false);
        }
    };
    private Unbinder bind;
    private String[] keys;
    private String[] keySfields;
    private List<String> noConfigKeys;
    private boolean isVerified = false;//是否已验证密码
    private String password;

    private int gunPos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_set);
        bind = ButterKnife.bind(this);
        createNewKey();
        initIntent();
        initHeaderView();
        initResource();
        initRecyclerView();
        connectSendMsg();
        setOnclickListener();
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
        tvRight.setText(R.string.m182保存);
        tvRight.setTextColor(ContextCompat.getColor(this, R.color.title_1));
        srlPull.setColorSchemeColors(ContextCompat.getColor(this, R.color.maincolor_1));
        srlPull.setOnRefreshListener(this::refresh);
    }


    private void initRecyclerView() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new WifiSetAdapter(list);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(mAdapter);
    }


    private void initResource() {
       /* keys = new String[]{
                getString(R.string.m255设备信息参数设置), getString(R.string.m146充电桩ID), getString(R.string.m260语言), getString(R.string.m264读卡器秘钥), getString(R.string.m265RCD保护值), getString(R.string.m296版本号),
                getString(R.string.m256设备以太网参数设置), getString(R.string.m156充电桩IP), getString(R.string.m157网关), getString(R.string.m158子网掩码), getString(R.string.m159网络MAC地址), getString(R.string.m161DNS地址),
                getString(R.string.m257设备账号密码参数设置), getString(R.string.m266Wifi名称), getString(R.string.m267Wifi密码), getString(R.string.m268蓝牙名称), getString(R.string.m269蓝牙密码), getString(R.string.m2704G用户名), getString(R.string.m2714G密码), getString(R.string.m2724GAPN),
                getString(R.string.m258设备服务器参数设置), getString(R.string.m160服务器URL), getString(R.string.m273握手登录授权秘钥), getString(R.string.m274心跳间隔时间), getString(R.string.m275PING间隔时间), getString(R.string.m276表计上传间隔时间),
                getString(R.string.m259设备充电参数设置), getString(R.string.m154充电模式), getString(R.string.m277电桩最大输出电流), getString(R.string.m152充电费率), getString(R.string.m278保护温度), getString(R.string.m279外部监测最大输入功率),
                getString(R.string.m280允许充电时间), getString(R.string.m297峰谷充电使能), getString(R.string.m298功率分配使能), getString(R.string.m外部电流采样接线方式), getString(R.string.mSolar模式), getString(R.string.m电表设备地址),
                getString(R.string.m电桩电子锁)
        };*/

        keys = new String[]{
                getString(R.string.m255设备信息参数设置), getString(R.string.m146充电桩ID), getString(R.string.m260语言), getString(R.string.m264读卡器秘钥), getString(R.string.m265RCD保护值), getString(R.string.m296版本号),
                getString(R.string.m256设备以太网参数设置), getString(R.string.m156充电桩IP), getString(R.string.m157网关), getString(R.string.m158子网掩码), getString(R.string.m159网络MAC地址), getString(R.string.m161DNS地址),
                getString(R.string.m257设备账号密码参数设置), getString(R.string.m266Wifi名称), getString(R.string.m267Wifi密码),getString(R.string.m2704G用户名), getString(R.string.m2714G密码), getString(R.string.m2724GAPN),
                getString(R.string.m258设备服务器参数设置), getString(R.string.m160服务器URL), getString(R.string.m273握手登录授权秘钥), getString(R.string.m274心跳间隔时间), getString(R.string.m275PING间隔时间), getString(R.string.m276表计上传间隔时间),
                getString(R.string.m259设备充电参数设置), getString(R.string.m154充电模式), getString(R.string.m277电桩最大输出电流), getString(R.string.m152充电费率), getString(R.string.m278保护温度), getString(R.string.m279外部监测最大输入功率),
                getString(R.string.m280允许充电时间), getString(R.string.m297峰谷充电使能), getString(R.string.m298功率分配使能), getString(R.string.m外部电流采样接线方式), getString(R.string.mSolar模式), getString(R.string.m电表设备地址),
                getString(R.string.m电桩电子锁)
        };

        keySfields = new String[]{ "", "chargeId", "G_ChargerLanguage","G_CardPin", "G_RCDProtection", "G_Version",
                "", "ip", "gateway","mask",  "mac", "dns",
                "", "G_WifiSSID","G_WifiPassword","G_4GUserName", "G_4GPassword", "G_4GAPN",
                "",  "host", "G_Authentication", "G_HearbeatInterval", "G_WebSocketPingInterval", "G_MeterValueInterval",
               "", "G_ChargerMode",  "G_MaxCurrent", "rate","G_MaxTemperature", "G_ExternalLimitPower",
                "G_AutoChargeTime","G_PeakValleyEnable",  "G_ExternalLimitPowerEnable", "G_ExternalSamplingCurWring", "G_SolarMode", "G_PowerMeterAddr",
             ""};
        if (Cons.getNoConfigBean() != null) {
            noConfigKeys = Cons.getNoConfigBean().getSfield();
            password=Cons.getNoConfigBean().getPassword();
        }
        if (noConfigKeys == null) noConfigKeys = new ArrayList<>();
        //初始化所有设置项
        initPileSetBean = new WifiParseBean();
        for (int i = 0; i < keys.length; i++) {
            WifiSetBean bean = new WifiSetBean();
            bean.setIndex(i);
            if (i == 0 || i == 6 || i == 12 || i == 18 || i == 24 || i == 36) {
                bean.setTitle(keys[i]);
                bean.setType(WifiSetAdapter.PARAM_TITILE);
            } else if (i == 1 || i == 5 || i == 10) {
                bean.setType(WifiSetAdapter.PARAM_ITEM_CANT_CLICK);
                bean.setKey(keys[i]);
                bean.setValue("");
            } else {
                bean.setType(WifiSetAdapter.PARAM_ITEM);
                bean.setKey(keys[i]);
                bean.setValue("");
            }
            bean.setSfield(keySfields[i]);
            if (noConfigKeys.contains(bean.getSfield())) {
                bean.setAuthority(false);
            } else {
                bean.setAuthority(true);
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
        enableArray = new String[]{getString(R.string.m300禁止), getString(R.string.m299使能)};
        wiringArray = new String[]{getString(R.string.mCT), getString(R.string.m电表)};
        solarArrray = new String[]{"FAST", "ECO", "ECO+"};
        gunArrray = new String[]{getString(R.string.m110A枪), getString(R.string.m111B枪), getString(R.string.m112C枪)};
        lockArrray = new String[]{getString(R.string.m已解锁), getString(R.string.m已锁住)};
        solarBeans = new ArrayList<>();
        lockBeans = new ArrayList<>();
    }


    /*建立TCP连接*/
    private void connectSendMsg() {
        Mydialog.Show(this);
        connectServer();
    }


    private void refresh() {
        if (!isConnected) connectSendMsg();
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
            MultiItemEntity multiItemEntity = mAdapter.getData().get(position);
            if (multiItemEntity == null) return;
            int itemType = multiItemEntity.getItemType();
            if (itemType == WifiSetAdapter.PARAM_ITEM) {
                WifiSetBean bean = (WifiSetBean) mAdapter.getData().get(position);
                if (!bean.isAuthority()&&!isVerified){//如果是不允许设置，又没有验证密码
                    showInputPassword();
                }else {
                    int index = bean.getIndex();
                    switch (index) {
                        case 2://设置语言
                            setLanguage();
                            break;
                        case 4:
                            setRcd();
                            break;
                        case 25:
                            setMode();
                            break;
                        case 30:
                            Intent intent = new Intent(this, TimeSelectActivity.class);
                            intent.putExtra("start", startTime);
                            intent.putExtra("end", endTime);
                            startActivityForResult(intent, 100);
                            break;
                        case 31:
                            if (chargingLength > 24)
                                setEnable(31);
                            else toast(R.string.m请先升级充电桩);
                            break;
                        case 32:
                            if (chargingLength > 25)
                                setEnable(32);
                            else toast(R.string.m请先升级充电桩);
                            break;
                        case 33:
                            if (chargingLength > 26)
                                setWiring();
                            else toast(R.string.m请先升级充电桩);
                            break;
                        case 34:
                            if (chargingLength > 27)
                                setSolarMode();
                            else toast(R.string.m请先升级充电桩);
                            break;
                        case 35://
                            if (chargingLength > 30)
                                inputEdit(index, String.valueOf(bean.getValue()));
                            else toast(R.string.m请先升级充电桩);
                            break;
                        default:
                            inputEdit(index, String.valueOf(bean.getValue()));
                            break;
                    }
                }
            } else if (itemType == WifiSetAdapter.PARAM_ITEM_SOLAR) {//设置solar限制
                ParamsSetBean bean = (ParamsSetBean) mAdapter.getData().get(34);
                if (!bean.isAuthority()&&!isVerified){//如果是不允许设置，又没有验证密码
                    showInputPassword();
                }else {
                    setECOLimit();
                }
            } else if (itemType == WifiSetAdapter.PARAM_ITEM_LOCK) {
                if (lockLength > 0) {
                    LockBean bean = (LockBean) mAdapter.getData().get(position);
                    gunPos=bean.getIndex();
                    setLock(bean.getGunId());
                } else toast(R.string.m请先升级充电桩);
            }

        });
    }


    private void setECOLimit() {
        tips = "1~8(A)";
        WifiSetBean bean = (WifiSetBean) mAdapter.getData().get(34);
        SolarBean subItem = (SolarBean) bean.getSubItem(0);
        String value = subItem.getValue();
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
                    boolean numeric_eco = MyUtil.isNumberiZidai(text);
                    if (!numeric_eco) {
                        T.make(getString(R.string.m177输入格式不正确), this);
                        return;
                    }

                    if (Integer.parseInt(text) < 1 || Integer.parseInt(text) > 8) {
                        T.make(getString(R.string.m290超出设置范围) + tips, WifiSetActivity.this);
                        return;
                    }

                    if (bytes.length > 1) {
                        T.make(getString(R.string.m286输入值超出规定长度), this);
                        return;
                    }
                    System.arraycopy(bytes, 0, solarCurrentByte, 0, bytes.length);
                    isEditCharging = true;
                    subItem.setValue(text);
                    mAdapter.notifyDataSetChanged();
                })
                .show(this.getSupportFragmentManager());

    }


    /**
     * 弹框输入修改内容
     * item 修改项
     */
    private void inputEdit(final int key, final String value) {
        tips = "";
        switch (key) {
            case 21:
            case 22:
            case 23://心跳间隔时间、PING间隔时间、表记上传时间
                tips = "5~300(s)";
                break;
            case 26://输出最大电流
                tips = getString(R.string.m291设定值不能小于);
                break;
            case 28://保护温度
                tips = "65~85(℃)";
                break;
            case 29://外部检测最大输入功率
                tips = getString(R.string.m291设定值不能小于);
                break;
            case 35:
                tips = getString(R.string.m输入字符长度不能超出) + " " + 3;
                break;
            default:
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
                            boolean letterDigit_card = MyUtil.isLetterDigit2(text);
                            if (!letterDigit_card) {
                                T.make(getString(R.string.m177输入格式不正确), this);
                                return;
                            }
                            if (bytes.length > 6) {
                                T.make(getString(R.string.m286输入值超出规定长度), this);
                                return;
                            }
                            cardByte = new byte[6];
                            System.arraycopy(bytes, 0, cardByte, 0, bytes.length);
                            isEditInfo = true;
                            break;
                        case 7:
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
                            isEditInterNet = true;
                            break;
                        case 8:
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
                            isEditInterNet = true;
                            break;
                        case 9:
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
                            isEditInterNet = true;
                            break;
                        case 10:
                            boolean letterDigit_mac = MyUtil.isLetterDigit2(text);
                            if (!letterDigit_mac) {
                                T.make(getString(R.string.m177输入格式不正确), this);
                                return;
                            }
                            if (bytes.length > 17) {
                                T.make(getString(R.string.m286输入值超出规定长度), this);
                                return;
                            }
                            macByte = new byte[17];
                            System.arraycopy(bytes, 0, macByte, 0, bytes.length);
                            isEditInterNet = true;
                            break;
                        case 11:
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
                            isEditInterNet = true;
                            break;
                        case 13:
                            boolean letterDigit_ssid = MyUtil.isWiFiLetter(text);
                            if (!letterDigit_ssid) {
                                T.make(getString(R.string.m177输入格式不正确), this);
                                return;
                            }

                            if (bytes.length > 16) {
                                T.make(getString(R.string.m286输入值超出规定长度), this);
                                return;
                            }
                            ssidByte = new byte[16];
                            System.arraycopy(bytes, 0, ssidByte, 0, bytes.length);
                            isEditWifi = true;
                            break;
                        case 14:
                            boolean letterDigit_key = MyUtil.isWiFiLetter(text);
                            if (!letterDigit_key) {
                                T.make(getString(R.string.m177输入格式不正确), this);
                                return;
                            }
                            if (bytes.length > 16) {
                                T.make(getString(R.string.m286输入值超出规定长度), this);
                                return;
                            }
                            wifiKeyByte = new byte[16];
                            System.arraycopy(bytes, 0, wifiKeyByte, 0, bytes.length);
                            isEditWifi = true;
                            break;

               /*         case 15:
                            boolean letterDigit_bltname = MyUtil.isWiFiLetter(text);
                            if (!letterDigit_bltname) {
                                T.make(getString(R.string.m177输入格式不正确), this);
                                return;
                            }
                            if (bytes.length > 16) {
                                T.make(getString(R.string.m286输入值超出规定长度), this);
                                return;
                            }
                            bltNameByte = new byte[16];
                            System.arraycopy(bytes, 0, bltNameByte, 0, bytes.length);
                            isEditWifi = true;
                            break;
                        case 16:
                            boolean letterDigit_bltpwd = MyUtil.isWiFiLetter(text);
                            if (!letterDigit_bltpwd) {
                                T.make(getString(R.string.m177输入格式不正确), this);
                                return;
                            }
                            if (bytes.length > 16) {
                                T.make(getString(R.string.m286输入值超出规定长度), this);
                                return;
                            }
                            bltPwdByte = new byte[16];
                            System.arraycopy(bytes, 0, bltPwdByte, 0, bytes.length);
                            isEditWifi = true;
                            break;*/
                        case 15:
                            boolean letterDigit_4gname = MyUtil.isWiFiLetter(text);
                            if (!letterDigit_4gname) {
                                T.make(getString(R.string.m177输入格式不正确), this);
                                return;
                            }
                            if (bytes.length > 16) {
                                T.make(getString(R.string.m286输入值超出规定长度), this);
                                return;
                            }
                            name4GByte = new byte[16];
                            System.arraycopy(bytes, 0, name4GByte, 0, bytes.length);
                            isEditWifi = true;
                            break;
                        case 16:
                            boolean letterDigit_4gpwd = MyUtil.isWiFiLetter(text);
                            if (!letterDigit_4gpwd) {
                                T.make(getString(R.string.m177输入格式不正确), this);
                                return;
                            }

                            if (bytes.length > 16) {
                                T.make(getString(R.string.m286输入值超出规定长度), this);
                                return;
                            }
                            pwd4GByte = new byte[16];
                            System.arraycopy(bytes, 0, pwd4GByte, 0, bytes.length);
                            isEditWifi = true;
                            break;
                        case 17:
                            boolean letterDigit_4gapn = MyUtil.isLetterDigit2(text);
                            if (!letterDigit_4gapn) {
                                T.make(getString(R.string.m177输入格式不正确), this);
                                return;
                            }

                            if (bytes.length > 16) {
                                T.make(getString(R.string.m286输入值超出规定长度), this);
                                return;
                            }
                            apn4GByte = new byte[16];
                            System.arraycopy(bytes, 0, apn4GByte, 0, bytes.length);
                            isEditWifi = true;
                            break;

                        case 19:
                            if (bytes.length > 70) {
                                T.make(getString(R.string.m286输入值超出规定长度), this);
                                return;
                            }
                            urlByte = new byte[70];
                            System.arraycopy(bytes, 0, urlByte, 0, bytes.length);
                            isEditUrl = true;
                            break;
                        case 20:
                            boolean letterDigit_hskey = MyUtil.isLetterDigit2(text);
                            if (!letterDigit_hskey) {
                                T.make(getString(R.string.m177输入格式不正确), this);
                                return;
                            }
                            if (bytes.length > 20) {
                                T.make(getString(R.string.m286输入值超出规定长度), this);
                                return;
                            }
                            hskeyByte = new byte[20];
                            System.arraycopy(bytes, 0, hskeyByte, 0, bytes.length);
                            isEditUrl = true;
                            break;
                        case 21:
                            boolean numeric_heat = MyUtil.isNumberiZidai(text);
                            if (!numeric_heat) {
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
                            isEditUrl = true;
                            break;
                        case 22:
                            boolean numeric_ping = MyUtil.isNumberiZidai(text);
                            if (!numeric_ping) {
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
                            isEditUrl = true;
                            break;
                        case 23:
                            boolean numeric_interval = MyUtil.isNumberiZidai(text);
                            if (!numeric_interval) {
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
//                            setUrl();
                            isEditUrl = true;
                            break;

                        case 26:
                            boolean numeric_maxcurrent = MyUtil.isNumberiZidai(text);
                            if (!numeric_maxcurrent) {
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
                            isEditCharging = true;
                            break;
                        case 27:
                            boolean numeric_rate = MyUtil.isNumeric(text);
                            if (!numeric_rate) {
                                T.make(getString(R.string.m177输入格式不正确), this);
                                return;
                            }
                            if (bytes.length > 5) {
                                T.make(getString(R.string.m286输入值超出规定长度), this);
                                return;
                            }
                            rateByte = new byte[5];
                            System.arraycopy(bytes, 0, rateByte, 0, bytes.length);
                            isEditCharging = true;
                            break;
                        case 28:
                            boolean numeric_temp = MyUtil.isNumberiZidai(text);
                            if (!numeric_temp) {
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
                            isEditCharging = true;
                            break;
                        case 29:
                            boolean numeric_power = MyUtil.isNumberiZidai(text);
                            if (!numeric_power) {
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
                            isEditCharging = true;
                            break;
                        case 35:
                            boolean numeric_ammeter = MyUtil.isNumberiZidai(text);
                            if (!numeric_ammeter) {
                                T.make(getString(R.string.m177输入格式不正确), this);
                                return;
                            }
                            if (bytes.length > 3) {
                                T.make(getString(R.string.m286输入值超出规定长度) + " " + 3, this);
                                return;
                            }
                            ammeterByte = new byte[12];
                            System.arraycopy(bytes, 0, ammeterByte, 0, bytes.length);
                            isEditCharging = true;
                            break;
                    }
                    setBean(key, text);
                    mAdapter.notifyDataSetChanged();
                })
                .show(this.getSupportFragmentManager());
    }


    private void setBean(int index, String value) {
        switch (index) {
            case 1:
                initPileSetBean.setDevId(value);
                break;
            case 2:
                initPileSetBean.setLan(value);
                break;
            case 3:
                initPileSetBean.setCard(value);
                break;
            case 4:
                initPileSetBean.setRcd(value);
                break;
            case 5:
                initPileSetBean.setVersion(value);
                break;
            case 7:
                initPileSetBean.setIp(String.valueOf(value));
                break;
            case 8:
                initPileSetBean.setGateway(value);
                break;
            case 9:
                initPileSetBean.setMask(value);
                break;
            case 10:
                initPileSetBean.setMac(value);
                break;
            case 11:
                initPileSetBean.setDns(value);
                break;
            case 13:
                initPileSetBean.setSsid(value);
                break;
            case 14:
                initPileSetBean.setWifiKey(value);
                break;
 /*           case 15:
                initPileSetBean.setBltName(value);
                break;
            case 16:
                initPileSetBean.setBltPwd(value);
                break;*/
            case 15:
                initPileSetBean.setName4G(value);
                break;
            case 16:
                initPileSetBean.setPwd4G(value);
                break;
            case 17:
                initPileSetBean.setApn4G(value);
                break;
            case 19:
                initPileSetBean.setUrl(value);
                break;
            case 20:
                initPileSetBean.setHskey(value);
                break;
            case 21:
                initPileSetBean.setHeat(value);
                break;
            case 22:
                initPileSetBean.setPing(value);
                break;
            case 23:
                initPileSetBean.setInterval(value);
                break;
            case 25:
                initPileSetBean.setMode(value);
                break;
            case 26:
                initPileSetBean.setMaxCurrent(value);
                break;
            case 27:
                initPileSetBean.setRate(value);
                break;
            case 28:
                initPileSetBean.setTemp(value);
                break;
            case 29:
                initPileSetBean.setPower(value);
                break;
            case 30:
                initPileSetBean.setTime(value);
                break;
            case 31:
                initPileSetBean.setChargingEnable(value);
                break;
            case 32:
                initPileSetBean.setPowerdistribution(value);
                break;
            case 33:
                initPileSetBean.setWiring(value);
                break;
            case 34:
                initPileSetBean.setSolar(value);
                break;
            case 35:
                initPileSetBean.setAmmeter(value);
                break;
            default:
                break;
        }
        refreshRv();

    }

    private void refreshRv() {
        List<MultiItemEntity> newlist = new ArrayList<>();
        for (int i = 0; i < keys.length; i++) {
            WifiSetBean bean = new WifiSetBean();
            bean.setIndex(i);
            switch (i) {
                case 0:
                case 6:
                case 12:
                case 18:
                case 24:
                    bean.setTitle(keys[i]);
                    bean.setType(WifiSetAdapter.PARAM_TITILE);
                    break;
                case 36:
                    bean.setTitle(keys[i]);
                    bean.setType(WifiSetAdapter.PARAM_TITILE);
                    if (lockBeans.size() == 0) {
                        LockBean lockBean = new LockBean();
                        lockBean.setValue("");
                        lockBean.setGunId(1);
                        lockBean.setIndex(0);
                        lockBean.setType(WifiSetAdapter.PARAM_ITEM_LOCK);
                        lockBean.setKey(gunArrray[0]);
                        lockBeans.add(lockBean);
                    }
                    for (int j = 0; j < lockBeans.size(); j++) {
                        LockBean lockBean = lockBeans.get(j);
                        bean.addSubItem(lockBean);
                    }
                    break;
                case 1:
                    bean.setType(WifiSetAdapter.PARAM_ITEM_CANT_CLICK);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getDevId());
                    break;
                case 2:
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getLan());
                    break;

                case 3:
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getCard());
                    break;
                case 4:
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getRcd());
                    break;
                case 5:
                    bean.setType(WifiSetAdapter.PARAM_ITEM_CANT_CLICK);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getVersion());
                    break;

                case 7:
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getIp());
                    break;
                case 8:
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getGateway());
                    break;
                case 9:
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getMask());
                    break;
                case 10:
                    bean.setType(WifiSetAdapter.PARAM_ITEM_CANT_CLICK);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getMac());
                    break;
                case 11:
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getDns());
                    break;

                case 13:
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getSsid());
                    break;
                case 14:
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getWifiKey());
                    break;
     /*           case 15:
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getBltName());
                    break;
                case 16:
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getBltPwd());
                    break;*/
                case 15:
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getName4G());
                    break;
                case 16:
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getPwd4G());
                    break;
                case 17:
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getApn4G());
                    break;

                case 19:
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getUrl());
                    break;
                case 20:
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getHskey());
                    break;
                case 21:
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getHeat());
                    break;
                case 22:
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getPing());
                    break;
                case 23:
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getInterval());
                    break;
                case 25:
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getMode());
                    break;
                case 26:
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getMaxCurrent());
                    break;
                case 27:
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getRate());
                    break;
                case 28:
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getTemp());
                    break;
                case 29:
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getPower());
                    break;
                case 30:
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getTime());
                    break;
                case 31:
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getChargingEnable());
                    break;
                case 32:
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getPowerdistribution());
                    break;
                case 33:
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getWiring());
                    break;
                case 34:
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getSolar());
                    for (int j = 0; j < solarBeans.size(); j++) {
                        SolarBean solarBean = solarBeans.get(j);
                        solarBean.setType(ParamsSetAdapter.PARAM_ITEM_SOLAR);
                        solarBean.setSfield(keySfields[i]);
                        if (noConfigKeys.contains(solarBean.getSfield())) {
                            solarBean.setAuthority(false);
                        } else {
                            solarBean.setAuthority(true);
                        }
                        bean.addSubItem(solarBean);
                    }
                    break;
                case 35:
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getAmmeter());
                    break;
            }
            bean.setSfield(keySfields[i]);
            if (noConfigKeys.contains(bean.getSfield())) {
                bean.setAuthority(false);
            } else {
                bean.setAuthority(true);
            }
            newlist.add(bean);
            mAdapter.setNewData(newlist);
            mAdapter.expandAll();
        }
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
        byte len = (byte) infoLength;
        byte[] prayload = new byte[infoLength];

        if (infoLength > 28) {
            if (idByte == null || lanByte == null || cardByte == null || rcdByte == null || versionByte == null) {
                T.make(R.string.m244设置失败, this);
                return;
            }
        } else {
            if (idByte == null || lanByte == null || cardByte == null || rcdByte == null) {
                T.make(R.string.m244设置失败, this);
                return;
            }
        }

        //id
        System.arraycopy(idByte, 0, prayload, 0, idByte.length);
        //语言
        System.arraycopy(lanByte, 0, prayload, idByte.length, lanByte.length);
        //card
        System.arraycopy(cardByte, 0, prayload, idByte.length + lanByte.length, cardByte.length);
        //rcd
        System.arraycopy(rcdByte, 0, prayload, idByte.length + lanByte.length + cardByte.length, rcdByte.length);
        if (infoLength > 28) {
            //版本号
            System.arraycopy(versionByte, 0, prayload, idByte.length + lanByte.length + cardByte.length + rcdByte.length, versionByte.length);
        }
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
        LogUtil.i("信息参数设置：" + SmartHomeUtil.bytesToHexString(setInfo));
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
        LogUtil.i("网络设置：" + SmartHomeUtil.bytesToHexString(setInterNet));
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
        LogUtil.i("wif设置：" + SmartHomeUtil.bytesToHexString(setWifi));
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
        LogUtil.i("设置url：" + SmartHomeUtil.bytesToHexString(setUrl));
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
        byte len = (byte) chargingLength;
        byte[] prayload = new byte[chargingLength];

        if (modeByte == null || maxCurrentByte == null || rateByte == null || tempByte == null
                || powerByte == null || timeByte == null) {
            T.make(R.string.m244设置失败, this);
            return;
        }

        if (chargingLength > 24) {//新增使能
            if (chargingEnableByte == null || powerdistributionByte == null) {
                T.make(R.string.m244设置失败, this);
                return;
            }
        }
        if (chargingLength > 26) {//外部电流接线方式
            if (wiringByte == null) {
                T.make(R.string.m244设置失败, this);
                return;
            }
        }
        if (chargingLength > 27) {//solar
            if (solarByte == null) {
                T.make(R.string.m244设置失败, this);
                return;
            }
        }

        if (chargingLength > 29) {
            String solarMode = MyUtil.ByteToString(solarByte);
            int modeIndext;
            try {
                modeIndext = Integer.parseInt(solarMode);
            } catch (NumberFormatException e) {
                modeIndext = 0;
            }
            if (modeIndext < 0) modeIndext = 1;
            if (modeIndext == 2) {
                if (solarCurrentByte == null) {
                    T.make(R.string.m244设置失败, this);
                }
            }
        }

        if (chargingLength > 30) {//电表地址
            if (ammeterByte == null) {
                T.make(R.string.m244设置失败, this);
                return;
            }
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
        if (chargingLength > 24) {
            //峰谷充电使能
            System.arraycopy(chargingEnableByte, 0, prayload, 24, chargingEnableByte.length);
            //功率分配使能
            System.arraycopy(powerdistributionByte, 0, prayload, 25, powerdistributionByte.length);
        }

        if (chargingLength > 26) {
            //外部电流采样接线方式（0:CT,1:电表）
            System.arraycopy(wiringByte, 0, prayload, 26, wiringByte.length);
        }
        if (chargingLength > 27) {//solar
            System.arraycopy(solarByte, 0, prayload, 27, solarByte.length);
        }

        if (chargingLength > 29) {
            String solarMode = MyUtil.ByteToString(solarByte);
            int modeIndext;
            try {
                modeIndext = Integer.parseInt(solarMode);
            } catch (NumberFormatException e) {
                modeIndext = 0;
            }
            if (modeIndext < 0) modeIndext = 1;
            if (modeIndext == 2) {
                System.arraycopy(solarCurrentByte, 0, prayload, 28, solarCurrentByte.length);
            }
        }
        if (chargingLength > 30) {
            System.arraycopy(ammeterByte, 0, prayload, 30, ammeterByte.length);
        }
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
        LogUtil.i("设置充电参数：" + SmartHomeUtil.bytesToHexString(setCharging));
    }


    //解锁
    private void setUnLoack() {
        //头1
        byte frame1 = WiFiMsgConstant.FRAME_1;
        //头2
        byte frame2 = WiFiMsgConstant.FRAME_2;
        //交流直流
        byte devType = this.devType;
        //加密方式
        byte encryption = this.encryption;
        //指令
        byte cmd = WiFiMsgConstant.CONSTANT_MSG_16;

        /*****有效数据*****/
        byte len = (byte) 1;
        byte[] prayload = new byte[1];
        if (lockByte == null) {
            T.make(R.string.m失败, this);
            return;
        }

        System.arraycopy(lockByte, 0, prayload, 0, lockByte.length);

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
        LogUtil.i("解锁：" + SmartHomeUtil.bytesToHexString(setUrl));
    }


    private void back() {
        if (isEditInfo || isEditInterNet || isEditWifi || isEditUrl || isEditCharging) {//未保存
            new CircleDialog.Builder()
                    .setTitle(getString(R.string.m27温馨提示))
                    .setWidth(0.8f)
                    .setText(getString(R.string.m设置未保存))
                    .setPositive(getString(R.string.m9确定), v -> {
                        sendCmdExit();
                        finish();
                    })
                    .setNegative(getString(R.string.m7取消), null)
                    .show(getSupportFragmentManager());
        } else {
            sendCmdExit();
            finish();
        }
    }


    /**
     * 保存设置
     */
    private void save() {
        if (!isEditInfo && !isEditInterNet && !isEditWifi && !isEditUrl && !isEditCharging) {//未更改
            new CircleDialog.Builder()
                    .setTitle(getString(R.string.m27温馨提示))
                    .setWidth(0.8f)
                    .setText(getString(R.string.m304设置未做任何更改))
                    .setPositive(getString(R.string.m9确定), v -> {
                        sendCmdExit();
                        finish();
                    })
                    .setNegative(getString(R.string.m7取消), null)
                    .show(getSupportFragmentManager());
        } else {
            new CircleDialog.Builder()
                    .setTitle(getString(R.string.m27温馨提示))
                    .setWidth(0.8f)
                    .setText(getString(R.string.m确认修改))
                    .setPositive(getString(R.string.m9确定), v -> {
                        if (isEditInfo) {//修改基础信息
                            setInfo();
                        } else if (isEditInterNet) {//修改联网参数
                            setInternt();
                        } else if (isEditWifi) {
                            setWifi();
                        } else if (isEditUrl) {
                            setUrl();
                        } else {
                            setCharging();
                        }
                    })
                    .setNegative(getString(R.string.m7取消), null)
                    .show(getSupportFragmentManager());
        }
    }


    /**********************************解析数据************************************/

    private void parseReceivData(byte[] data) {
        if (data == null) return;
        int length = data.length;
        if (length > 4) {
            byte frame1 = data[0];
            byte frame2 = data[1];
            byte end = data[length - 1];
            if (frame1 == WiFiMsgConstant.FRAME_1 && frame2 == WiFiMsgConstant.FRAME_2 && end == WiFiMsgConstant.BLT_MSG_END){
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
                  /*  if ((int) exit == 1) {
                        T.make(getString(R.string.m281电桩断开), WifiSetActivity.this);
                    }*/
                        SocketClientUtil.close(mClientUtil);
                        break;

                    case WiFiMsgConstant.CONSTANT_MSG_01://获取信息参数
                        infoLength = len;
                        idByte = new byte[20];
                        System.arraycopy(prayload, 0, idByte, 0, 20);
                        String devId = MyUtil.ByteToString(idByte);
                        setBean(1, devId);
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
                        setBean(2, lan);
                        cardByte = new byte[6];
                        System.arraycopy(prayload, 21, cardByte, 0, 6);
                        String card = MyUtil.ByteToString(cardByte);
                        setBean(3, card);
                        rcdByte = new byte[1];
                        System.arraycopy(prayload, 27, rcdByte, 0, 1);
                        String rcd = MyUtil.ByteToString(rcdByte);
                        int i;
                        try {
                            i = Integer.parseInt(rcd);
                        } catch (NumberFormatException e) {
                            i = 0;
                        }
                        if (i <= 0) i = 1;
                        String s = rcdArray[i - 1];
                        setBean(4, s);
                        //兼容老版本
                        if (len > 28) {
                            versionByte = new byte[24];
                            System.arraycopy(prayload, 28, versionByte, 0, 24);
                            String version = MyUtil.ByteToString(versionByte);
                            setBean(5, version);
                        }

                        mAdapter.notifyDataSetChanged();

                        getDeviceInfo(WiFiMsgConstant.CONSTANT_MSG_02);
                        break;
                    case WiFiMsgConstant.CONSTANT_MSG_02://获取以太网参数
                        internetLength = len;
                        ipByte = new byte[15];
                        System.arraycopy(prayload, 0, ipByte, 0, 15);
                        String devIp = MyUtil.ByteToString(ipByte);
                        setBean(7, devIp);
                        gatewayByte = new byte[15];
                        System.arraycopy(prayload, 15, gatewayByte, 0, 15);
                        String gateway = MyUtil.ByteToString(gatewayByte);
                        setBean(8, gateway);
                        maskByte = new byte[15];
                        System.arraycopy(prayload, 30, maskByte, 0, 15);
                        String mask = MyUtil.ByteToString(maskByte);
                        setBean(9, mask);
                        macByte = new byte[17];
                        System.arraycopy(prayload, 45, macByte, 0, 17);
                        String mac = MyUtil.ByteToString(macByte);
                        setBean(10, mac);
                        dnsByte = new byte[15];
                        System.arraycopy(prayload, 62, dnsByte, 0, 15);
                        String dns = MyUtil.ByteToString(dnsByte);
                        setBean(11, dns);
                        mAdapter.notifyDataSetChanged();
                        getDeviceInfo(WiFiMsgConstant.CONSTANT_MSG_03);
                        break;
                    case WiFiMsgConstant.CONSTANT_MSG_03://获取设备帐号密码参数
                        wifiLength = len;
                        ssidByte = new byte[16];
                        System.arraycopy(prayload, 0, ssidByte, 0, 16);
                        String ssid = MyUtil.ByteToString(ssidByte);
                        setBean(13, ssid);
                        wifiKeyByte = new byte[16];
                        System.arraycopy(prayload, 16, wifiKeyByte, 0, 16);
                        String wifikey = MyUtil.ByteToString(wifiKeyByte);
                        setBean(14, wifikey);
                        bltNameByte = new byte[16];
                        System.arraycopy(prayload, 32, bltNameByte, 0, 16);
                        String bltName = MyUtil.ByteToString(bltNameByte);
//                    setBean(15, bltName);
                        bltPwdByte = new byte[16];
                        System.arraycopy(prayload, 48, bltPwdByte, 0, 16);
                        String bltPwd = MyUtil.ByteToString(bltPwdByte);
//                    setBean(16, bltPwd);
                        name4GByte = new byte[16];
                        System.arraycopy(prayload, 64, name4GByte, 0, 16);
                        String name4G = MyUtil.ByteToString(name4GByte);
                        setBean(15, name4G);
                        pwd4GByte = new byte[16];
                        System.arraycopy(prayload, 80, pwd4GByte, 0, 16);
                        String pwd4G = MyUtil.ByteToString(pwd4GByte);
                        setBean(16, pwd4G);
                        apn4GByte = new byte[16];
                        System.arraycopy(prayload, 96, apn4GByte, 0, 16);
                        String apn4G = MyUtil.ByteToString(apn4GByte);
                        setBean(17, apn4G);
                        mAdapter.notifyDataSetChanged();
                        getDeviceInfo(WiFiMsgConstant.CONSTANT_MSG_04);
                        break;
                    case WiFiMsgConstant.CONSTANT_MSG_04://获取服务器参数
                        urlLength = len;
                        urlByte = new byte[70];
                        System.arraycopy(prayload, 0, urlByte, 0, 70);
                        String url = MyUtil.ByteToString(urlByte);
                        setBean(19, url);

                        hskeyByte = new byte[20];
                        System.arraycopy(prayload, 70, hskeyByte, 0, 20);
                        String hskey = MyUtil.ByteToString(hskeyByte);
                        setBean(20, hskey);

                        heatByte = new byte[4];
                        System.arraycopy(prayload, 90, heatByte, 0, 4);
                        String heat = MyUtil.ByteToString(heatByte);
                        setBean(21, heat);

                        pingByte = new byte[4];
                        System.arraycopy(prayload, 94, pingByte, 0, 4);
                        String ping = MyUtil.ByteToString(pingByte);
                        setBean(22, ping);

                        intervalByte = new byte[4];
                        System.arraycopy(prayload, 98, intervalByte, 0, 4);
                        String interval = MyUtil.ByteToString(intervalByte);
                        setBean(23, interval);

                        mAdapter.notifyDataSetChanged();
                        getDeviceInfo(WiFiMsgConstant.CONSTANT_MSG_05);
                        break;

                    case WiFiMsgConstant.CONSTANT_MSG_05://获取充电参数
                        chargingLength = len;
                        modeByte = new byte[1];
                        System.arraycopy(prayload, 0, modeByte, 0, 1);
                        String mode = MyUtil.ByteToString(modeByte);
                        int modeSet;
                        try {
                            modeSet = Integer.parseInt(mode);
                        } catch (NumberFormatException e) {
                            modeSet = 0;
                        }
                        if (modeSet <= 0) modeSet = 1;
                        String modeValue = modeArray[modeSet - 1];
                        setBean(25, modeValue);

                        maxCurrentByte = new byte[2];
                        System.arraycopy(prayload, 1, maxCurrentByte, 0, 2);
                        String maxCurrent = MyUtil.ByteToString(maxCurrentByte);
                        setBean(26, maxCurrent);

                        rateByte = new byte[5];
                        System.arraycopy(prayload, 3, rateByte, 0, 5);
                        String rate = MyUtil.ByteToString(rateByte);
                        setBean(27, rate);

                        tempByte = new byte[3];
                        System.arraycopy(prayload, 8, tempByte, 0, 3);
                        String temp = MyUtil.ByteToString(tempByte);
                        setBean(28, temp);

                        powerByte = new byte[2];
                        System.arraycopy(prayload, 11, powerByte, 0, 2);
                        String power = MyUtil.ByteToString(powerByte);
                        setBean(29, power);

                        timeByte = new byte[11];
                        System.arraycopy(prayload, 13, timeByte, 0, 11);
                        String time = MyUtil.ByteToString(timeByte);
                        if (time.contains("-")) {
                            String[] split = time.split("-");
                            if (split.length >= 2) {
                                startTime = split[0];
                                endTime = split[1];
                            }
                        }
                        setBean(30, time);


                        //兼容老版本
                        if (len > 24) {
                            chargingEnableByte = new byte[1];
                            System.arraycopy(prayload, 24, chargingEnableByte, 0, 1);
                            String chargingEnable = MyUtil.ByteToString(chargingEnableByte);
                            int enable1;
                            try {
                                enable1 = Integer.parseInt(chargingEnable);
                            } catch (NumberFormatException e) {
                                enable1 = 0;
                            }
                            if (enable1 < 0) enable1 = 1;
                            String enableValue1 = enableArray[enable1];
                            setBean(31, enableValue1);
                        }


                        if (len > 25) {
                            powerdistributionByte = new byte[1];
                            System.arraycopy(prayload, 25, powerdistributionByte, 0, 1);
                            String powerdistribution = MyUtil.ByteToString(powerdistributionByte);
                            int enable2;
                            try {
                                enable2 = Integer.parseInt(powerdistribution);
                            } catch (NumberFormatException e) {
                                enable2 = 0;
                            }
                            if (enable2 < 0) enable2 = 1;
                            String enableValue2 = enableArray[enable2];
                            setBean(32, enableValue2);
                        }

                        if (len > 26) {
                            wiringByte = new byte[1];
                            System.arraycopy(prayload, 26, wiringByte, 0, 1);
                            String wiringType = MyUtil.ByteToString(wiringByte);
                            int wiring;
                            try {
                                wiring = Integer.parseInt(wiringType);
                            } catch (NumberFormatException e) {
                                wiring = 0;
                            }
                            if (wiring < 0) wiring = 1;
                            String wiringValue = wiringArray[wiring];
                            setBean(33, wiringValue);
                        }
                        if (len > 27) {
                            solarByte = new byte[1];
                            System.arraycopy(prayload, 27, solarByte, 0, 1);
                            String solarMode = MyUtil.ByteToString(solarByte);
                            int modeIndext;
                            try {
                                modeIndext = Integer.parseInt(solarMode);
                            } catch (NumberFormatException e) {
                                modeIndext = 0;
                            }
                            if (modeIndext < 0) modeIndext = 1;
                            String solarModeValue = solarArrray[modeIndext];
                            setBean(34, solarModeValue);
                            WifiSetBean bean = (WifiSetBean) mAdapter.getData().get(36);
                            if (len > 29) {
                                solarCurrentByte = new byte[2];
                                System.arraycopy(prayload, 28, solarCurrentByte, 0, 2);
                                if (modeIndext == 2) {//ECO+
                                    String current = MyUtil.ByteToString(solarCurrentByte);//限制电流最大8A
                                    SolarBean solarBean = new SolarBean();
                                    solarBean.setValue(current);
                                    solarBean.setType(WifiSetAdapter.PARAM_ITEM_SOLAR);
                                    solarBean.setKey(getString(R.string.m电流限制));
                                    solarBeans.add(solarBean);
                             /*   if (!bean.isExpanded()) {
                                    mAdapter.expand(36, false);
                                    ((SolarBean) bean.getSubItem(0)).setValue(current);
                                }*/
                                }
                            }
                        }
                        if (len > 30) {
                            ammeterByte = new byte[12];
                            System.arraycopy(prayload, 30, ammeterByte, 0, 12);
                            String ammeterAdd = MyUtil.ByteToString(ammeterByte);
                            setBean(35, ammeterAdd);
                        }
                        mAdapter.notifyDataSetChanged();
                        getDeviceInfo(WiFiMsgConstant.CONSTANT_MSG_06);
                        break;
                    case WiFiMsgConstant.CONSTANT_MSG_06:
                        lockLength = len;
                        lockByte = new byte[len];
                        System.arraycopy(prayload, 0, lockByte, 0, len);
                        String lockStatus = MyUtil.ByteToString(lockByte);
                        lockBeans.clear();
                        if (!TextUtils.isEmpty(lockStatus)) {
                            char[] chars = lockStatus.toCharArray();
                            for (int k = 0; k < chars.length; k++) {
                                LockBean lockBean = new LockBean();
                                if (String.valueOf(chars[k]).equals("1")) {
                                    lockBean.setValue(lockArrray[1]);
                                } else {
                                    lockBean.setValue(lockArrray[0]);
                                }
                                lockBean.setType(WifiSetAdapter.PARAM_ITEM_LOCK);
                                lockBean.setKey(gunArrray[k]);
                                lockBean.setGunId(k + 1);
                                lockBean.setIndex(k);
                                lockBeans.add(lockBean);
                            }
                        } else {
                            LockBean lockBean = new LockBean();
                            lockBean.setValue("");
                            lockBean.setGunId(1);
                            lockBean.setType(WifiSetAdapter.PARAM_ITEM_LOCK);
                            lockBean.setKey(gunArrray[0]);
                            lockBeans.add(lockBean);
                        }
                        refreshRv();
                        break;
                    //设置回应
                    case WiFiMsgConstant.CONSTANT_MSG_11:
                        if (isEditInterNet) {//修改联网参数
                            setInternt();
                        } else if (isEditWifi) {
                            setWifi();
                        } else if (isEditUrl) {
                            setUrl();
                        } else if (isEditCharging) {
                            setCharging();
                        } else {
                            byte result = prayload[0];
                            if ((int) result == 1) {
                                T.make(getString(R.string.m243设置成功), WifiSetActivity.this);
                            } else {
                                T.make(getString(R.string.m244设置失败), WifiSetActivity.this);
                            }
                            sendCmdExit();
                            finish();
                        }
                        break;
                    case WiFiMsgConstant.CONSTANT_MSG_12:
                        if (isEditWifi) {
                            setWifi();
                        } else if (isEditUrl) {
                            setUrl();
                        } else if (isEditCharging) {
                            setCharging();
                        } else {
                            byte result = prayload[0];
                            if ((int) result == 1) {
//                        getDeviceInfo(WiFiMsgConstant.CONSTANT_MSG_01);
                                T.make(getString(R.string.m243设置成功), WifiSetActivity.this);
                            } else {
                                T.make(getString(R.string.m244设置失败), WifiSetActivity.this);
                            }
                            sendCmdExit();
                            finish();
                        }
                        break;
                    case WiFiMsgConstant.CONSTANT_MSG_13:
                        if (isEditUrl) {
                            setUrl();
                        } else if (isEditCharging) {
                            setCharging();
                        } else {
                            byte result = prayload[0];
                            if ((int) result == 1) {
//                        getDeviceInfo(WiFiMsgConstant.CONSTANT_MSG_01);
                                T.make(getString(R.string.m243设置成功), WifiSetActivity.this);
                            } else {
                                T.make(getString(R.string.m244设置失败), WifiSetActivity.this);
                            }
                            sendCmdExit();
                            finish();
                        }
                        break;
                    case WiFiMsgConstant.CONSTANT_MSG_14:
                        if (isEditCharging) {
                            setCharging();
                        } else {
                            byte result = prayload[0];
                            if ((int) result == 1) {
//                        getDeviceInfo(WiFiMsgConstant.CONSTANT_MSG_01);
                                T.make(getString(R.string.m243设置成功), WifiSetActivity.this);
                            } else {
                                T.make(getString(R.string.m244设置失败), WifiSetActivity.this);
                            }
                            sendCmdExit();
                            finish();
                        }
                        break;
                    case WiFiMsgConstant.CONSTANT_MSG_15:
                        byte result = prayload[0];
                        if ((int) result == 1) {
//                        getDeviceInfo(WiFiMsgConstant.CONSTANT_MSG_01);
                            T.make(getString(R.string.m243设置成功), WifiSetActivity.this);
                        } else {
                            T.make(getString(R.string.m244设置失败), WifiSetActivity.this);
                        }
                        sendCmdExit();
                        finish();
                        break;
                    case WiFiMsgConstant.CONSTANT_MSG_16://解锁
                        byte unlock = prayload[0];
                        if ((int) unlock == 1) {
                            lockBeans.get(gunPos).setValue(lockArrray[0]);
                            refreshRv();
                        } else {
                            T.make(getString(R.string.m失败), WifiSetActivity.this);
                        }
                        break;
                    default:
                        break;
                }
            }

        }


    }


    @OnClick({R.id.ivLeft, R.id.tvRight})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivLeft:
                back();
                break;
            case R.id.tvRight:
                save();
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
        OptionsPickerView<String> pvOptions = new OptionsPickerBuilder(this, (options1, options2, options3, v) -> {
            final String tx = list.get(options1);
            String pos = String.valueOf(options1 + 1);
            byte[] bytes = pos.trim().getBytes();
            if (bytes.length > 1) {
                T.make(getString(R.string.m286输入值超出规定长度), WifiSetActivity.this);
                return;
            }
            lanByte = new byte[1];
            System.arraycopy(bytes, 0, lanByte, 0, bytes.length);
//                setInfo();
            setBean(2, tx);
            mAdapter.notifyDataSetChanged();
            isEditInfo = true;
        })
                .setTitleText(getString(R.string.m260语言))
                .setSubmitText(getString(R.string.m9确定))
                .setCancelText(getString(R.string.m7取消))
                .setTitleBgColor(0xffffffff)
                .setTitleColor(0xff333333)
                .setSubmitColor(0xff333333)
                .setCancelColor(0xff999999)
                .setBgColor(0xffffffff)
                .setTitleSize(18)
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
//                setInfo();
                setBean(4, tx);
                mAdapter.notifyDataSetChanged();
                isEditInfo = true;
            }
        })
                .setTitleText(getString(R.string.m265RCD保护值))
                .setSubmitText(getString(R.string.m9确定))
                .setCancelText(getString(R.string.m7取消))
                .setTitleBgColor(0xffffffff)
                .setTitleColor(0xff333333)
                .setSubmitColor(0xff333333)
                .setCancelColor(0xff999999)
                .setBgColor(0xffffffff)
                .setTitleSize(18)
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
//                setCharging();
                setBean(25, tx);
                mAdapter.notifyDataSetChanged();
                isEditCharging = true;
            }
        })
                .setTitleText(getString(R.string.m154充电模式))
                .setSubmitText(getString(R.string.m9确定))
                .setCancelText(getString(R.string.m7取消))
                .setTitleBgColor(0xffffffff)
                .setTitleColor(0xff333333)
                .setSubmitColor(0xff333333)
                .setCancelColor(0xff999999)
                .setBgColor(0xffffffff)
                .setTitleSize(18)
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
                    setBean(32, chargingTime);
                    mAdapter.notifyDataSetChanged();
                    isEditCharging = true;
//                    setCharging();
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
                .setTitleSize(18)//标题文字大小
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
                .setLabel("", "", "", getString(R.string.m207时), getString(R.string.m208分), "")//默认设置为年月日时分秒
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(false)//是否显示为对话框样式
                .build();
        pvCustomTime.show();
    }


    /*设置使能*/
    private void setEnable(int position) {
        String title;
        List<String> list = Arrays.asList(enableArray);
        if (position == 31) title = getString(R.string.m297峰谷充电使能);
        else title = getString(R.string.m298功率分配使能);
        OptionsPickerView<String> pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                final String tx = list.get(options1);
                String pos = String.valueOf(options1);
                byte[] bytes = pos.trim().getBytes();
                if (bytes.length > 1) {
                    T.make(getString(R.string.m286输入值超出规定长度), WifiSetActivity.this);
                    return;
                }
                if (position == 31) {
                    chargingEnableByte = new byte[1];
                    System.arraycopy(bytes, 0, chargingEnableByte, 0, bytes.length);
                } else {
                    powerdistributionByte = new byte[1];
                    System.arraycopy(bytes, 0, powerdistributionByte, 0, bytes.length);
                }
                setBean(position, tx);
                mAdapter.notifyDataSetChanged();
//                setCharging();
                isEditCharging = true;
            }
        })
                .setTitleText(title)
                .setSubmitText(getString(R.string.m9确定))
                .setCancelText(getString(R.string.m7取消))
                .setTitleBgColor(0xffffffff)
                .setTitleColor(0xff333333)
                .setSubmitColor(0xff333333)
                .setCancelColor(0xff999999)
                .setBgColor(0xffffffff)
                .setTitleSize(17)
                .setTextColorCenter(0xff333333)
                .build();
        pvOptions.setPicker(list);
        pvOptions.show();
    }


    /*接线方式*/
    private void setWiring() {
        List<String> list = Arrays.asList(wiringArray);
        OptionsPickerView<String> pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                final String tx = list.get(options1);
                String pos = String.valueOf(options1);
                byte[] bytes = pos.trim().getBytes();
                if (bytes.length > 1) {
                    T.make(getString(R.string.m286输入值超出规定长度), WifiSetActivity.this);
                    return;
                }
                wiringByte = new byte[1];
                System.arraycopy(bytes, 0, wiringByte, 0, bytes.length);
//                setInfo();
                setBean(33, tx);
                mAdapter.notifyDataSetChanged();
                isEditCharging = true;
            }
        })
                .setTitleText(getString(R.string.m外部电流采样接线方式))
                .setSubmitText(getString(R.string.m9确定))
                .setCancelText(getString(R.string.m7取消))
                .setTitleBgColor(0xffffffff)
                .setTitleColor(0xff333333)
                .setSubmitColor(0xff333333)
                .setCancelColor(0xff999999)
                .setBgColor(0xffffffff)
                .setTitleSize(18)
                .setTextColorCenter(0xff333333)
                .build();
        pvOptions.setPicker(list);
        pvOptions.show();
    }


    /*solar模式*/
    private void setSolarMode() {
        List<String> list = Arrays.asList("FAST", "ECO");
        OptionsPickerView<String> pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                final String tx = list.get(options1);
                String pos = String.valueOf(options1);
                byte[] bytes = pos.trim().getBytes();
                if (bytes.length > 1) {
                    T.make(getString(R.string.m286输入值超出规定长度), WifiSetActivity.this);
                    return;
                }
                solarByte = new byte[1];
                System.arraycopy(bytes, 0, solarByte, 0, bytes.length);
//                setInfo();
                setBean(34, tx);
                WifiSetBean bean = (WifiSetBean) mAdapter.getData().get(36);
               /* if (options1 == 2) {//ECO+
                    String current = MyUtil.ByteToString(solarCurrentByte);
                    if (!bean.isExpanded()) {
                        mAdapter.expand(36, false);
                        bean.getSubItem(0).setValue(current);
                    }
                }else {
                    if (bean.isExpanded()) {
                        mAdapter.collapse(36, false);
                    }
                }*/
                if (bean.isExpanded()) {
                    mAdapter.collapse(34, false);
                }
                mAdapter.notifyDataSetChanged();
                isEditCharging = true;
            }
        })
                .setTitleText(getString(R.string.mSolar模式))
                .setSubmitText(getString(R.string.m9确定))
                .setCancelText(getString(R.string.m7取消))
                .setTitleBgColor(0xffffffff)
                .setTitleColor(0xff333333)
                .setSubmitColor(0xff333333)
                .setCancelColor(0xff999999)
                .setBgColor(0xffffffff)
                .setTitleSize(18)
                .setTextColorCenter(0xff333333)
                .build();
        pvOptions.setPicker(list);
        pvOptions.show();
    }


    private void setLock(int gunId) {
        new CircleDialog.Builder().setTitle(getString(R.string.m27温馨提示))
                .setText(getString(R.string.m是否解除该枪电子锁))
                .setWidth(0.75f)
                .setPositive(getString(R.string.m9确定), view -> {
                    String pos = String.valueOf(gunId);
                    byte[] bytes = pos.trim().getBytes();
                    if (bytes.length > 1) {
                        T.make(getString(R.string.m286输入值超出规定长度), WifiSetActivity.this);
                        return;
                    }
                    lockByte = new byte[1];
                    System.arraycopy(bytes, 0, lockByte, 0, bytes.length);
                    setUnLoack();
                })
                .setNegative(getString(R.string.m7取消), view -> {

                })
                .show(getSupportFragmentManager());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 100:
                    String starttime = data.getStringExtra("start");
                    String endtime = data.getStringExtra("end");
                    if (TextUtils.isEmpty(starttime)) {
                        toast(R.string.m130未设置开始时间);
                        return;
                    }
                    if (TextUtils.isEmpty(endtime)) {
                        toast(R.string.m284未设置结束时间);
                        return;
                    }
                    startTime = starttime;
                    endTime = endtime;
                    String chargingTime = starttime + "-" + endtime;
                    byte[] bytes = chargingTime.trim().getBytes();
                    if (bytes.length > 11) {
                        T.make(getString(R.string.m286输入值超出规定长度), WifiSetActivity.this);
                        return;
                    }
                    timeByte = new byte[11];
                    System.arraycopy(bytes, 0, timeByte, 0, bytes.length);
                    setBean(30, chargingTime);
                    mAdapter.notifyDataSetChanged();
                    isEditCharging = true;
                    break;
            }
        }
    }




    private void showInputPassword(){
        new CircleDialog.Builder()
                .setTitle(getString(R.string.m27温馨提示))
                //添加标题，参考普通对话框
                .setInputHint(getString(R.string.m26请输入密码))//提示
                .setInputHeight(100)//输入框高度
                .autoInputShowKeyboard()//自动弹出键盘
                .configInput(params -> {
                    params.gravity = Gravity.CENTER;
                    params.textSize = 45;
//                            params.backgroundColor=ContextCompat.getColor(ChargingPileActivity.this, R.color.preset_edit_time_background);
                    params.strokeColor = ContextCompat.getColor(this, R.color.preset_edit_time_background);
                })
                .setPositiveInput(getString(R.string.m9确定), (text, v) -> {
                    if (password.equals(text)){
                        isVerified=true;
                        toast(R.string.m验证成功);
                    }else {
                        toast(R.string.m验证失败);
                    }
                })
                //添加取消按钮，参考普通对话框
                .setNegative(getString(R.string.m7取消), v -> {

                })
                .show(getSupportFragmentManager());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bind != null) bind.unbind();
    }
}
