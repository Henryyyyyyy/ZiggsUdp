package me.example.henry.ziggsudp.wrap;

import java.net.InetAddress;

/**
 * Created by henry on 2017/11/13.
 */

public interface DataCallBack {
    void udp_receive(byte[] buffer, InetAddress inetAddress, int port);

}
