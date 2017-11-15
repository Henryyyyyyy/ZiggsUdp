package me.example.henry.ziggsudp.serverthings;

import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import me.example.henry.ziggsudp.ServerActivity;

public class UdpServer implements Runnable {
    private String ip = null;
    private int port = 0;  
    private DatagramPacket dpRcv = null,dpSend = null;
    private static DatagramSocket ds = null;
    private InetSocketAddress inetSocketAddress = null;
    private byte[] msgRcv = new byte[1024];  
    private boolean udpLife = true;     //udp生命线程  
    private boolean udpLifeOver = true; //生命结束标志，false为结束  
  
  
    public UdpServer(String mIp, int mPort) {
        this.ip = mIp;  
        this.port = mPort;  
    }  
  
    private void SetSoTime(int ms) throws SocketException {
        ds.setSoTimeout(ms);  
    }  
  
    //返回udp生命线程因子是否存活  
    public boolean isUdpLife(){  
        if (udpLife){  
            return true;  
        }  
  
        return false;  
    }  
  
    //返回具体线程生命信息是否完结  
    public boolean getLifeMsg(){  
        return udpLifeOver;  
    }  
  
    //更改UDP生命线程因子  
    public void setUdpLife(boolean b){  
        udpLife = b;  
    }  
  
    public void Send(String sendStr) throws IOException {
        Log.i("SocketInfo","客户端IP："+ dpRcv.getAddress().getHostAddress() +"客户端Port:"+ dpRcv.getPort());
        dpSend = new DatagramPacket(sendStr.getBytes(),sendStr.getBytes().length,dpRcv.getAddress(),dpRcv.getPort());
        ds.send(dpSend);  
    }  
  
  
    @Override
    public void run() {  
        inetSocketAddress = new InetSocketAddress(ip, port);
        try {  
            ds = new DatagramSocket(inetSocketAddress);
            Log.i("SocketInfo", "UDP服务器已经启动");
  
            SetSoTime(3000);
            //设置超时，不需要可以删除  
        } catch (SocketException e) {
            e.printStackTrace();  
        }  
  
        dpRcv = new DatagramPacket(msgRcv, msgRcv.length);
        while (udpLife) {  
            try {  
                Log.i("SocketInfo", "UDP监听中");
                ds.receive(dpRcv);
                Log.i("SocketInfo", "receive之后");
                String string = new String(dpRcv.getData(), dpRcv.getOffset(), dpRcv.getLength());
                Log.i("SocketInfo", "收到信息：" + string);
                Send("data from server");
                Intent intent =new Intent();
                intent.setAction("udpReceiver");  
                intent.putExtra("udpReceiver",string);  
                ServerActivity.context.sendBroadcast(intent);//将消息发送给主界面
  
            } catch (IOException e) {
                e.printStackTrace();  
            }  
  
        }  
        ds.close();  
        Log.i("SocketInfo","UDP监听关闭");
        //udp生命结束  
        udpLifeOver = false;  
    }  
}  