package me.henry.ziggslibrary.main;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import me.henry.ziggslibrary.callbacks.IUdpService;
import me.henry.ziggslibrary.others.UdpConfigs;

/**
 * Created by henry on 2017/11/16.
 */

public class UdpService extends Service {
    private UdpConnect mUdp = null;
    private Thread mConnectThread=null;

    @Override
    public void onCreate() {
        super.onCreate();
        mUdp = new UdpConnect(UdpConfigs.port, UdpConfigs.address, UdpConfigs.timeout);
       mConnectThread= new Thread(mUdp);
       mConnectThread.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyUdpBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {

        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUdp != null) {
            mUdp.destory();
        }

    }

    public UdpConnect getUdp() {
        return mUdp;
    }


    class MyUdpBinder extends Binder implements IUdpService {

        @Override
        public UdpConnect getZiggsUdp() {
            return getUdp();
        }
    }
}
