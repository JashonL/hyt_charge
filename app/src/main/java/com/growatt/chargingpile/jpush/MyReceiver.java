package com.growatt.chargingpile.jpush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;


import com.growatt.chargingpile.activity.ChargingPileActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 *
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
	private static final String TAG = "推送消息";
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

		Map<String, Object>map=new HashMap<String, Object>();
		for (String key : bundle.keySet()) {
			map.put(key, bundle.get(key));
		}

		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
			String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
			Log.d(TAG, "[shinephone] 接收Registration Id : " + regId);
			//send the Registration Id to your server...

		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {

			Log.d(TAG, "[shinephone] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));

//        	processCustomMessage(context, bundle);
//        	T.make(JPushInterface.EXTRA_MESSAGE);
//        	T.dialog(context,bundle.getString(JPushInterface.EXTRA_MESSAGE));
		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
			Log.d(TAG, "[shinephone] 接收到推送下来的通知");
			int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
			Log.d(TAG, "[shinephone] 接收到推送下来的通知的ID: " + notifactionId);

		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
			Log.d(TAG, "[shinephone] 用户点击打开了通知");
			//打开自定义的Activity
			try {

				if(map.containsKey("cn.jpush.android.EXTRA")){
					JSONObject jsonObject=new JSONObject(map.get("cn.jpush.android.EXTRA").toString());
					String str=jsonObject.get("type").toString();
					if("0".equals(str)){
//    					SqliteUtil.Message(map);
						Intent i = new Intent(context, ChargingPileActivity.class);
						i.putExtras(bundle);
						i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
						context.startActivity(i);
					}
					if("1".equals(str)){
						String id=jsonObject.get("id").toString();

						Intent in = context.getPackageManager()
								.getLaunchIntentForPackage(context.getPackageName());
						in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(in);
						//发广播至main进行界面跳转
						Intent in2=new Intent("intent.action.Message_My");
						if (android.os.Build.VERSION.SDK_INT >= 12) {
							in2.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);//3.1以后的版本需要设置Intent.FLAG_INCLUDE_STOPPED_PACKAGES
						}
						in2.putExtra("id", id);
						context.sendStickyBroadcast(in2);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
			Log.d(TAG, "[shinephone] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
			//在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

		} else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
			boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
			Log.w(TAG, "[shinephone]" + intent.getAction() +" connected state change to "+connected);
		} else {
			Log.d(TAG, "[shinephone] Unhandled intent - " + intent.getAction());
		}

	}
	// 打印所有的 intent extra 数据
//	private static String printBundle(Bundle bundle) {
//		StringBuilder sb = new StringBuilder();
//
//
//		}
//		if(map.containsKey("cn.jpush.android.NOTIFICATION_ID")){
//			values.put("id", map.get("cn.jpush.android.NOTIFICATION_ID").toString());
//		}
//		if(map.containsKey("cn.jpush.android.MSG_ID")){
//			values.put("msgid", map.get("cn.jpush.android.MSG_ID").toString());
//			base.insert("message", null, values);
//		}
//		return sb.toString();
//	}



	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			}else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
				if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
					Log.d(TAG, "This message has no Extra data");
					continue;
				}

				try {
					JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
					Iterator<String> it =  json.keys();

					while (it.hasNext()) {
						String myKey = it.next();
						sb.append("\nkey:" + key + ", value: [" +
								myKey + " - " +json.optString(myKey) + "]");
					}
				} catch (JSONException e) {
					Log.d(TAG, "Get message extra JSON error!");
				}

			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.get(key));
			}
		}
		return sb.toString();
	}

}


//if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
//	System.out.println(1);
//	sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
//}else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
//	sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
//} else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
//	if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
//		Log.i(TAG, "This message has no Extra data");
//		continue;
//	}
//
//	try {
//		JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
//		Iterator<String> it =  json.keys();
//
//		while (it.hasNext()) {
//			String myKey = it.next().toString();
//			sb.append("\nkey:" + key + ", value: [" +
//					myKey + " - " +json.optString(myKey) + "]");
//		}
//	} catch (JSONException e) {
//		Log.e(TAG, "Get message extra JSON error!");
//	}
//
//} else {
//	sb.append("\n键:" + key + ", 值:" + bundle.getString(key));
//}
