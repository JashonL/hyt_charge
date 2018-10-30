package com.growatt.chargingpile.util;

import org.json.JSONObject;

import java.text.DecimalFormat;
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
        String result = "";
        for (int i = 0; i < bytes.length; i++) {
            String hexString = Integer.toHexString(bytes[i] & 0xFF);
            if (hexString.length() == 1) {
                hexString = '0' + hexString;
            }
            result += hexString.toUpperCase();
        }
        return result;
    }

    /**
     * 数据解密密钥
     */
    public static byte[] commonkeys = {(byte) 0x5A, (byte) 0xA5, (byte) 0x5A, (byte) 0xA5};

    public static byte[] decodeKey(byte[] src, byte[] keys) {
        if (src == null) return src;
        for (int j = 0; j < src.length; j++)    // Payload数据做掩码处理
        {
            src[j] = (byte) (src[j] ^ keys[j % 4]);
        }
        return src;
    }

    /**
     * 新的密钥
     */
    public static byte[] newkeys = new byte[4];

    public static byte[] createKey() {
        Random randomno = new Random();
        byte[] nbyte = new byte[4];
        randomno.nextBytes(nbyte);
        setNewkeys(nbyte);
        return nbyte;
    }

    public static byte[] getNewkeys() {
        return newkeys;
    }

    private static void setNewkeys(byte[] keys) {
        newkeys = keys;
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
        byte end = (byte) (sum & 0xff);
        return end;
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
}
