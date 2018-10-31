package com.growatt.chargingpile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.util.MyUtil;
import com.growatt.chargingpile.view.NumberPicker;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChargingPresetEditActivity extends BaseActivity {

    @BindView(R.id.headerView)
    View headerView;
    @BindView(R.id.tv_preset_type)
    TextView tvType;

    @BindView(R.id.ll_ele_money)
    LinearLayout llEleMoney;
    @BindView(R.id.et_input_sn)
    EditText etValue;

    @BindView(R.id.ll_time)
    LinearLayout llTime;

    @BindView(R.id.np_hour)
    NumberPicker npHour;

    @BindView(R.id.np_minute)
    NumberPicker npMinute;

    private int type = 1;//1.金额 2.电量 3.时长
    private String textType;

    private String[] hours;
    private String[] minutes;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charging_preset_edit);
        ButterKnife.bind(this);
        initHeaderView();
        initIntent();
        initViews();
        initResource();
    }

    private void initResource() {
        textType = getString(R.string.m209预设);
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
        initData(npHour, hours, "00");
        initData(npMinute, minutes, "00");
    }

    private void initHeaderView() {
        setHeaderImage(headerView, R.drawable.back, Position.LEFT, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setHeaderTitle(headerView, getString(R.string.m198预设充电方案), R.color.title_1, false);
    }


    private void initViews() {
        String scheme = null;
        String hint = null;
        if (type == 1) {
            MyUtil.showAllView(llEleMoney);
            MyUtil.hideAllView(View.GONE, llTime);
            scheme = getString(R.string.m200金额);
            etValue.setHint(getString(R.string.m210请输入金额));
        } else if (type == 2) {
            MyUtil.showAllView(llEleMoney);
            MyUtil.hideAllView(View.GONE, llTime);
            scheme = getString(R.string.m201电量);
            etValue.setHint(getString(R.string.m211请输入电量));
        } else {
            MyUtil.showAllView(llTime);
            MyUtil.hideAllView(View.GONE, llEleMoney);
            scheme = getString(R.string.m202时长);
        }
        textType = String.format(getString(R.string.m198预设充电方案) + "-%s", scheme);
        SpannableString spannableString = new SpannableString(textType);
        if (getLanguage()==0){
            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.green_1)), 7, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }else {
            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.green_1)), 23, textType.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        tvType.setText(spannableString);
    }

    private void initIntent() {
        type = getIntent().getIntExtra("type", 1);
    }

    @OnClick(R.id.btConfirm)
    public void onClickListener(View view) {
        switch (view.getId()) {
            case R.id.btConfirm:
                backMainPage();
                break;
        }

    }

    private void backMainPage() {
        Intent intent = new Intent();
        String value = etValue.getText().toString();
        if (type == 1) {
            if (TextUtils.isEmpty(value)) {
                toast(getString(R.string.m210请输入金额));
                return;
            }
            if (!MyUtil.isNumber(value)) {
                toast(getString(R.string.m输入金额不正确));
                return;
            }
            intent.putExtra("money", value);
        } else if (type == 2) {
            if (TextUtils.isEmpty(value)) {
                toast(getString(R.string.m211请输入电量));
                return;
            }
            if (!MyUtil.isNumber(value)) {
                toast(getString(R.string.m输入电量不正确));
                return;
            }
            intent.putExtra("electric", value);
        } else {
            String hour = hours[npHour.getValue()];
            String minute = minutes[npMinute.getValue()];
            if ("00".equals(hour) && "00".equals(minute)) {
                toast(getString(R.string.m129时长设置不能为空));
                return;
            }
            intent.putExtra("hour", hour);
            intent.putExtra("minute", minute);
        }
        setResult(RESULT_OK, intent);
        finish();
    }


    private void initData(NumberPicker numberPicker, String[] values, String value) {
        //展示值，一个数组
        numberPicker.setDisplayedValues(values);
        int index = 0;
        for (int i = 0; i < values.length; i++) {
            if (value.equals(values[i])) {
                index = i;
                break;
            }
        }
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(values.length - 1);
        //设置当前值
        numberPicker.setValue(index);
    }

}
