package com.growatt.chargingpile.util;

import com.growatt.chargingpile.bean.NoConfigBean;
import com.growatt.chargingpile.bean.RegisterMap;
import com.growatt.chargingpile.bean.UserBean;

public class Cons {
    public static String isflagId="ceshi007";
    public static RegisterMap regMap = new RegisterMap();
    public static UserBean userBean;

    public static NoConfigBean noConfigBean;

    public static NoConfigBean getNoConfigBean() {
        return noConfigBean;
    }

    public static void setNoConfigBean(NoConfigBean noConfigBean) {
        Cons.noConfigBean = noConfigBean;
    }
}
