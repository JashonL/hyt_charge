package com.growatt.chargingpile.activity;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.EventBusMsg.AddDevMsg;
import com.growatt.chargingpile.EventBusMsg.FreshListMsg;
import com.growatt.chargingpile.EventBusMsg.FreshTimingMsg;
import com.growatt.chargingpile.EventBusMsg.RefreshRateMsg;
import com.growatt.chargingpile.EventBusMsg.SearchDevMsg;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.adapter.ChargingListAdapter;
import com.growatt.chargingpile.adapter.GunSwitchAdapter;
import com.growatt.chargingpile.adapter.ReservaCharingAdapter;
import com.growatt.chargingpile.application.MyApplication;
import com.growatt.chargingpile.bean.ChargeStartFreshMsg;
import com.growatt.chargingpile.bean.ChargingBean;
import com.growatt.chargingpile.bean.GunBean;
import com.growatt.chargingpile.bean.NoConfigBean;
import com.growatt.chargingpile.bean.PileSetBean;
import com.growatt.chargingpile.bean.ReservationBean;
import com.growatt.chargingpile.connutil.PostUtil;
import com.growatt.chargingpile.jpush.PushUtils;
import com.growatt.chargingpile.jpush.TagAliasOperatorHelper;
import com.growatt.chargingpile.util.CircleDialogUtils;
import com.growatt.chargingpile.util.Cons;
import com.growatt.chargingpile.util.Constant;
import com.growatt.chargingpile.util.CustomTimer;
import com.growatt.chargingpile.util.MathUtil;
import com.growatt.chargingpile.util.MyUtil;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.util.SharedPreferencesUnit;
import com.growatt.chargingpile.util.SmartHomeUrlUtil;
import com.growatt.chargingpile.util.SmartHomeUtil;
import com.growatt.chargingpile.view.RoundProgressBar;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.view.listener.OnInputClickListener;
import com.mylhyl.circledialog.view.listener.OnLvItemClickListener;
import com.tencent.mmkv.MMKV;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.util.LogUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

import static com.growatt.chargingpile.jpush.TagAliasOperatorHelper.ALIAS_ACTION;
import static com.growatt.chargingpile.jpush.TagAliasOperatorHelper.ALIAS_DATA;


public class ChargingPileActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {

    @BindView(R.id.ivRight)
    ImageView ivSetting;

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


    /*???????????????*/
    @BindView(R.id.rv_charging)
    RecyclerView mRvCharging;
    private List<ChargingBean.DataBean> mChargingList = new ArrayList<>();
    private ChargingListAdapter mAdapter;

    /*?????????????????????*/
    @BindView(R.id.rl_solar)
    RelativeLayout mRlSolar;

    @BindView(R.id.iv_limit)
    ImageView mIvLimit;

    @BindView(R.id.tv_solar)
    TextView mTvSolar;

    TextView mTvContent;

    @BindView(R.id.rl_switch_gun)
    RelativeLayout rlSwitchGun;


    //???????????????popuwindow
    private PopupWindow popupGun;
    private GunSwitchAdapter popGunAdapter;


    //???????????? ??????  ?????? ?????????
    public static final int REQUEST_MONEY = 100;
    public static final int REQUEST_ELE = 101;
    public static final int REQUEST_TIME = 102;


    //?????????????????? 0 :????????????????????????  1?????????  2?????????  3?????????
    private int presetType = 0;

    private boolean isReservation = false;//????????????
    private String startTime;//??????????????????
    private double reserveMoney;//????????????
    private double reserveEle;//????????????
    private int reserveTime;//????????????(??????)
    private String timeEvaryDay;//??????????????????????????????

    //--------------??????---------------
    private View availableView;

    //--------------?????????---------------
    private View preparingView;
    private TextView tvPpmoney;
    private ImageView ivPpmoney;
    private TextView tvPpEle;
    private ImageView ivPpEle;
    private TextView tvPpTime;
    private ImageView ivPpTime;
    private LinearLayout llReserveView;
    private TextView tvStartTime;
    private ImageView ivResever;
    private TextView tvTextStart;
    private TextView tvTextOpenClose;
    private CheckBox cbEveryday;
    private TextView tvEveryDay;
    private LinearLayout llReserve;

    //--------------?????????---------------
    //????????????
    private View normalChargingView;
    private TextView tvChargingEle;
    private TextView tvChargingRate;
    private TextView tvChargingCurrent;
    private TextView tvChargingDuration;
    private TextView tvChargingMoney;
    private TextView tvChargingVoltage;
    //??????????????????
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
    private TextView tvPercentCenter;


    private int transactionId;//?????????????????????????????????

    /*????????????*/
    private View chargeFinishView;
    private TextView tvFinishEle;
    private TextView tvFinishRate;
    private TextView tvFinishTime;
    private TextView tvFinishMoney;

    /*??????*/
    private View chargeSuspendeevView;


    /*??????*/
    private View chargeFaultedView;

    /*??????*/
    private View chargeExpiryView;

    /*?????????*/
    private View chargeUnvailableView;

    /*???????????????*/
    private View chargeWorkedView;


    /*????????????*/
    private View reservationView;
    private TextView tvTimeKey;
    private TextView tvRateValue;
    private LinearLayout llTimeRate;
    private RecyclerView rvTimeReserva;
    private TextView tvTips;
    private TextView tvReserValue;
    private TextView tvReserType;
    private TextView tvReserCalValue;
    private TextView tvReserTypeText;
    private ImageView ivReserChargedType;
    private Group gpPreset;


    //?????????????????????
    private String currenStatus = GunBean.NONE;


    private long mExitTime;


    private Animation animation;

    //??????????????????
    public ChargingBean.DataBean mCurrentPile;
    //??????????????????
    public GunBean mCurrentGunBean;

    //???????????????????????????????????????
    private Map<String, Integer> gunIds = new HashMap<>();

    private ReservaCharingAdapter reservaAdapter;
    private List<ReservationBean.DataBean> reserveNow;
    public String[] solarArrray;
    private String searchId;


    private String moneyUnit = "";

    private boolean isfirst = true;
    private String jumpId;
    private DialogFragment dialogFragment;

    //??????????????????

    private CustomTimer mOperaterTimer;

    //????????????????????????
    private CustomTimer mFreshTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charging_pile);
        ButterKnife.bind(this);

        experience();

        initPermission();
        initHeaderViews();
        initCharging();
        initListeners();
        initPullView();
        initStatusView();
        initResource();
//        setJPushAlias();
        freshData();
        //??????????????????
        startFreshTimer();
    }

    //????????????????????????
    private void experience() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String nowDate = simpleDateFormat.format(date);
        if ("20220630".equals(nowDate)){
            MyApplication.getInstance().exit();
            finish();
        }
    }


    /**
     * ???????????????????????????
     *
     * @param period
     * @param delay
     */

    int taskRunTimes = 0;

    private void startOperaterTimer(long period) {
        stopOperaterTimer();
        //???????????????????????????????????????
        mOperaterTimer = new CustomTimer(() -> {
            if (TextUtils.isEmpty(currenStatus)) {
                return;
            }
            try {
                if (taskRunTimes <= 3) {
                    Integer gunId = gunIds.get(mCurrentPile.getChargeId());
                    if (gunId == null) gunId = 1;
                    freshChargingGun(mCurrentPile.getChargeId(), gunId);
                    taskRunTimes++;
                } else {
                    mOperaterTimer.timerDestroy();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, period, 300);

        mOperaterTimer.timerStart();
    }


    private void stopOperaterTimer() {
        taskRunTimes = 0;
        if (mOperaterTimer != null) {
            mOperaterTimer.timerDestroy();
            mOperaterTimer = null;
        }
    }


    /**
     * ???????????????????????????
     */

    private void startFreshTimer() {
        //???????????????????????????????????????
        mFreshTimer = new CustomTimer(() -> {
            try {
                if (mCurrentPile != null) {
                    Integer gunId = gunIds.get(mCurrentPile.getChargeId());
                    if (gunId == null) gunId = 1;

                    if (GunBean.PREPARING.equals(currenStatus)){
                        timeTaskRefresh(mCurrentPile.getChargeId(), gunId);
                    }else {
                        freshChargingGun(mCurrentPile.getChargeId(), gunId);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 60 * 1000, 1000 * 60);

        mFreshTimer.timerStart();
    }


    private void stopFreshTimer() {
        if (mFreshTimer != null) {
            mFreshTimer.timerDestroy();
            mFreshTimer = null;
        }
    }


    private void setJPushAlias() {
        String registrationId = PushUtils.getRegistrationId(this);
        MMKV.defaultMMKV().putString(ALIAS_DATA, Cons.userBean.getAccountName());
        MMKV.defaultMMKV().putInt(ALIAS_ACTION, 1);
        PushUtils.setAlias(this,Cons.userBean.getAccountName(),PushUtils.sequence++);
    }


    private void initPermission() {
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(this, String.format("%s:%s", getString(R.string.m???????????????????????????), getString(R.string.m??????)), 11001, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //?????????????????????????????????????????????
        if (mFreshTimer != null && mFreshTimer.isPause()) {
            //????????????
            mFreshTimer.timerRuning();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        animation = null;
        if (mFreshTimer != null && !mFreshTimer.isPause()) {
            //????????????
            mFreshTimer.timerPause();
        }
    }


    private void initPullView() {
        srlPull.setColorSchemeColors(ContextCompat.getColor(this, R.color.maincolor_1));
        srlPull.setOnRefreshListener(this::freshData);
    }


    /**
     * ???????????????
     */
    private void initHeaderViews() {
        ivUserCenter.setImageResource(R.drawable.user_index);
        ivSetting.setImageResource(R.drawable.link_wifi_set1);
    }


    /**
     * ?????????????????????View
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
        initSuspendeevView();
        initReservation();
    }

    private void initReservation() {
        reservationView = LayoutInflater.from(this).inflate(R.layout.status_charging_reservation_layout, mStatusGroup, false);
        tvTimeKey = reservationView.findViewById(R.id.tv_time_key);
        tvRateValue = reservationView.findViewById(R.id.tv_rate_value);
        llTimeRate = reservationView.findViewById(R.id.ll_time_rate);
        rvTimeReserva = reservationView.findViewById(R.id.rv_time_reserva);
        gpPreset = reservationView.findViewById(R.id.gp_preset);
        tvTips = reservationView.findViewById(R.id.tv_tips);
        tvReserValue = reservationView.findViewById(R.id.tv_preset_value);
        tvReserType = reservationView.findViewById(R.id.tv_preset_type);
        tvReserCalValue = reservationView.findViewById(R.id.tv_preset_value_cal);
        tvReserTypeText = reservationView.findViewById(R.id.tv_type_text);
        ivReserChargedType = reservationView.findViewById(R.id.icon_charged_type);
        rvTimeReserva.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        reservaAdapter = new ReservaCharingAdapter(R.layout.item_raserva_time, new ArrayList<>());
        rvTimeReserva.setAdapter(reservaAdapter);
        reservaAdapter.setOnItemClickListener(this);
    }

    private void initSuspendeevView() {
        chargeSuspendeevView = LayoutInflater.from(this).inflate(R.layout.status_charging_suspendeev_layout, mStatusGroup, false);
        mTvContent = chargeSuspendeevView.findViewById(R.id.tv_content);
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
     * ??????
     */
    private void initAvailableView() {
        availableView = LayoutInflater.from(this).inflate(R.layout.status_charging_available, mStatusGroup, false);
    }


    /**
     * ?????????
     */
    private void initPreparingView() {
        preparingView = LayoutInflater.from(this).inflate(R.layout.status_charging_prepare_layout, mStatusGroup, false);
        //??????
        RelativeLayout rlPpmoney = preparingView.findViewById(R.id.rl_prepare_money);
        RelativeLayout rlPpmoneyEdit = preparingView.findViewById(R.id.rl_prepare_money_edit);
        tvPpmoney = preparingView.findViewById(R.id.tv_prepare_money_num);
        ivPpmoney = preparingView.findViewById(R.id.iv_prepare_money_select);
        //??????
        RelativeLayout rlPpEle = preparingView.findViewById(R.id.rl_prepare_ele);
        RelativeLayout rlPpEleEdit = preparingView.findViewById(R.id.rl_prepare_ele_edit);
        tvPpEle = preparingView.findViewById(R.id.tv_prepare_ele_num);
        ivPpEle = preparingView.findViewById(R.id.iv_prepare_ele_select);
        //??????
        RelativeLayout rlPpTime = preparingView.findViewById(R.id.rl_prepare_time);
        RelativeLayout rlPpTimeEdit = preparingView.findViewById(R.id.rl_prepare_time_edit);
        tvPpTime = preparingView.findViewById(R.id.tv_prepare_time_num);
        ivPpTime = preparingView.findViewById(R.id.iv_prepare_time_select);
        //????????????
        tvTextStart = preparingView.findViewById(R.id.tv_time);
        //??????????????????
        tvTextOpenClose = preparingView.findViewById(R.id.tv_text_open_close);
        cbEveryday = preparingView.findViewById(R.id.cb_everyday);
        tvEveryDay = preparingView.findViewById(R.id.tv_time_every_day);

        cbEveryday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                tvEveryDay.setTextColor(ContextCompat.getColor(this, R.color.main_text_color));
            } else {
                tvEveryDay.setTextColor(ContextCompat.getColor(this, R.color.title_2));
            }
        });

        rlPpmoney.setOnClickListener(v -> {
            if (SmartHomeUtil.isFlagUser()) {
                toast(getString(R.string.m66??????????????????????????????));
                return;
            }
            if (presetType == 1) {
                setMoneyUi(false, "--");
                presetType = 0;
            } else {
                presetType = 1;
                setMoneyUi(true, "");
            }
        });


        rlPpmoneyEdit.setOnClickListener(v -> {
            if (SmartHomeUtil.isFlagUser()) {
                toast(getString(R.string.m66??????????????????????????????));
                return;
            }
            Intent intent = new Intent(ChargingPileActivity.this, ChargingPresetEditActivity.class);
            intent.putExtra("type", 1);
            intent.putExtra("symbol", moneyUnit);
            startActivityForResult(intent, REQUEST_MONEY);
        });


        rlPpEle.setOnClickListener(v -> {
            if (SmartHomeUtil.isFlagUser()) {
                toast(getString(R.string.m66??????????????????????????????));
                return;
            }
            if (presetType == 2) {
                setEleUi(false, "--kWh");
                presetType = 0;
            } else {
                presetType = 2;
                setEleUi(true, "");
            }

        });

        rlPpEleEdit.setOnClickListener(v -> {
            if (SmartHomeUtil.isFlagUser()) {
                toast(getString(R.string.m66??????????????????????????????));
                return;
            }
            Intent intent = new Intent(ChargingPileActivity.this, ChargingPresetEditActivity.class);
            intent.putExtra("type", 2);
            startActivityForResult(intent, REQUEST_ELE);
        });


        rlPpTime.setOnClickListener(v -> {
            if (SmartHomeUtil.isFlagUser()) {
                toast(getString(R.string.m66??????????????????????????????));
                return;
            }
            LogUtil.d("????????????" + presetType);
            if (presetType == 3) {
                setTimeUi(false, "-h-min");
                presetType = 0;
//                isReservation = false;
//                setReserveUi(getString(R.string.m204????????????), getString(R.string.m206?????????), R.drawable.checkbox_off, "--:--", true, false);
            } else {
                presetType = 3;
                setTimeUi(true, "");

            }

        });

        rlPpTimeEdit.setOnClickListener(v -> {
            if (SmartHomeUtil.isFlagUser()) {
                toast(getString(R.string.m66??????????????????????????????));
                return;
            }
            Intent intent = new Intent(ChargingPileActivity.this, ChargingPresetEditActivity.class);
            intent.putExtra("type", 3);
            startActivityForResult(intent, REQUEST_TIME);
        });


        //??????
        llReserveView = preparingView.findViewById(R.id.ll_reserve_view);
        tvStartTime = preparingView.findViewById(R.id.tv_start_time);
        ivResever = preparingView.findViewById(R.id.iv_resever_switch);
        llReserve = preparingView.findViewById(R.id.ll_reserve);
        ivResever.setOnClickListener(v -> {
            if (SmartHomeUtil.isFlagUser()) {
                toast(getString(R.string.m66??????????????????????????????));
                return;
            }
            if (presetType == 3) {
                String duration = tvPpTime.getText().toString();
                if (TextUtils.isEmpty(duration) || duration.contains("-")) {//???????????????
                    //?????????????????????
                    Intent intent = new Intent(ChargingPileActivity.this, ChargingDurationActivity.class);
                    intent.putExtra("sn", mCurrentPile.getChargeId());
                    jumpTo(intent, false);
                } else {
                    if (isReservation) {
                        isReservation = false;
                        initReserveUi();
                    } else {
                        selectTime();
                    }
                }

            } else {
                if (isReservation) {
                    isReservation = false;
                    initReserveUi();
                } else {
                    selectTime();
                }

            }
        });


        llReserveView.setOnClickListener(v -> {
            if (SmartHomeUtil.isFlagUser()) {
                toast(getString(R.string.m66??????????????????????????????));
                return;
            }
            if (presetType == 3) {
                String duration = tvPpTime.getText().toString();
                if (TextUtils.isEmpty(duration) || duration.contains("-")) {//???????????????
                    //?????????????????????
                    Intent intent = new Intent(ChargingPileActivity.this, ChargingDurationActivity.class);
                    intent.putExtra("sn", mCurrentPile.getChargeId());
                    jumpTo(intent, false);
                } else {
                    selectTime();
                }

            } else {
                selectTime();
            }
        });

    }


    /**
     * ?????????
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
        tvPercentCenter = presetChargingView.findViewById(R.id.tv_percent_center);

    }


    /**
     * ????????????
     */
    private void initChargeFinshView() {
        chargeFinishView = LayoutInflater.from(this).inflate(R.layout.status_charging_finish_layout, mStatusGroup, false);
        tvFinishEle = chargeFinishView.findViewById(R.id.tv_ele);
        tvFinishRate = chargeFinishView.findViewById(R.id.tv_rate);
        tvFinishTime = chargeFinishView.findViewById(R.id.tv_time);
        tvFinishMoney = chargeFinishView.findViewById(R.id.tv_money);
    }


    /**
     * ??????????????????
     * position :??????????????????????????????
     * millis
     */
    private void freshData() {
        Mydialog.Show(this);
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("userId", SmartHomeUtil.getUserName());//??????id
        if (!TextUtils.isEmpty(searchId)) jsonMap.put("chargeId", searchId);
        jsonMap.put("lan", getLanguage());
        String json = SmartHomeUtil.mapToJsonString(jsonMap);
        LogUtil.i(json);
        PostUtil.postJson(SmartHomeUrlUtil.postGetChargingList(), json, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                srlPull.setRefreshing(false);
                try {
                    List<ChargingBean.DataBean> charginglist = new ArrayList<>();
                    JSONObject object = new JSONObject(json);
                    int currentPos = 0;
                    if (object.getInt("code") == 0) {
                        ChargingBean chargingListBean = new Gson().fromJson(json, ChargingBean.class);
                        if (chargingListBean != null) {
                            jumpId = getIntent().getStringExtra("chargeId");
                            charginglist = chargingListBean.getData();
                            if (charginglist == null) charginglist = new ArrayList<>();
                            for (int i = 0; i < charginglist.size(); i++) {
                                ChargingBean.DataBean bean = charginglist.get(i);
                                bean.setDevType(ChargingBean.CHARGING_PILE);
                                bean.setName(bean.getName());
                                String chargeId = bean.getChargeId();
                                if (chargeId.equals(jumpId)) currentPos = i;
                            }

                        }

                    }
                    //?????????????????????
                    if (charginglist.size() > 0) {
                        HeadRvAddButton(charginglist);
                        mAdapter.replaceData(charginglist);
                        if (isfirst) {
                            mAdapter.setNowSelectPosition(currentPos);
                            isfirst = false;
                        }
                        MyUtil.hideAllView(View.GONE, emptyPage);
                        MyUtil.showAllView(rlCharging, linearlayout);
                        refreshChargingUI();
                    } else {
                        mAdapter.replaceData(charginglist);
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
                Mydialog.Dismiss();
                srlPull.setRefreshing(false);
                MyUtil.hideAllView(View.GONE, rlCharging);
                MyUtil.showAllView(emptyPage);
            }
        });

    }


    /**
     * ?????????????????????????????????ui
     */

    private void refreshChargingUI() {
        int nowSelectPosition = mAdapter.getNowSelectPosition();
        mCurrentPile = mAdapter.getData().get(nowSelectPosition);
        if (!TextUtils.isEmpty(mCurrentPile.getSymbol())) moneyUnit = mCurrentPile.getSymbol();
        //????????????
        String Modle = mCurrentPile.getModel();
        if (Modle.toLowerCase().contains("/")) {
            tvModel.setText(getString(R.string.m?????????));
        } else if ("ac".equals(Modle.toLowerCase())) {
            tvModel.setText(getString(R.string.m112??????));
        } else {
            tvModel.setText(getString(R.string.m113??????));
        }

        String gun = mCurrentPile.getConnectors() + " " + getString(R.string.???);

        if (mCurrentPile.getConnectors() == 1) {
            rlSwitchGun.setVisibility(View.GONE);
            tvGun.setText(gun);
        } else {
            rlSwitchGun.setVisibility(View.VISIBLE);
            tvGun.setText(gun);
        }
        //?????????????????????
//        int solar = mCurrentPile.getSolar();
        int solar = mCurrentPile.getG_SolarMode();
        initSolarUi(solar);
        //??????????????????????????????????????????,????????????A???
        Integer gunId = gunIds.get(mCurrentPile.getChargeId());
        if (gunId == null) gunId = 1;
        freshChargingGun(mCurrentPile.getChargeId(), gunId);
        mFreshTimer.timerRuning();
    }


    /**
     * ?????????????????????
     * ???????????????????????????????????????ui
     */

    private void timeTaskRefresh(final String chargingId, final int connectorId) {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("sn", chargingId);//??????id
        jsonMap.put("connectorId", connectorId);//??????id
        jsonMap.put("userId", SmartHomeUtil.getUserName());//??????id
        jsonMap.put("lan", getLanguage());//??????id
        String json = SmartHomeUtil.mapToJsonString(jsonMap);
        LogUtil.i(json);
        PostUtil.postJson(SmartHomeUrlUtil.postGetChargingGunNew(), json, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                try {
                    JSONObject object = new JSONObject(json);
                    if (object.getInt("code") == 0) {
                        GunBean gunBean = new Gson().fromJson(json, GunBean.class);
                        GunBean.DataBean data = gunBean.getData();
                        mCurrentGunBean = gunBean;
                        if (data != null) {
                            String status = data.getStatus();
                            //????????????,???????????????????????????
                            if (!status.equals(currenStatus)) {
                                currenStatus = status;
                            }
                        }
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


    /**
     * ?????????????????????
     * ??????
     * chargingId ????????????id
     * connectorId ????????????id
     */

    private void freshChargingGun(final String chargingId, final int connectorId) {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("sn", chargingId);//??????id
        jsonMap.put("connectorId", connectorId);//??????id
        jsonMap.put("userId", SmartHomeUtil.getUserName());//??????id
        jsonMap.put("lan", getLanguage());//??????id
        String json = SmartHomeUtil.mapToJsonString(jsonMap);
        LogUtil.i(json);
        PostUtil.postJson(SmartHomeUrlUtil.postGetChargingGunNew(), json, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                try {
                    JSONObject object = new JSONObject(json);
                    if (object.getInt("code") == 0) {
                        GunBean gunBean = new Gson().fromJson(json, GunBean.class);
                        if (gunBean != null) {
                            gunBean.getData().setConnectorId(connectorId);
                            mCurrentGunBean = gunBean;
                            refreshBygun(gunBean);
                        }
                    }

                } catch (Exception e) {
                    mStatusGroup.removeAllViews();
                    hideAnim();
                    mStatusGroup.addView(chargeUnvailableView);
                    setChargGunUi(R.drawable.charging_unavailable, getString(R.string.m122?????????), ContextCompat.getColor(ChargingPileActivity.this, R.color.title_3), R.drawable.btn_start_charging, getString(R.string.m122?????????));
                    MyUtil.showAllView(llBottomGroup);
                }

            }

            @Override
            public void LoginError(String str) {

            }

        });

    }

    /**
     * ?????????????????????ui
     *
     * @param gunBean ?????????
     */
    private void refreshBygun(GunBean gunBean) {
        mStatusGroup.removeAllViews();
        //?????????????????????
        GunBean.DataBean data = gunBean.getData();
        if (data == null) {
            hideAnim();
            mStatusGroup.addView(chargeUnvailableView);
            setChargGunUi(R.drawable.charging_unavailable, getString(R.string.m122?????????), ContextCompat.getColor(this, R.color.title_3), R.drawable.btn_start_charging, getString(R.string.m103??????));
            MyUtil.showAllView(llBottomGroup);
            return;
        }
        String name = SmartHomeUtil.getLetter().get(data.getConnectorId() - 1) + " " + getString(R.string.???);
        tvSwitchGun.setText(name);
        /*//????????????????????????????????????
        getLastAction();*/
        //????????????????????????
        String status = data.getStatus();
        switch (status) {
            case GunBean.AVAILABLE://??????
                if (mCurrentPile.getType() == 0) {//??????
                    initPresetUi();
                    initReserveUi();
                    mStatusGroup.addView(preparingView);
                    MyUtil.showAllView(llReserveView, llReserve);
                } else {//????????????
                    mStatusGroup.addView(availableView);
                    MyUtil.hideAllView(View.GONE, llReserveView, llReserve);
                }
                hideAnim();
                setChargGunUi(R.drawable.charging_available, getString(R.string.m117??????), ContextCompat.getColor(this, R.color.title_1), R.drawable.btn_start_charging, getString(R.string.m103??????));
                MyUtil.showAllView(llBottomGroup);
                break;
            case GunBean.ACCEPTED:
            case GunBean.RESERVENOW:
            case GunBean.RESERVED:
                mStatusGroup.addView(reservationView);
                hideAnim();
                setChargGunUi(R.drawable.charging_available, getString(R.string.m339??????), ContextCompat.getColor(this, R.color.charging_text_color_2), R.drawable.btn_stop_charging, getString(R.string.m340????????????));
                MyUtil.showAllView(llBottomGroup);
                getReservaNow();
                break;
            case GunBean.PREPARING:
                initPresetUi();
                initReserveUi();
                mStatusGroup.addView(preparingView);
                if (mCurrentPile.getType() == 0) {
                    MyUtil.showAllView(llReserveView, llReserve);
                } else {
                    MyUtil.hideAllView(View.GONE, llReserveView, llReserve);
                }
                hideAnim();
                setChargGunUi(R.drawable.charging_available, getString(R.string.m119?????????), ContextCompat.getColor(this, R.color.title_1), R.drawable.btn_start_charging, getString(R.string.m103??????));
                MyUtil.showAllView(llBottomGroup);
                break;

            case GunBean.CHARGING:
                transactionId = data.getTransactionId();
                setChargGunUi(R.drawable.charging_available, getString(R.string.m118?????????), ContextCompat.getColor(this, R.color.title_1), R.drawable.btn_stop_charging, getString(R.string.m108????????????));
                MyUtil.showAllView(llBottomGroup);
                startAnim();
                String presetType = data.getcKey();
                if ("0".equals(presetType) || TextUtils.isEmpty(presetType)) {
                    mStatusGroup.addView(normalChargingView);
                    setNormalCharging(data);
                } else {
                    String money = MathUtil.roundDouble2String(data.getCost(), 3);
                    if (!TextUtils.isEmpty(moneyUnit)) {
                        money = moneyUnit + money;
                    }
                    String energy = MathUtil.roundDouble2String(data.getEnergy(), 3) + "kWh";
                    int timeCharging = data.getCtime();
                    int hourCharging = timeCharging / 60;
                    int minCharging = timeCharging % 60;
                    String sTimeCharging = hourCharging + "h" + minCharging + "min";
                    switch (presetType) {
                        case "G_SetAmount":
                            mStatusGroup.addView(presetChargingView);
                            String scheme = String.format(getString(R.string.m198??????????????????) + "-%s", getString(R.string.m200??????));
                            setPresetChargingUi(scheme, moneyUnit + data.getcValue(), money, getString(R.string.m192????????????),
                                    R.drawable.charging_ele, energy, getString(R.string.m189????????????), R.drawable.charging_time, sTimeCharging, getString(R.string.m191????????????),
                                    Double.parseDouble(data.getcValue()), data.getCost(),
                                    String.valueOf(data.getRate()), data.getCurrent() + "A", data.getVoltage() + "V");
                            break;

                        case "G_SetEnergy":
                            mStatusGroup.addView(presetChargingView);
                            String scheme1 = String.format(getString(R.string.m198??????????????????) + "-%s", getString(R.string.m201??????));
                            setPresetChargingUi(scheme1, data.getcValue() + "kWh", energy, getString(R.string.m189????????????),
                                    R.drawable.charging_money, money, getString(R.string.m192????????????), R.drawable.charging_time, sTimeCharging, getString(R.string.m191????????????),
                                    Double.parseDouble(data.getcValue()), data.getEnergy(),
                                    String.valueOf(data.getRate()), data.getCurrent() + "A", data.getVoltage() + "V");
                            break;

                        default:
                            mStatusGroup.addView(presetChargingView);
                            String scheme2 = String.format(getString(R.string.m198??????????????????) + "-%s", getString(R.string.m202??????));
                            double presetTime = Double.parseDouble(data.getcValue());
                            int hourPreset = (int) (presetTime / 60);
                            int minPreset = (int) (presetTime % 60);
                            String sTimePreset = hourPreset + "h" + minPreset + "min";
                            setPresetChargingUi(scheme2, sTimePreset, sTimeCharging, getString(R.string.m191????????????),
                                    R.drawable.charging_money, money, getString(R.string.m192????????????), R.drawable.charging_ele, energy, getString(R.string.m189????????????),
                                    Double.parseDouble(data.getcValue()), data.getCtime(),
                                    String.valueOf(data.getRate()), data.getCurrent() + "A", data.getVoltage() + "V");
                            break;

                    }
                }
                break;

            case GunBean.SUSPENDEEV:
                hideAnim();
                mStatusGroup.addView(chargeSuspendeevView);
                mTvContent.setText(R.string.m293?????????????????????);
                setChargGunUi(R.drawable.charging_unavailable, getString(R.string.m133???????????????), ContextCompat.getColor(this, R.color.title_1), R.drawable.btn_start_charging, getString(R.string.m103??????));
                MyUtil.showAllView(llBottomGroup);
                break;
            case GunBean.SUSPENDEDEVSE:
                hideAnim();
                mTvContent.setText(R.string.m294?????????????????????);
                mStatusGroup.addView(chargeSuspendeevView);
                setChargGunUi(R.drawable.charging_unavailable, getString(R.string.m292???????????????), ContextCompat.getColor(this, R.color.title_1), R.drawable.btn_start_charging, getString(R.string.m103??????));
                MyUtil.showAllView(llBottomGroup);
                break;

            case GunBean.FINISHING:
                int timeFinishing = data.getCtime();
                int hourFinishing = timeFinishing / 60;
                int minFinishing = timeFinishing % 60;
                String sTimeFinishing = hourFinishing + "h" + minFinishing + "min";
                mStatusGroup.addView(chargeFinishView);
                String energy = MathUtil.roundDouble2String(data.getEnergy(), 2) + "kWh";
                tvFinishEle.setText(energy);
                tvFinishRate.setText(String.valueOf(data.getRate()));
                tvFinishTime.setText(sTimeFinishing);
                String cost = MathUtil.roundDouble2String(data.getCost(), 2);
                if (!TextUtils.isEmpty(moneyUnit)) {
                    cost = moneyUnit + cost;
                }
                tvFinishMoney.setText(cost);
                stopAnim();
                MyUtil.showAllView(llBottomGroup);
                setChargGunUi(R.drawable.charging_available, getString(R.string.m120????????????), ContextCompat.getColor(this, R.color.title_1), R.drawable.btn_stop_charging, getString(R.string.m108????????????));
                break;

            case GunBean.EXPIRY:
                hideAnim();
                mStatusGroup.addView(chargeExpiryView);
                setChargGunUi(R.drawable.charging_available, getString(R.string.m118?????????), ContextCompat.getColor(this, R.color.title_1), R.drawable.btn_start_charging, getString(R.string.m103??????));
                MyUtil.hideAllView(View.GONE, llBottomGroup);
                break;
            case GunBean.FAULTED:
                hideAnim();
                mStatusGroup.addView(chargeFaultedView);
                setChargGunUi(R.drawable.charging_available, getString(R.string.m121??????), ContextCompat.getColor(this, R.color.charging_fault), R.drawable.btn_start_charging, getString(R.string.m103??????));
                MyUtil.showAllView(llBottomGroup);
                break;

            case GunBean.UNAVAILABLE:
                hideAnim();
                mTvContent.setText(R.string.m216????????????????????????);
                mStatusGroup.addView(chargeUnvailableView);
                setChargGunUi(R.drawable.charging_unavailable, getString(R.string.m122?????????), ContextCompat.getColor(this, R.color.charging_fault), R.drawable.btn_start_charging, getString(R.string.m103??????));
                MyUtil.showAllView(llBottomGroup);
                break;
            case GunBean.WORK:
                hideAnim();
                mStatusGroup.addView(chargeWorkedView);
                setChargGunUi(R.drawable.charging_available, getString(R.string.m126???????????????), ContextCompat.getColor(this, R.color.title_1), R.drawable.btn_start_charging, getString(R.string.m103??????));
                MyUtil.hideAllView(View.GONE, llBottomGroup);
                break;

            default:
                hideAnim();
                mStatusGroup.addView(chargeUnvailableView);
                setChargGunUi(R.drawable.charging_unavailable, getString(R.string.m122?????????), ContextCompat.getColor(this, R.color.charging_fault), R.drawable.btn_start_charging, getString(R.string.m103??????));
                MyUtil.showAllView(llBottomGroup);
                break;
        }
        //????????????  ????????????????????????
        if (!status.equals(currenStatus)) {
            currenStatus = status;
            stopOperaterTimer();
        }
    }


    private void setNormalCharging(GunBean.DataBean data) {
        String symbol = data.getSymbol();
        int timeCharging = data.getCtime();
        int hourCharging = timeCharging / 60;
        int minCharging = timeCharging % 60;
        String sTimeCharging = hourCharging + "h" + minCharging + "min";
        String energy = MathUtil.roundDouble2String(data.getEnergy(), 2) + "kWh";
        tvChargingEle.setText(energy);
        tvChargingRate.setText(String.valueOf(data.getRate()));
        String s = data.getCurrent() + "A";
        tvChargingCurrent.setText(s);
        tvChargingDuration.setText(sTimeCharging);
        String money = MathUtil.roundDouble2String(data.getCost(), 2);
        if (!TextUtils.isEmpty(moneyUnit)) {
            money = moneyUnit + money;
            String rate=  moneyUnit+ data.getRate()+"/kWh";
            tvChargingRate.setText(rate);
        }
        tvChargingMoney.setText(money);
        s = data.getVoltage() + "V";
        tvChargingVoltage.setText(s);
    }


    /**
     * ??????????????????
     */
    private void getReservaNow() {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("chargeId", mCurrentPile.getChargeId());//??????id
        //??????????????????????????????????????????,????????????A???
        Integer gunId = gunIds.get(mCurrentPile.getChargeId());
        if (gunId == null) gunId = 1;
        jsonMap.put("connectorId", gunId);//??????id
        jsonMap.put("lan", getLanguage());
        jsonMap.put("userId", SmartHomeUtil.getUserName());
        String json = SmartHomeUtil.mapToJsonString(jsonMap);
        PostUtil.postJson(SmartHomeUrlUtil.postRequestReserveNowList(), json, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        ReservationBean bean = new Gson().fromJson(json, ReservationBean.class);
                        setReserveNowUi(bean);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void LoginError(String str) {

            }
        });
    }


    /**
     * ????????????????????????ui
     */
    private void setReserveNowUi(ReservationBean gunBean) {
        reserveNow = gunBean.getData();
        if (reserveNow.size() != 0) {
            //????????????
            isReservation = true;
            ReservationBean.DataBean mCurrentReservationBean = reserveNow.get(0);
            //?????????????????????
            String cKey = reserveNow.get(0).getCKey();
            if (TextUtils.isEmpty(cKey) || "0".equals(cKey)) {
                String loopValue = mCurrentReservationBean.getExpiryDate();
                loopValue = loopValue.substring(11, 16);
                String rate = String.valueOf(mCurrentReservationBean.getRate());
                String symbol = mCurrentReservationBean.getSymbol();
                rate = symbol + rate + "/kWh";
                tvTimeKey.setText(loopValue);
                tvRateValue.setText(rate);
                gpPreset.setVisibility(View.GONE);
                tvTips.setVisibility(View.VISIBLE);
                rvTimeReserva.setVisibility(View.GONE);
                llTimeRate.setVisibility(View.VISIBLE);
                tvTips.setText(R.string.m338????????????????????????);
            } else {
                switch (cKey) {
                    case "G_SetAmount": //????????????
                        presetType = 1;
                        String loopValue = mCurrentReservationBean.getExpiryDate();
                        loopValue = loopValue.substring(11, 16);
                        String rate = String.valueOf(mCurrentReservationBean.getRate());
                        String symbol = mCurrentReservationBean.getSymbol();
                        rate = symbol + rate + "/kWh";
                        String typeValue = getString(R.string.m335????????????) + getString(R.string.m200??????);
                        tvTimeKey.setText(loopValue);
                        tvRateValue.setText(rate);
                        gpPreset.setVisibility(View.VISIBLE);
                        tvTips.setVisibility(View.GONE);
                        rvTimeReserva.setVisibility(View.GONE);
                        llTimeRate.setVisibility(View.VISIBLE);
                        tvReserValue.setText(typeValue);
                        tvReserType.setText(R.string.m336????????????);
                        String cValue = mCurrentReservationBean.getcValue() + symbol;
                        tvReserCalValue.setText(cValue);
                        String reserType = getString(R.string.m196??????) + getString(R.string.m200??????);
                        tvReserTypeText.setText(reserType);
                        ivReserChargedType.setImageResource(R.drawable.charging_money);
                        break;

                    case "G_SetEnergy": //????????????
                        presetType = 2;
                        String loopValue1 = mCurrentReservationBean.getExpiryDate();
                        loopValue1 = loopValue1.substring(11, 16);
                        String rate1 = String.valueOf(mCurrentReservationBean.getRate());
                        String symbol1 = mCurrentReservationBean.getSymbol();
                        rate1 = symbol1 + rate1 + "/kWh";
                        String typeValue1 = getString(R.string.m335????????????) + getString(R.string.m201??????);
                        tvTimeKey.setText(loopValue1);
                        tvRateValue.setText(rate1);
                        gpPreset.setVisibility(View.VISIBLE);
                        tvTips.setVisibility(View.GONE);
                        rvTimeReserva.setVisibility(View.GONE);
                        tvReserValue.setText(typeValue1);
                        tvReserType.setText(R.string.m336????????????);
                        String cValue1 = mCurrentReservationBean.getcValue() + "kWh";
                        tvReserCalValue.setText(cValue1);
                        String reserType1 = getString(R.string.m196??????) + getString(R.string.m201??????);
                        tvReserTypeText.setText(reserType1);
                        ivReserChargedType.setImageResource(R.drawable.charging_record_ele);
                        break;

                    case "G_SetTime": //???????????????
                        presetType = 3;
                        gpPreset.setVisibility(View.VISIBLE);
                        tvTips.setVisibility(View.GONE);
                        rvTimeReserva.setVisibility(View.VISIBLE);
                        llTimeRate.setVisibility(View.GONE);
                        String typeValue2 = getString(R.string.m335????????????) + getString(R.string.m202??????);
                        tvReserValue.setText(typeValue2);
                        tvReserType.setText(R.string.m336????????????);
                        String reserType2 = getString(R.string.m337??????) + getString(R.string.m200??????);
                        tvReserTypeText.setText(reserType2);
                        ivReserChargedType.setImageResource(R.drawable.charging_money);
                        reservaAdapter.replaceData(reserveNow);
                        double calVaule = 0;
                        String symbol2 = mCurrentReservationBean.getSymbol();
                        for (int i = 0; i < reserveNow.size(); i++) {
                            calVaule += reserveNow.get(i).getCost();
                        }
                        String s = calVaule + symbol2;
                        tvReserCalValue.setText(s);
                        break;

                    default: //????????????????????????
                        presetType = 0;
                        String loopValue3 = mCurrentReservationBean.getExpiryDate();
                        loopValue3 = loopValue3.substring(11, 16);
                        String rate3 = String.valueOf(mCurrentReservationBean.getRate());
                        String symbol3 = mCurrentReservationBean.getSymbol();
                        rate = symbol3 + rate3 + "/kWh";
                        tvTimeKey.setText(loopValue3);
                        tvRateValue.setText(rate);
                        gpPreset.setVisibility(View.GONE);
                        tvTips.setVisibility(View.VISIBLE);
                        rvTimeReserva.setVisibility(View.GONE);
                        llTimeRate.setVisibility(View.VISIBLE);
                        tvTips.setText(R.string.m338????????????????????????);
                        ivReserChargedType.setImageResource(R.drawable.charging_money);
                        break;

                }
            }

        } else {
            initReserVaUi();
        }
    }


    /**
     * ????????????????????????????????????ui
     */
    private void setPresetChargingUi(String scheme, String presetValue, String chargedVaule, String type,
                                     int resOther, String otherValue, String otherText, int resOhter2,
                                     String otherValue2, String otherText2, double presetValue_value,
                                     double chargedValue_value, String rateString, String currentString, String voltageString) {
        tvPresetText.setText(scheme);
        tvPresetValue.setText(presetValue);
        tvChargingValue.setText(chargedVaule);
        tvPresetType.setText(type);

        ivChargedOther.setImageResource(resOther);
        tvOtherValue.setText(otherValue);
        tvOtherText.setText(otherText);

        ivChargedOther2.setImageResource(resOhter2);
        tvOtherValue2.setText(otherValue2);
        tvOtherText2.setText(otherText2);
        if (presetValue_value > 0) {
            roundProgressBar.setMax((float) presetValue_value);
        }
        roundProgressBar.setProgress((float) chargedValue_value);
        double v = chargedValue_value * 100 / presetValue_value;
        double percent = MyUtil.divide(v, 2);
        tvPercentCenter.setText(percent + "%");
        roundProgressBar.setTextSize(getResources().getDimensionPixelSize(R.dimen.xa26));

        tvRate.setText(rateString);
        tvCurrent.setText(currentString);
        tvVoltage.setText(voltageString);
    }


    @OnClick({R.id.ivLeft, R.id.ll_Authorization, R.id.ll_record, R.id.ll_charging,
            R.id.rl_switch_gun, R.id.to_add_device, R.id.rl_solar, R.id.ivRight, R.id.rl_lock})
    public void onClickListener(View view) {
        switch (view.getId()) {
            case R.id.ivLeft:
                Intent intent = new Intent(this, MeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                jumpTo(intent, false);
                break;
            case R.id.ll_Authorization:
                if (SmartHomeUtil.isFlagUser()) {
                    toast(getString(R.string.m66??????????????????????????????));
                    return;
                }
                if (mCurrentPile.getType() == 1) {
                    toast(getString(R.string.m66??????????????????????????????));
                    return;
                }
                List<ChargingBean.DataBean.PriceConfBean> conf = mCurrentPile.getPriceConf();
                ArrayList<ChargingBean.DataBean.PriceConfBean> priceConf = new ArrayList<>();
                if (conf != null) priceConf.addAll(conf);
                Intent intent2 = new Intent(this, ChargingSetActivity.class);
                intent2.putExtra("sn", mCurrentPile.getChargeId());
                intent2.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent2.putParcelableArrayListExtra("rate", priceConf);
                jumpTo(intent2, false);
                break;
            case R.id.ll_charging:
                if (SmartHomeUtil.isFlagUser()) {
                    toast(getString(R.string.m66??????????????????????????????));
                    return;
                }
                toChargingOrStop();
                break;
            case R.id.rl_switch_gun:
                if (mCurrentPile.getConnectors() == 1) {//??????
                    return;
                }
                showStorageList(tvSwitchGun);
                break;
            case R.id.to_add_device:
                addChargingPile();
                break;
            case R.id.ll_record:
                Intent intent4 = new Intent(this, ChargingRecoderActivity.class);
                intent4.putExtra("sn", mCurrentPile.getChargeId());
                intent4.putExtra("symbol", moneyUnit);
                intent4.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                jumpTo(intent4, false);
                break;
            case R.id.rl_solar:
                if (SmartHomeUtil.isFlagUser()) {
                    toast(getString(R.string.m66??????????????????????????????));
                    return;
                }
                setPowerLimit();
                break;
            case R.id.rl_lock:
                if (SmartHomeUtil.isFlagUser()) {
                    toast(getString(R.string.m66??????????????????????????????));
                    return;
                }
                setLock();
                break;
            case R.id.ivRight:
                if (SmartHomeUtil.isFlagUser()) {
                    toast(getString(R.string.m66??????????????????????????????));
                    return;
                }
                if (mCurrentPile == null) {
                    toast(R.string.m212?????????????????????);
                    return;
                }
                if (mCurrentPile.getType() == 1) {
                    toast(getString(R.string.m66??????????????????????????????));
                    return;
                }
                if (Cons.getNoConfigBean() == null) {
                    getNoConfigParams();
                } else {
                    toConfig();
                }
                break;
        }

    }


    /**
     * ??????????????????????????????
     */
    private void getNoConfigParams() {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("userId", SmartHomeUtil.getUserName());//??????id
        jsonMap.put("cmd", "noConfig");
        jsonMap.put("lan", getLanguage());
        String json = SmartHomeUtil.mapToJsonString(jsonMap);
        LogUtil.i(json);
        Mydialog.Show(this);
        PostUtil.postJson(SmartHomeUrlUtil.postByCmd(), json, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                Mydialog.Dismiss();
                NoConfigBean bean = null;
                try {
                    JSONObject object = new JSONObject(json);
                    if (object.getInt("code") == 0) {
                        JSONObject jsonObject = object.optJSONObject("data");
                        bean = new Gson().fromJson(jsonObject.toString(), NoConfigBean.class);
                    }
                    Cons.setNoConfigBean(bean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                toConfig();
            }

            @Override
            public void LoginError(String str) {
                Mydialog.Dismiss();
                toConfig();
            }
        });

    }


    /**
     * ??????WiFi????????????
     */
    private void toConfig() {
        boolean isGuide = SharedPreferencesUnit.getInstance(this).getBoolean(Constant.WIFI_GUIDE_KEY);
        Class activity;
        if (!(mAdapter.getData().size() > 1)) {
            toast(R.string.m212?????????????????????);
            return;
        }
        if (isGuide) {
            activity = ConnetWiFiActivity.class;
        } else {
            activity = WifiSetGuideActivity.class;
        }
        Intent intent5 = new Intent(this, activity);
        intent5.putExtra("sn", mCurrentPile.getChargeId());
        int online;
        if (mCurrentGunBean != null && GunBean.UNAVAILABLE.equals(mCurrentGunBean.getData().getStatus())) {
            online = 1;
        } else {
            online = 0;
        }
        intent5.putExtra("online", online);
        intent5.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        jumpTo(intent5, false);
    }

    /**
     * ????????????Solar??????  0 FAST 1 ECO 2 ECO+
     */
    private void setPowerLimit() {
        //?????????????????????
        mFreshTimer.timerPause();
        View view = LayoutInflater.from(ChargingPileActivity.this).inflate(R.layout.popuwindow_power_limit, null);
        TextView tvSolarMode = view.findViewById(R.id.tv_text1);
        TextView tvLimitPower = view.findViewById(R.id.tv_text2);
        TextView tvSwitch = view.findViewById(R.id.tv_switch);
        TextView tvConfirm = view.findViewById(R.id.tv_confirm);
        TextView tvCancel = view.findViewById(R.id.tv_cancel);
        int solarMode = mCurrentPile.getG_SolarMode();
//        if (solarMode > 2 || solarMode < 0) solarMode = 0;
        String mSolarMode = getString(R.string.mSolar??????) + ":" + solarArrray[solarMode];
        tvSolarMode.setText(mSolarMode);
        String switchText;
        if (solarMode == 0) {
            switchText = "";
            tvSwitch.setVisibility(View.GONE);
            tvLimitPower.setVisibility(View.GONE);
            tvConfirm.setText(R.string.m183??????);
        } else if (solarMode == 1) {
            switchText = getString(R.string.m132??????) + ":" + solarArrray[2];
            tvSwitch.setVisibility(View.VISIBLE);
            tvConfirm.setText(R.string.m184??????);
            tvLimitPower.setVisibility(View.VISIBLE);
            float solarLimitPower = mCurrentPile.getG_SolarLimitPower();
            String mSolarLimitPower = getString(R.string.m????????????) + ":" + solarLimitPower + "kW";
            tvLimitPower.setText(mSolarLimitPower);
        } else {
            switchText = getString(R.string.m132??????) + ":" + solarArrray[1];
            tvSwitch.setVisibility(View.VISIBLE);
            tvLimitPower.setVisibility(View.GONE);
            tvConfirm.setText(R.string.m184??????);
        }
        tvSwitch.setText(switchText);
        int width = getResources().getDimensionPixelSize(R.dimen.xa450);
        int height = getResources().getDimensionPixelSize(R.dimen.xa230);
        PopupWindow pwPowerTips = new PopupWindow(view, width, height, true);
        tvConfirm.setOnClickListener(v -> {
            PileSetBean pileSetBean = new PileSetBean();
            PileSetBean.DataBean dataBean = new PileSetBean.DataBean();
            if (solarMode == 0) {
                pwPowerTips.dismiss();
                setNosetRateDialog();
            } else {
                dataBean.setG_SolarMode(0);
                pileSetBean.setData(dataBean);
                requestLimit(pileSetBean);
                pwPowerTips.dismiss();
            }
        });

        tvSwitch.setOnClickListener(v -> {
            PileSetBean pileSetBean = new PileSetBean();
            PileSetBean.DataBean dataBean = new PileSetBean.DataBean();
            if (solarMode == 2) {
                dataBean.setG_SolarMode(1);
            } else {
                dataBean.setG_SolarMode(2);
            }
            pileSetBean.setData(dataBean);
            requestLimit(pileSetBean);
            pwPowerTips.dismiss();
        });
        tvCancel.setOnClickListener(v -> pwPowerTips.dismiss());
        pwPowerTips.setOutsideTouchable(true);
        pwPowerTips.setTouchable(true);
        pwPowerTips.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        pwPowerTips.setBackgroundDrawable(new ColorDrawable(0));
        int[] location = new int[2];
        mRlSolar.getLocationOnScreen(location);
        pwPowerTips.showAtLocation(mRlSolar, Gravity.NO_GRAVITY, location[0] + mRlSolar.getWidth(), location[1]);

    }


    private void setNosetRateDialog() {
        String[] array = new String[]{"ECO", "ECO+"};
        new CircleDialog.Builder()
                .setItems(array, new OnLvItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        int mode = position + 1;
                        PileSetBean pileSetBean = new PileSetBean();
                        PileSetBean.DataBean dataBean = new PileSetBean.DataBean();
                        dataBean.setG_SolarMode(mode);
                        pileSetBean.setData(dataBean);
                        requestLimit(pileSetBean);
                        mFreshTimer.timerRuning();
                        return true;
                    }
                })
                .setNegative(getString(R.string.m7??????), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mFreshTimer.timerRuning();
                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        })
                .setGravity(Gravity.BOTTOM)
                .show(getSupportFragmentManager());
    }


    private void setLock() {
        mFreshTimer.timerPause();
        new CircleDialog.Builder().setTitle(getString(R.string.m27????????????))
                .setText(getString(R.string.m???????????????????????????))
                .setWidth(0.75f)
                .setPositive(getString(R.string.m9??????), view -> {
                    requestUnlock();
                })
                .setNegative(getString(R.string.m7??????), view -> {
                    mFreshTimer.timerRuning();
                })
                .setOnDismissListener(dialogInterface -> {

                })
                .show(getSupportFragmentManager());
    }


    /**
     * ?????????????????????????????????
     */

    private void requestLimit(PileSetBean pileSetBean) {
        Mydialog.Show(this);
        if (pileSetBean == null) return;
        PileSetBean.DataBean data = pileSetBean.getData();
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("chargeId", mCurrentPile.getChargeId());//??????id
        jsonMap.put("userId", SmartHomeUtil.getUserName());//??????id
        jsonMap.put("lan", getLanguage());//??????id
        jsonMap.put("G_SolarMode", data.getG_SolarMode());
        String json = SmartHomeUtil.mapToJsonString(jsonMap);
        PostUtil.postJson(SmartHomeUrlUtil.postSetChargingParams(), json, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                try {
                    JSONObject object = new JSONObject(json);
                    int code = object.getInt("code");
                    if (code == 0) {
                        freshData();
                        mFreshTimer.timerRuning();
                    }
                    String data = object.getString("data");
                    toast(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void LoginError(String str) {

            }
        });
    }


    /**
     * ?????????????????????
     */

    private void requestUnlock() {
        Mydialog.Show(this);
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("cmd", "unlock");//??????id
        jsonMap.put("chargeId", mCurrentPile.getChargeId());//??????id
        jsonMap.put("lan", getLanguage());//??????id
        jsonMap.put("userId", SmartHomeUtil.getUserName());//??????id
        jsonMap.put("connectorId", mCurrentGunBean.getData().getConnectorId());
        String json = SmartHomeUtil.mapToJsonString(jsonMap);
        PostUtil.postJson(SmartHomeUrlUtil.postByCmd(), json, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                try {
                    JSONObject object = new JSONObject(json);
                    String data = object.getString("data");
                    toast(data);
                    mFreshTimer.timerRuning();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void LoginError(String str) {

            }
        });
    }


    private void addChargingPile() {
        if (SmartHomeUtil.isFlagUser()) {//????????????
            FragmentManager fragmentManager = ChargingPileActivity.this.getSupportFragmentManager();
            new CircleDialog.Builder()
                    .setTitle(getString(R.string.m27????????????))
                    //????????????????????????????????????
                    .setInputHint(getString(R.string.m26???????????????))//??????
                    .setInputCounter(1000, (maxLen, currentLen) -> "")
                    .configInput(params -> {
                        params.gravity = Gravity.CENTER;
//                        params.textSize = 45;
//                            params.backgroundColor=ContextCompat.getColor(ChargingPileActivity.this, R.color.preset_edit_time_background);
                        params.strokeColor = ContextCompat.getColor(ChargingPileActivity.this, R.color.preset_edit_time_background);
                    })
                    .setPositiveInput(getString(R.string.m9??????), new OnInputClickListener() {
                        @Override
                        public boolean onClick(String text, View v) {
                            Map<String, Object> params = new HashMap<>();
                            params.put("code", text);
                            params.put("userId", SmartHomeUtil.getUserName());
                            params.put("lan", getLanguage());
                            String json = SmartHomeUtil.mapToJsonString(params);
                            PostUtil.postJson(SmartHomeUrlUtil.postGetDemoCode(), json, new PostUtil.postListener() {
                                @Override
                                public void Params(Map<String, String> params) {

                                }

                                @Override
                                public void success(String json) {
                                    try {
                                        JSONObject object = new JSONObject(json);
                                        int code = object.getInt("code");
                                        if (code == 0) {
                                            Intent intent = new Intent(ChargingPileActivity.this, AddChargingActivity.class);
                                            jumpTo(intent, false);
                                        } else {
                                            String data = object.getString("data");
                                            toast(data);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void LoginError(String str) {

                                }
                            });
                            return true;
                        }
                    })
                    //??????????????????????????????????????????
                    .setNegative(getString(R.string.m7??????), v -> {

                    })
                    .show(fragmentManager);
        } else {
            Intent intent = new Intent(ChargingPileActivity.this, AddChargingActivity.class);
            jumpTo(intent, false);
        }

    }

    /**
     * ??????????????????
     */
    private void toChargingOrStop() {
        if (mCurrentGunBean == null || mCurrentPile == null) {
            return;
        }
        //????????????
        String status = mCurrentGunBean.getData().getStatus();
        if (TextUtils.isEmpty(status)) {
            toast(R.string.m?????????????????????);
            return;
        }
        //??????????????????????????????
        if (mCurrentPile.getType() == 0) {//??????
            switch (status) {
                case GunBean.ACCEPTED:
                case GunBean.RESERVED:
                case GunBean.RESERVENOW:
                    if (reserveNow == null) return;
                    for (int i = 0; i < reserveNow.size(); i++) {
                        deleteTime(reserveNow.get(i), reserveNow.size(), i);
                    }
                    break;

                case GunBean.AVAILABLE://????????????????????????????????????
                case GunBean.PREPARING://?????????
                    int loopType = cbEveryday.isChecked() ? 0 : -1;
                    switch (presetType) {
                        case 0://????????????????????????
                            if (isReservation) {//?????????????????????
                                //????????????
                                if (TextUtils.isEmpty(startTime)) {
                                    toast(getString(R.string.m130?????????????????????));
                                    return;
                                }
                                requestReserve(0, startTime, "", "", loopType);
                            } else {
                                requestNarmal(0, "", "");
                            }
                            break;
                        case 1:
                            String money = tvPpmoney.getText().toString();
                            if (TextUtils.isEmpty(money) || money.contains("-")) {
                                toast(R.string.m?????????????????????);
                                return;
                            }
                            if (isReservation) {//?????????????????????
                                if (TextUtils.isEmpty(startTime)) {
                                    toast(getString(R.string.m130?????????????????????));
                                    return;
                                }
                                requestReserve(1, startTime, "G_SetAmount", reserveMoney, loopType);
                            } else {
                                requestNarmal(1, "G_SetAmount", reserveMoney);
                            }
                            break;
                        case 2:
                            String ele = tvPpEle.getText().toString();
                            if (TextUtils.isEmpty(ele) || ele.contains("-")) {
                                toast(R.string.m?????????????????????);
                                return;
                            }
                            if (isReservation) {//?????????????????????
                                if (TextUtils.isEmpty(startTime)) {
                                    toast(getString(R.string.m130?????????????????????));
                                    return;
                                }
                                requestReserve(2, startTime, "G_SetEnergy", reserveEle, loopType);
                            } else {
                                requestNarmal(2, "G_SetEnergy", reserveEle);
                            }
                            break;
                        case 3:
                            String duration = tvPpTime.getText().toString();
                            if (TextUtils.isEmpty(duration) || duration.contains("-")) {
                                toast(R.string.m129????????????????????????);
                                return;
                            }
                            if (isReservation) {//?????????????????????
                                if (TextUtils.isEmpty(startTime)) {
                                    toast(getString(R.string.m130?????????????????????));
                                    return;
                                }
                                requestReserve(3, startTime, "G_SetTime", reserveTime, loopType);
                            } else {
                                requestNarmal(3, "G_SetTime", reserveTime);
                            }
                            break;
                    }
                    break;
                case GunBean.CHARGING://??????????????????????????????
                    requestStop();
                    break;
                case GunBean.EXPIRY:
                    break;
                case GunBean.FAULTED:
                    toast(getString(R.string.m215????????????));
                    break;
                case GunBean.FINISHING:
                    requestStop();
                    break;
                case GunBean.SUSPENDEEV:
                    requestStop();
                    break;
                case GunBean.SUSPENDEDEVSE:

                    break;
                case GunBean.UNAVAILABLE:
                    toast(getString(R.string.m216????????????????????????));
                    break;
                case GunBean.WORK:
                    break;
                default:
                    toast(getString(R.string.m216????????????????????????));
                    break;

            }

        } else {//????????????
            switch (status) {
                case GunBean.ACCEPTED:
                case GunBean.RESERVENOW:
                case GunBean.RESERVED:
                    toast(getString(R.string.m66??????????????????????????????));
                    break;
                case GunBean.AVAILABLE://????????????????????????????????????
                case GunBean.PREPARING://?????????
                    if (presetType == 0) {//????????????????????????
                        requestNarmal(0, "", "");
                    } else if (presetType == 1) {//??????????????????
                        requestNarmal(1, "G_SetAmount", reserveMoney);
                    } else if (presetType == 2) {//??????????????????
                        requestNarmal(2, "G_SetEnergy", reserveEle);
                    } else if (presetType == 3) {
                        requestNarmal(3, "G_SetTime", reserveTime);
                    }
                    break;

                case GunBean.CHARGING://??????????????????????????????
                    requestStop();
                    break;
                case GunBean.EXPIRY:
                    tvStatus.setText(getString(R.string.m124????????????));
                    break;
                case GunBean.FAULTED:
                    tvStatus.setText(getString(R.string.m124????????????));

                    break;
                case GunBean.FINISHING:
                    tvStatus.setText(getString(R.string.m120????????????));
                    break;
                case GunBean.SUSPENDEEV:
                case GunBean.UNAVAILABLE:
                    toast(getString(R.string.m216????????????????????????));
                    break;
                case GunBean.WORK:
                    tvStatus.setText(getString(R.string.m126???????????????));
                    break;
                default:
                    toast(getString(R.string.m216????????????????????????));
                    break;
            }
        }


    }

    /**
     * ???????????????
     */
    private void initCharging() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mAdapter = new ChargingListAdapter(mChargingList);
        mRvCharging.setLayoutManager(mLinearLayoutManager);
        mRvCharging.setAdapter(mAdapter);
    }

    /**
     * ?????????
     */
    private void HeadRvAddButton(List<ChargingBean.DataBean> newList) {
        ChargingBean.DataBean bean = new ChargingBean.DataBean();
        bean.setDevType(ChargingBean.ADD_DEVICE);
        newList.add(bean);
    }


    private void initListeners() {
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            ChargingBean.DataBean bean = mAdapter.getItem(position);
            if (bean == null) return;
            int type = bean.getDevType();
            if (type == ChargingBean.ADD_DEVICE) {
                addChargingPile();
            } else {
                animation = null;
                mFreshTimer.timerPause();
                currenStatus = GunBean.NONE;
                mAdapter.setNowSelectPosition(position);
                refreshChargingUI();
            }
        });

        mAdapter.setOnItemLongClickListener((adapter, view, position) -> {
            ChargingBean.DataBean bean = mAdapter.getItem(position);
            if (bean == null) return false;
            int type = bean.getDevType();
            if (type != ChargingBean.ADD_DEVICE) {
                requestDelete(bean);
            }
            return false;
        });
    }


    private void requestDelete(final ChargingBean.DataBean bean) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        new CircleDialog.Builder()
                .setWidth(0.75f)
                .setTitle(getString(R.string.m8??????))
                .setText(getString(R.string.m????????????))
                .setGravity(Gravity.CENTER).setPositive(getString(R.string.m9??????), v -> {
            LogUtil.d("???????????????");
            Mydialog.Show(ChargingPileActivity.this);
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("sn", bean.getChargeId());
            jsonMap.put("userId", bean.getUserName());
            jsonMap.put("lan", getLanguage());
            String json = SmartHomeUtil.mapToJsonString(jsonMap);
            LogUtil.i(json);
            PostUtil.postJson(SmartHomeUrlUtil.postRequestDeleteCharging(), json, new PostUtil.postListener() {
                @Override
                public void Params(Map<String, String> params) {

                }

                @Override
                public void success(String json) {
                    Mydialog.Dismiss();
                    try {
                        JSONObject object = new JSONObject(json);
                        if (object.getInt("code") == 0) {
                            toast(getString(R.string.m135????????????));
                            //????????????,????????????
                            freshData();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void LoginError(String str) {
                }


            });
        })
                .setNegative(getString(R.string.m7??????), null)
                .show(fragmentManager);


    }

    /**
     * ?????????
     *
     * @param v popuwindow???????????????view??????
     */
    public void showStorageList(View v) {
        mFreshTimer.timerPause();
        List<GunBean.DataBean> gunlist = new ArrayList<>();
        List<String> letters = SmartHomeUtil.getLetter();
        String unit = getString(R.string.???);
        for (int i = 0; i < mCurrentPile.getConnectors(); i++) {
            GunBean.DataBean data = new GunBean.DataBean();
            data.setConnectorId(i + 1);
            data.setName(letters.get(i) + " " + unit);
            gunlist.add(data);
        }

        View contentView = LayoutInflater.from(this).inflate(
                R.layout.dialog_switch_list, null);
        RecyclerView lv = contentView.findViewById(R.id.listView1);
        View flCon = contentView.findViewById(R.id.frameLayout);
        flCon.setOnClickListener(v1 -> {
            if (popupGun != null) {
                popupGun.dismiss();
            }
        });
        popGunAdapter = new GunSwitchAdapter(gunlist);
        lv.setLayoutManager(new LinearLayoutManager(this));
        lv.setAdapter(popGunAdapter);
        popGunAdapter.setOnItemClickListener((adapter, view, position) -> {
            int id = popGunAdapter.getData().get(position).getConnectorId();
            animation = null;
            gunIds.put(mCurrentPile.getChargeId(), id);
            String name = SmartHomeUtil.getLetter().get(id - 1) + " " + getString(R.string.???);
            tvSwitchGun.setText(name);
            popupGun.dismiss();
            mFreshTimer.timerRuning();
            //???????????????
//            freshChargingGun(mCurrentPile.getChargeId(), id);
        });
        int width = getResources().getDimensionPixelSize(R.dimen.xa150);
        popupGun = new PopupWindow(contentView, width, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupGun.setTouchable(true);
        popupGun.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupGun.setTouchInterceptor((v12, event) -> false);
        popupGun.setBackgroundDrawable(new ColorDrawable(0));
        popupGun.showAsDropDown(v);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            //?????????????????????????????????ui
            initReserveUi();
            initPresetUi();
            if (requestCode == REQUEST_MONEY) {
                String money = data.getStringExtra("money");
                reserveMoney = Double.parseDouble(money);
                presetType = 1;
                isReservation = false;

                //?????????????????????ui
                setMoneyUi(true, money);
                //???????????????ui
                startTime = null;
                setReserveUi(getString(R.string.m204????????????), getString(R.string.m206?????????), R.drawable.checkbox_off, "--:--", true, false);

            }
            if (requestCode == REQUEST_ELE) {
                String electric = data.getStringExtra("electric");
                reserveEle = Double.parseDouble(electric);
                presetType = 2;
                isReservation = false;

                setEleUi(true, electric + "kWh");
                startTime = null;
                //?????????????????????????????????
                setReserveUi(getString(R.string.m204????????????), getString(R.string.m206?????????), R.drawable.checkbox_off, "--:--", true, false);
            }
            if (requestCode == REQUEST_TIME) {
                String hour = data.getStringExtra("hour");
                String minute = data.getStringExtra("minute");
                String time = hour + "h" + minute + "min";
                timeEvaryDay = hour + ":" + minute;
                reserveTime = Integer.parseInt(hour) * 60 + Integer.parseInt(minute);
                presetType = 3;
                setTimeUi(true, time);
                //?????????????????????????????????
                isReservation = false;
                startTime = null;
                //?????????????????????????????????
                setReserveUi(getString(R.string.m204????????????), getString(R.string.m206?????????), R.drawable.checkbox_off, "--:--", true, false);
            }
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventFreshTiming(FreshTimingMsg msg) {
        refreshChargingUI();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void addDev(AddDevMsg msg) {
        freshData();
    }


    private String[] hours;
    private String[] minutes;

    private void initResource() {
        hours = new String[24];
        for (int i = 0; i < 24; i++) {
            if (i < 10) {
                hours[i] = "0" + i;
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
        solarArrray = new String[]{"FAST", "ECO", "ECO+"};
    }

    /**
     * ?????????????????????
     */
    private void selectTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        if (!TextUtils.isEmpty(startTime)) {
            String selectTime = startTime.substring(11, 16);
            String[] time = selectTime.split("[\\D]");
            hour = Integer.parseInt(time[0]);
            min = Integer.parseInt(time[1]);
        }
        dialogFragment = CircleDialogUtils.showWhiteTimeSelect(this, hour, min, getSupportFragmentManager(), false, new CircleDialogUtils.timeSelectedListener() {
            @Override
            public void cancle() {
                dialogFragment.dismiss();
            }

            @Override
            public void ok(boolean status, int hour, int min) {
                String hourString = hour < 10 ? ("0" + hour) : hour + "";
                String minString = min < 10 ? ("0" + min) : min + "";
                String time = hourString + ":" + minString;

                //????????????
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date = new Date();
                String yMd = sdf.format(date);
                startTime = yMd + "T" + time + ":00.000Z";
                isReservation = true;
                setReserveUi(getString(R.string.m204????????????), getString(R.string.m205?????????), R.drawable.checkbox_on, time, true, false);
                dialogFragment.dismiss();
            }
        });
    }


    /**
     * ????????????,?????????????????????
     */
    private void requestNarmal(final int type, String key, Object value) {
        LogUtil.d("???????????????????????????");
        Mydialog.Show(this);
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("action", "remoteStartTransaction");
        Integer gunId = gunIds.get(mCurrentPile.getChargeId());
        if (gunId == null) gunId = 1;
        jsonMap.put("connectorId", gunId);
        jsonMap.put("userId", SmartHomeUtil.getUserName());
        jsonMap.put("chargeId", mCurrentPile.getChargeId());
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
        PostUtil.postJson(SmartHomeUrlUtil.postRequestReseerveCharging(), json, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                try {
                    JSONObject object = new JSONObject(json);
                    int type = object.optInt("type");
                    if (type == 0) {
                        ChargeStartFreshMsg chargeStartFreshMsg = new ChargeStartFreshMsg();
                        chargeStartFreshMsg.setPeriod(2 * 1000);
                        EventBus.getDefault().post(chargeStartFreshMsg);
                    } else {
                        Mydialog.Dismiss();
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


    /**
     * ??????????????????
     */
    private void requestStop() {
        LogUtil.d("?????????????????????????????????");
        Mydialog.Show(this);
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("action", "remoteStopTransaction");
        Integer gunId = gunIds.get(mCurrentPile.getChargeId());
        if (gunId == null) gunId = 1;
        jsonMap.put("connectorId", gunId);
        jsonMap.put("userId", SmartHomeUtil.getUserName());
        jsonMap.put("chargeId", mCurrentPile.getChargeId());
        jsonMap.put("transactionId", transactionId);
        jsonMap.put("lan", getLanguage());
        String json = SmartHomeUtil.mapToJsonString(jsonMap);
        LogUtil.i(json);
        PostUtil.postJson(SmartHomeUrlUtil.postRequestReseerveCharging(), json, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                try {
                    JSONObject object = new JSONObject(json);
                    int type = object.optInt("type");
                    if (type == 0) {
                        ChargeStartFreshMsg chargeStartFreshMsg = new ChargeStartFreshMsg();
                        chargeStartFreshMsg.setPeriod(2 * 1000);
                        EventBus.getDefault().post(chargeStartFreshMsg);
                    } else {
                        Mydialog.Dismiss();
                    }
                    if (30!=object.optInt("code")){
                        toast(object.getString("data"));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void LoginError(String str) {
                Mydialog.Dismiss();
            }

        });
    }


    /**
     * ????????????
     */
    private void requestReserve(int type, String expiryDate, String key, Object value, int loopType) {
        LogUtil.d("???????????????????????????");
        Mydialog.Show(this);
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("action", "ReserveNow");
        jsonMap.put("expiryDate", expiryDate);
        Integer gunId = gunIds.get(mCurrentPile.getChargeId());
        if (gunId == null) gunId = 1;
        jsonMap.put("connectorId", gunId);
        jsonMap.put("chargeId", mCurrentPile.getChargeId());
        jsonMap.put("userId", SmartHomeUtil.getUserName());
        jsonMap.put("loopType", loopType);
        jsonMap.put("lan", getLanguage());
        if (loopType == 0) {
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
        PostUtil.postJson(SmartHomeUrlUtil.postRequestReseerveCharging(), json, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                Mydialog.Dismiss();
                try {
                    JSONObject object = new JSONObject(json);
                    String data = object.getString("data");
                    int code = object.optInt("type");
                    if (code == 0) {
                        ChargeStartFreshMsg chargeStartFreshMsg = new ChargeStartFreshMsg();
                        chargeStartFreshMsg.setPeriod(2 * 1000);
                        EventBus.getDefault().post(chargeStartFreshMsg);
                    }
                    toast(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void LoginError(String str) {
                Mydialog.Dismiss();
            }
        });
    }


    /**
     * ??????????????????ui
     */
    private void initSolarUi(int solar) {
        if (solar == 0) {
            mRlSolar.setBackgroundResource(R.drawable.selector_circle_btn_white);
            mIvLimit.setImageResource(R.drawable.limit_power_on);
            mTvSolar.setTextColor(ContextCompat.getColor(this, R.color.maincolor_1));
        } else {
            mRlSolar.setBackgroundResource(R.drawable.selector_circle_btn_green_gradient);
            mIvLimit.setImageResource(R.drawable.limit_power_off);
            mTvSolar.setTextColor(ContextCompat.getColor(this, R.color.headerView));
        }
        mTvSolar.setText(R.string.solar);

    }


    /**
     * ?????????????????????ui
     */
    private void initPresetUi() {
        ivPpmoney.setImageResource(R.drawable.charging_prepare_not_selected);
        String s = moneyUnit + "--";
        tvPpmoney.setText(s);
        ivPpEle.setImageResource(R.drawable.charging_prepare_not_selected);
        s = "--kWh";
        tvPpEle.setText(s);
        ivPpTime.setImageResource(R.drawable.charging_prepare_not_selected);
        s = "-h-min";
        tvPpTime.setText(s);
        presetType = 0;
    }

    /**
     * ??????????????????ui
     */

    private void setMoneyUi(boolean isCheck, String money) {
        ivPpmoney.setImageResource(isCheck ? R.drawable.charging_prepare_selected : R.drawable.charging_prepare_not_selected);
        if (isCheck) {
            setEleUi(false, "--kWh");
            setTimeUi(false, "-h-min");
        }
        if (!TextUtils.isEmpty(money)) {
            String s = moneyUnit + money;
            tvPpmoney.setText(s);
        }

    }


    /**
     * ??????????????????ui
     */

    private void setEleUi(boolean isCheck, String ele) {
        ivPpEle.setImageResource(isCheck ? R.drawable.charging_prepare_selected : R.drawable.charging_prepare_not_selected);
        if (isCheck) {
            setMoneyUi(false, "--");
            setTimeUi(false, "-h-min");
        }
        if (!TextUtils.isEmpty(ele)) {
            tvPpEle.setText(ele);
        }
    }


    /**
     * ??????????????????ui
     */

    private void setTimeUi(boolean isCheck, String time) {
        ivPpTime.setImageResource(isCheck ? R.drawable.charging_prepare_selected : R.drawable.charging_prepare_not_selected);
        if (isCheck) {
            setEleUi(false, "--kWh");
            setMoneyUi(false, "--");
        }
        if (!TextUtils.isEmpty(time)) {
            tvPpTime.setText(time);
        }
    }

    /**
     * ?????????????????????ui
     */
    private void initReserveUi() {
        tvTextStart.setText(getString(R.string.m204????????????));
        tvTextOpenClose.setText(getString(R.string.m206?????????));
        ivResever.setImageResource(R.drawable.checkbox_off);
        tvStartTime.setText("--:--");
        cbEveryday.setChecked(false);
        tvEveryDay.setTextColor(ContextCompat.getColor(this, R.color.title_2));
        MyUtil.showAllView(tvEveryDay, cbEveryday);
        isReservation = false;
    }


    /**
     * ?????????????????????
     */

    private void initReserVaUi() {
        isReservation = false;
        presetType = 0;
        gpPreset.setVisibility(View.GONE);
        tvTips.setVisibility(View.VISIBLE);
        rvTimeReserva.setVisibility(View.GONE);
        llTimeRate.setVisibility(View.VISIBLE);
        tvTips.setText(R.string.m338????????????????????????);
    }


    /**
     * ??????????????????ui
     */
    private void setReserveUi(String startText, String onOffText, int resCheckbox, String startTime, boolean everyDay, boolean isEveryDay) {
        tvTextStart.setText(startText);
        tvTextOpenClose.setText(onOffText);
        ivResever.setImageResource(resCheckbox);
        tvStartTime.setText(startTime);
        if (everyDay) {
            MyUtil.showAllView(tvEveryDay, cbEveryday);
            cbEveryday.setChecked(isEveryDay);
        } else {
            MyUtil.hideAllView(View.GONE, tvEveryDay, cbEveryday);
        }
    }

    /**
     * ????????????????????????????????????Ui
     */
    private void setChargGunUi(int resStatus, String textStatus, int statusColor, int resOnOff, String textOnOff) {
        ivChargingIcon.setImageResource(resStatus);
        tvStatus.setText(textStatus);
        tvStatus.setTextColor(statusColor);
        ivChargingStatus.setImageResource(resOnOff);
        tvChargingStatus.setText(textOnOff);
    }

    /**
     * ??????????????????
     */

    private void startAnim() {
        MyUtil.hideAllView(View.GONE, ivfinishBackground);
        MyUtil.showAllView(ivAnim);
        if (animation == null) {
            animation = AnimationUtils.loadAnimation(this, R.anim.pile_charging);
            ivAnim.startAnimation(animation);
        }
    }

    /**
     * ????????????
     */
    private void stopAnim() {
        animation = null;
        MyUtil.hideAllView(View.GONE);
        MyUtil.showAllView(ivfinishBackground);
        ivAnim.clearAnimation();
        ivfinishBackground.setImageResource(R.drawable.charging_finish_anim);
    }

    /**
     * ????????????
     */
    private void hideAnim() {
        animation = null;
        ivAnim.clearAnimation();
        MyUtil.hideAllView(View.GONE, ivAnim, ivfinishBackground);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                toast(R.string.m????????????);
                mExitTime = System.currentTimeMillis();
            } else {
                MyApplication.getInstance().exit();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * ????????????
     */
    private void deleteTime(ReservationBean.DataBean bean, int size, int pos) {
        LogUtil.d("????????????");
        JSONObject object = new JSONObject();
        try {
            object.put("cKey", bean.getcKey());
            object.put("cValue", bean.getcValue());
            object.put("connectorId", bean.getConnectorId());
            object.put("expiryDate", bean.getExpiryDate());
            object.put("loopValue", bean.getLoopValue());
            object.put("reservationId", bean.getReservationId());
            object.put("sn", mCurrentPile.getChargeId());
            object.put("userId", SmartHomeUtil.getUserName());
            object.put("ctype", "2");
            object.put("lan", getLanguage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        PostUtil.postJson(SmartHomeUrlUtil.postUpdateChargingReservelist(), object.toString(), new PostUtil.postListener() {
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
                        if (size - 1 == pos) {
                            ChargeStartFreshMsg chargeStartFreshMsg = new ChargeStartFreshMsg();
                            chargeStartFreshMsg.setPeriod(2 * 1000);
                            EventBus.getDefault().post(chargeStartFreshMsg);
                        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        searchId = null;
        stopFreshTimer();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void freshAll(ChargeStartFreshMsg msg) {
        //?????????????????????????????????????????? ????????????..
        startOperaterTimer(msg.getPeriod());
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void freshRate(RefreshRateMsg msg) {
        if (msg.getPriceConfBeanList() != null) {
            freshData();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void freshAll(FreshListMsg msg) {
        freshData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void searchFresh(SearchDevMsg msg) {
        searchId = msg.getDevSn();
        freshData();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (adapter == reservaAdapter) {
            Intent intent = new Intent(ChargingPileActivity.this, ChargingDurationActivity.class);
            intent.putExtra("sn", mCurrentPile.getChargeId());
            jumpTo(intent, false);
        }
    }
}
