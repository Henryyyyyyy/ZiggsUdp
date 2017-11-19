package me.henry.ziggslibrary.callbacks;

import java.net.InetAddress;
import java.util.Observable;

/**
 * Created by henry on 2017/11/16.
 */

public class ZiggsObservable extends Observable{
    public byte[] buffer;
    public InetAddress inetAddress;
    public int port;

    public ZiggsObservable() {
    }

    public void setData(byte[] buffer, InetAddress inetAddress, int port) {
        this.buffer = buffer;
        this.inetAddress = inetAddress;
        this.port = port;
        setChanged();
        notifyObservers();
    }
}
