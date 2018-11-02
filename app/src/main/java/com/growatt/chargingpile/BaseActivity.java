package com.growatt.chargingpile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.growatt.chargingpile.activity.LoginActivity;
import com.growatt.chargingpile.application.MyApplication;
import com.growatt.chargingpile.util.BarTextColorUtils;
import com.growatt.chargingpile.util.EToast;
import com.growatt.chargingpile.util.MyUtil;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.util.PermissionCodeUtil;

import org.xutils.x;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Administrator on 2018/10/16.
 */

public abstract class BaseActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    Toast toast;
    protected Context mContext;
    /**
     * 是否继续询问权限
     */
    protected boolean isContinue = true;

    public enum Position {
        LEFT, CENTER, RIGHT
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //沉浸式状态栏处理
        initStatusBar();
        BarTextColorUtils.StatusBarLightMode(this);
        mContext = this;
        x.view().inject(this);
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        if (savedInstanceState != null) {
            savedInstanceState(savedInstanceState);
            return;
        }
        MyApplication.getInstance().addActivity(this);
    }


    /**
     * 通过不同类型activity请求
     */
    public void requestWindowTitleByActivity(){
        if (this instanceof AppCompatActivity){
            ((AppCompatActivity)this).supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        }else {
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
    }

    /**
     * 沉浸式状态栏处理
     */
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0 全透明实现
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);//calculateStatusColor(Color.WHITE, (int) alphaValue)
        }
    }

    public void savedInstanceState(Bundle b) {
        Intent intent = new Intent(MyApplication.context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        MyApplication.context.startActivity(intent);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        outState.putInt("num", 0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
    }

    @Override
    protected void onDestroy() {
        EToast.reset();
        Mydialog.Dismiss();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * ��ȡ��ǰ���
     *
     * @return
     */
    public static int getCurrentYear() {

        return Calendar.getInstance().get(Calendar.YEAR);
    }

    /**
     * @return
     */
    public int getLanguage() {
        int lan;
        Locale locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.toLowerCase().contains("zh")) {
            lan = 0;
        } else if (language.toLowerCase().contains("en")) {
            lan = 1;
        } else {
            lan = 2;
        }
        return lan;
    }

    public String getLanguageStr() {
        Locale locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.toLowerCase().contains("zh")) {
            language = "zh_cn";
        } else if (language.toLowerCase().contains("en")) {
            language = "en";
        } else if (language.toLowerCase().contains("fr")) {
            language = "fr";
        } else if (language.toLowerCase().contains("ja")) {
            language = "ja";
        } else if (language.toLowerCase().contains("it")) {
            language = "it";
        } else if (language.toLowerCase().contains("ho")) {
            language = "ho";
        } else if (language.toLowerCase().contains("tk")) {
            language = "tk";
        } else if (language.toLowerCase().contains("pl")) {
            language = "pl";
        } else if (language.toLowerCase().contains("gk")) {
            language = "gk";
        } else if (language.toLowerCase().contains("gm")) {
            language = "gm";
        } else {
            language = "en";
        }
        return language;
    }

    public TextView setHeaderTitle(View headerView, String title, Position position, int textColor, boolean isBold) {
        TextView tv = (TextView) headerView.findViewById(R.id.tvTitle);
        if (textColor != -1) {
            tv.setTextColor(ContextCompat.getColor(this, textColor));
        }
        if (isBold) {
            tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        }
        if (title == null) {
            tv.setText("TITLE");
        } else {
            tv.setText(title);
        }

        switch (position) {
            case LEFT:
                tv.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                break;

            default:
                tv.setGravity(Gravity.CENTER);
                break;
        }
        return tv;

    }

    public TextView setHeaderTvRight(View headerView, String title, View.OnClickListener listener, int textColor) {
        TextView tv = (TextView) headerView.findViewById(R.id.tvRight);
        tv.setTextColor(textColor);
        if (title == null) {
            tv.setText("");
        } else {
            tv.setText(title);
        }
        if (listener != null) {
            tv.setOnClickListener(listener);
        }
        return tv;
    }

    public void setHeaderTvLeft(View headerView, String text, View.OnClickListener listener) {
        TextView tv = (TextView) headerView.findViewById(R.id.tvLeft);
        if (text == null) {
            tv.setText("");
        } else {
            tv.setText(text);
        }
        if (listener != null) {
            tv.setOnClickListener(listener);
        }
    }

    public void setHeaderTitle(View headerView, String title) {
        setHeaderTitle(headerView, title, -1, false);
    }


    public void setHeaderTitle(View headerView, String title, int textcolor, boolean isBold) {
        setHeaderTitle(headerView, title, Position.CENTER, textcolor, isBold);
    }

    /**
     * @param headerView
     * @param resId
     * @param listener
     */
    public ImageView setHeaderImage(View headerView, int resId, Position position, View.OnClickListener listener) {
        ImageView iv = null;
        switch (position) {
            case LEFT:
                iv = (ImageView) headerView.findViewById(R.id.ivLeft);
                break;

            default:
                iv = (ImageView) headerView.findViewById(R.id.ivRight);
                break;
        }

        iv.setImageResource(resId);
//		iv.setColorFilter(Color.WHITE,Mode.SRC_ATOP);
        if (listener != null) {
            iv.setOnClickListener(listener);
        }
        return iv;
    }

    public void setHeaderImage(View headerView, int resId, Position position) {
        setHeaderImage(headerView, resId, position, null);
    }

    public void setHeaderImage(View headerView, int resId) {
        setHeaderImage(headerView, resId, Position.LEFT);
    }


    public void toast(String text) {
        toast(text, Toast.LENGTH_LONG);
    }

    public void toast(String text, int len) {

        if (TextUtils.isEmpty(text)) {
            return;
        }
        if (MyUtil.isNotificationEnabled(this)) {
            Toast.makeText(this, text, len).show();
        } else {
            EToast.makeText(this, text, len).show();
        }
    }

    public void toast(int resId) {
        toast(resId, Toast.LENGTH_LONG);
    }

    public void toast(int resId, int len) {
        String text = getString(resId);
        if (TextUtils.isEmpty(text)) {
            return;
        }
        if (MyUtil.isNotificationEnabled(this)) {
            Toast.makeText(this, text, len).show();
        } else {
            EToast.makeText(this, text, len).show();
        }
    }

    public void log(String log) {
        Log.d("TAG", this.getClass().getSimpleName() + ": " + log);
    }

    public void toastAndLog(String text, String log) {
        toast(text);
        log(log);
    }

    public void jumpTo(Class<?> clazz, boolean isFinish) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
        if (isFinish) {
            finish();
        }
    }

    public void jumpTo(Intent intent, boolean isFinish) {
        startActivity(intent);
        if (isFinish) {
            finish();
        }
    }

    /**
     * @param editTexts
     */

    public boolean isEmpty(EditText... editTexts) {

        for (EditText et : editTexts) {
            if (TextUtils.isEmpty(et.getText().toString())) {
                toast(getString(R.string.m140不能为空));
                return true;
            }
        }

        return false;
    }


    public String getNumberFormat(String str, int num) {
        BigDecimal bd = new BigDecimal(str);
        return bd.setScale(num, BigDecimal.ROUND_HALF_UP) + "";
    }

    //获取屏幕密度
    public float getDensity() {
        return getResources().getDisplayMetrics().density;
    }

    public void onStart() {
        super.onStart();
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onPause() {
        super.onPause();
    }

    public void onStop() {
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    public void onPermissionsDenied(int requestCode, List<String> perms, String permission) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            if (getLanguage() == 0) {
                new AppSettingsDialog
                        .Builder(this)
                        .setTitle(R.string.m权限请求)
                        .setRationale(String.format(getString(R.string.m权限请求步骤), permission, permission))
                        .setPositiveButton(R.string.m9确定)
                        .setRequestCode(requestCode)
                        .setNegativeButton(R.string.m7取消)
                        .build()
                        .show();
            } else {
                new AppSettingsDialog.Builder(this).setRequestCode(requestCode).build().show();
            }
        }
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        /**
         * 全局判断权限
         */
        switch (requestCode) {

        }
    }


    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        switch (requestCode) {
            case PermissionCodeUtil.PERMISSION_EXTERNAL_STORAGE_CODE:
                onPermissionsDenied(requestCode, perms, getString(R.string.m存储));
                break;
            case PermissionCodeUtil.PERMISSION_CAMERA_CODE:
                onPermissionsDenied(requestCode, perms, getString(R.string.m相机));
                break;
            case PermissionCodeUtil.PERMISSION_CAMERA_ONE_CODE:
                onPermissionsDenied(requestCode, perms, getString(R.string.m相机单));
                break;
            case PermissionCodeUtil.PERMISSION_LOCATION_CODE:
                onPermissionsDenied(requestCode, perms, getString(R.string.m位置权限));
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode >= PermissionCodeUtil.PERMISSION_CAMERA_CODE) {
            this.onPermissionsGranted(requestCode, null);
        }
    }


}
