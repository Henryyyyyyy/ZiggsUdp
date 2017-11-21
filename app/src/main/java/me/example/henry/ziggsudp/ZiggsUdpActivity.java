package me.example.henry.ziggsudp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.net.InetAddress;

import me.henry.ziggslibrary.UdpManager;
import me.henry.ziggslibrary.ZiggsUdp;
import me.henry.ziggslibrary.callbacks.UdpDataCallBack;
import me.henry.ziggslibrary.callbacks.UdpDataWatcher;
import me.henry.ziggslibrary.main.UdpConnect;


public class ZiggsUdpActivity extends AppCompatActivity implements UdpDataCallBack {
    Button btn_start, btn_stop;
    TextView tv_test;
    UdpConnect udp;
    UdpDataWatcher mWatcher;
    private int count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ziggs_udp);
        btn_start = findViewById(R.id.btn_start);
        btn_stop = findViewById(R.id.btn_stop);
        tv_test = findViewById(R.id.tv_test);
        mWatcher = new UdpDataWatcher(ZiggsUdpActivity.this);

        ZiggsUdp.getInstance().addWatcher(mWatcher);

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZiggsUdp.getInstance().send("hahahaha".getBytes());
            }
        });
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ZiggsUdpActivity.this, SecondActivity.class));
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
        UdpManager.getInstance().destoryUdpService();

    }
}