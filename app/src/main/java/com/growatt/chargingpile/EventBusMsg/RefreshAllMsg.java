package com.growatt.chargingpile.EventBusMsg;

import com.growatt.chargingpile.bean.ChargingBean;

import java.util.List;

/**
 * Created by Administrator on 2019/6/21.
 */

public class RefreshAllMsg {
    private List<ChargingBean.DataBean.PriceConfBean> priceConfBeanList;
    public RefreshAllMsg(List<ChargingBean.DataBean.PriceConfBean> priceConfBeanList) {
        this.priceConfBeanList = priceConfBeanList;
    }

    public List<ChargingBean.DataBean.PriceConfBean> getPriceConfBeanList() {
        return priceConfBeanList;
    }

    public void setPriceConfBeanList(List<ChargingBean.DataBean.PriceConfBean> priceConfBeanList) {
        this.priceConfBeanList = priceConfBeanList;
    }
}
