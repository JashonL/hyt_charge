package com.growatt.chargingpile.bean;


import com.growatt.chargingpile.util.SmartHomeUtil;

/**
 * wifi消息实体
 */

public class WiFiRequestMsgBean {
    /**
     * 帧头：1字节， 0xA5
     * 帧头：1字节， 0x5A
     * 类型：1字节， 0x01表示交流桩，0x02表示直流桩，0x03表示交直流桩
     * 加密类型：1字节，0x00表示不加密，0x01表示掩码加密，其他加密预留
     * 消息：1字节，对应业务流程命令类型
     * 数据长度：1字节，指示有效数据长度
     * 有效数据：ASCII码，升级为二进制，单包最长(240字节) (低字节在前高字节在后)
     * 数据校验：1字节，和校验，取低字节
     * 结束符：1字节， 0x88
     */

    //帧头1
    private byte frame;
    //帧头2
    private byte frame2;
    //类型
    private byte devType;
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

    public byte getDevType() {
        return devType;
    }

    public void setDevType(byte devType) {
        this.devType = devType;
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

    public byte[] getRequestMsg() {
        int length = getPayload().length + 8;
        byte[] result = new byte[length];
        result[0] = getFrame();
        result[1] = getFrame2();
        result[2] = getDevType();
        result[3] = getEncryption();
        result[4] = getCmd();
        result[5] = getDataLen();
        byte[] payload = getPayload();
        System.arraycopy(payload, 0, result, 6, payload.length);
        result[length - 2] = SmartHomeUtil.getCheckSum(result);
        result[length - 1] = getEnd();
        return result;
    }


    public interface IBuilder {
        WiFiRequestMsgBean.IBuilder setFrame_1(byte head1);

        WiFiRequestMsgBean.IBuilder setFrame_2(byte head2);

        WiFiRequestMsgBean.IBuilder setDevType(byte type);

        WiFiRequestMsgBean.IBuilder setEncryption(byte encryption);

        WiFiRequestMsgBean.IBuilder setCmd(byte cmd);

        WiFiRequestMsgBean.IBuilder setDataLen(byte dataLen);

        WiFiRequestMsgBean.IBuilder setPrayload(byte[] payload);

        WiFiRequestMsgBean.IBuilder setSum(byte sum);

        WiFiRequestMsgBean.IBuilder setMsgEnd(byte end);

        byte[] create();
    }

    public static class Builder implements WiFiRequestMsgBean.IBuilder {

        private WiFiRequestMsgBean requestMsgBean;

        public Builder() {
            this.requestMsgBean = new WiFiRequestMsgBean();
        }

        public static WiFiRequestMsgBean.Builder newInstance() {

            return new WiFiRequestMsgBean.Builder();
        }

        @Override
        public IBuilder setFrame_1(byte head1) {
            requestMsgBean.setFrame(head1);
            return this;
        }

        @Override
        public IBuilder setFrame_2(byte head2) {
            requestMsgBean.setFrame2(head2);
            return this;
        }

        @Override
        public IBuilder setDevType(byte protocol) {
            requestMsgBean.setDevType(protocol);
            return this;
        }

        @Override
        public IBuilder setEncryption(byte encryption) {
            requestMsgBean.setEncryption(encryption);
            return this;
        }

        @Override
        public IBuilder setPrayload(byte[] payload) {
            requestMsgBean.setPayload(payload);
            return this;
        }

        @Override
        public IBuilder setSum(byte sum) {
            requestMsgBean.setSum(sum);
            return null;
        }


        @Override
        public IBuilder setCmd(byte cmd) {
            requestMsgBean.setCmd(cmd);
            return this;
        }

        @Override
        public IBuilder setDataLen(byte dataLen) {
            requestMsgBean.setDataLen(dataLen);
            return this;
        }

        @Override
        public IBuilder setMsgEnd(byte end) {
            requestMsgBean.setEnd(end);
            return this;
        }

        @Override
        public byte[] create() {
            return requestMsgBean.getRequestMsg();
        }
    }

}
