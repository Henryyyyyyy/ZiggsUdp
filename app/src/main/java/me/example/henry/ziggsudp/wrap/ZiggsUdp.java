package me.example.henry.ziggsudp.wrap;

import android.util.Log;

import java.io.IOException;
import java.net.SocketException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by henry on 2017/11/13.
 */

public class ZiggsUdp implements Runnable {
    private int mPort;
    private String mAddress;
    private int mTimeOut;

    private static Vector<byte[]> datas = new Vector<byte[]>();// 待发送数据队列
    private boolean isConnect = false;
    private ZiggsSocket mSocket;
    private SendRunnable sendRunnable;

    private String TAG = "ZiggsUdp";

    public ZiggsUdp(int port, String address, int timeOut) {
        this.mPort = port;
        this.mAddress = address;
        this.mTimeOut = timeOut;
        sendRunnable = new SendRunnable();
        isConnect = true;
        try {
            mSocket = new ZiggsSocket(mAddress, mPort, mTimeOut);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        Log.d(TAG, "连接信息 -> " + mAddress + ":" + mPort);
        if (mAddress == null || mPort == -1 || !isIPV4(mAddress)) {
            return;
        }
        while (isConnect) {
            //开启 发送线程,一直等待消息进入队列
            new Thread(sendRunnable).start();
            try {
                Log.d(TAG, "开始接受UDP消息");
                mSocket.receive();
            } catch (IOException e) {
                break;
            }

        }
        //当销毁连接线程的时候，发送线程和socket都要销毁
        //当isConnect==false的时候，上面不再执行,sendrunnable关闭
        try {

            if (sendRunnable != null) {
                sendRunnable.stop();
            }

            if (mSocket != null) {
                mSocket.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    private class SendRunnable implements Runnable {
        private Object lock = new Object();
        private boolean isSend = true;

        @Override
        public void run() {

            synchronized (lock) {
                while (isSend) {
                    if (datas.size() < 1) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            continue;
                        }
                    }
                    while (datas.size() > 0) {
                        byte[] buffer = new byte[0];// 获取一条发送数据
                        try {
                            buffer = datas.remove(0);
                        } catch (Exception e) {

                        }
                        if (isSend) {
                            try {
                                if (mSocket != null)
                                    mSocket.send(buffer);
                            } catch (IOException e) {

                            }
                        } else {
                            lock.notify();
                        }
                    }
                }
            }
            Log.d(TAG, "UDP发送线程结束");
        }

        public void stop() {
            synchronized (lock) {
                isSend = false;
                lock.notify();
            }
        }

        public void send(byte[] data) {
            synchronized (lock) {
                datas.add(data);
                lock.notify();
            }
        }
    }

    public static boolean isIPV4(String ipAddress) {
        if (ipAddress.equals("") || ipAddress == null)
            return false;
        //IPv4正则
        String ippattern = "^((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)$";
        Pattern pattern = Pattern.compile(ippattern);
        Matcher matcher = pattern.matcher(ipAddress);
        //XLog.d(TAG, "正则匹配IP:" + ipAddress + ",返回" + matcher.matches());
        return matcher.matches();

    }
    public void send(byte[] data) {
        sendRunnable.send(data);
    }

    public void addCallBack(DataCallBack cb) {
        mSocket.addCallBack(cb);
    }

    public void destory() {
        mSocket.removeAllCallback();
        isConnect = false;

    }
}
