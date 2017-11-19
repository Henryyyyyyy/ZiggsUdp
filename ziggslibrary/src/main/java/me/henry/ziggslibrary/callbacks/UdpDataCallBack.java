package me.henry.ziggslibrary.callbacks;

import java.net.InetAddress;

/**
 * Created by henry on 2017/11/13.
 */

public interface UdpDataCallBack {
    void udp_receive(byte[] buffer, InetAddress inetAddress, int port);

}
