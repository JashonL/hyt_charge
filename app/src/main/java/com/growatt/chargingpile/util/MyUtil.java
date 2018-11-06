package com.growatt.chargingpile.util;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.growatt.chargingpile.connutil.PostUtil;
import com.growatt.chargingpile.connutil.Urlsutil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.provider.Settings.System.DATE_FORMAT;

/**
 * Created by Administrator on 2018/10/16.
 */

public class MyUtil {

    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

    /**
     * 判断通知栏是否开启
     *
     * @param context 调用方法的上下文
     * @return
     */

    @SuppressLint("NewApi")
    public static boolean isNotificationEnabled(Context context) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT) {
            return true;
        }
        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;
        Class appOpsClass = null; /* Context.APP_OPS_MANAGER */
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE, String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
            int value = (Integer) opPostNotificationValue.get(Integer.class);
            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 校验手机号
     *
     * @param phone 手机号
     * @return
     */
    public static boolean regexCheckPhone(String phone) {
        boolean flag = false;
        try {
            Pattern regex = Pattern.compile("^(((13[0-9])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8})|(0\\d{2}-\\d{8})|(0\\d{3}-\\d{7})$");
            Matcher matcher = regex.matcher(phone);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    public static boolean regexCheckEmail(String email) {
        boolean flag = false;
        try {
            String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }


    /**
     * 通过国家码获取国家或者国家区号
     *
     * @param context
     * @param status:status=1(获取国家);status=2(获取国际区号);
     * @return
     */
    public static String getCountryAndPhoneCodeByCountryCode(Context context, int status) {
        String countryCode = Locale.getDefault().getCountry();
        String country = getCountryAndPhoneCode(context, countryCode, status);
        return country;
    }

    private static String getCountryAndPhoneCode(Context context, String countryCode, int status) {
        String countryOrPhoneCode = null;
        if (status == 1) {
            countryOrPhoneCode = "Other";
        } else if (status == 2) {
            countryOrPhoneCode = "86";
        }
        try {
            String json = readCountryByAssets(context);
            countryOrPhoneCode = parseJsonByCountryCode(countryOrPhoneCode, json, countryCode, status);
            return countryOrPhoneCode;
        } catch (Exception e) {
            e.printStackTrace();
            return countryOrPhoneCode;
        }
    }

    private static String parseJsonByCountryCode(String countryOrPhoneCode, String json, String countryCode, int status) throws Exception {
        //解析Json
        JSONObject jsonObj = new JSONObject(json);
        JSONArray array = jsonObj.getJSONArray("data");
        if (array != null) {
            int length = array.length();
            for (int i = 0; i < length; i++) {
                JSONObject countryObj = array.getJSONObject(i);
                if (status == 1) {
                    if (countryCode.contains(countryObj.getString("countryCode")) || countryCode.toUpperCase().contains(countryObj.getString("countryCode"))) {
                        countryOrPhoneCode = countryObj.getString("countryName");
                        break;
                    }
                } else if (status == 2) {
                    if (countryCode.contains(countryObj.getString("countryCode")) || countryCode.toUpperCase().contains(countryObj.getString("countryCode"))) {
                        countryOrPhoneCode = countryObj.getString("phoneCode");
                        break;
                    }
                }

            }
        }
        return countryOrPhoneCode;
    }

    private static String readCountryByAssets(Context context) throws Exception {
        InputStream inputStream = context.getAssets().open("englishCountryJson.txt");
        byte[] buffer = new byte[inputStream.available()];
        inputStream.read(buffer);
        String json = new String(buffer, "GB2312");
        return json;
    }


    public static void putAppErrMsg(final String errMsg, final Context context) {
        final String msg = "SystemType:1" +
                ";AppVersion:" + getAppVersion(context) +
                ";" + "SystemVersion:" + android.os.Build.VERSION.RELEASE +
                ";" + "PhoneModel:" + android.os.Build.MODEL +
                ";" + "UserName:" + (Cons.userBean != null ? Cons.userBean.getAccountName() : "") +
                ";" + "msg:" + errMsg;
        Log.i("TAG", msg);
        PostUtil.post(new Urlsutil().postSaveAppErrorMsg, new PostUtil.postListener() {

            @Override
            public void success(String json) {
            }

            @Override
            public void Params(Map<String, String> params) {
                params.put("time", MyUtil.getFormatDate("", null));
                params.put("msg", msg);
//				params.put("msg", "AppVersion:"+getAppVersion(context));
            }

            @Override
            public void LoginError(String str) {
            }
        });
    }


    public static String getFormatDate(String dateFromat, Date date) {
        if (TextUtils.isEmpty(dateFromat)) {
            dateFromat = DATE_FORMAT;
        }
        if (date == null) {
            date = new Date();
        }
        return getDateFormat(dateFromat).format(date);
    }

    private static ThreadLocal<DateFormat> threadLocal = new ThreadLocal<DateFormat>();

    /**
     * 获取SimpleDateFormat对象，线程安全
     *
     * @param dateFormat："yyyy-MM-dd HH:mm:ss"
     * @return
     */
    public static DateFormat getDateFormat(String dateFormat) {
        DateFormat df = threadLocal.get();
        if (df == null) {
            df = new SimpleDateFormat(dateFormat);
            threadLocal.set(df);
        }
        return df;
    }


    public static String getAppVersion(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "1.0";
    }


    /**
     * 隐藏所有的view
     *
     * @param visibity VISIBLE, INVISIBLE, or GONE
     * @param views
     */
    public static void hideAllView(int visibity, View... views) {
        for (View view : views) {
            if (view != null && view.getVisibility() == View.VISIBLE) {
                view.setVisibility(visibity);
            }
        }
    }

    /**
     * 隐藏所有的view
     *
     * @param views
     */
    public static void showAllView(View... views) {
        for (View view : views) {
            if (view != null && view.getVisibility() != View.VISIBLE) {
                view.setVisibility(View.VISIBLE);
            }
        }
    }

    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    //显示虚拟键盘
    public static void showKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.showSoftInput(v, 0);

    }


    //小数验证
    public static boolean isNumber(String str) {
        Pattern pattern = Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$"); // 判断小数点后2位的数字的正则表达式
        Matcher match = pattern.matcher(str);
        if (match.matches() == false) {
            return false;
        } else {
            return true;
        }
    }


    /**
     * 判断是否为合法IP * @return the ip
     */
    public static boolean isboolIp(String ipAddress) {
        if(TextUtils.isEmpty(ipAddress))
            return false;
        Pattern pattern = Pattern.compile("^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$");
        Matcher matcher = pattern.matcher(ipAddress);
        return matcher.find();
    }

}
