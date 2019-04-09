package com.growatt.chargingpile.util;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * Created by dg on 2017/9/21.
 */

public class SocketClientUtil {
    private Socket mSocket;
    public  String mIP = "192.168.0.32";//服务器地址
    public  int mPort = 8888;//服务器端口号
    private Handler mHandler;//回调处理器
    //发送数据类型常量
    public static final int SOCKET_EXCETION_CLOSE = -1;//socket异常关闭
    public static final int SOCKET_CLOSE = 0;//socket关闭
    public static final int SOCKET_OPEN = 1;//socket连接
    public static final int SOCKET_SEND_MSG = 2;//发送消息提示
    public static final int SOCKET_RECEIVE_MSG = 3;//接收消息
    public static final int SOCKET_CONNECT = 4;//已连接
    public static final int SOCKET_SEND = 5;//发送消息命令
    public static final int SOCKET_SERVER_SET = 6;//跳转到WIFI列表
    public static final int SOCKET_RECEIVE_BYTES = 7;//接收消息为byte数组
    public static final int SOCKET_AUTO_REFRESH = 10;//自动刷新
    public static final int SOCKET_AUTO_DELAY = 11;///延迟刷新；以防上一个界面刷新停止马上刷新导致失败
    public static final int SOCKET_10_READ = 12;//继续刷新
    public static final int MAX_ONOFF = 100;//开关机
    //socket输入流
    private InputStream socketIn = null;


//    public Socket getSocket() {
//        return mSocket;
//    }
//
//    public void setSocket(Socket socket) {
//        mSocket = socket;
//    }

    private static SocketClientUtil mInstance;
    public static SocketClientUtil newInstance(){
        if (mInstance == null){
            mInstance = new SocketClientUtil();
        }
        return mInstance;
    }
    private SocketClientUtil() {
        this(null);
    }
    private SocketClientUtil(Handler handler) {
        this(null,-1,handler);
    }

    public void connect(Handler handler){
        this.mHandler = handler;
        initConnect();
    }

    public void connect(Handler handler,String ip, int port){
        this.mHandler = handler;
        mIP=ip;
        mPort=port;
        initConnect();
    }

    private void initConnect() {
        new Thread(() -> {
            try {
                    try {
                        mSocket = new Socket();
                        SocketAddress socAddress = new InetSocketAddress(mIP, mPort);
                        mSocket.connect(socAddress, 2500);
                        mSocket.setTcpNoDelay(true);
                        //创建一个线程接收服务器发送的消息
                        receiveMsg(mSocket);
                        if (mHandler != null) {
                            mHandler.sendEmptyMessage(SOCKET_OPEN);
                            mHandler.sendEmptyMessageDelayed(SOCKET_SEND, 50);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        //1发送命令跳转到Wifi界面
                        try {
                            if (mSocket != null){ mSocket.isClosed();}
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }finally {
                            mSocket = null;
                        }
                        mHandler.sendEmptyMessage(SOCKET_SERVER_SET);
                    }
            } catch (Exception e) {
                e.printStackTrace();
                execptionClose(e.getMessage());
            }
        }).start();
    }

    private void initConnectAuto(final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mSocket == null || mSocket.isClosed()) {
                    try {
                        mSocket = new Socket();
                        SocketAddress socAddress = new InetSocketAddress(mIP, mPort);
                        mSocket.connect(socAddress, 2500);
                        mSocket.setTcpNoDelay(true);
                        //创建一个线程接收服务器发送的消息
                        receiveMsg(mSocket);
                        if (mHandler != null) {
                            mHandler.sendEmptyMessage(SOCKET_OPEN);
                            mHandler.sendEmptyMessageDelayed(SOCKET_SEND, 50);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        //1发送命令跳转到Wifi界面
                        try {
                            if (mSocket != null){ mSocket.isClosed();}
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }finally {
                            mSocket = null;
                        }
                        handler.sendEmptyMessage(SOCKET_SERVER_SET);
                    }
                    }
                    else {
                        if (mHandler != null) {
                            mHandler.sendEmptyMessage(SOCKET_SEND);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    execptionClose(e.getMessage());
                }
            }
        }).start();
    }

    public void connect(Handler handler,int type){
        this.mHandler = handler;
        if (mSocket == null || mSocket.isClosed()) {
            //2初始化连接
            initConnect(handler,type);
        }
        else {
            if (mHandler != null) {
                mHandler.sendEmptyMessage(type);
            }
        }
    }

    private void initConnect(final Handler handler, final int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mSocket == null || mSocket.isClosed()) {
                        try {
                            mSocket = new Socket();
                            SocketAddress socAddress = new InetSocketAddress(mIP, mPort);
                            mSocket.connect(socAddress, 2500);
                            //创建一个线程接收服务器发送的消息
                            receiveMsg(mSocket);
                            if (mHandler != null) {
                                mHandler.sendEmptyMessageDelayed(type, 50);
                            }
                        } catch (SocketTimeoutException e) {
                            e.printStackTrace();
                            //1发送命令跳转到Wifi界面
                            try {
                                if (mSocket != null){ mSocket.isClosed();}
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }finally {
                                mSocket = null;
                            }
                            handler.sendEmptyMessage(SOCKET_SERVER_SET);
                        }
                    }
                    else {
                        if (mHandler != null) {
                            mHandler.sendEmptyMessage(type);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    execptionClose(e.getMessage());
                }
            }
        }).start();
    }
    /**
     * 构建socket连接
     * @param ip
     * @param port
     * @param handler
     */
    private SocketClientUtil(String ip, int port, final Handler handler) {
        this.mHandler = handler;
        if (!TextUtils.isEmpty(ip)){
            this.mIP = ip;
        }
        if (port != -1){
            this.mPort = port;
        }
    }

    /**
     * 发送消息到服务器
     * @param sendText
     */
    public void sendMsg(String sendText){
        try {
            OutputStream socketOut = mSocket.getOutputStream();
            PrintWriter out=new PrintWriter(socketOut);
            out.println(sendText);
            out.flush();

            Message msg = Message.obtain();
            msg.what = SOCKET_SEND_MSG;
            msg.obj = sendText;
            mHandler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
            execptionClose(e.getMessage());
        }
    }
    public void sendMsg(char[] chars){
        try {
            OutputStream socketOut = mSocket.getOutputStream();
            PrintWriter out=new PrintWriter(socketOut);
            out.println(chars);
            out.flush();

            Message msg = Message.obtain();
            msg.what = SOCKET_SEND_MSG;
            msg.obj = chars.toString();
            mHandler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
            execptionClose(e.getMessage());
        }
    }
    /**
     * 发送消息到服务器
     * @param sendText
     */
    public void sendMsg(final byte[] sendText){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OutputStream socketOut = mSocket.getOutputStream();
                    socketOut.write(sendText);
                    socketOut.flush();
//            DataOutputStream out = new DataOutputStream(socketOut);
//            PrintWriter out=new PrintWriter(socketOut);
//            out.println(sendText);
//            out.write(sendText);
//            out.flush();

                    Message msg = Message.obtain();
                    msg.what = SOCKET_SEND_MSG;
                    msg.obj = bytesToHexString(sendText);
                    mHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    execptionClose(e.getMessage());
                }
            }
        }).start();
    }
    /**
     * 接收服务器发送的消息
     */
    public void receiveMsg(Socket socket){
        new ClientThread(socket).start();
    }

    /**
     * 关闭socket连接
     */
    public void closeSocket(){
        mInstance = null;
        if (socketIn != null){
            try {
                socketIn.close();
                socketIn=null;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        if (mSocket != null){
            try {
                mSocket.close();
                mSocket = null;
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }
    /**
     * 异常关闭
     */
    public void execptionClose(String exceptionMsg){
        if (mHandler != null){
            Message msg = Message.obtain();
            msg.what = SOCKET_EXCETION_CLOSE;
            msg.obj = exceptionMsg;
            mHandler.sendMessage(msg);
        }
        closeSocket();
    }


    /**
     * 接收消息类处理器
     */

    public class ClientThread extends Thread {
        private Socket socket;
        public ClientThread(Socket socket) {
            this.socket = socket;
        }
        public void run() {
//                InputStreamReader inputStreamReader = new InputStreamReader(in);
//                BufferedReader br = new BufferedReader(inputStreamReader);
                try {
                    socketIn = socket.getInputStream();
                    byte[] buffer = new byte[1024];
                    String[] Receive_date = null;
                    String receive = "";
                    int len = 0;
                    while((len = socketIn.read(buffer)) != -1) {
                        byte[] bytes = new byte[len];
                        for (int i =0;i<len;i++){
                            bytes[i] = buffer[i];
                        }
                        receive = bytesToHexString(bytes);
                        //接收字符串信息
                        Message msg = Message.obtain();
                        msg.what = SOCKET_RECEIVE_MSG;
                        msg.obj = receive;
                        mHandler.sendMessage(msg);
                        //接收字节数组
                        Message msg2 = Message.obtain();
                        msg2.what = SOCKET_RECEIVE_BYTES;
                        msg2.obj = bytes;
                        mHandler.sendMessage(msg2);
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                    if (mHandler !=null){
                        mHandler.sendEmptyMessage(SOCKET_CLOSE);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    execptionClose(e.getMessage());
                }
        }
    }

    /**
     * byte数组转16进制字符串
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv).append(" ");
        }
        return stringBuilder.toString();
    }
//    /**
//     * byte数组转寄存器值
//     * @param src:原数组未包含任何协议
//     * @return
//     */
//    public static String bytesToRegisterValueStr(byte[] src){
//        if (src == null) return "";
//        StringBuilder sb = new StringBuilder();
//        for (int i=0;i<src.length/2;i++){
//            int registerValue = MaxWifiParseUtil.obtainValueOne(MaxWifiParseUtil.subBytesFull(src, i, 0, 1));
//            sb.append(registerValue).append(" ");
//        }
//        if (sb.length() > 1) {
//            sb.deleteCharAt(sb.length() - 1);
//        }
//        return sb.toString();
//    }
    public static SocketClientUtil connectServer(Handler mhandler) {
        SocketClientUtil clientUtil = SocketClientUtil.newInstance();
        if (clientUtil != null) {
            clientUtil.connect(mhandler);
        }
        return clientUtil;
    }
    public static SocketClientUtil connectServerAuto(Handler mhandler) {
        SocketClientUtil clientUtil = SocketClientUtil.newInstance();
        if (clientUtil != null) {
            clientUtil.connectAuto(mhandler);
        }
        return clientUtil;
    }

    public void connectAuto(Handler handler){
        this.mHandler = handler;
        initConnectAuto(handler);
    }
    /**
     * 根据命令以及起始寄存器发送查询命令
     * @param clientUtil
     * @param sends
     * @return：返回发送的字节数组
     */
/*    public static byte[] sendMsgToServer(SocketClientUtil clientUtil, int[] sends) {
        if (clientUtil != null) {
            byte[] sendBytes= ModbusUtil.sendMsg(sends[0], sends[1], sends[2]);
            clientUtil.sendMsg(sendBytes);
            return sendBytes;
        }
        return null;
    }*/
    /**
     * 根据命令以及起始寄存器发送查询命令
     * @param clientUtil
     * @param sends
     * @return：返回发送的字节数组
     */
  /*  public static byte[] sendMsgToServer10(SocketClientUtil clientUtil, int[] sends,int[] values) {
        if (clientUtil != null) {
            byte[] sendBytes= ModbusUtil.sendMsg10(sends[0], sends[1], sends[2],values);
            clientUtil.sendMsg(sendBytes);
            return sendBytes;
        }
        return null;
    }*/
    /**
     * 关闭socket
     * @param socketClientUtil
     */
    public static void close(SocketClientUtil socketClientUtil){
        if (socketClientUtil != null){
            socketClientUtil.closeSocket();
        }
    }
}
