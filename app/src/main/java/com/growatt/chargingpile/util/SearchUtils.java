package com.growatt.chargingpile.util;

import com.growatt.chargingpile.bean.ChargingBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索工具
 */
public class SearchUtils {

    public static List<ChargingBean.DataBean> search(CharSequence charSequence,
                                                     List<ChargingBean.DataBean> allContacts) {
        List<ChargingBean.DataBean> dataBeanArrayList = new ArrayList<>();

        for (ChargingBean.DataBean dataBean : allContacts) {
            if (contains(dataBean, charSequence.toString())) {
                dataBeanArrayList.add(dataBean);
            }
        }
        return dataBeanArrayList;
    }

    private static boolean contains(ChargingBean.DataBean dataBean, String search) {
        if (dataBean.getName().toLowerCase().contains(search.toLowerCase())){
            return true;
        }
        return false;
    }

}
