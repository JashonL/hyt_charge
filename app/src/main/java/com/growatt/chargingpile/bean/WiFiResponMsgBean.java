package com.growatt.chargingpile.bean;


import com.growatt.chargingpile.util.SmartHomeUtil;
import com.growatt.chargingpile.util.WiFiMsgConstant;

import org.xutils.common.util.LogUtil;

/**
 * Created by Administrator on 2018/7/24.
 */

public class WiFiResponMsgBean {
    //帧头1
    private byte frame;
    //帧头2
    private byte frame2;
    //协议类型
    private byte protocol;
    //加密类型
    private byte encryption;
    //消息类型
    private byte cmd;
    //数据长度
    private byte dataLen;
    //有效数据
    private byte[] payload;
    //数据校验
    private byte sum;
    //结束符
    private byte end;


    public byte getFrame() {
        return frame;
    }

    public void setFrame(byte frame) {
        this.frame = frame;
    }

    public byte getFrame2() {
        return frame2;
    }

    public void setFrame2(byte frame2) {
        this.frame2 = frame2;
    }

    public byte getProtocol() {
        return protocol;
    }

    public void setProtocol(byte protocol) {
        this.protocol = protocol;
    }

    public byte getEncryption() {
        return encryption;
    }

    public void setEncryption(byte encryption) {
        this.encryption = encryption;
    }

    public byte getCmd() {
        return cmd;
    }

    public void setCmd(byte cmd) {
        this.cmd = cmd;
    }

    public byte getDataLen() {
        return dataLen;
    }

    public void setDataLen(byte dataLen) {
        this.dataLen = dataLen;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public byte getSum() {
        return sum;
    }

    public void setSum(byte sum) {
        this.sum = sum;
    }

    public byte getEnd() {
        return end;
    }

    public void setEnd(byte end) {
        this.end = end;
    }

    public WiFiResponMsgBean(byte[] bytes) {
        formatBytes(bytes);
    }

    private void formatBytes(byte[] bytes) {
        int length = bytes.length;
        if (length <= 0) {
            return;
        }
        setFrame(bytes[0]);
        setFrame(bytes[1]);
        setProtocol(bytes[2]);
        setEncryption(bytes[3]);
        setCmd(bytes[4]);
        setDataLen(bytes[5]);
        byte[] body = new byte[length - 8];
        System.arraycopy(bytes, 6, body, 0, body.length);
        if (getCmd() == WiFiMsgConstant.CONSTANT_MSG_01) {
            body = SmartHomeUtil.decodeKey(body, SmartHomeUtil.commonkeys);
        } else {
            body = SmartHomeUtil.decodeKey(body, SmartHomeUtil.getNewkeys());
        }
        setPayload(body);
        setSum(bytes[length - 2]);
        setEnd(bytes[length - 1]);
        LogUtil.i("解密之后有效数据：" + SmartHomeUtil.bytesToHexString(getPayload()));
    }


}
