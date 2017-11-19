package me.henry.ziggslibrary.main;

import android.util.Log;

import java.io.IOException;
import java.net.SocketException;
import java.util.Vector;

import me.henry.ziggslibrary.callbacks.UdpDataWatcher;
import me.henry.ziggslibrary.others.ZiggsUtils;


/**
 * Created by henry on 2017/11/13.
 */

public class UdpConnect implements Runnable {
    private int mPort;
    private String mAddress;
    private int mTimeOut;

    private static Vector<byte[]> datas = new Vector<byte[]>();// 待发送数据队列
    private boolean isConnect = false;
    private SocketController mSocket;
    private SendRunnable sendRunnable;

    private String TAG = "UdpConnect";

    public UdpConnect(int port, String address, int timeOut) {
        this.mPort = port;
        this.mAddress = address;
        this.mTimeOut = timeOut;
        sendRunnable = new SendRunnable();
        isConnect = true;
        try {
            mSocket = new SocketController(mAddress, mPort, mTimeOut);
        } catch (SocketException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void run() {
        Log.d(TAG, "连接信息 -> " + mAddress + ":" + mPort);
        if (mAddress == null || mPort == -1 || !ZiggsUtils.isIPV4(mAddress)) {
            return;
        }
        new Thread(sendRunnable).start();
        //socket.close就会跳出去
        while (isConnect) {
            Log.d(TAG, "开启 发送线程,一直等待消息进入队列");
            try {
                Log.d(TAG, "开始接受UDP消息");
                Thread.sleep(1);
                mSocket.receive();
            } catch (IOException e) {
                break;
            }catch (InterruptedException e){
                isConnect=false;
            }

        }

        Log.d("miaomm", "跳出");
        try {
            if (sendRunnable != null) {
                sendRunnable.stop();
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


    public void send(byte[] data) {
        sendRunnable.send(data);
    }

    public void addWatcher(UdpDataWatcher watcher) {
        if (mSocket != null) {
            mSocket.addWatcher(watcher);
        }
    }

    public void removeWatcher(UdpDataWatcher watcher) {
        if (mSocket != null) {
            mSocket.removeWatcher(watcher);
        }
    }

    public void destory() {
        mSocket.removeAllWatchers();
        mSocket.close();
    }
}
