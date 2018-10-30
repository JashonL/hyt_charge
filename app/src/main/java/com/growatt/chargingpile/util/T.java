package com.growatt.chargingpile.util;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.growatt.chargingpile.R;


public class T {
//	public static void make(int object) {
//		make(object, ShineApplication.context);
//	}
//	public static void make(String object) {
//		make(object , ShineApplication.context);
//	}
	public static void make(int object, Context context) {
		if(MyUtil.isNotificationEnabled(context)){
			Toast.makeText(context, object, Toast.LENGTH_LONG).show();
		}else{
		    EToast.makeText(context, object, EToast.LENGTH_LONG).show();
		}
	}
	public static void make(String object, Context context) {
		if(MyUtil.isNotificationEnabled(context)){
			Toast.makeText(context, object, Toast.LENGTH_LONG).show();
		}else{
		    EToast.makeText(context, object, EToast.LENGTH_LONG).show();
		}
	}
	public static void dialog(Context context,String object){
		Builder builder = new Builder(context);
		builder.setTitle(R.string.m27温馨提示).setMessage(object).setNegativeButton(R.string.m9确定, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
			}
		}).create();
		builder.show();
	}
	public static void dialog(Context context,int object){
		Builder builder = new Builder(context);
		builder.setTitle(R.string.m27温馨提示).setMessage(object).setNegativeButton(R.string.m9确定, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
			}
		}).create();
		builder.show();
	}
}
