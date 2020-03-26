package com.growatt.chargingpile.util;

import android.text.TextUtils;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Administrator on 2018/6/13.
 */

public class SmartHomeUtil {

    /**
     * map数组转成String
     *
     * @param map
     * @return
     */
    public static String mapToJsonString(Map<String, Object> map) {
        JSONObject jsonObject = new JSONObject(map);
        return jsonObject.toString();
    }



    public interface OperationListener {
        void sendCommandSucces();

        void sendCommandError(String code, String error);
    }

    /**
     * byte数组转为String
     */

    public static String bytesToHexString(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte aByte : bytes) {
            String hexString = Integer.toHexString(aByte & 0xFF);
            if (hexString.length() == 1) {
                hexString = '0' + hexString;
            }
            result.append(hexString.toUpperCase());
        }
        return result.toString();
    }

    /**
     * 数据解密密钥
     */
    public static byte[] commonkeys = {(byte) 0x5A, (byte) 0xA5, (byte) 0x5A, (byte) 0xA5};

    public static byte[] decodeKey(byte[] src, byte[] keys) {
        if (src == null) return null;
        for (int j = 0; j < src.length; j++)    // Payload数据做掩码处理
        {
            src[j] = (byte) (src[j] ^ keys[j % 4]);
        }
        return src;
    }

    /**
     * 生成新的密钥
     */
    public static byte[] createKey() {
        Random randomno = new Random();
        byte[] nbyte = new byte[4];
        randomno.nextBytes(nbyte);
        return nbyte;
    }


    /**
     * @param buffer 有效数据数组
     * @return
     */
    public static byte getCheckSum(byte[] buffer) {
        int sum = 0;
        int length = buffer.length;
        for (int i = 0; i < length - 2; i++) {
            sum += (int) buffer[i];
        }
        return (byte) (sum & 0xff);
    }

    /**
     * 字节数组转为普通字符串（ASCII对应的字符）
     *
     * @param bytearray byte[]
     * @return String
     */
    public static String bytetoString(byte[] bytearray) {
        try {
            int length = 0;
            for (int i = 0; i < bytearray.length; ++i) {
                if (bytearray[i] == 0) {
                    length = i;
                    break;
                }
            }
            return new String(bytearray, 0, length, "ascii");
        } catch (Exception e) {
            return "";
        }
    }


    /**
     * 不显示科学计数法
     */
    public static String showDouble(double value) {
        DecimalFormat df = new DecimalFormat("0.#####");
        return df.format(value);
    }


    /**
     * 判断是否是浏览用户
     */

    public static boolean isFlagUser(){
        int auth = Cons.userBean.getAuthnum();
        boolean isflag=false;
        if (auth==1){
            isflag=true;
        }
        return isflag;
    }



    /**
     * 获取用户名
     *
     * @return
     */
    public static String getUserId() {
//        return "user02";
//        return Cons.userBean.getId();
        if (Cons.userBean==null)return "";
        if (TextUtils.isEmpty(Cons.userBean.getName()))return "";
        return Cons.userBean.getName();
    }


    /**
     * 获取用户名
     *
     * @return
     */
    public static String getUserName() {
//        return "user02";
//        return Cons.userBean.getId();
        if (Cons.userBean==null)return "";
        if (TextUtils.isEmpty(Cons.userBean.getName()))return "";
        return Cons.userBean.getName();
    }


    /**
     * 获取权限
     *
     * @return
     */
    public static String getUserAuthority() {
//        return "user02";
//        return Cons.userBean.getId();
        if (Cons.userBean==null)return "endUser";
        if (TextUtils.isEmpty(Cons.userBean.getRoleId()))return "endUser";
        return Cons.userBean.getRoleId();
    }


    /**
     * 获取字母列表
     */

    public static List<String> getLetter(){
        List<String>letters=new ArrayList<>();
        for (int i=0;i<26;i++){
            char letter= (char) ('A'+i);
            letters.add(String.valueOf(letter));
        }
        return letters;
    }
}
