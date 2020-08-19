package com.growatt.chargingpile.util;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.growatt.chargingpile.R;
import com.maning.mndialoglibrary.MProgressDialog;

public class Mydialog {
	/**
	 * 默认延时消失时间
	 */
	private static final int DEFAULT_DELAY_MILLIS = 30000;
	private static Handler mDelayHandler = new Handler();
	private static Runnable runnableDelay = MProgressDialog::dismissProgress;
	public static void Show(@NonNull Activity act, String msg){
		if (TextUtils.isEmpty(msg)){
			msg = "";
		}
		MProgressDialog.showProgress(act,msg);
		delayDismissDialog(DEFAULT_DELAY_MILLIS);
	}
	public static void Show(@NonNull Activity act){
		MProgressDialog.showProgress(act,"");
		delayDismissDialog(DEFAULT_DELAY_MILLIS);
	}
	public static void Show(@NonNull Activity act, int msg){
		MProgressDialog.showProgress(act,"");
		delayDismissDialog(DEFAULT_DELAY_MILLIS);
	}
	public static void Show(@NonNull Context act){
		MProgressDialog.showProgress(act,"");
		delayDismissDialog(DEFAULT_DELAY_MILLIS);
	}
	public static void Show(@NonNull Context act, String msg){
		if (TextUtils.isEmpty(msg)){
			msg = "";
		}
		MProgressDialog.showProgress(act,msg);
		delayDismissDialog(DEFAULT_DELAY_MILLIS);
	}
	public static void Show(@NonNull Context act, int msg){
		MProgressDialog.showProgress(act,"");
		delayDismissDialog(DEFAULT_DELAY_MILLIS);
	}
	public static void Dismiss(){
		MProgressDialog.dismissProgress();
	}

	/**
	 * 延时消失dialog
	 * @param delayMillis:时间：毫秒
	 * @param act
	 */
	public static void showDelayDismissDialog(long delayMillis ,@NonNull Context act){
		MProgressDialog.showProgress(act,"");
		delayDismissDialog(delayMillis);
	}
	public static void delayDismissDialog(long delayMillis) {
		mDelayHandler.removeCallbacks(runnableDelay);
		mDelayHandler.postDelayed(runnableDelay,delayMillis);
	}
}
