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
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.bean.ReservationBean;
import com.growatt.chargingpile.connutil.PostUtil;
import com.growatt.chargingpile.util.AlertPickDialog;
import com.growatt.chargingpile.util.Cons;
import com.growatt.chargingpile.util.MyUtil;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.util.SmartHomeUrlUtil;
import com.growatt.chargingpile.util.SmartHomeUtil;
import com.mylhyl.circledialog.CircleDialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.util.LogUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_duration);
        ButterKnife.bind(this);
        initHeaderView();
        initResource();
        initIntent();
        initViews();
    }


    private void initIntent() {
        String jsonBean = getIntent().getStringExtra("bean");
        if (!TextUtils.isEmpty(jsonBean)) {
            type = 1;
            dataBean = new Gson().fromJson(jsonBean, ReservationBean.DataBean.class);
            expiryDate = dataBean.getExpiryDate();
            endDate = dataBean.getEndDate();
            reservationId = dataBean.getReservationId();
            loopType = dataBean.getLoopType();
            duration=dataBean.getCValue();
            //获取年月
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            String yMd = sdf.format(date);
            //把时间改成今天的
            expiryDate = yMd + "T" + expiryDate.substring(11, 16) + ":00.000Z";
            endDate = yMd + "T" + endDate.substring(11, 16) + ":00.000Z";
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
            tvOpen.setText(expiryDate.substring(11, 16));
            tvClose.setText(endDate.substring(11, 16));
            MyUtil.showAllView(rlDelete);
            String time = calculationTime(expiryDate, endDate);
            tvDuration.setText(time);
        }
      /*  if (reservationId == -1) {
            MyUtil.hideAllView(View.GONE, rlDelete);
        } else {
            MyUtil.showAllView(rlDelete);
        }*/
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
        minutes = new String[6];
        int minute = 0;
        for (int i = 0; i < 6; i++) {
            if (i == 0) {
                minutes[i] = "00";
            } else {
                minutes[i] = String.valueOf(minute);
            }
            minute += 10;
        }
    }

    private void initHeaderView() {
        setHeaderImage(headerView, R.drawable.back, Position.LEFT, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setHeaderTitle(headerView, getString(R.string.m181设置定时), R.color.title_1, true);
        setHeaderTvRight(headerView, getString(R.string.m182保存), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == 1) {
                    if (duration > 0) {
                        if (cbEveryday.isChecked()) {
                            dataBean.setLoopType(0);
                        } else {
                            Date todayDate = new Date();
                            long daytime = todayDate.getTime();
                            long onTime = 0;
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                            //开始的日期
                            try {
                                Date start = format.parse(expiryDate);
                                onTime = start.getTime();
                                LogUtil.d("开启时间:" + expiryDate + "关闭时间：" + start);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (daytime > onTime) {
                                toast(getString(R.string.m请选择正确的时间段));
                                return;
                            }
                            dataBean.setLoopType(-1);
                        }
                        editTime("1", expiryDate, dataBean.getLoopType());
                    } else {
                        toast(getString(R.string.m请选择正确的时间段));
                    }
                } else {
                    if (duration <= 0) {
                        toast(getString(R.string.m请选择正确的时间段));
                        return;
                    }
                    int loopType;
                    if (cbEveryday.isChecked()) {
                        loopType = 0;
                    } else {
                        Date todayDate = new Date();
                        long daytime = todayDate.getTime();
                        long onTime = 0;
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        //开始的日期
                        try {
                            Date start = format.parse(expiryDate);
                            onTime = start.getTime();
                            LogUtil.d("开启时间:" + expiryDate + "关闭时间：" + start);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (daytime > onTime) {
                            toast(getString(R.string.m请选择正确的时间段));
                            return;
                        }
                        loopType = -1;
                    }
                    addReserve(loopType, duration, loopValue);

                }
            }
        }, R.color.blue_1);
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
                delete("3");
                break;
        }
    }


    private void selectTime(final int openOrclose) {
        AlertPickDialog.showTimePickerDialog(this, hours, "00", minutes, "00", new AlertPickDialog.AlertPickCallBack() {

            @Override
            public void confirm(String hour, String minute) {
                String time = hour + ":" + minute;
                //获取年月
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
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
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                try {
                    //开始的日期
                    Date start = format.parse(expiryDate);
                    //结束的日期
                    Date end = format.parse(endDate);
                    //计算时长
                    long startTime = start.getTime();
                    long endTime = end.getTime();
                    long diffTime = endTime - startTime;

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


    private void delete(final String ctype) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        new CircleDialog.Builder()
                .setWidth(0.75f)
                .setTitle(getString(R.string.m8警告))
                .setText(getString(R.string.m确认删除))
                .setGravity(Gravity.CENTER).setPositive(getString(R.string.m9确定), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTime(ctype);
            }
        })
                .setNegative(getString(R.string.m7取消), null)
                .show(fragmentManager);

    }


    private String calculationTime(String start, String end) {
        //国际标准格式
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            //开始的日期
            Date startDate = format.parse(start);
            //结束的日期
            Date endDate = format.parse(end);
            //计算时长
            long startTime = startDate.getTime();
            long endTime = endDate.getTime();
            long diffTime = endTime - startTime;

            //毫秒转成分
            long nd = 1000 * 24 * 60 * 60;
            long nh = 1000 * 60 * 60;
            long nm = 1000 * 60;
            // 计算差多少小时
            long diffHour = diffTime % nd / nh;
            // 计算差多少分钟
            long diffMin = diffTime % nd % nh / nm;
            String sDiffTime = diffHour + "h" + diffMin + "min";
            return sDiffTime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "0h0min";
    }


    /**
     * 删除预约
     *
     * @param ctype
     */

    private void deleteTime(String ctype) {
        LogUtil.d("删除预约");
        Mydialog.Show(EditDurationActivity.this);
        String json = new Gson().toJson(dataBean);
        JSONObject object = null;
        try {
            object = new JSONObject(json);
            object.put("ctype", ctype);
            object.put("lan", getLanguage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtil.i(json);
        PostUtil.postJson(SmartHomeUrlUtil.UPDATE_CHARGING_RESERVELIST, object.toString(), new PostUtil.postListener() {
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
     * @param ctype
     * @param expiryDate
     * @param loopType
     */

    private void editTime(String ctype, String expiryDate, int loopType) {
        LogUtil.d("修改预约时间段");
        Mydialog.Show(EditDurationActivity.this);
        Map<String, Object> jsonMap = new LinkedHashMap<String, Object>();
        jsonMap.put("sn", Cons.mCurrentPile.getChargeId());
        jsonMap.put("userId", Cons.mCurrentPile.getUserId());
        jsonMap.put("ctype", ctype);
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
        PostUtil.postJson(SmartHomeUrlUtil.UPDATE_CHARGING_RESERVELIST, json, new PostUtil.postListener() {
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
        jsonMap.put("chargeId", Cons.mCurrentPile.getChargeId());
        jsonMap.put("userId", Cons.userBean.getId());
        jsonMap.put("cKey", "G_SetTime");
        jsonMap.put("cValue", cValue);
        jsonMap.put("loopType", loopType);
        jsonMap.put("lan", getLanguage());
        if (loopType==0) {
            jsonMap.put("loopValue", loopValue);
        }
        String json = SmartHomeUtil.mapToJsonString(jsonMap);
        LogUtil.i(json);
        PostUtil.postJson(SmartHomeUrlUtil.REQUEST_RESEERVE_CHARGING, json, new PostUtil.postListener() {
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

}
