package com.growatt.chargingpile.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.List;
import java.util.Set;

public class SharedPreferencesUnit {
    private static SharedPreferencesUnit sharedPreferencesUnit;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    //一个私有的构造方法
    private SharedPreferencesUnit(Context context){
        //属于文件上下文
        sharedPreferences =context.getSharedPreferences("info",context.MODE_PRIVATE );
        editor= sharedPreferences.edit();
    }
    //向外提供一个当前的对象
    public static SharedPreferencesUnit getInstance(Context context){
        if(sharedPreferencesUnit== null){
            sharedPreferencesUnit = new SharedPreferencesUnit(context);
        }
        return sharedPreferencesUnit;
    }

    //保存
    public void put(String key ,String value){
        editor.putString(key, value);
        editor.commit();
    }
    //保存
    public void putSet(String key ,Set<String> value){
        editor.putStringSet(key, value);
        editor.commit();
    }
    //保存
    public void putList(String key , Object list){
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.commit();
    }
    /**
     * 保存List
     * @param tag
     * @param datalist
     */
    public <T> void setDataList(String tag, List<T> datalist) {
        if (null == datalist || datalist.size() <= 0) return;

        Gson gson = new Gson();
        //转换成json数据，再保存
        String strJson = gson.toJson(datalist);
        editor.clear();
        editor.putString(tag, strJson);
        editor.commit();

    }
    //获取
    public String get(String key){
        return get(key, "0");
    }
    //获取
    public String getDefNull(String key){
        return get(key, "");
    }
    //获取
    public String get(String key,String defValues){
        return sharedPreferences.getString(key, defValues);
    }
    //获取
    public Set<String> getSet(String key,Set<String> defValues){
        return sharedPreferences.getStringSet(key,defValues);
    }
    //保存
    public void putInt(String key ,int value){
        editor.putInt(key, value);
        editor.commit();
    }
    //获取
    public int getInt(String key){
        return sharedPreferences.getInt(key, 0);
    }


    //保存
    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    //获取
    public boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }
    public void clear(){
        editor.clear();
        editor.commit();
    }
}
