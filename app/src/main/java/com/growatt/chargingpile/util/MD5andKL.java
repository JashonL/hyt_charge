package com.growatt.chargingpile.util;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;


public class MD5andKL {

    

    private final static int ENCRYPTKEY =0xFF;

    /**

    * 加密MD5

    * @param password

    * @return encryptPassword

    */

    public static String encryptPassword(String password){

       MessageDigest md5=null;

       StringBuffer cacheChar = new StringBuffer();

       try{

           byte defaultByte[] =password.getBytes();

           md5 =MessageDigest.getInstance("MD5");

           md5.reset();

           md5.update(defaultByte);

           byte resultByte[] =md5.digest();
           
           String results = new String(resultByte);

           for(int i=0;i<resultByte.length;i++){

              String hex = Integer.toHexString(ENCRYPTKEY & resultByte[i]);

                 if (hex.length() == 1){

                        cacheChar.append("c");

                    }

                    cacheChar.append(hex);

           }

           return cacheChar.toString();

       }catch(Exception e){


           e.printStackTrace();

           return  null;

       }

    }

    /**

    * @param sourcePass

    * @param resultPass

    * @return

    */

    public static boolean comparePassword(String sourcePass,String resultPass){

       byte[] expectedBytes = null,actualBytes = null;

       try {

           expectedBytes = sourcePass.getBytes("UTF-8");

           actualBytes = resultPass.getBytes("UTF-8");

       } catch (UnsupportedEncodingException e) {


           e.printStackTrace();

       }

        int expectedLength = expectedBytes == null ? -1 : expectedBytes.length;

        int actualLength = actualBytes == null ? -1 : actualBytes.length;

        if (expectedLength != actualLength) {

            return false;

        }

        int result = 0;

        for (int i = 0; i < expectedLength; i++) {

            result |= expectedBytes[i] ^ actualBytes[i];

        }

        return result == 0;

    }

    

    public static  void main(String args[]){


    }

 

}


