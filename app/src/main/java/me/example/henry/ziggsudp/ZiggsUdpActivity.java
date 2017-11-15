package me.example.henry.ziggsudp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.net.InetAddress;

import me.example.henry.ziggsudp.wrap.DataCallBack;
import me.example.henry.ziggsudp.wrap.UdpManager;
import me.example.henry.ziggsudp.wrap.ZiggsUdp;

public class ZiggsUdpActivity extends AppCompatActivity implements DataCallBack {
    Button btn_start;
    TextView tv_test;
    ZiggsUdp udp;
    private int count = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ziggs_udp);
        btn_start = findViewById(R.id.btn_start);
        tv_test =  findViewById(R.id.tv_test);

        udp = UdpManager.getInstance().createUdp(this);

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                udp.send("ziggs data".getBytes());
            }
        });
    }



    @Override
    public void udp_receive(byte[] buffer, InetAddress inetAddress, int port) {
        Log.e("miao", "buffer=" + new String(buffer));
        tv_test.setText(new String(buffer) + count);
        count++;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        udp.destory();
    }
}