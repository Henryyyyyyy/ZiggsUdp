package me.example.henry.ziggsudp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;

import me.example.henry.ziggsudp.serverthings.NetworkUtils;
import me.example.henry.ziggsudp.serverthings.UdpServer;

public class ServerActivity extends AppCompatActivity {

    private Button btnUdpStart, btnUdpClose, btnRcvClear, btnSendClear, btnUdpSend;
    private TextView txtRcv, txtSend;
    private EditText editSend, editIp, editPort;

    public static Context context;

    private MyHandler myHandler = new MyHandler(this);
    private MyBroadcastReceiver myBroadcastReceiver = new MyBroadcastReceiver();
    private MyBtnClick myBtnClick = new MyBtnClick();
    private static UdpServer udpServer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        context = this;
        BindWidget();
        Listening();
        BindReceiver();
        String ip= NetworkUtils.getIPAddress(this);
        editIp.setText(ip);
    }

    private void BindWidget() {
        btnUdpClose = (Button) findViewById(R.id.btn_udpClose);
        btnUdpStart = (Button) findViewById(R.id.btn_udpStart);
        btnUdpSend = (Button) findViewById(R.id.btn_Send);
        btnRcvClear = (Button) findViewById(R.id.btn_CleanRcv);
        btnSendClear = (Button) findViewById(R.id.btn_CleanSend);
        txtRcv = (TextView) findViewById(R.id.txt_Rcv);
        txtSend = (TextView) findViewById(R.id.txt_Send);
        editIp = (EditText) findViewById(R.id.editIp);
        editPort = (EditText) findViewById(R.id.editPort);
        editSend = (EditText) findViewById(R.id.edit_Send);
    }

    private void Listening() {
        btnUdpStart.setOnClickListener(myBtnClick);
        btnUdpClose.setOnClickListener(myBtnClick);
        btnUdpSend.setOnClickListener(myBtnClick);
        btnRcvClear.setOnClickListener(myBtnClick);
        btnSendClear.setOnClickListener(myBtnClick);

    }

    private void BindReceiver() {
        IntentFilter intentFilter = new IntentFilter("udpReceiver");
        registerReceiver(myBroadcastReceiver, intentFilter);
    }

    private class MyHandler extends Handler {
        private final WeakReference<ServerActivity> mActivity;

        public MyHandler(ServerActivity activity) {
            mActivity = new WeakReference<ServerActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ServerActivity activity = mActivity.get();
            if (null != activity) {
                switch (msg.what) {
                    case 1:
                        String str = msg.obj.toString();
                        txtRcv.append(str);
                        break;
                    case 2:
                        String stra = msg.obj.toString();
                        txtSend.append(stra);
                        break;
                    case 3:
                        break;
                }
            }
        }
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String mAction = intent.getAction();
            switch (mAction) {
                case "udpReceiver":
                    String msg = intent.getStringExtra("udpReceiver");
                    Message message = new Message();
                    message.what = 1;
                    message.obj = msg;
                    myHandler.sendMessage(message);
                    break;
            }
        }
    }


    private class MyBtnClick implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_udpStart:
                    Log.i("asd", "asdasd");

                    if (!editIp.getText().toString().isEmpty() && !editIp.getText().toString().isEmpty()) {
                        int mPort = Integer.parseInt(editPort.getText().toString());

                        udpServer = new UdpServer(editIp.getText().toString(), mPort);
                        Thread thread = new Thread(udpServer);
                        thread.start();
                        btnUdpStart.setEnabled(false);
                        btnUdpClose.setEnabled(true);
                    } else {
                        Log.i("DebugInfo", "请输入Ip或者Port");
                    }
                    break;
                case R.id.btn_udpClose:
                    btnUdpClose.setEnabled(false);
                    final Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //关闭UDP
                            udpServer.setUdpLife(false);
                            //等待udp阻塞结束，这里就体现出超时的好处了
                            while (udpServer.getLifeMsg()) ;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    btnUdpStart.setEnabled(true);
                                }
                            });

                        }
                    });
                    thread.start();

                    break;
                case R.id.btn_CleanRcv:
                    txtRcv.setText("");
                    break;
                case R.id.btn_CleanSend:
                    txtSend.setText("");
                    break;
                case R.id.btn_Send:
                    Thread thread1 = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (editSend.getText().toString() != null) {
                                try {
                                    udpServer.Send(editSend.getText().toString());
                                    Message message = new Message();
                                    message.what = 2;
                                    message.obj = editSend.getText().toString();
                                    myHandler.sendMessage(message);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Log.i("DebugInfo", "请输入发送内容");
                            }

                        }
                    });
                    thread1.start();
                    break;
            }
        }
    }
}
