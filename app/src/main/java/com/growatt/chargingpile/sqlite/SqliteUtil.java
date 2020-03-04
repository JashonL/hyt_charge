package com.growatt.chargingpile.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.growatt.chargingpile.application.MyApplication;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class SqliteUtil {
	public static String inquiryurl(){
		String times = "";
		DateSqlite dataSQiLte=new DateSqlite(MyApplication.context);
		SQLiteDatabase base = dataSQiLte.getWritableDatabase();
		Cursor c = base.query("url",null,null,null,null,null,null);
		if(c.getCount()>0){
			if(c.moveToFirst()){
				for(int i=0;i<c.getCount();i++){
					times=c.getString(c.getColumnIndex("url"));
					c.moveToNext();
				}
			}
		}
		c.close();
		base.close();

		return times;
	}
	public static Map<String, Object> inquirylogin(){
		Map<String, Object >map=new HashMap<String, Object>();
		DateSqlite dataSQiLte=new DateSqlite(MyApplication.context);
		SQLiteDatabase base = dataSQiLte.getWritableDatabase();
		Cursor c = base.query("login",null,null,null,null,null,null);
		if(c.getCount()>0){
			if(c.moveToLast()){
				map.put("name", c.getString(c.getColumnIndex("name")));
				map.put("pwd", c.getString(c.getColumnIndex("pwd")));
			}
		}
		c.close();
		base.close();
		return map;
	}

	public static void url(String url){
		DateSqlite dataSQiLte=new DateSqlite(MyApplication.context);
		SQLiteDatabase base = dataSQiLte.getWritableDatabase();
		base.delete("url",null, null);
		ContentValues values = new ContentValues();
		values.put("url", url);
		base.insert("url", null, values);
		base.close();
	}


	/**
	 * 按用户名保存
	 * @param map
	 */
	public static void Message(Map<String, Object> map){
		System.out.println(map.toString());
		DateSqlite dataSQiLte=new DateSqlite(MyApplication.context);
		SQLiteDatabase base = dataSQiLte.getWritableDatabase();
		ContentValues values = new ContentValues();
		try {
			JSONObject jsonObject=new JSONObject(map.get("cn.jpush.android.EXTRA").toString());
			values.put("content", jsonObject.get("content").toString());
			values.put("type", jsonObject.get("type").toString());
			values.put("date", jsonObject.get("date").toString());
			values.put("title", jsonObject.get("title").toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		values.put("id", map.get("cn.jpush.android.NOTIFICATION_ID").toString());
		values.put("msgid", map.get("cn.jpush.android.MSG_ID").toString());
		try {
			final Map<String, Object> userMap = SqliteUtil.inquirylogin();
			if(userMap.size() > 0){
                values.put("username",userMap.get("name").toString().trim());
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		Cursor c = base.query("message",new String[]{"msgid"},"msgid = ?",new String[]{map.get("cn.jpush.android.MSG_ID").toString()},null,null,null);
		if(c.getCount()==0){
			base.insert("message", null, values);
		}
		c.close();
		base.close();
	}
	public static void login(String name,String pwd){
		DateSqlite dataSQiLte=new DateSqlite(MyApplication.context);
		SQLiteDatabase base = dataSQiLte.getWritableDatabase();
		base.delete("login","name = ?", new String[]{name});
		ContentValues values = new ContentValues();
		values.put("name", name);
		values.put("pwd", pwd);
		base.insert("login", null, values);
		base.close();
	}

	public static void plant(String time){
		DateSqlite dataSQiLte=new DateSqlite(MyApplication.context);
		SQLiteDatabase base = dataSQiLte.getWritableDatabase();
		base.delete("plant",null, null);
		ContentValues values = new ContentValues();
		values.put("plant", time);
		base.insert("plant", null, values);
		base.close();
	}



	public static void setService(String service,int app_code){
		DateSqlite dataSQiLte=new DateSqlite(MyApplication.context);
		SQLiteDatabase base = dataSQiLte.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("service", service);
		values.put("app_code", app_code);
		base.insert("service", null, values);
		base.close();
	}


	public static int getApp_Code(){
		int app_code=-1;
		DateSqlite dataSQiLte=new DateSqlite(MyApplication.context);
		SQLiteDatabase base = dataSQiLte.getWritableDatabase();
		Cursor c = base.query("service",null,null,null,null,null,null);
		if(c.getCount()>0){
			if(c.moveToFirst()){
				for(int i=0;i<c.getCount();i++){
					app_code=c.getInt(c.getColumnIndex("app_code"));
					c.moveToNext();
				}
			}
		}
		c.close();
		base.close();
		return app_code;
	}


}













