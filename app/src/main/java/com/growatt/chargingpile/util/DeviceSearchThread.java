package com.growatt.chargingpile.util;

import android.util.Log;

import com.growatt.chargingpile.bean.UdpSearchBean;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Set;

import static com.growatt.chargingpile.util.MyUtil.bytesToHexString;

/**
 * Created by Administrator on 2019/3/29.
 */

public abstract class DeviceSearchThread extends Thread {

    private static final String TAG = DeviceSearchThread.class.getSimpleName();

    private static final int DEVICE_FIND_PORT = 48899;
    private static final int RECEIVE_TIME_OUT = 1500; // 接收超时时间
    private static final int RESPONSE_DEVICE_MAX = 200; // 响应设备的最大个数，防止UDP广播攻击

    private DatagramSocket hostSocket;
    private Set<UdpSearchBean> mDeviceSet;


    public DeviceSearchThread() {
        mDeviceSet = new HashSet<>();
    }

    @Override
    public void run() {
        try {
            onSearchStart();
            hostSocket = new DatagramSocket();
            // 设置接收超时时间
            hostSocket.setSoTimeout(RECEIVE_TIME_OUT);

            byte[] sendData = new byte[1024];
            InetAddress broadIP = InetAddress.getByName("255.255.255.255");
            DatagramPacket sendPack = new DatagramPacket(sendData, sendData.length, broadIP, DEVICE_FIND_PORT);
            sendData = "www.usr.cn".getBytes();
            String s1 = bytesToHexString(sendData);
            Log.d("liaojinsha", "发送广播" + s1);

                // 发送搜索广播
                sendPack.setData(sendData);
                hostSocket.send(sendPack);

                // 监听来信
                byte[] receData = new byte[1024];
                DatagramPacket recePack = new DatagramPacket(receData, receData.length);
                try {
                    // 最多接收200个，或超时跳出循环
                    int rspCount = RESPONSE_DEVICE_MAX;
                    while (rspCount-- > 0) {
                        recePack.setData(receData);
                        hostSocket.receive(recePack);
                        if (recePack.getLength() > 0) {
                           /* String mDeviceIP = recePack.getAddress().getHostAddress();
                            byte[] data = recePack.getData();
                            String mDeviceName = new String(data, 0, recePack.getLength());
                            String s = bytesToHexString(data);
                            Log.i("liaojinsha", "接收到的字节数组: " + s);
                            Log.i("liaojinsha", "@@@zjun: 设备上线：" + mDeviceIP);
                            Log.i("liaojinsha", "@@@zjun: 名称：" + mDeviceName);
                              *//*  if (parsePack(recePack)) {
                                    Log.i("liaojinsha", "@@@zjun: 设备上线：" + mDeviceIP);
                                    // 发送一对一的确认信息。使用接收报，因为接收报中有对方的实际IP，发送报时广播IP
                                    mPackType = PACKET_TYPE_FIND_DEVICE_CHK_12;
                                    recePack.setData(packData(rspCount)); // 注意：设置数据的同时，把recePack.getLength()也改变了
                                    hostSocket.send(recePack);
                                }*/
                            parsePack(recePack);
                        }
                    }
                } catch (SocketTimeoutException e) {
                }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            onSearchFinish(mDeviceSet);
            if (hostSocket != null) {
                hostSocket.close();
            }
        }

    }


    /**
     * 搜索开始时执行
     */
    public abstract void onSearchStart();

    /**
     * 搜索结束后执行
     *
     * @param deviceSet 搜索到的设备集合
     */
    public abstract void onSearchFinish(Set deviceSet);


    private void parsePack(DatagramPacket recePack) {
        if (recePack == null || recePack.getAddress() == null) {
            return;
        }
        String mDeviceIP = recePack.getAddress().getHostAddress();
        for (UdpSearchBean d : mDeviceSet) {
            if (d.getDevIp().equals(mDeviceIP)) {
                return;
            }
        }
        UdpSearchBean device = new UdpSearchBean();
        byte[] data = recePack.getData();
        String messge = new String(data, 0, recePack.getLength());
        String[] split = messge.split(",");
        String mDeviceName = split[2];
        device.setDevIp(mDeviceIP);
        device.setDevName(mDeviceName);
        String s = bytesToHexString(data);
        mDeviceSet.add(device);
        Log.i("liaojinsha", "接收到的字节数组: " + s);
        Log.i("liaojinsha", "转成string: " + messge);
        Log.i("liaojinsha", "@@@zjun: 设备上线：" + mDeviceIP);
        Log.i("liaojinsha", "@@@zjun: 名称：" + mDeviceName);
    }

}
