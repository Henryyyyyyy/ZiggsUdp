package me.henry.ziggslibrary;


import android.os.Handler;

import me.henry.ziggslibrary.callbacks.UdpDataWatcher;
import me.henry.ziggslibrary.main.UdpConnect;

/**
 * Created by henry on 2017/11/16.
 */

public class ZiggsUdp {
    private UdpConnect mUdp = null;

    private static final class Holder {
        private static final ZiggsUdp Instance = new ZiggsUdp();
    }

    public static ZiggsUdp getInstance() {
        return Holder.Instance;
    }

    public void setUdp(UdpConnect udp) {
        this.mUdp = udp;
    }

    /**
     * 加延时操作的原因是,当service刚启动的同时，你一进入activity调用到需要mUdp方法时，mUdp可能为空
     * 因为service启动需要一点时间。经过测试，会有230ms左右的延迟。正常情况下，在app初始化，经过启动页，时间是足够的，
     * 那时候mUdp已经赋值了。加了这个延时操作只是为了万无一失。
     */

    public boolean send(final byte[] data) {
        if (mUdp == null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    send(data);
                }
            }, 1000);
            return false;
        } else {
            mUdp.send(data);
            return true;
        }
    }

    public boolean addWatcher(final UdpDataWatcher watcher) {
        if (mUdp == null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    addWatcher(watcher);
                }
            }, 1000);
            return false;
        } else {
            mUdp.addWatcher(watcher);
            return true;
        }
    }

    public boolean removeWatcher(final UdpDataWatcher watcher) {
        if (mUdp == null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    removeWatcher(watcher);
                }
            }, 1000);
            return false;
        } else {
            mUdp.removeWatcher(watcher);
            return true;
        }
    }


}
