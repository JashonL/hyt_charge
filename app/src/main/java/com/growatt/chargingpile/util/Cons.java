package com.growatt.chargingpile.util;

import android.text.TextUtils;

import com.growatt.chargingpile.bean.ChargingBean;
import com.growatt.chargingpile.bean.GunBean;
import com.growatt.chargingpile.bean.RegisterMap;
import com.growatt.chargingpile.bean.UserBean;
import com.growatt.chargingpile.sqlite.SqliteUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Cons {
    //监控用户服务器url:server.growatt.com;
    public static String account_url;

    //获取account_url
    public static String getAccount_url() {
        if (!TextUtils.isEmpty(account_url)) {
            return account_url;
        }
        String url = SqliteUtil.inquiryurl();
        if (!TextUtils.isEmpty(url)) {
            return url;
        }
        return "";
    }

    public static String hour;//ѡ��Сʱ
    public static String min;//ѡ�����
    public static String userId = "";
    public static String plant;
    public static String url = "";
    //	public static String guestyrl="";
//	public static boolean flag=false;
    public static boolean isflag = false;
    public static ArrayList<Map<String, Object>> plants = new ArrayList<Map<String, Object>>();
    public static RegisterMap regMap = new RegisterMap();
    public static UserBean userBean;
    public static boolean addQuestion = false;//���ʿ���
    public static boolean isFirst = false;//�Ƿ��һ�ν���ҳ��
    public static boolean isExit = false;//�Ƿ��˳�Ӧ��
    public static int num = 0;//kill����
    //��Ƶ����
    public static List<Map<String, String>> videoList = new ArrayList<Map<String, String>>();
    public static boolean isCodeUpdate;//app�汾���Ƿ���£�����б仯�򵯴�
    public static String server = "http://server.growatt.com";


    //oss服务器列表map集合
    public static List<Map<String, String>> ossServerList;


    //登录接口中是否通过手机或邮箱验证
    public static boolean isValiEmail;
    public static boolean isValiPhone;
    //当前选中的桩
    public static ChargingBean.DataBean mCurrentPile = new ChargingBean.DataBean();
    //当前选中的枪
    public static GunBean mCurrentGunBean = new GunBean();
    //当前选中位置
    public static int mSeletPos = 0;
    //当前充电枪id
    public static int mCurrentGunBeanId = 1;

}
