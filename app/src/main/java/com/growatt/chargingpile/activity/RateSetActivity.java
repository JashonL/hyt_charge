package com.growatt.chargingpile.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.EventBusMsg.SetRateMsg;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.adapter.RateSetAdapter;
import com.growatt.chargingpile.bean.ChargingBean;
import com.growatt.chargingpile.connutil.PostUtil;
import com.growatt.chargingpile.util.Cons;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.util.SmartHomeUrlUtil;
import com.mylhyl.circledialog.CircleDialog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class RateSetActivity extends BaseActivity implements BaseQuickAdapter.OnItemChildClickListener {


    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.ivLeft)
    ImageView ivLeft;
    @BindView(R.id.tvRight)
    TextView tvRight;
    @BindView(R.id.headerView)
    LinearLayout headerView;
    @BindView(R.id.rv_rate_list)
    RecyclerView rvRateList;

    private RateSetAdapter mAdapter;

    private List<ChargingBean.DataBean.PriceConfBean> priceConfBeanList;
    private String chargingId;
    private Unbinder bind;
    private String symbol;
    private String endTime;
    private String startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_set);
        bind = ButterKnife.bind(this);
        initIntent();
        initViews();
    }

    private void initIntent() {
        chargingId = getIntent().getStringExtra("sn");
        symbol = getIntent().getStringExtra("symbol");
        if (TextUtils.isEmpty(symbol)) symbol = "";
        priceConfBeanList = getIntent().getParcelableArrayListExtra("rate");
        if (priceConfBeanList == null) priceConfBeanList = new ArrayList<>();
        for (int i = 0; i < priceConfBeanList.size(); i++) {
            priceConfBeanList.get(i).setSymbol(symbol);
        }
    }

    private void initViews() {
        ivLeft.setImageResource(R.drawable.back);
        tvTitle.setText(R.string.m152充电费率);
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.title_1));
        tvRight.setText(R.string.m182保存);
        tvRight.setTextColor(ContextCompat.getColor(this, R.color.maincolor_1));
        //初始化列表
        mAdapter = new RateSetAdapter(R.layout.item_set_rate, priceConfBeanList);
        rvRateList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvRateList.setAdapter(mAdapter);
        mAdapter.setOnItemChildClickListener(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bind != null) bind.unbind();
    }

    @OnClick({R.id.btnAdd, R.id.ivLeft, R.id.tvRight})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnAdd:
                addItem();
                break;
            case R.id.ivLeft:
                finish();
                break;
            case R.id.tvRight:
                if (isItemAllSetting()) {
                    if (isTimeCoveredOneDay())
                        requestEdit();
                    else
                        toast(R.string.m331时间必须包含24小时);
                } else {
                    toast(R.string.m330有时间段或费率未输入);
                }
                break;
        }
    }


    private void addItem() {
        if (priceConfBeanList.size() < 6) {
            ChargingBean.DataBean.PriceConfBean bean = new ChargingBean.DataBean.PriceConfBean();
            bean.setSymbol(symbol);
            mAdapter.addData(bean);
        } else {
            toast(R.string.m328设置不能超过6条);
        }
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        switch (view.getId()) {
            case R.id.fl_delete:
                mAdapter.remove(position);
                break;
            case R.id.tv_select_time:
                showTimePickView(false, position);
                break;
            case R.id.tv_rate_value:
                inputEdit(position);
                break;
        }
    }

    /**
     * 弹出时间选择器
     */
    public void showTimePickView(boolean isEnd, int pos) {
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
                    if (isTimeCoincide(statValue, endValue, pos)) {
                        toast(R.string.m333时间段不能重叠);
                        return;
                    }
                    if (statValue == endValue) {
                        toast(R.string.m332时间不能相同);
                        return;
                    }
                    String rateTime = startTime + "-" + endTime;
                    mAdapter.getData().get(pos).setTimeX(rateTime);
                    mAdapter.notifyItemChanged(pos);
                } else {
                    startTime = time;
                    showTimePickView(true, pos);
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


    /**
     * 请求设置费率
     */

    private void requestEdit() {
        Mydialog.Show(this);
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("cmd","addPrice");
            jsonObject.put("userId",Cons.userBean.getAccountName());
            jsonObject.put("chargeId",chargingId);
            jsonObject.put("lan", getLanguage());
            JSONArray jsonArray = new JSONArray();
            List<ChargingBean.DataBean.PriceConfBean> data = mAdapter.getData();
            if (!data.isEmpty()) {
                for (ChargingBean.DataBean.PriceConfBean bean : data) {
                    JSONObject rateJson = new JSONObject();
                    rateJson.put("time",bean.getTimeX());
                    rateJson.put("price",bean.getPrice());
                    rateJson.put("name","");
                    jsonArray.put(rateJson);
                }

                jsonObject.put("priceConf", jsonArray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json = jsonObject.toString();
        PostUtil.postJson(SmartHomeUrlUtil.postByCmd(), json, new PostUtil.postListener() {
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
                        EventBus.getDefault().post(new SetRateMsg(mAdapter.getData()));
                        finish();
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


    /*判断时间是否重合*/
    private boolean isTimeCoincide(int start, int end, int pos) {
        List<ChargingBean.DataBean.PriceConfBean> data = mAdapter.getData();
        for (int i = 0; i < data.size(); i++) {
            ChargingBean.DataBean.PriceConfBean bean = data.get(i);
            String time = bean.getTimeX();
            if (TextUtils.isEmpty(time)) continue;
            if (i == pos) continue;//修改时不跟自己比
            String[] s = time.split("[\\D]");
            int AlreadyStart = Integer.parseInt(s[0]) * 60 + Integer.parseInt(s[1]);
            int AlreadyEnd = Integer.parseInt(s[2]) * 60 + Integer.parseInt(s[3]);
            if ((start > AlreadyStart && start < AlreadyEnd) || (end > AlreadyStart && end < AlreadyEnd))
                return true;
            if ((AlreadyStart > start && AlreadyStart < end) || (AlreadyEnd > start && AlreadyEnd < end))
                return true;
            if (AlreadyStart == start && AlreadyEnd == end)
                return true;
        }
        return false;
    }


    /*判断时间是否涵盖24小时*/
    private boolean isTimeCoveredOneDay() {
        List<ChargingBean.DataBean.PriceConfBean> data = mAdapter.getData();
        int timeLong = 0;
        int oneday = 24 * 60;
        for (int i = 0; i < data.size(); i++) {
            ChargingBean.DataBean.PriceConfBean bean = data.get(i);
            String time = bean.getTimeX();
            if (TextUtils.isEmpty(time)) continue;
            String[] s = time.split("[\\D]");
            int start = Integer.parseInt(s[0]) * 60 + Integer.parseInt(s[1]);
            int end = Integer.parseInt(s[2]) * 60 + Integer.parseInt(s[3]);
            if (start > end) {
                timeLong += (oneday - start) + end;
            } else {
                timeLong += end - start;
            }
        }
        return timeLong == oneday;
    }

    /*判断是否有item未设置时间*/
    private boolean isItemAllSetting() {
        List<ChargingBean.DataBean.PriceConfBean> data = mAdapter.getData();
        for (int i = 0; i < data.size(); i++) {
            ChargingBean.DataBean.PriceConfBean bean = data.get(i);
            String time = bean.getTimeX();
            if (TextUtils.isEmpty(time)) return false;
        }
        return true;
    }


    /**
     * 弹框输入修改内容
     * item 修改项
     */
    private void inputEdit(int pos) {
        double price = mAdapter.getData().get(pos).getPrice();
        new CircleDialog.Builder()
                .setWidth(0.8f)
                .setInputHeight(100)
                .setTitle(this.getString(R.string.m152充电费率))
                .setInputText(String.valueOf(price))
                .setNegative(this.getString(R.string.m7取消), null)
                .configInput(params -> {
                    params.inputType = InputType.TYPE_CLASS_NUMBER
                            | InputType.TYPE_TEXT_FLAG_MULTI_LINE;
                })
                .setPositiveInput(this.getString(R.string.m9确定), (text, v) -> {
                    if (!TextUtils.isEmpty(text)){
                        mAdapter.getData().get(pos).setPrice(Double.parseDouble(text));
                        mAdapter.notifyItemChanged(pos);
                    }
                })
                .show(this.getSupportFragmentManager());
    }
}
