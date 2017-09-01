package com.demo.imudges.socketdemo;

import Bean.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvServerMsg;
    private EditText etCLientSendMsg;
    private Button btnSendMsg;
    private TelephonyManager telephonyManager;
    private String IMEI = "";
    private Client client;
    private Message message;
    private Gson gson = new Gson();

    /**
     * 初始化控件
     * */
    private void initViews(){
        etCLientSendMsg = (EditText) findViewById(R.id.et_cilent_send_msg);
        tvServerMsg = (TextView) findViewById(R.id.tv_server_msg);
        btnSendMsg = (Button) findViewById(R.id.btn_send_msg);
        btnSendMsg.setOnClickListener(this);
        telephonyManager = (TelephonyManager) this.getSystemService(this.TELEPHONY_SERVICE);
        //获取设备唯一ID
        IMEI = telephonyManager.getDeviceId();
    }

    /**
     * 初始化事件
     * */
    private void initEvents(){
        //判断是否获取到IMEI
        if(IMEI != null && !IMEI.equals("")){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    client = new Client(MainActivity.this);
                    client.setListener(new Client.Listener() {
                        @Override
                        public void update(final String msg) {
                            if(msg!=null){
                                //子线程中更新界面
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //Message messageFromServer = gson.fromJson(msg,Message.class);
                                                tvServerMsg.setText(msg);
                                            }
                                        });
                                    }
                                }).start();
                            }
                        }
                    });
                    client.getServerMsg();
                }
            }).start();
            message = new Message();
            message.setClientID(IMEI);
        } else {
            Toast.makeText(this,"获取IMEI失败",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initEvents();
    }

    @Override
    public void onClick(View view) {
        if(!TextUtils.isEmpty(etCLientSendMsg.getText())){
            message.setMsg(etCLientSendMsg.getText().toString());
            String msg = gson.toJson(message);
            client.setMsg(msg);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    client.sendMessage();
                }
            }).start();
        } else {

        }
    }
}
