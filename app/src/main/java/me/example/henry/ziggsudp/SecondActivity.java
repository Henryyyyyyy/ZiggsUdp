package me.example.henry.ziggsudp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.net.InetAddress;

import me.henry.ziggslibrary.ZiggsUdp;
import me.henry.ziggslibrary.callbacks.UdpDataCallBack;
import me.henry.ziggslibrary.callbacks.UdpDataWatcher;

public class SecondActivity extends AppCompatActivity implements UdpDataCallBack {
   Button second_send;
   TextView second_show;
   private UdpDataWatcher watcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        second_send=findViewById(R.id.second_send);
        second_show=findViewById(R.id.second_show);
        watcher=new UdpDataWatcher(this);
        ZiggsUdp.getInstance().addWatcher(watcher);
        second_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZiggsUdp.getInstance().send("cao".getBytes());
            }
        });
    }

    @Override
    public void udp_receive(byte[] buffer, InetAddress inetAddress, int port) {
        second_show.setText(new String(buffer));
    }
}
