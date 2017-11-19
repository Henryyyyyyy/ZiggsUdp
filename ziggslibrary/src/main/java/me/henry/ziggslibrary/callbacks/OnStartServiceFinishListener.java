package me.henry.ziggslibrary.callbacks;

import me.henry.ziggslibrary.main.UdpConnect;

/**
 * Created by henry on 2017/11/16.
 */

public interface OnStartServiceFinishListener {
    void onStartSuccess(UdpConnect udp);
}
