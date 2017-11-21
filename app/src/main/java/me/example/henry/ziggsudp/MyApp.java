package me.example.henry.ziggsudp;

import android.app.Application;

import me.henry.ziggslibrary.UdpManager;
import me.henry.ziggslibrary.ZiggsUdp;
import me.henry.ziggslibrary.callbacks.OnStartServiceFinishListener;
import me.henry.ziggslibrary.main.UdpConnect;


/**
 * Created by henry on 2017/11/15.
 */

public class MyApp extends Application implements OnStartServiceFinishListener {
    @Override
    public void onCreate() {
        super.onCreate();
        UdpManager.getInstance()
                .context(this)
                .address("192.168.1.72")//udp服务器地址
                .port(8084)//端口
                .timeOut(5)//超时时间
                .CustomTimeOutString("FFFFFF")//超时的时候返回的信息
                .init()
                .createUdpService(this);
    }


    @Override
    public void onStartSuccess(UdpConnect udp) {
        ZiggsUdp.getInstance().setUdp(udp);

    }
}
