package com.growatt.chargingpile.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.growatt.chargingpile.application.MyApplication;

import java.util.Locale;

/**
 * Created：2021/8/30 on 11:33:52
 * Author:gaideng on admin
 * Description:
 */

public class Utils {

    public static String getVersionName(Context context) {
        PackageManager manager = context.getPackageManager();
        String name = null;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }

    public static int getLanguage() {
        int lan = 1;
        Locale locale = MyApplication.getInstance().getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.toLowerCase().contains("zh")) {
            language = "zh_cn";
            lan = 0;
            if (!locale.getCountry().toLowerCase().equals("cn")) {
                lan = 14;
            }
        }
        if (language.toLowerCase().contains("en")) {
            language = "en";
            lan = 1;
        }
        if (language.toLowerCase().contains("fr")) {
            language = "fr";
            lan = 2;
        }
        if (language.toLowerCase().contains("ja")) {
            language = "ja";
            lan = 3;
        }
        if (language.toLowerCase().contains("it")) {
            language = "it";
            lan = 4;
        }
        if (language.toLowerCase().contains("ho")) {
            language = "ho";
            lan = 5;
        }
        if (language.toLowerCase().contains("tk")) {
            language = "tk";
            lan = 6;
        }
        if (language.toLowerCase().contains("pl")) {
            language = "pl";
            lan = 7;
        }
        if (language.toLowerCase().contains("gk")) {
            language = "gk";
            lan = 8;
        }
        if (language.toLowerCase().contains("gm")) {
            language = "gm";
            lan = 9;
        }
        if (language.toLowerCase().contains("pt")) {
            language = "pt";
            lan = 10;
        }
        if (language.toLowerCase().contains("sp")) {
            language = "sp";
            lan = 11;
        }
        if (language.toLowerCase().contains("vn")) {
            language = "vn";
            lan = 12;
        }
        if (language.toLowerCase().contains("hu")) {
            language = "hu";
            lan = 13;
        }
        return lan;
    }

}
