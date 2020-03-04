package com.growatt.chargingpile.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2019/11/25.
 */

public class DateUtil {
    public static String getOtherDay(int day) {
        //声明日期格式化样式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date dateNow = new Date();
        //通过Calendar的实现类获得Calendar实例
        Calendar calendar = Calendar.getInstance();
        //设置格式化的日期
        calendar.setTime(dateNow);
        calendar.add(Calendar.DATE, day);
        Date currenDate = calendar.getTime();
        return dateFormat.format(currenDate);
    }
}
