package com.growatt.chargingpile.jpush;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;


import com.growatt.chargingpile.application.MyApplication;
import com.growatt.chargingpile.util.LoginUtil;
import com.growatt.chargingpile.util.SmartHomeUtil;
import com.tencent.mmkv.MMKV;

import org.xutils.common.util.LogUtil;

import java.util.Locale;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.JPushMessage;

/**
 * 处理tagalias相关的逻辑
 */
public class TagAliasOperatorHelper {
    private static final String TAG = "JIGUANG-TagAliasHelper";
    public static final String ALIAS_DATA = "alias_data";
    public static final String MN_DATA = "mn_data";
    public static final String ALIAS_ACTION = "alias_action";//记录这次操作是删除还是添加

    private Context context;

    private static TagAliasOperatorHelper mInstance;

    private TagAliasOperatorHelper() {
    }

    public static TagAliasOperatorHelper getInstance() {
        if (mInstance == null) {
            synchronized (TagAliasOperatorHelper.class) {
                if (mInstance == null) {
                    mInstance = new TagAliasOperatorHelper();
                }
            }
        }
        return mInstance;
    }

    public void init(Context context) {
        if (context != null) {
            this.context = context.getApplicationContext();
        }
    }


    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
        int sequence = jPushMessage.getSequence();
        Log.i(TAG, "action - onTagOperatorResult, sequence:" + sequence + ",tags:" + jPushMessage.getTags());
        Log.i(TAG, "tags size:" + jPushMessage.getTags().size());
        init(context);

        if (jPushMessage.getErrorCode() == 0) {
            Log.i(TAG, "action - modify tag Success,sequence:" + sequence);
            Log.i(TAG, "标签修改成功" + sequence);
        } else {
            String logs = "Failed to modify tags";
            if (jPushMessage.getErrorCode() == 6018) {
                //tag数量超过限制,需要先清除一部分再add
                logs += ", tags is exceed limit need to clean";
            }
            logs += ", errorCode:" + jPushMessage.getErrorCode();
            Log.e(TAG, logs);
        }
    }

    public void onCheckTagOperatorResult(Context context, JPushMessage jPushMessage) {
        int sequence = jPushMessage.getSequence();
        Log.i(TAG, "action - onCheckTagOperatorResult, sequence:" + sequence + ",checktag:" + jPushMessage.getCheckTag());
        init(context);
        if (jPushMessage.getErrorCode() == 0) {
            String logs = "modify tag " + jPushMessage.getCheckTag() + " bind state success,state:" + jPushMessage.getTagCheckStateResult();
            Log.i(TAG, logs);
        } else {
            String logs = "Failed to modify tags, errorCode:" + jPushMessage.getErrorCode();
            Log.e(TAG, logs);
        }
    }

    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        int sequence = jPushMessage.getSequence();
        Log.i(TAG, "action - onAliasOperatorResult, sequence:" + sequence + ",alias:" + jPushMessage.getAlias());
        init(context);

        if (jPushMessage.getErrorCode() == 0) {
            Log.i(TAG, "action - modify alias Success,sequence:" + sequence);
        } else {
            String logs = "Failed to modify alias, errorCode:" + jPushMessage.getErrorCode();
            Log.e(TAG, logs);
            MMKV.defaultMMKV().putString(ALIAS_DATA, "");
            if (jPushMessage.getErrorCode() == 6002) {//别名设置超时，重新设置
                if (sequence <= 10) {
                    int anInt = MMKV.defaultMMKV().getInt(ALIAS_ACTION, 0);
                    if (anInt==1){
                        MMKV.defaultMMKV().putString(ALIAS_DATA, SmartHomeUtil.getUserName());
                        MMKV.defaultMMKV().putInt(ALIAS_ACTION, 1);
                        PushUtils.setAlias(this.context, SmartHomeUtil.getUserName(), PushUtils.sequence++);
                    }
                }
            }


        }
    }

    //设置手机号码回调
    public void onMobileNumberOperatorResult(Context context, JPushMessage jPushMessage) {
        int sequence = jPushMessage.getSequence();
        Log.i(TAG, "action - onMobileNumberOperatorResult, sequence:" + sequence + ",mobileNumber:" + jPushMessage.getMobileNumber());
        init(context);
        if (jPushMessage.getErrorCode() == 0) {
            Log.i(TAG, "action - set mobile number Success,sequence:" + sequence);
        } else {
            String logs = "Failed to set mobile number, errorCode:" + jPushMessage.getErrorCode();
            Log.e(TAG, logs);
            MMKV.defaultMMKV().putString(MN_DATA, "");
        }
    }

}
