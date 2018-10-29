package com.growatt.chargingpile.util;

import android.Manifest;

/**
 * Created：2018/4/19 on 14:54
 * Author:gaideng on dg
 * Description:权限标示
 */

public class PermissionCodeUtil {
 /**
* 权限字符串
 */
   /**
    * 相机和存储权限
    */
    public static final String[] PERMISSION_CAMERA = {Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
/**
 * 单独存储权限
 */
public static final String[] PERMISSION_EXTERNAL_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
/**
 * 单独相机权限
 */
public static final String PERMISSION_CAMERA_ONE = Manifest.permission.CAMERA;
/**
 * 定位权限 + 存储权限 + 手机状态权限组
 */
public static final String PERMISSION_LOCATION[] = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE};



/**
* 权限码
*/
    /**
     * 相机和存储权限
     */
    public static final int PERMISSION_CAMERA_CODE = 11001;
    /**
     * 存储权限
     */
    public static final int PERMISSION_EXTERNAL_STORAGE_CODE = 11002;
    /**
     * 相机权限
     */
    public static final int PERMISSION_CAMERA_ONE_CODE = 11003;
    /**
     * 位置权限
     */
    public static final int PERMISSION_LOCATION_CODE = 11004;
}
