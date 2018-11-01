package com.growatt.chargingpile.activity;


import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.adapter.ChargingListAdapter;
import com.growatt.chargingpile.adapter.GunSwitchAdapter;
import com.growatt.chargingpile.application.MyApplication;
import com.growatt.chargingpile.bean.ChargingBean;
import com.growatt.chargingpile.bean.GunBean;
import com.growatt.chargingpile.connutil.PostUtil;
import com.growatt.chargingpile.util.AlertPickDialog;
import com.growatt.chargingpile.util.Cons;
import com.growatt.chargingpile.util.MyUtil;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.util.SmartHomeUrlUtil;
import com.growatt.chargingpile.util.SmartHomeUtil;
import com.growatt.chargingpile.view.RoundProgressBar;
import com.mylhyl.circledialog.CircleDialog;

import org.json.JSONObject;
import org.xutils.common.util.LogUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChargingPileActivity extends BaseActivity {

    @BindView(R.id.ivLeft)
    ImageView ivUserCenter;

    @BindView(R.id.linearlayout)
    LinearLayout linearlayout;

    @BindView(R.id.linearlayout2)
    LinearLayout mStatusGroup;
    @BindView(R.id.smart_home_empty_page)
    View emptyPage;
    @BindView(R.id.rl_Charging)
    RelativeLayout rlCharging;
    @BindView(R.id.tv_AC_DC)
    TextView tvModel;
    @BindView(R.id.tv_Gun)
    TextView tvGun;
    @BindView(R.id.tvSwitchGun)
    TextView tvSwitchGun;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.iv_charging_status)
    ImageView ivChargingStatus;
    @BindView(R.id.tv_charging_status)
    TextView tvChargingStatus;
    @BindView(R.id.iv_charging_icon)
    ImageView ivChargingIcon;
    @BindView(R.id.linearlayout3)
    LinearLayout llBottomGroup;
    @BindView(R.id.iv_anim)
    ImageView ivAnim;
    @BindView(R.id.iv_circle_background)
    ImageView ivfinishBackground;
    @BindView(R.id.srl_pull)
    SwipeRefreshLayout srlPull;


    /*充电桩列表*/
    @BindView(R.id.rv_charging)
    RecyclerView mRvCharging;
    private List<ChargingBean.DataBean> mChargingList = new ArrayList<>();
    private LinearLayoutManager mLinearLayoutManager;
    private ChargingListAdapter mAdapter;


    //选择充电桩popuwindow
    private PopupWindow popupGun;
    private GunSwitchAdapter popGunAdapter;


    //设置金额 电量  时长 跳转码
    public static final int REQUEST_MONEY = 100;
    public static final int REQUEST_ELE = 101;
    public static final int REQUEST_TIME = 102;
    //开启或关闭时间段
    public static final int REQUEST_OPEN_DURATION = 103;
    //添加充电桩
    public static final int REQUEST_ADD_CHARGING = 104;

    //预约充电方案 0 :只预约了充电时间  1：金额  2：电量  3：时长
    private int presetType = 0;

    private boolean isReservation = false;//是否预约
    private String startTime;//预约开始时间
    private double reserveMoney;//预约金额
    private double reserveEle;//预约电量
    private int ReserveTime;//预约时长(分钟)
    private String timeEvaryDay;//定时每天这个时间开启

    //--------------空闲---------------
    private View availableView;

    //--------------准备中---------------
    private View preparingView;
    private RelativeLayout rlPpmoney;
    private TextView tvPpmoney;
    private CheckBox cbPpmoney;
    private RelativeLayout rlPpEle;
    private TextView tvPpEle;
    private CheckBox cbPpEle;
    private RelativeLayout rlPpTime;
    private TextView tvPpTime;
    private CheckBox cbPpTime;
    private RelativeLayout rlReserve;
    private TextView tvStartTime;
    private ImageView ivResever;
    private TextView tvTextStart;
    private TextView tvTextOpenClose;
    private CheckBox cbEveryday;
    private TextView tvEveryDay;
    private LinearLayout llReserve;

    //--------------充电中---------------
    //正常充电
    private View normalChargingView;
    private TextView tvChargingEle;
    private TextView tvChargingRate;
    private TextView tvChargingCurrent;
    private TextView tvChargingDuration;
    private TextView tvChargingMoney;
    private TextView tvChargingVoltage;
    //预设方案充电
    private View presetChargingView;
    private TextView tvPresetText;
    private TextView tvPresetValue;
    private TextView tvChargingValue;
    private TextView tvPresetType;
    private RoundProgressBar roundProgressBar;
    private ImageView ivChargedOther;
    private TextView tvOtherValue;
    private TextView tvOtherText;
    private ImageView ivChargedOther2;
    private TextView tvOtherValue2;
    private TextView tvOtherText2;
    private TextView tvRate;
    private TextView tvCurrent;
    private TextView tvVoltage;


    private int transactionId;//充电编号，停止充电时用

    //--------------充电结束---------------
    private View chargeFinishView;
    private TextView tvFinishEle;
    private TextView tvFinishRate;
    private TextView tvFinishTime;
    private TextView tvFinishMoney;


    //--------------故障---------------
    private View chargeFaultedView;

    //--------------注销---------------
    private View chargeExpiryView;

    //--------------不可用---------------
    private View chargeUnvailableView;

    //--------------已经工作过---------------
    private View chargeWorkedView;

    //--------------启用中---------------
    private View chargeAcceptedView;

    //充电桩的上一个状态
    private String previous = null;
    //是否在刷新中
    private boolean isFreshing = false;


    private long mExitTime;

    /**
     * 状态是否发生改变
     */
    private boolean isStatusChange = false;
    private int cycleTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charging_pile);
        ButterKnife.bind(this);
        initHeaderViews();
        initCharging();
        initListeners();
        initPullView();
        initStatusView();
        initResource();
//        freshData(0, 1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshAll();
    }

    @Override
    protected void onPause() {
        super.onPause();
        timeHandler.removeMessages(1);
    }

    /*定时刷新机制*/
    private boolean isTimeRefresh = false;
    private Handler timeHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    isTimeRefresh = true;
                    refreshAll();
                    break;
            }
        }
    };
    private void initPullView() {
        srlPull.setColorSchemeColors(ContextCompat.getColor(this, R.color.green_1));
        srlPull.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isTimeRefresh = false;
                refreshAll();
            }
        });
    }

    /**
     * 刷新充电桩+枪数据
     */
    private void refreshAll() {
        timeHandler.removeMessages(1);
        timeHandler.sendEmptyMessageDelayed(1,10*1000);
        if (Cons.mSeletPos > mAdapter.getData().size() - 1) {
            Cons.mSeletPos = 0;
        }
        freshData(Cons.mSeletPos, Cons.mCurrentGunBeanId);
    }


    /**
     * 初始化头部
     */
    private void initHeaderViews() {
        ivUserCenter.setImageResource(R.drawable.user_index);
    }


    /**
     * 各个状态对应的View
     */
    private void initStatusView() {
        initAvailableView();
        initPreparingView();
        initChargingView();
        initChargeFinshView();
        initFaultedView();
        initExpiryView();
        initUnavailableView();
        initWorkedView();
        initAcceptView();
    }

    private void initAcceptView() {
        chargeAcceptedView = LayoutInflater.from(this).inflate(R.layout.status_charging_accepted, mStatusGroup, false);
    }

    private void initWorkedView() {
        chargeWorkedView = LayoutInflater.from(this).inflate(R.layout.status_charging_work, mStatusGroup, false);
    }

    private void initUnavailableView() {
        chargeUnvailableView = LayoutInflater.from(this).inflate(R.layout.status_charging_unavailable, mStatusGroup, false);
    }

    private void initExpiryView() {
        chargeExpiryView = LayoutInflater.from(this).inflate(R.layout.status_charging_expiry, mStatusGroup, false);
    }

    private void initFaultedView() {
        chargeFaultedView = LayoutInflater.from(this).inflate(R.layout.status_charging_faulted, mStatusGroup, false);
    }

    /**
     * 空闲
     */
    private void initAvailableView() {
        availableView = LayoutInflater.from(this).inflate(R.layout.status_charging_available, mStatusGroup, false);
    }


    /**
     * 准备中
     */
    private void initPreparingView() {
        preparingView = LayoutInflater.from(this).inflate(R.layout.status_charging_prepare_layout, mStatusGroup, false);
        //金额
        rlPpmoney = preparingView.findViewById(R.id.rl_prepare_money);
        tvPpmoney = preparingView.findViewById(R.id.tv_prepare_money_num);
        cbPpmoney = preparingView.findViewById(R.id.cb_prepare_money_select);
        //电量
        rlPpEle = preparingView.findViewById(R.id.rl_prepare_ele);
        tvPpEle = preparingView.findViewById(R.id.tv_prepare_ele_num);
        cbPpEle = preparingView.findViewById(R.id.cb_prepare_ele_select);
        //时长
        rlPpTime = preparingView.findViewById(R.id.rl_prepare_time);
        tvPpTime = preparingView.findViewById(R.id.tv_prepare_time_num);
        cbPpTime = preparingView.findViewById(R.id.cb_prepare_time_select);
        //开始时间
        tvTextStart = preparingView.findViewById(R.id.tv_time);
        //开启或者关闭
        tvTextOpenClose = preparingView.findViewById(R.id.tv_text_open_close);
        cbEveryday = preparingView.findViewById(R.id.cb_everyday);
        tvEveryDay = preparingView.findViewById(R.id.tv_time_every_day);


        rlPpmoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (presetType == 1) {
                    setMoneyUi(false, "--");
                    presetType = 0;
                } else {
                    Intent intent = new Intent(ChargingPileActivity.this, ChargingPresetEditActivity.class);
                    intent.putExtra("type", 1);
                    startActivityForResult(intent, REQUEST_MONEY);
                }
            }
        });


        rlPpEle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (presetType == 2) {
                    setEleUi(false, "--");
                    presetType = 0;
                } else {
                    Intent intent = new Intent(ChargingPileActivity.this, ChargingPresetEditActivity.class);
                    intent.putExtra("type", 2);
                    startActivityForResult(intent, REQUEST_ELE);
                }

            }
        });

        rlPpTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.d("选中方案" + presetType);
                if (presetType == 3) {
                    setTimeUi(false, "--");
                    presetType = 0;
                    isReservation = false;
                    setReserveUi(getString(R.string.m204开始时间), getString(R.string.m184关闭), R.drawable.checkbox_off, "--:--", true);
                } else {
                    Intent intent = new Intent(ChargingPileActivity.this, ChargingPresetEditActivity.class);
                    intent.putExtra("type", 3);
                    startActivityForResult(intent, REQUEST_TIME);
                }

            }
        });

        cbPpmoney.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbPpEle.setChecked(false);
                    cbPpTime.setChecked(false);
                }
            }
        });


        cbPpEle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbPpmoney.setChecked(false);
                    cbPpTime.setChecked(false);
                }
            }
        });


        cbPpTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbPpmoney.setChecked(false);
                    cbPpEle.setChecked(false);
                }
            }
        });

        //预约
        rlReserve = preparingView.findViewById(R.id.rl_reserve);
        tvStartTime = preparingView.findViewById(R.id.tv_start_time);
        ivResever = preparingView.findViewById(R.id.iv_resever_switch);
        llReserve = preparingView.findViewById(R.id.ll_reserve);
        ivResever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (presetType == 3) {
                    if (!isReservation) {
                        //去预约列表操作
                        Intent intent = new Intent(ChargingPileActivity.this, ChargingDurationActivity.class);
                        startActivityForResult(intent, REQUEST_OPEN_DURATION);
                    } else {
                        //关闭预约
                        isReservation = false;
                        ivResever.setImageResource(R.drawable.checkbox_off);
                    }

                } else {
                    if (isReservation) {
                        isReservation = false;
                        ivResever.setImageResource(R.drawable.checkbox_off);
                    } else {
                        selectTime();
                    }

                }
            }
        });


        rlReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isReservation) {
                    if (presetType == 3) {
                        //去预约列表操作
                        Intent intent = new Intent(ChargingPileActivity.this, ChargingDurationActivity.class);
                        startActivityForResult(intent, REQUEST_OPEN_DURATION);
                    } else {
                        if (presetType != 3) {
                            selectTime();
                        }
                    }
                } else {
                    if (presetType != 3) {
                        selectTime();
                    }
                }
            }
        });

    }


    /**
     * 充电中
     */
    private void initChargingView() {
        normalChargingView = LayoutInflater.from(this).inflate(R.layout.status_charging_normal_layout, mStatusGroup, false);
        tvChargingEle = normalChargingView.findViewById(R.id.tv_charging_ele);
        tvChargingRate = normalChargingView.findViewById(R.id.tv_charging_rate);
        tvChargingCurrent = normalChargingView.findViewById(R.id.tv_charging_current);
        tvChargingDuration = normalChargingView.findViewById(R.id.tv_charging_duration);
        tvChargingMoney = normalChargingView.findViewById(R.id.tv_charging_money);
        tvChargingVoltage = normalChargingView.findViewById(R.id.tv_current_voltage);

        presetChargingView = LayoutInflater.from(this).inflate(R.layout.status_charging_preset_layout, mStatusGroup, false);
        tvPresetText = presetChargingView.findViewById(R.id.tv_preset_text);
        tvPresetValue = presetChargingView.findViewById(R.id.tv_preset_value);
        tvChargingValue = presetChargingView.findViewById(R.id.tv_charging_value);
        tvPresetType = presetChargingView.findViewById(R.id.tv_preset_type);
        roundProgressBar = presetChargingView.findViewById(R.id.roundProgressBar1);
        ivChargedOther = presetChargingView.findViewById(R.id.icon_charged_other);
        tvOtherValue = presetChargingView.findViewById(R.id.tv_other_value);
        tvOtherText = presetChargingView.findViewById(R.id.tv_other_text);
        ivChargedOther2 = presetChargingView.findViewById(R.id.icon_charged_other_2);
        tvOtherValue2 = presetChargingView.findViewById(R.id.tv_other_value_2);
        tvOtherText2 = presetChargingView.findViewById(R.id.tv_other_text_2);
        tvRate = presetChargingView.findViewById(R.id.tv_rate);
        tvCurrent = presetChargingView.findViewById(R.id.tv_current);
        tvVoltage = presetChargingView.findViewById(R.id.tv_voltage);

    }


    /**
     * 充电结束
     */
    private void initChargeFinshView() {
        chargeFinishView = LayoutInflater.from(this).inflate(R.layout.status_charging_finish_layout, mStatusGroup, false);
        tvFinishEle = chargeFinishView.findViewById(R.id.tv_ele);
        tvFinishRate = chargeFinishView.findViewById(R.id.tv_rate);
        tvFinishTime = chargeFinishView.findViewById(R.id.tv_time);
        tvFinishMoney = chargeFinishView.findViewById(R.id.tv_money);
    }


    /**
     * 刷新列表数据
     * position :刷新列表时选中第几项
     * millis
     */
    private void freshData(final int position, final int gunPosition) {
        if (!isTimeRefresh) {Mydialog.Show(this);}
        isFreshing = true;
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("userId", Cons.userBean.getId());//测试id
        jsonMap.put("lan", getLanguage());
        String json = SmartHomeUtil.mapToJsonString(jsonMap);
        LogUtil.i(json);
        PostUtil.postJson(SmartHomeUrlUtil.GET_MY_ADD_CHARGING_LIST, json, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                Mydialog.Dismiss();
                srlPull.setRefreshing(false);
                isFreshing = false;
                try {
                    List<ChargingBean.DataBean> charginglist = new ArrayList<>();
                    JSONObject object = new JSONObject(json);
                    if (object.getInt("code") == 0) {
                        ChargingBean chargingListBean = new Gson().fromJson(json, ChargingBean.class);
                        charginglist = chargingListBean.getData();
                        for (int i = 0; i < charginglist.size(); i++) {
                            ChargingBean.DataBean bean = charginglist.get(i);
                            bean.setDevType(ChargingBean.CHARGING_PILE);
                            bean.setName(bean.getName());
                        }
                        HeadRvAddButton(charginglist);
                    }
                    //默认选中第一项
                    if (charginglist.size() > 1) {
                        MyUtil.hideAllView(View.GONE, emptyPage);
                        MyUtil.showAllView(rlCharging, linearlayout);
                        refreshChargingUI(position, gunPosition);
                    } else {
                        MyUtil.hideAllView(View.GONE, rlCharging, linearlayout);
                        MyUtil.showAllView(emptyPage);
                    }
                } catch (Exception e) {
                    srlPull.setRefreshing(false);
                    e.printStackTrace();
                }
            }

            @Override
            public void LoginError(String str) {
                srlPull.setRefreshing(false);
                isFreshing = false;
                MyUtil.hideAllView(View.GONE, rlCharging);
                MyUtil.showAllView(emptyPage);
            }
        });

    }


    /**
     * 根据选中项刷新充电桩的ui
     */

    private void refreshChargingUI(int chargingPilepos, int gunId) {
        //刷新选中状态列表
        for (int i = 0; i < mAdapter.getData().size(); i++) {
            mAdapter.getData().get(i).setChecked(false);
        }
        ChargingBean.DataBean dataBean = mAdapter.getData().get(chargingPilepos);
        Cons.mCurrentPile = dataBean;
        dataBean.setChecked(true);
        mAdapter.notifyDataSetChanged();
        //电桩信息
        String Modle = dataBean.getModel();
        if ("ACChargingPoint".equals(Modle)) {
            tvModel.setText(getString(R.string.m112交流));
        } else {
            tvModel.setText(getString(R.string.m113直流));
        }
        if (dataBean.getConnectors() == 1) {
            tvGun.setText(getString(R.string.m114单枪));
        } else {
            tvGun.setText(getString(R.string.m115双枪));
        }
//        connectorId = 1;
        //根据选中项刷新充电桩的充电枪,默认刷新A枪
        freshChargingGun(dataBean.getChargeId(), gunId);
    }

    /**
     * 刷新充电枪状态
     * 参数
     * chargingId 充电桩的id
     * connectorId 充电枪的id
     * millis
     * needSenmessage 是否需要发送通知
     * isStartOrStop 是否操作过
     */

    private void freshChargingGun(final String chargingId, final int connectorId) {
        Mydialog.Show(this);
        isFreshing = true;
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("sn", chargingId);//测试id
        jsonMap.put("connectorId", connectorId);//测试id
        jsonMap.put("userId", Cons.userBean.getId());//测试id
        jsonMap.put("lan", getLanguage());//测试id
        String json = SmartHomeUtil.mapToJsonString(jsonMap);
        LogUtil.i(json);
        PostUtil.postJson(SmartHomeUrlUtil.GET_CHARGING_GUN_DATA, json, new PostUtil.JsonListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                Mydialog.Dismiss();
                isFreshing = false;
                try {
                    JSONObject object = new JSONObject(json);
                    if (object.getInt("code") == 0) {
                        GunBean gunBean = new Gson().fromJson(json, GunBean.class);
                        Cons.mCurrentGunBean = gunBean;
                        refreshBygun(gunBean);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void LoginError(String str) {
                isFreshing = false;
            }

            //到达指定时间时会执行这个方法，在这里判断是否需要刷新
            @Override
            public void sendMsgByTime(Handler handler) {

            }
        });

    }

    /**
     * 根据充电枪刷新ui
     *
     * @param gunBean 充电枪
     */
    private void refreshBygun(GunBean gunBean) {
        //充电枪详细数据
        GunBean.DataBean data = gunBean.getData();
        String name = getString(R.string.m110A枪);
        if (data.getConnectorId() == 1) {
            name = getString(R.string.m110A枪);
        } else {
            getString(R.string.m111B枪);
        }
        tvSwitchGun.setText(name);
        //初始化充电枪准备中的显示
        gunPrepareInfoByLastAction(gunBean);
        //设置当前状态显示
        String status = data.getStatus();
        //状态改变,并且不是充电完成时不再循环刷新
        if (!status.equals(previous)) {
            cycleTime = 0;
        } else {
            cycleTime = 0;
        }
        //记录上一个状态
        previous = status;

        mStatusGroup.removeAllViews();
        switch (status) {
            case GunBean.AVAILABLE://空闲
                if (Cons.mCurrentPile.getType() == 0) {//桩主
                    mStatusGroup.addView(preparingView);
                } else {//普通用户
                    mStatusGroup.addView(availableView);
                }
                hideAnim();
                setChargGunUi(R.drawable.charging_available, getString(R.string.m117空闲), ContextCompat.getColor(this, R.color.charging_text_green), R.drawable.btn_start_charging, getString(R.string.m103充电));
                MyUtil.showAllView(llBottomGroup);
                break;
            case GunBean.RESERVED:
            case GunBean.PREPARING:
                mStatusGroup.addView(preparingView);
                MyUtil.showAllView(rlReserve);
                hideAnim();
                setChargGunUi(R.drawable.charging_available, getString(R.string.m119准备中), ContextCompat.getColor(this, R.color.charging_text_green), R.drawable.btn_start_charging, getString(R.string.m103充电));
                MyUtil.showAllView(llBottomGroup);
                break;

            case GunBean.CHARGING:
                transactionId = data.getTransactionId();
                setChargGunUi(R.drawable.charging_available, getString(R.string.m118充电中), ContextCompat.getColor(this, R.color.charging_text_green), R.drawable.btn_stop_charging, getString(R.string.m108停止充电));
                MyUtil.showAllView(llBottomGroup);
                startAnim();
                String presetType = data.getcKey();
                if ("0".equals(presetType)||TextUtils.isEmpty(presetType)) {
                    mStatusGroup.addView(normalChargingView);
                    setNormalCharging(data);
                } else if (presetType.equals("G_SetAmount")) {
                    mStatusGroup.addView(presetChargingView);
                    String scheme = String.format(getString(R.string.m198预设充电方案) + "-%s", getString(R.string.m200金额));
                    int timeCharging = data.getCtime();
                    int hourCharging = timeCharging / 60;
                    int minCharging = timeCharging % 60;
                    String sTimeCharging = hourCharging + "h" + minCharging + "min";
                    setPresetChargingUi(data, scheme, getString(R.string.m192消费金额), R.drawable.charging_ele, String.valueOf(data.getEnergy()), getString(R.string.m189已充电量), R.drawable.charging_time, sTimeCharging, getString(R.string.m191已充时长));
                } else if (presetType.equals("G_SetEnergy")) {
                    mStatusGroup.addView(presetChargingView);
                    String scheme = String.format(getString(R.string.m198预设充电方案) + "-%s", getString(R.string.m201电量));
                    int timeCharging = data.getCtime();
                    int hourCharging = timeCharging / 60;
                    int minCharging = timeCharging % 60;
                    String sTimeCharging = hourCharging + "h" + minCharging + "min";
                    setPresetChargingUi(data, scheme, getString(R.string.m189已充电量), R.drawable.charging_money, String.valueOf(data.getCost()), getString(R.string.m192消费金额), R.drawable.charging_time, sTimeCharging, getString(R.string.m191已充时长));
                } else {
                    mStatusGroup.addView(presetChargingView);
                    String scheme = String.format(getString(R.string.m198预设充电方案) + "-%s", getString(R.string.m202时长));
                    setPresetChargingUi(data, scheme, getString(R.string.m191已充时长), R.drawable.charging_time, String.valueOf(data.getCost()), getString(R.string.m192消费金额), R.drawable.charging_ele, String.valueOf(data.getCtime()), getString(R.string.m189已充电量));
                }
                break;

            case GunBean.FINISHING:
                int timeFinishing = data.getCtime();
                int hourFinishing = timeFinishing / 60;
                int minFinishing = timeFinishing % 60;
                String sTimeFinishing = hourFinishing + "h" + minFinishing + "min";
                mStatusGroup.addView(chargeFinishView);
                tvFinishEle.setText(String.valueOf(data.getEnergy()) + "kWh");
                tvFinishRate.setText(String.valueOf(data.getRate()));
                tvFinishTime.setText(sTimeFinishing);
                tvFinishMoney.setText(String.valueOf(data.getCost()));
                stopAnim();
                MyUtil.showAllView(llBottomGroup);
                setChargGunUi(R.drawable.charging_available, getString(R.string.m120充电结束), ContextCompat.getColor(this, R.color.charging_text_green), R.drawable.btn_start_charging, getString(R.string.m103充电));
                break;

            case GunBean.EXPIRY:
                mStatusGroup.addView(chargeExpiryView);
                setChargGunUi(R.drawable.charging_available, getString(R.string.m118充电中), ContextCompat.getColor(this, R.color.charging_text_green), R.drawable.btn_stop_charging, getString(R.string.m124已经注销));
                MyUtil.hideAllView(View.GONE, llBottomGroup);
                break;
            case GunBean.FAULTED:
                hideAnim();
                mStatusGroup.addView(chargeFaultedView);
                setChargGunUi(R.drawable.charging_faulted, getString(R.string.m121故障), ContextCompat.getColor(this, R.color.red_faulted), R.drawable.btn_stop_charging, getString(R.string.m121故障));
                MyUtil.showAllView(llBottomGroup);
                break;

            case GunBean.UNAVAILABLE:
                hideAnim();
                mStatusGroup.addView(chargeUnvailableView);
                setChargGunUi(R.drawable.charging_unavailable, getString(R.string.m122不可用), ContextCompat.getColor(this, R.color.title_3), R.drawable.btn_start_charging, getString(R.string.m122不可用));
                MyUtil.showAllView(llBottomGroup);
                break;
            case GunBean.WORK:
                hideAnim();
                mStatusGroup.addView(chargeWorkedView);
                setChargGunUi(R.drawable.charging_available, getString(R.string.m126已经工作过), ContextCompat.getColor(this, R.color.charging_text_green), R.drawable.btn_start_charging, getString(R.string.m103充电));
                MyUtil.hideAllView(View.GONE, llBottomGroup);
                break;

            case GunBean.ACCEPTED:
                hideAnim();
                mStatusGroup.addView(chargeAcceptedView);
                setChargGunUi(R.drawable.charging_available, getString(R.string.m125启用中), ContextCompat.getColor(this, R.color.charging_text_green), R.drawable.btn_stop_charging, getString(R.string.m124已经注销));
                MyUtil.hideAllView(View.GONE, llBottomGroup);
                break;
        }
    }

    private void setNormalCharging(GunBean.DataBean data) {
        int timeCharging = data.getCtime();
        int hourCharging = timeCharging / 60;
        int minCharging = timeCharging % 60;
        String sTimeCharging = hourCharging + "h" + minCharging + "min";
        tvChargingEle.setText(String.valueOf(data.getEnergy()) + "kWh");
        tvChargingRate.setText(String.valueOf(data.getRate()));
        tvChargingCurrent.setText(String.valueOf(data.getCurrent() + "A"));
        tvChargingDuration.setText(sTimeCharging);
        tvChargingMoney.setText(String.valueOf(data.getCost()));
        tvChargingVoltage.setText(String.valueOf(data.getVoltage()) + "V");
    }

    /**
     * 根据预约信息来初始化
     *
     * @param gunBean
     */
    private void gunPrepareInfoByResever(GunBean gunBean) {
        List<GunBean.ReserveNowBean> reserveNow = gunBean.getReserveNow();
        if (Cons.mCurrentPile.getType() == 0) {
            if (reserveNow.size() == 0) {  //没有预约
                isReservation = false;
                presetType = 0;
                initPresetUi();
                initReserveUi();
            } else {
                isReservation = true;
                //判断是什么预约
                String cKey = reserveNow.get(0).getCKey();
                if (cKey.equals("G_SetAmount")) {//金额预约
                    presetType = 1;
                    //先全部初始化，在设置金额预约相关
                    initPresetUi();
                    initReserveUi();
                    String expiryDate = reserveNow.get(0).getExpiryDate();
//                String expiryDate = "2018-10-26T19:13:25.000Z";
                    reserveMoney = reserveNow.get(0).getCValue();
                    startTime = expiryDate;
                    setMoneyUi(true, String.valueOf(reserveMoney));
                    setReserveUi(getString(R.string.m204开始时间), getString(R.string.m183开启), R.drawable.checkbox_on, expiryDate.substring(11, 16), true);

                } else if (cKey.equals("G_SetEnergy")) {//电量预约
                    presetType = 2;
                    initPresetUi();
                    initReserveUi();
                    String expiryDate = reserveNow.get(0).getExpiryDate();
                    reserveEle = reserveNow.get(0).getCValue();
                    startTime = expiryDate;
                    setEleUi(true, String.valueOf(reserveEle));
                    setReserveUi(getString(R.string.m204开始时间), getString(R.string.m183开启), R.drawable.checkbox_on, expiryDate.substring(11, 16), true);
                } else if (cKey.equals("G_SetTime")) {//时间段预约
                    presetType = 3;
                    initPresetUi();
                    initReserveUi();
                    StringBuilder stringBuilder = new StringBuilder();
                    //显示多个时间段
                    int duration = 0;
                    for (int i = 0; i < reserveNow.size(); i++) {
                        GunBean.ReserveNowBean bean = reserveNow.get(i);
                        String expiryDate = bean.getExpiryDate();//开始时间
                        String endDate = null;
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        try {
                            if (!TextUtils.isEmpty(expiryDate)) {
                                Date startDate = sdf.parse(expiryDate);
                                long endDateValue = (long) (startDate.getTime() + bean.getCValue() * 60 * 1000);
                                Date endTime = new Date(endDateValue);
                                endDate = sdf.format(endTime);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (!TextUtils.isEmpty(expiryDate)) {
                            String start = expiryDate.substring(11, 16);
                            String end = endDate.substring(11, 16);
                            stringBuilder.append(start).append("~").append(end);
                            if (i != reserveNow.size() - 1) {
                                stringBuilder.append(",");
                            }
                            duration += bean.getCValue();
                        }
                    }
                    //显示累计时长
                    int hour = duration / 60;
                    int min = duration % 60;
                    String sTime = hour + "h" + min + "min";
                    setTimeUi(true, sTime);
                    setReserveUi(getString(R.string.m预约时间段), getString(R.string.m183开启), R.drawable.checkbox_on, stringBuilder.toString(), false);

                } else {//只预约了开始时间
                    presetType = 0;
                    initPresetUi();
                    initReserveUi();
                    String expiryDate = reserveNow.get(0).getExpiryDate();
                    setReserveUi(getString(R.string.m204开始时间), getString(R.string.m183开启), R.drawable.checkbox_on, expiryDate.substring(11, 16), true);
                }
            }
        } else {//非桩主
            isReservation = false;
            initPresetUi();
            initReserveUi();
            MyUtil.hideAllView(View.GONE, rlReserve, llReserve);
        }
    }


    private void gunPrepareInfoByLastAction(GunBean gunBean) {
        //最后一次操作
        GunBean.LastActionBean actionBean = gunBean.getActionBean();
        if (Cons.mCurrentPile.getType() == 0) {//桩主
            if (actionBean == null) {  //没有操作过
                isReservation = false;
                presetType = 0;
                initPresetUi();
                initReserveUi();
            } else {
                String action = actionBean.getAction();
                //直接开始充电
                if (action.equals("remoteStartTransaction") || action.equals("remoteStopTransaction")) {
                    isReservation = false;
                    presetType = 0;
                    initPresetUi();
                    initReserveUi();
                } else {//预约充电
                    //预约信息
                    isReservation = true;
                    List<GunBean.ReserveNowBean> reserveNow = gunBean.getReserveNow();
                    if (reserveNow.size() != 0) {
                        //判断是什么预约
                        String cKey = reserveNow.get(0).getCKey();
                        if (cKey.equals("G_SetAmount")) {//金额预约
                            presetType = 1;
                            //先全部初始化，在设置金额预约相关
                            initPresetUi();
                            initReserveUi();
                            String expiryDate = reserveNow.get(0).getExpiryDate();
//                String expiryDate = "2018-10-26T19:13:25.000Z";
                            reserveMoney = reserveNow.get(0).getCValue();
                            startTime = expiryDate;
                            setMoneyUi(true, String.valueOf(reserveMoney));
                            setReserveUi(getString(R.string.m204开始时间), getString(R.string.m183开启), R.drawable.checkbox_on, expiryDate.substring(11, 16), true);

                        } else if (cKey.equals("G_SetEnergy")) {//电量预约
                            presetType = 2;
                            initPresetUi();
                            initReserveUi();
                            String expiryDate = reserveNow.get(0).getExpiryDate();
                            reserveEle = reserveNow.get(0).getCValue();
                            startTime = expiryDate;
                            setEleUi(true, String.valueOf(reserveEle));
                            setReserveUi(getString(R.string.m204开始时间), getString(R.string.m183开启), R.drawable.checkbox_on, expiryDate.substring(11, 16), true);
                        } else if (cKey.equals("G_SetTime")) {//时间段预约
                            presetType = 3;
                            initPresetUi();
                            initReserveUi();
                            StringBuilder stringBuilder = new StringBuilder();
                            //显示多个时间段
                            int duration = 0;
                            for (int i = 0; i < reserveNow.size(); i++) {
                                GunBean.ReserveNowBean bean = reserveNow.get(i);
                                String expiryDate = bean.getExpiryDate();//开始时间
                                String endDate = null;
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                try {
                                    if (!TextUtils.isEmpty(expiryDate)) {
                                        Date startDate = sdf.parse(expiryDate);
                                        long endDateValue = (long) (startDate.getTime() + bean.getCValue() * 60 * 1000);
                                        Date endTime = new Date(endDateValue);
                                        endDate = sdf.format(endTime);
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                if (!TextUtils.isEmpty(expiryDate)) {
                                    String start = expiryDate.substring(11, 16);
                                    String end = endDate.substring(11, 16);
                                    stringBuilder.append(start).append("~").append(end);
                                    if (i != reserveNow.size() - 1) {
                                        stringBuilder.append(",");
                                    }
                                    duration += bean.getCValue();
                                }
                            }
                            //显示累计时长
                            int hour = duration / 60;
                            int min = duration % 60;
                            String sTime = hour + "h" + min + "min";
                            setTimeUi(true, sTime);
                            setReserveUi(getString(R.string.m预约时间段), getString(R.string.m183开启), R.drawable.checkbox_on, stringBuilder.toString(), false);

                        } else {//只预约了开始时间
                            presetType = 0;
                            initPresetUi();
                            initReserveUi();
                            String expiryDate = reserveNow.get(0).getExpiryDate();
                            setReserveUi(getString(R.string.m204开始时间), getString(R.string.m183开启), R.drawable.checkbox_on, expiryDate.substring(11, 16), true);
                        }
                    }
                }
            }
        } else {//非桩主
            isReservation = false;
            initPresetUi();
            initReserveUi();
            MyUtil.hideAllView(View.GONE, rlReserve, llReserve);
        }
    }


    /**
     * 设置预设充电时，充电中的ui
     *
     * @param data
     * @param scheme
     * @param type
     * @param resOther
     * @param otherValue
     * @param otherText
     * @param resOhter2
     * @param otherValue2
     * @param otherText2
     */
    private void setPresetChargingUi(GunBean.DataBean data, String scheme, String type, int resOther, String otherValue, String otherText, int resOhter2, String otherValue2, String otherText2) {
        tvPresetText.setText(scheme);
        tvPresetValue.setText(String.valueOf(data.getcValue()));
        tvChargingValue.setText(String.valueOf(data.getCost()));
        tvPresetType.setText(type);

        ivChargedOther.setImageResource(resOther);
        tvOtherValue.setText(otherValue);
        tvOtherText.setText(otherText);

        ivChargedOther2.setImageResource(resOhter2);
        tvOtherValue2.setText(otherValue2);
        tvOtherText2.setText(otherText2);
        if (data.getcValue() > 0) {
            roundProgressBar.setMax((int) data.getcValue());
        }
        roundProgressBar.setProgress((int) data.getCost());
        roundProgressBar.setTextSize(getResources().getDimensionPixelSize(R.dimen.xa26));

        tvRate.setText(String.valueOf(data.getRate()));
        tvCurrent.setText(String.valueOf(data.getCurrent()));
        tvVoltage.setText(String.valueOf(data.getVoltage()));
    }


    @OnClick({R.id.ivLeft, R.id.ll_Authorization, R.id.ll_record, R.id.ll_charging, R.id.rl_switch_gun, R.id.to_add_device})
    public void onClickListener(View view) {
        switch (view.getId()) {
            case R.id.ivLeft:
                Intent intent = new Intent(this, MeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;
            case R.id.ll_Authorization:
                if (Cons.mCurrentPile.getType() == 1) {
                    toast(getString(R.string.m66你的账号没有操作权限));
                    return;
                }
                Intent intent2 = new Intent(this, ChargingSetActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent2);
                break;
            case R.id.ll_charging:
                toChargingOrStop();
                break;
            case R.id.rl_switch_gun:
                if (Cons.mCurrentPile.getConnectors() == 1) {//单枪
                    return;
                }
                showStorageList(tvSwitchGun);
                break;
            case R.id.to_add_device:
                Intent intent1 = new Intent(ChargingPileActivity.this, AddChargingActivity.class);
                startActivityForResult(intent1, REQUEST_ADD_CHARGING);
                break;
            case R.id.ll_record:
                jumpTo(ChargingRecoderActivity.class, false);
                break;
        }

    }


    /**
     * 去充电或停充
     */
    private void toChargingOrStop() {
        if (Cons.mCurrentGunBean == null || Cons.mCurrentPile == null) {
            return;
        }
        //获取状态
        String status = Cons.mCurrentGunBean.getData().getStatus();
        //判断桩主或者普通用户
        if (Cons.mCurrentPile.getType() == 0) {//桩主
            switch (status) {
                case GunBean.AVAILABLE://空闲状态，桩主：只能预约
                    if (!isReservation) {
                        toast(getString(R.string.m131空闲状态无法直接开始充电));
                        return;
                    } else {
                        String loopType;
                        if (cbEveryday.isChecked()) {
                            loopType = "0";
                        } else {
                            loopType = "-1";
                        }
                        if (presetType == 0) {//没有选择充电方案
                            //预约充电,只预约了时间
                            //预约充电
                            if (TextUtils.isEmpty(startTime)) {
                                toast(getString(R.string.m130未设置开始时间));
                                return;
                            }
                            requestReserve(0, startTime, "", "", loopType);
                        } else if (presetType == 1) {
                            //设置金额预约
                            //预约充电
                            if (TextUtils.isEmpty(startTime)) {
                                toast(getString(R.string.m130未设置开始时间));
                                return;
                            }
                            requestReserve(1, startTime, "G_SetAmount", reserveMoney, loopType);
                        } else if (presetType == 2) {
                            //预约充电
                            if (TextUtils.isEmpty(startTime)) {
                                toast(getString(R.string.m130未设置开始时间));
                                return;
                            }
                            //设置预约电量
                            requestReserve(2, startTime, "G_SetEnergy", reserveEle, loopType);
                        } else if (presetType == 3) {
                            //预约时段的时候
                            FragmentManager fragmentManager = ChargingPileActivity.this.getSupportFragmentManager();
                            new CircleDialog.Builder().setTitle(getString(R.string.m27温馨提示))
                                    .setText(getString(R.string.m167现在正处于时段预约状态))
                                    .setPositive(getString(R.string.m9确定), null)
                                    .show(fragmentManager);
                        }
                    }

                    break;
                case GunBean.RESERVED:
                case GunBean.PREPARING://准备中
                    //没有预约
                    if (!isReservation) {
                        if (presetType == 0) {//没有选择充电方案
                            requestNarmal(0, "", "");
                        } else if (presetType == 1) {//设置金额预约
                            requestNarmal(1, "G_SetAmount", reserveMoney);
                        } else if (presetType == 2) {//设置预约电量
                            requestNarmal(2, "G_SetEnergy", reserveEle);
                        } else if (presetType == 3) {
                            requestNarmal(3, "G_SetTime", ReserveTime);
                        }

                    } else {
                        String loopType;
                        if (cbEveryday.isChecked()) {
                            loopType = "0";
                        } else {
                            loopType = "-1";
                        }

                        if (presetType == 0) {//没有选择充电方案,只有时间
                            //预约充电
                            if (TextUtils.isEmpty(startTime)) {
                                toast(getString(R.string.m130未设置开始时间));
                                return;
                            }
                            requestReserve(0, startTime, "", "", loopType);
                        } else if (presetType == 1) {//设置金额预约
                            if (TextUtils.isEmpty(startTime)) {
                                toast(getString(R.string.m130未设置开始时间));
                                return;
                            }
                            requestReserve(1, startTime, "G_SetAmount", reserveMoney, loopType);
                        } else if (presetType == 2) {//设置预约电量
                            if (TextUtils.isEmpty(startTime)) {
                                toast(getString(R.string.m130未设置开始时间));
                                return;
                            }
                            requestReserve(2, startTime, "G_SetEnergy", reserveEle, loopType);
                        } else if (presetType == 3) {
                            FragmentManager fragmentManager = ChargingPileActivity.this.getSupportFragmentManager();
                            new CircleDialog.Builder().setTitle(getString(R.string.m27温馨提示))
                                    .setText(getString(R.string.m167现在正处于时段预约状态))
                                    .setPositive(getString(R.string.m9确定), null)
                                    .show(fragmentManager);
                        }
                    }
                    break;

                case GunBean.CHARGING://充电中，点击停止充电
                    requestStop();
                    break;
                case GunBean.EXPIRY:
                    break;
                case GunBean.FAULTED:
                    toast(getString(R.string.m215电桩故障));
                    break;
                case GunBean.FINISHING:
                    requestNarmal(0, "", "");
                    break;
                case GunBean.UNAVAILABLE:
                    toast(getString(R.string.m216桩体状态为不可用));
                    break;
                case GunBean.WORK:
                    break;
                case GunBean.ACCEPTED:
                    break;
            }

        } else {//普通用户
            switch (status) {
                case GunBean.AVAILABLE://空闲状态，桩主：只能预约
                    toast(getString(R.string.m131空闲状态无法直接开始充电));
                    break;
                case GunBean.RESERVED:
                case GunBean.PREPARING://准备中
                    //没有预约
                    if (presetType == 0) {//没有选择充电方案
                        //预约充电
                        requestNarmal(0, "", "");
                    } else if (presetType == 1) {//设置金额预约
                        requestNarmal(1, "G_SetAmount", reserveMoney);
                    } else if (presetType == 2) {//设置预约电量
                        requestNarmal(2, "G_SetEnergy", reserveEle);
                    } else if (presetType == 3) {
                        requestNarmal(3, "G_SetTime", ReserveTime);
                    }
                    break;

                case GunBean.CHARGING://充电中，点击停止充电
                    requestStop();
                    break;
                case GunBean.EXPIRY:
                    tvStatus.setText(getString(R.string.m124已经注销));
                    break;
                case GunBean.FAULTED:
                    tvStatus.setText(getString(R.string.m124已经注销));

                    break;
                case GunBean.FINISHING:
                    tvStatus.setText(getString(R.string.m120充电结束));

                    break;
                case GunBean.UNAVAILABLE:
                    toast(getString(R.string.m216桩体状态为不可用));
                    break;
                case GunBean.WORK:
                    tvStatus.setText(getString(R.string.m126已经工作过));
                    break;

                case GunBean.ACCEPTED:
                    tvStatus.setText(getString(R.string.m125启用中));
                    break;
            }
        }


    }

    /**
     * 初始化列表
     */
    private void initCharging() {
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mAdapter = new ChargingListAdapter(mChargingList);
        mRvCharging.setLayoutManager(mLinearLayoutManager);
        mRvCharging.setAdapter(mAdapter);
        HeadRvAddButton(mChargingList);
    }

    /**
     * 添加项
     */
    private void HeadRvAddButton(List<ChargingBean.DataBean> newList) {
        ChargingBean.DataBean bean = new ChargingBean.DataBean();
        bean.setDevType(ChargingBean.ADD_DEVICE);
        newList.add(bean);
        mAdapter.replaceData(newList);
    }


    private void initListeners() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ChargingBean.DataBean bean = mAdapter.getItem(position);
                int type = bean.getDevType();
                if (type == ChargingBean.ADD_DEVICE) {
                    Intent intent = new Intent(ChargingPileActivity.this, AddChargingActivity.class);
                    startActivityForResult(intent, REQUEST_ADD_CHARGING);
                } else {
                    Cons.mSeletPos = position;
                    refreshChargingUI(position, 1);
                }
            }
        });

        mAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                ChargingBean.DataBean bean = mAdapter.getItem(position);
                requestDelete(bean);
                return false;
            }
        });
    }


    private void requestDelete(final ChargingBean.DataBean bean) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        new CircleDialog.Builder()
                .setWidth(0.75f)
                .setTitle(getString(R.string.m8警告))
                .setText(getString(R.string.m确认删除))
                .setGravity(Gravity.CENTER).setPositive(getString(R.string.m9确定), new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                LogUtil.d("删除充电桩");
                Mydialog.Show(ChargingPileActivity.this);
                isFreshing = true;
                Map<String, Object> jsonMap = new HashMap<String, Object>();
                jsonMap.put("sn", bean.getChargeId());
                jsonMap.put("userId", bean.getUserId());
                jsonMap.put("lan", getLanguage());
                String json = SmartHomeUtil.mapToJsonString(jsonMap);
                LogUtil.i(json);
                PostUtil.postJson(SmartHomeUrlUtil.REQUEST_DELETE_CHARGING, json, new PostUtil.postListener() {
                    @Override
                    public void Params(Map<String, String> params) {

                    }

                    @Override
                    public void success(String json) {
                        Mydialog.Dismiss();
                        isFreshing = false;
                        try {
                            JSONObject object = new JSONObject(json);
                            if (object.getInt("code") == 0) {
                                toast(getString(R.string.m135删除成功));
                                //重新刷新
                                freshData(0, 1);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void LoginError(String str) {
                        isFreshing = false;
                    }


                });
            }
        })
                .setNegative(getString(R.string.m7取消), null)
                .show(fragmentManager);


    }

    /**
     * 切换枪
     *
     * @param v popuwindow显示在哪个view下面
     */
    public void showStorageList(View v) {
        List<GunBean.DataBean> gunlist = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            GunBean.DataBean data = new GunBean().getData();
            data.setConnectorId(i);
            gunlist.add(data);
        }

        View contentView = LayoutInflater.from(this).inflate(
                R.layout.dialog_switch_list, null);
        RecyclerView lv = (RecyclerView) contentView.findViewById(R.id.listView1);
        View flCon = contentView.findViewById(R.id.frameLayout);
        flCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupGun != null) {
                    popupGun.dismiss();
                }
            }
        });
        popGunAdapter = new GunSwitchAdapter(gunlist);
        lv.setLayoutManager(new LinearLayoutManager(this));
        lv.setAdapter(popGunAdapter);
        popGunAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (position != Cons.mCurrentGunBeanId) {
                    Cons.mCurrentGunBeanId = position;
                    String name;
                    if (position == 0) {
                        name = getString(R.string.m110A枪);
                    } else {
                        name = getString(R.string.m111B枪);
                    }
                    tvSwitchGun.setText(name);
                    //刷新充电枪
                    freshChargingGun(Cons.mCurrentPile.getChargeId(), Cons.mCurrentGunBeanId);
                }
            }
        });
        popupGun = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupGun.setTouchable(true);
        popupGun.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupGun.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        popupGun.setBackgroundDrawable(new ColorDrawable(0));
        popupGun.showAsDropDown(v);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            //初始化预设方案和预约的ui
            initReserveUi();
            initPresetUi();
            if (requestCode == REQUEST_MONEY) {
                String money = data.getStringExtra("money");
                reserveMoney = Double.parseDouble(money);
                presetType = 1;
                isReservation = false;

                //设置预设方案的ui
                setMoneyUi(true, money);
                //设置预约的ui
                startTime = null;
                setReserveUi(getString(R.string.m204开始时间), getString(R.string.m184关闭), R.drawable.checkbox_off, "--:--", true);

            }
            if (requestCode == REQUEST_ELE) {
                String electric = data.getStringExtra("electric");
                reserveEle = Double.parseDouble(electric);
                presetType = 2;
                isReservation = false;

                setEleUi(true, electric);
                startTime = null;
                //初始化预约充电相关控件
                setReserveUi(getString(R.string.m204开始时间), getString(R.string.m184关闭), R.drawable.checkbox_off, "--:--", true);
            }
            if (requestCode == REQUEST_TIME) {
                String hour = data.getStringExtra("hour");
                String minute = data.getStringExtra("minute");
                String time = hour + "h" + minute + "min";
                timeEvaryDay = hour + ":" + minute;
                ReserveTime = Integer.parseInt(hour) * 60 + Integer.parseInt(minute);
                presetType = 3;
                setTimeUi(true, time);
                //初始化预约充电相关控件
                isReservation = false;
                setReserveUi(getString(R.string.m预约时间段), getString(R.string.m184关闭), R.drawable.checkbox_off, "--:--", false);
            }
            if (requestCode == REQUEST_OPEN_DURATION) {
                refreshChargingUI(Cons.mSeletPos, Cons.mCurrentGunBeanId);
            }

            if (requestCode == REQUEST_ADD_CHARGING) {
                freshData(0, 1);
            }
        }

    }


    private String[] hours;
    private String[] minutes;

    private void initResource() {
        hours = new String[24];
        for (int i = 0; i < 24; i++) {
            if (i < 10) {
                hours[i] = "0" + String.valueOf(i);
            } else {
                hours[i] = String.valueOf(i);
            }
        }
        minutes = new String[60];

        for (int i = 0; i < minutes.length; i++) {
            if (i < 10) {
                minutes[i] = "0" + String.valueOf(i);
            } else {
                minutes[i] = String.valueOf(i);
            }

        }
    }

    /**
     * 弹起时间选择器
     */
    private void selectTime() {
        AlertPickDialog.showTimePickerDialog(this, hours, "00", minutes, "00", new AlertPickDialog.AlertPickCallBack() {

            @Override
            public void confirm(String hour, String minute) {
                String time = hour + ":" + minute;
                //获取年月
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                String yMd = sdf.format(date);
                startTime = yMd + "T" + time + ":00.000Z";
                isReservation = true;
                setReserveUi(getString(R.string.m204开始时间), getString(R.string.m183开启), R.drawable.checkbox_on, time, true);
            }

            @Override
            public void cancel() {

            }
        });
    }


    /**
     * 正常充电,或预设方案充电
     */
    private void requestNarmal(final int type, String key, Object value) {
        LogUtil.d("正常充电，指令发送");
        Mydialog.Show(this);
        isFreshing = true;
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("action", "remoteStartTransaction");
        jsonMap.put("connectorId", Cons.mCurrentGunBeanId);
        jsonMap.put("userId", Cons.userBean.getId());
        jsonMap.put("chargeId", Cons.mCurrentPile.getChargeId());
        jsonMap.put("lan", getLanguage());
        if (type != 0) {
            jsonMap.put("cKey", key);
            jsonMap.put("cValue", value);
        }
        if (type == 3) {
            jsonMap.put("loopType", -1);
            jsonMap.put("loopValue", timeEvaryDay);
        }
        String json = SmartHomeUtil.mapToJsonString(jsonMap);
        LogUtil.i(json);
        PostUtil.postJson(SmartHomeUrlUtil.REQUEST_RESEERVE_CHARGING, json, new PostUtil.JsonListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                Mydialog.Dismiss();
                isFreshing = false;
                try {
                    JSONObject object = new JSONObject(json);
                    toast(object.getString("data"));
                    cycleTime = 0;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void LoginError(String str) {
                isFreshing = false;
            }

            @Override
            public void sendMsgByTime(Handler handler) {
                freshChargingGun(Cons.mCurrentPile.getChargeId(), Cons.mCurrentGunBeanId);
            }
        });
    }


    /**
     * 请求停止充电
     */
    private void requestStop() {
        LogUtil.d("手动停止充电，指令发送");
        Mydialog.Show(this);
        isFreshing = true;
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("action", "remoteStopTransaction");
        jsonMap.put("connectorId", Cons.mCurrentGunBeanId);
        jsonMap.put("userId", Cons.userBean.getId());
        jsonMap.put("chargeId", Cons.mCurrentPile.getChargeId());
        jsonMap.put("transactionId", transactionId);
        jsonMap.put("lan", getLanguage());
        String json = SmartHomeUtil.mapToJsonString(jsonMap);
        LogUtil.i(json);
        PostUtil.postJson(SmartHomeUrlUtil.REQUEST_RESEERVE_CHARGING, json, new PostUtil.JsonListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                Mydialog.Dismiss();
                isFreshing = false;
                try {
                    JSONObject object = new JSONObject(json);
                    toast(object.getString("data"));
                    cycleTime = 0;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void LoginError(String str) {
                isFreshing = false;
            }

            @Override
            public void sendMsgByTime(Handler handler) {
                //请求成功后5秒钟刷新状态
                freshChargingGun(Cons.mCurrentPile.getChargeId(), Cons.mCurrentGunBeanId);
            }
        });
    }


    /**
     * 预约充电
     */
    private void requestReserve(int type, String expiryDate, String key, Object value, String loopType) {
        LogUtil.d("预约充电，指令发送");

        Date todayDate = new Date();
        long daytime = todayDate.getTime();
        long onTime = 0;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        //开始的日期
        try {
            Date start = format.parse(expiryDate);
            onTime = start.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (daytime > onTime) {
            toast(getString(R.string.m204开始时间));
            return;
        }

        Mydialog.Show(this);
        isFreshing = true;
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("action", "ReserveNow");
        jsonMap.put("expiryDate", expiryDate);
        jsonMap.put("connectorId", Cons.mCurrentGunBeanId);
        jsonMap.put("chargeId", Cons.mCurrentPile.getChargeId());
        jsonMap.put("userId", Cons.userBean.getId());
        jsonMap.put("loopType", loopType);
        jsonMap.put("lan", getLanguage());
        if (loopType.equals("0")) {
            String loopValue = expiryDate.substring(11, 16);
            jsonMap.put("loopValue", loopValue);
        }
        if (type != 0) {
            jsonMap.put("cKey", key);
            jsonMap.put("cValue", value);
        }
        if (type == 3) {
            jsonMap.put("loopType", -1);
            jsonMap.put("loopValue", timeEvaryDay);
        }
        String json = SmartHomeUtil.mapToJsonString(jsonMap);
        LogUtil.i(json);
        PostUtil.postJson(SmartHomeUrlUtil.REQUEST_RESEERVE_CHARGING, json, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                isFreshing = false;
                try {
                    Mydialog.Dismiss();
                    JSONObject object = new JSONObject(json);
                    String data = object.getString("data");
                    toast(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void LoginError(String str) {
                isFreshing = false;
            }
        });
    }


    /**
     * 初始化预设相关ui
     */
    private void initPresetUi() {
        cbPpmoney.setChecked(false);
        tvPpmoney.setText("--");
        cbPpEle.setChecked(false);
        tvPpEle.setText("--");
        cbPpTime.setChecked(false);
        tvPpTime.setText("--");

    }

    /**
     * 设置金额相关ui
     */

    private void setMoneyUi(boolean isCheck, String money) {
        cbPpmoney.setChecked(isCheck);
        tvPpmoney.setText(money);
    }


    /**
     * 设置电量相关ui
     */

    private void setEleUi(boolean isCheck, String ele) {
        cbPpEle.setChecked(isCheck);
        tvPpEle.setText(ele + "kWh");
    }


    /**
     * 设置时长相关ui
     */

    private void setTimeUi(boolean isCheck, String time) {
        cbPpTime.setChecked(isCheck);
        tvPpTime.setText(time);
    }

    /**
     * 初始化预约相关ui
     */
    private void initReserveUi() {
        tvTextStart.setText(getString(R.string.m204开始时间));
        tvTextOpenClose.setText(getString(R.string.m184关闭));
        ivResever.setImageResource(R.drawable.checkbox_off);
        tvStartTime.setText("--:--");
        cbEveryday.setChecked(false);
        MyUtil.showAllView(tvEveryDay, cbEveryday);
    }

    /**
     * 设置预约相关ui
     *
     * @param startText
     * @param onOffText
     * @param resCheckbox
     * @param startTime
     * @param everyDay
     */
    private void setReserveUi(String startText, String onOffText, int resCheckbox, String startTime, boolean everyDay) {
        tvTextStart.setText(startText);
        tvTextOpenClose.setText(onOffText);
        ivResever.setImageResource(resCheckbox);
        tvStartTime.setText(startTime);
        if (everyDay) {
            MyUtil.showAllView(tvEveryDay, cbEveryday);
        } else {
            MyUtil.hideAllView(View.GONE, tvEveryDay, cbEveryday);
        }
    }

    /**
     * 状态改变时设置充电枪相关Ui
     */
    private void setChargGunUi(int resStatus, String textStatus, int statusColor, int resOnOff, String textOnOff) {
        ivChargingIcon.setImageResource(resStatus);
        tvStatus.setText(textStatus);
        tvStatus.setTextColor(statusColor);
        ivChargingStatus.setImageResource(resOnOff);
        tvChargingStatus.setText(textOnOff);
    }

    /**
     * 开始旋转动画
     */

    private void startAnim() {
        MyUtil.hideAllView(View.GONE, ivfinishBackground);
        MyUtil.showAllView(ivAnim);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.pile_charging);
        ivAnim.startAnimation(animation);
    }

    /**
     * 完成充电
     */
    private void stopAnim() {
        MyUtil.hideAllView(View.GONE);
        MyUtil.showAllView(ivfinishBackground);
        ivAnim.clearAnimation();
        ivfinishBackground.setImageResource(R.drawable.charging_finish_anim);
    }

    /**
     * 隐藏动画
     */
    private void hideAnim() {
        ivAnim.clearAnimation();
        MyUtil.hideAllView(View.GONE, ivAnim, ivfinishBackground);
    }

    /**
     * @param keyCode
     * @param event
     * @return
     */

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                toast(R.string.m确认退出);
                mExitTime = System.currentTimeMillis();
            } else {
                MyApplication.getInstance().exit();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
