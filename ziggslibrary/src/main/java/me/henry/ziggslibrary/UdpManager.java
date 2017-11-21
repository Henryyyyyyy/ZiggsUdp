package me.henry.ziggslibrary;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import me.henry.ziggslibrary.callbacks.IUdpService;
import me.henry.ziggslibrary.callbacks.OnStartServiceFinishListener;
import me.henry.ziggslibrary.main.UdpService;
import me.henry.ziggslibrary.others.UdpConfigs;
import me.henry.ziggslibrary.others.ZiggsUtils;

/**
 * Created by henry on 2017/11/13.
 */

public class UdpManager {
    private Context mContext;
    private boolean isReady = false;
    //--------about service
    private Intent intent = null;
    private ZiggsServiceConnnection conn = null;
    private IUdpService iUdpService = null;
    private OnStartServiceFinishListener mListener = null;
    private boolean isServiceStarted = false;

    private static final class Holder {
        private static final UdpManager Instance = new UdpManager();
    }

    public static UdpManager getInstance() {
        return Holder.Instance;
    }

    public UdpManager context(Context context) {
        mContext = context;
        return this;
    }

    public UdpManager port(int port) {
        UdpConfigs.port = port;
        return this;
    }

    public UdpManager address(String address) {
        UdpConfigs.address = address;
        return this;
    }

    public UdpManager timeOut(int timeOut) {
        UdpConfigs.timeout = timeOut;
        return this;
    }
    public UdpManager CustomTimeOutString(String str) {
        UdpConfigs.TimeOut_Str = str;
        return this;
    }
    public UdpManager init() {
        if ((UdpConfigs.port != -1) && (UdpConfigs.address != null) && (UdpConfigs.timeout != -1) && (mContext != null)&&UdpConfigs.TimeOut_Str!=null) {
            isReady = true;
        } else {
            isReady = false;
        }
        return this;
    }


    public void createUdpService(OnStartServiceFinishListener listener) {
        if (!isReady) return;

        if (!ZiggsUtils.isServiceRunning(mContext, UdpConfigs.UdpServiceName)) {
            //如果没在运行，就是说没创建，就要创建，创建的同时它会自己创建udp连接
            mListener = listener;
            intent = new Intent(mContext, UdpService.class);
            conn = new ZiggsServiceConnnection();
            mContext.bindService(intent, conn, Context.BIND_AUTO_CREATE);
            isServiceStarted = true;
        }

    }

    public void destoryUdpService() {
        if (conn != null) {
            mContext.unbindService(conn);
            conn = null;
            iUdpService = null;
        }

    }

    class ZiggsServiceConnnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            iUdpService = (IUdpService) iBinder;
            mListener.onStartSuccess(iUdpService.getZiggsUdp());
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    }
}
