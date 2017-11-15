package me.example.henry.ziggsudp.wrap;


/**
 * Created by henry on 2017/11/13.
 */

public class UdpManager {
    private int mPort = -1;
    private String mAddress = null;
    private int mTimeOut = -1;
    private ZiggsUdp mUdp = null;
    private boolean isReady = false;

    private static final class Holder {
        private static final UdpManager Instance = new UdpManager();
    }

    public static UdpManager getInstance() {
        return Holder.Instance;
    }

    public UdpManager port(int port) {
        this.mPort = port;
        return this;
    }

    public UdpManager address(String address) {
        this.mAddress = address;
        return this;
    }

    public UdpManager timeOut(int timeOut) {
        this.mTimeOut = timeOut;
        return this;
    }

    public void init() {
        if ((mTimeOut != -1) && (mAddress != null) && (mPort != -1)) {
            isReady = true;
        } else {
            isReady = false;
        }

    }

    /**
     * 创建udp控制类
     *
     * @param cb
     * @return
     */
    public ZiggsUdp createUdp(DataCallBack cb) {
        if (isReady) {
            mUdp = new ZiggsUdp(mPort, mAddress, mTimeOut);
            new Thread(mUdp).start();
            mUdp.addCallBack(cb);
            return mUdp;
        } else {
            return null;
        }

    }



}
