package me.example.henry.ziggsudp;

import android.app.Application;

import me.example.henry.ziggsudp.wrap.UdpManager;

/**
 * Created by henry on 2017/11/15.
 */

public class MyApp extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        UdpManager.getInstance()
                .address("192.168.2.19")
                .port(8084)
                .timeOut(5)
                .init();
    }
}
