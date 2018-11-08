package com.growatt.chargingpile.util;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2018/11/8.
 */

public class MathUtil {

    /**
     *
     * @param f 源数据
     * @param num 保留的位数
     * @return
     */
    public static String roundDouble2String(double f,int num){
        BigDecimal bg = new BigDecimal(f);
        double f1 = bg.setScale(num, BigDecimal.ROUND_HALF_UP).doubleValue();
        return String.valueOf(f1);
    }
}
