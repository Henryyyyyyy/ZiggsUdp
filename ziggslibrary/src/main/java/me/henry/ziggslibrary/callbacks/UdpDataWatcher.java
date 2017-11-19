package me.henry.ziggslibrary.callbacks;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by henry on 2017/11/16.
 */

public class UdpDataWatcher implements Observer {
    private UdpDataCallBack callback;
    ZiggsObservable mObserverable;

    public UdpDataWatcher(UdpDataCallBack c) {
        this.callback = c;
    }


    @Override
    public void update(Observable observable, Object o) {
        mObserverable = (ZiggsObservable) observable;
        callback.udp_receive(mObserverable.buffer, mObserverable.inetAddress, mObserverable.port);

    }
}
