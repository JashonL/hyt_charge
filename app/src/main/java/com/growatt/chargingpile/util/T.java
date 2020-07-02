package com.growatt.chargingpile.util;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;

import com.growatt.chargingpile.R;
import com.hjq.toast.ToastUtils;

public class T {
	//	public static void make(int object) {
//		make(object, ShineApplication.context);
//	}
//	public static void make(String object) {
//		make(object , ShineApplication.context);
//	}
	public static void make(int object, Context context) {
		ToastUtils.show(object);
//		if(MyControl.isNotificationEnabled(context)){
//			Toast.makeText(context, object, Toast.LENGTH_LONG).show();
//		}else{
//		    EToast.makeText(context, object, EToast.LENGTH_LONG).show();
//		}
	}
	public static void make(String object, Context context) {
		ToastUtils.show(object);
//		if(MyControl.isNotificationEnabled(context)){
//			Toast.makeText(context, object, Toast.LENGTH_LONG).show();
//		}else{
//		    EToast.makeText(context, object, EToast.LENGTH_LONG).show();
//		}
	}
	public static void toast(String object) {
		ToastUtils.show(object);
	}
	public static void toast(int object) {
		ToastUtils.show(object);
	}

}
