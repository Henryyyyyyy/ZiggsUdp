package me.henry.ziggslibrary.main;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;

import me.henry.ziggslibrary.callbacks.UdpDataWatcher;
import me.henry.ziggslibrary.callbacks.ZiggsObservable;


/**
 * Created by henry on 2017/11/14.
 * socket操作类
 */

public class SocketController {
    private final String TAG = "SocketController";
    long t1;
    Thread mThread;
    private static final int MSG_RECEIVE_DATA = 2701;
    private static final int MSG_RECEIVE_TIMEOUT = 2702;
    private int dataLength = 1024;
    private byte[] receiveData = new byte[dataLength];
    private DatagramSocket mSocket;
    private DatagramPacket receivePacket, sendPacket;
    private SocketAddress sendAddress;
    private byte[] currentReceiveByteData = null;
    private HandlerThread mUIHandlerThread;
    private Handler mHandler;
    private int mTimeOut = 5;//默认超时时间
    private ZiggsObservable mObservable;


    public SocketController(String host, int port, int timeout) throws SocketException {
        mObservable = new ZiggsObservable();
        receivePacket = new DatagramPacket(receiveData, receiveData.length);
        mTimeOut = timeout;
        sendAddress = new InetSocketAddress(host, port);
        mSocket = new DatagramSocket();
        mSocket.setSoTimeout(0);// 永远等待接受消息！
        initUIHandlerAndThread();

    }


    public void send(byte[] data) throws IOException {
        if (data == null || sendAddress == null)
            return;
        sendBuffer(data);

    }

    /**
     * 发送数据包,并启动自定义设置超时机制
     *
     * @param data
     * @throws IOException
     */
    private void sendBuffer(byte[] data) throws IOException {

        if (data.length < dataLength) {
            sendPacket = new DatagramPacket(data, data.length, sendAddress);
            if (mSocket != null && sendPacket != null) {
                setTimeout();//自定义设置超时机制
                mSocket.send(sendPacket);
            }
        } else {
            throw new IOException("发送数据包大于限定长度");
        }
    }

    public void receive() throws IOException {
        if (mSocket == null) return;
        if (mSocket != null && receivePacket != null) {
            mSocket.receive(receivePacket);
        }
        if (mObservable.countObservers() > 0) {
            currentReceiveByteData = new byte[receivePacket.getLength()];
            try {
                System.arraycopy(receivePacket.getData(), 0, currentReceiveByteData, 0, receivePacket.getLength());
                mHandler.sendEmptyMessage(MSG_RECEIVE_DATA);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //接收消息后休眠10毫秒
            Log.e("heihei","aaaa");
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.interrupted();
            }
            currentReceiveByteData = null;
        }


    }

    /**
     * 设置超时统计,需要覆盖上一个超时统计
     *
     * @param
     */

    private void setTimeout() {


        if (mThread == null) {
            mThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    sendTimeout();//因为统计超时的同时也要发送数据,所以要新开一个线程
                }
            });
            mThread.start();
        } else {
            if (!mThread.isAlive()) {//false 线程运行结束,重新开始,再走一次这个方法
                mThread = null;
                setTimeout();
            } else {
                t1 = System.currentTimeMillis();// 线程在运行中，改变t1的值,覆盖上一个命令
            }
        }


    }

    /**
     * 模拟发送超时数据
     */
    private void sendTimeout() {
        t1 = System.currentTimeMillis();
        Log.d(TAG, "UDP超时开始计算");
        while (currentReceiveByteData == null) {//此处根据currentReceiveByteData 进行判断是否是接收数据超时！！
            long curr = (System.currentTimeMillis() - (t1));
            if (curr > mTimeOut * 1000) {
                Log.d(TAG, ((InetSocketAddress) sendAddress).getHostName() + ":" + ((InetSocketAddress) sendAddress).getPort() + "  UDP超时" + mTimeOut + "秒，伪超时数据返回。");
                if (mObservable.countObservers() > 0) {
                    mHandler.sendEmptyMessage(MSG_RECEIVE_TIMEOUT);
                    break;
                }
            }
        }
    }


    public void close() {
        if (mSocket == null)
            return;
        try {
            if (mSocket.isConnected()) {
                mSocket.disconnect();
            }
            if (!mSocket.isClosed()) {
                mSocket.close();
            }
            if (mUIHandlerThread != null) {
                mUIHandlerThread.quit();
            }
            if (mHandler != null) {
                mHandler.removeCallbacksAndMessages(null);
                mHandler = null;
            }
        } catch (Exception ex) {

        } finally {
            mSocket = null;
        }

    }

    private void initUIHandlerAndThread() {
        mUIHandlerThread = new HandlerThread("ziggs_socket_UItransimit_thread");
        mUIHandlerThread.start();
        mHandler = new Handler(Looper.getMainLooper()) {//主线程handler
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG_RECEIVE_DATA:
                        mObservable.setData(currentReceiveByteData, receivePacket.getAddress(), receivePacket.getPort());
                        break;
                    case MSG_RECEIVE_TIMEOUT:
                        mObservable.setData("FFFFFF".getBytes(), null, 0);
                        break;

                }

            }
        };
    }

    public void addWatcher(UdpDataWatcher watcher) {
        mObservable.addObserver(watcher);

    }

    public void removeWatcher(UdpDataWatcher watcher) {
        mObservable.deleteObserver(watcher);
    }
    public void removeAllWatchers() {
        mObservable.deleteObservers();
    }
}
