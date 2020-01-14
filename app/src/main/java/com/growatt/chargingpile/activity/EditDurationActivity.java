package com.growatt.chargingpile.activity;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.EventBusMsg.FreshTimingMsg;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.bean.ReservationBean;
import com.growatt.chargingpile.connutil.PostUtil;
import com.growatt.chargingpile.util.AlertPickDialog;
import com.growatt.chargingpile.util.MyUtil;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.util.SmartHomeUrlUtil;
import com.growatt.chargingpile.util.SmartHomeUtil;
import com.mylhyl.circledialog.CircleDialog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;
import org.xutils.common.util.LogUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class EditDurationActivity extends BaseActivity {

    @BindView(R.id.headerView)
    View headerView;

    @BindView(R.id.tv_open)
    TextView tvOpen;
    @BindView(R.id.tv_close)
    TextView tvClose;
    @BindView(R.id.tv_duration_time)
    TextView tvDuration;
    @BindView(R.id.rl_delete_reserva)
    RelativeLayout rlDelete;
    @BindView(R.id.cb_everyday)
    CheckBox cbEveryday;

    private String[] hours;
    private String[] minutes;
    private long duration = 0;
    private int reservationId;
    private ReservationBean.DataBean dataBean;
    private int type = 1;


    private String expiryDate;
    private String endDate;
    private int loopType = -1;

    private String loopValue;
    private String chargingId;
    private Unbinder bind;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_duration);
        bind = ButterKnife.bind(this);
        initHeaderView();
        initResource();
        initIntent();
        initViews();
    }


    private void initIntent() {
        chargingId = getIntent().getStringExtra("sn");
        String jsonBean = getIntent().getStringExtra("bean");
        if (!TextUtils.isEmpty(jsonBean)) {
            type = 1;
            dataBean = new Gson().fromJson(jsonBean, ReservationBean.DataBean.class);
            expiryDate = dataBean.getExpiryDate();
            endDate = dataBean.getEndDate();
            reservationId = dataBean.getReservationId();
            loopType = dataBean.getLoopType();
            duration= Long.parseLong(dataBean.getcValue2());
        } else {
            type = 2;
        }
    }

    private void initViews() {
        if (type == 2) {
            tvOpen.setText(getString(R.string.m185未设置));
            tvClose.setText(getString(R.string.m185未设置));
            MyUtil.hideAllView(View.GONE, rlDelete);
        } else {
            cbEveryday.setChecked(loopType != -1);
            if (!TextUtils.isEmpty(expiryDate))
            tvOpen.setText(expiryDate.substring(11, 16));
            if (!TextUtils.isEmpty(endDate))
            tvClose.setText(endDate.substring(11, 16));
            MyUtil.showAllView(rlDelete);
            long hour = duration / 60;
            long min = duration % 60;
            String time = hour + "h" + min + "min";
            tvDuration.setText(time);
        }
    }

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

    private void initHeaderView() {
        setHeaderImage(headerView, R.drawable.back, Position.LEFT, v -> finish());
        setHeaderTitle(headerView, getString(R.string.m181设置定时), R.color.title_1, true);
        setHeaderTvRight(headerView, getString(R.string.m182保存), v -> {
            if (type == 1) {
                dataBean.setLoopType(cbEveryday.isChecked()?0:-1);
                editTime( expiryDate, dataBean.getLoopType());
            } else {
                int loopType = cbEveryday.isChecked()?0:-1;
                addReserve(loopType, duration, loopValue);

            }
        }, R.color.main_text_color);
    }

    @OnClick({R.id.rl_start, R.id.rl_close, R.id.rl_delete_reserva})
    public void onClickListener(View view) {
        switch (view.getId()) {
            case R.id.rl_start:
                selectTime(1);
                break;
            case R.id.rl_close:
                selectTime(2);
                break;
            case R.id.rl_delete_reserva:
                delete();
                break;
        }
    }


    private void selectTime(final int openOrclose) {
        AlertPickDialog.showTimePickerDialog(this, hours, "00", minutes, "00", new AlertPickDialog.AlertPickCallBack() {

            @Override
            public void confirm(String hour, String minute) {
                String time = hour + ":" + minute;
                //获取年月
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
                Date date = new Date();
                String yMd = sdf.format(date);
                if (openOrclose == 1) {
                    tvOpen.setText(time);
                    expiryDate = yMd + "T" + time + ":00.000Z";
                    loopValue = time;
                } else {
                    tvClose.setText(time);
                    endDate = yMd + "T" + time + ":00.000Z";
                }
                if (TextUtils.isEmpty(expiryDate) || TextUtils.isEmpty(endDate)) {
                    return;
                }
                //国际标准格式
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",Locale.getDefault());
                try {
                    //开始的日期
                    Date start = format.parse(expiryDate);
                    //结束的日期
                    Date end = format.parse(endDate);
                    //计算时长
                    long startTime = start.getTime();
                    long endTime = end.getTime();
                    long diffTime =0;
                    if (startTime>endTime){
                        diffTime=endTime+24*60*60*1000-startTime;
                    }else {
                        diffTime = endTime - startTime;
                    }
                    //毫秒转成分
                    long nd = 1000 * 24 * 60 * 60;
                    long nh = 1000 * 60 * 60;
                    long nm = 1000 * 60;
                    // 计算差多少小时
                    long diffHour = diffTime % nd / nh;
                    // 计算差多少分钟
                    long diffMin = diffTime % nd % nh / nm;
                    duration = diffHour * 60 + diffMin;
                    String sDiffTime = diffHour + "h" + diffMin + "min";
                    tvDuration.setText(sDiffTime);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void cancel() {

            }
        });
    }


    private void delete() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        new CircleDialog.Builder()
                .setWidth(0.75f)
                .setTitle(getString(R.string.m8警告))
                .setText(getString(R.string.m确认删除))
                .setGravity(Gravity.CENTER).setPositive(getString(R.string.m9确定), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTime();
            }
        })
                .setNegative(getString(R.string.m7取消), null)
                .show(fragmentManager);

    }




    /**
     * 删除预约
     *
     */

    private void deleteTime() {
        LogUtil.d("删除预约");
        Mydialog.Show(EditDurationActivity.this);
        String json = new Gson().toJson(dataBean);
        JSONObject object = null;
        try {
            object = new JSONObject(json);
            object.put("ctype", "3");
            object.put("lan", getLanguage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtil.i(json);
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
                        toast(R.string.m135删除成功);
                        EventBus.getDefault().post(new FreshTimingMsg());
                        finish();
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
     * 修改预约
     *
     */

    private void editTime(String expiryDate, int loopType) {
        LogUtil.d("修改预约时间段");
        Mydialog.Show(EditDurationActivity.this);
        Map<String, Object> jsonMap = new LinkedHashMap<String, Object>();
        jsonMap.put("sn", chargingId);
        jsonMap.put("userId", SmartHomeUtil.getUserName());
        jsonMap.put("ctype", "1");
        jsonMap.put("connectorId", dataBean.getConnectorId());
        jsonMap.put("cKey", dataBean.getCKey());
        jsonMap.put("cValue", duration);
        jsonMap.put("reservationId", dataBean.getReservationId());
        jsonMap.put("expiryDate", expiryDate);
        jsonMap.put("lan", getLanguage());

        if (loopType == -1) {
            jsonMap.put("loopType", loopType);
        } else {
            jsonMap.put("loopType", loopType);
            jsonMap.put("loopValue", expiryDate.substring(11, 16));
        }
        String json = SmartHomeUtil.mapToJsonString(jsonMap);
        LogUtil.i(json);
        PostUtil.postJson(SmartHomeUrlUtil.postUpdateChargingReservelist(), json, new PostUtil.postListener() {
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
                        EventBus.getDefault().post(new FreshTimingMsg());
                        toast(R.string.m修改成功);
                        finish();
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


    private void addReserve(int loopType, long cValue, String loopValue) {
        Mydialog.Show(EditDurationActivity.this);
        Map<String, Object> jsonMap = new LinkedHashMap<String, Object>();
        jsonMap.put("action", "ReserveNow");
        jsonMap.put("connectorId", 1);
        jsonMap.put("expiryDate", expiryDate);
        jsonMap.put("chargeId",chargingId);
        jsonMap.put("userId",SmartHomeUtil.getUserName());
        jsonMap.put("cKey", "G_SetTime");
        jsonMap.put("cValue", cValue);
        jsonMap.put("loopType", loopType);
        jsonMap.put("lan", getLanguage());
        jsonMap.put("loopValue", loopValue);
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
                    int code = object.getInt("code");
                    String data = object.getString("data");
                    toast(data);
                    if (code == 0) {
                        EventBus.getDefault().post(new FreshTimingMsg());
                        finish();
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
    }
}
