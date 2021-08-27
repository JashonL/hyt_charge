package com.growatt.chargingpile.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class TimeSelectActivity extends BaseActivity {

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
    @BindView(R.id.start)
    TextView start;
    @BindView(R.id.end)
    TextView end;

    private Unbinder bind;
    private String endTime;
    private String startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_select);
        bind = ButterKnife.bind(this);
        initHeaderView();
        initIntent();
        initViews();
    }

    private void initViews() {
        if (!TextUtils.isEmpty(startTime))
            start.setText(startTime);
        if (!TextUtils.isEmpty(endTime))
            end.setText(endTime);
    }

    private void initIntent() {
        startTime = getIntent().getStringExtra("start");
        endTime = getIntent().getStringExtra("end");
    }


    private void initHeaderView() {
        ivLeft.setImageResource(R.drawable.back);
        tvRight.setText(R.string.m182保存);
        tvTitle.setText(R.string.m283选择时间);
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.title_1));
        //设置字体加粗
        tvTitle.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        tvRight.setTextColor(ContextCompat.getColor(this, R.color.title_1));
    }


    @OnClick({R.id.ivLeft, R.id.tvRight, R.id.start, R.id.end})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivLeft:
                finish();
                break;
            case R.id.tvRight:
                Intent intent = new Intent();
                intent.putExtra("start", startTime);
                intent.putExtra("end", endTime);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.start:
                showTimePickView(1);
                break;
            case R.id.end:
                showTimePickView(2);
                break;
        }
    }

    /**
     * 弹出时间选择器
     */
    public void showTimePickView(int type) {
        Calendar selectedDate = Calendar.getInstance();//系统当前时间
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        TimePickerView pvCustomTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", getResources().getConfiguration().locale);
                String time = sdf.format(date);
                if (type==2) {
                    endTime = time;
                    end.setText(endTime);
                } else {
                    startTime = time;
                    start.setText(startTime);
                }
            }
        })
                .setType(new boolean[]{false, false, false, true, true, false})// 默认全部显示
                .setCancelText(getString(R.string.m7取消))//取消按钮文字
                .setSubmitText(getString(R.string.m9确定))//确认按钮文字
                .setContentTextSize(18)
                .setTitleSize(18)//标题文字大小
                .setTitleText(getString(R.string.m283选择时间))//标题文字
                .setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(true)//是否循环滚动
                .setTitleColor(0xff333333)//标题文字颜色
                .setSubmitColor(0xff333333)//确定按钮文字颜色
                .setCancelColor(0xff999999)//取消按钮文字颜色
                .setTitleBgColor(0xffffffff)//标题背景颜色 Night mode
                .setBackgroundId(R.drawable.shape_white_corner_bg)
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
