package com.growatt.chargingpile.util;

import android.text.TextUtils;

import com.google.zxing.common.StringUtils;
import com.growatt.chargingpile.bean.ChargingBean;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 搜索工具
 */
public class SearchUtils {

    public static List<ChargingBean.DataBean> search(CharSequence charSequence,
                                                     List<ChargingBean.DataBean> allContacts) {
        List<ChargingBean.DataBean> dataBeanArrayList = new ArrayList<ChargingBean.DataBean>();

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
