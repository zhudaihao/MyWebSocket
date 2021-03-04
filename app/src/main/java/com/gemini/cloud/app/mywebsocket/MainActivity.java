package com.gemini.cloud.app.mywebsocket;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity implements ServerConnection.ServerListener{
    private final String SERVER_URL = "ws://192.168.11.227:8443/v1";//192.168.11.227:8443是电脑本机ip
    private ServerConnection mServerConnection;
    private int mCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化Ws
        mServerConnection = new ServerConnection(SERVER_URL);
    }

    //发送
    public void testWs(View view) {
        Log.e("zdh","---------------点击发送 ");
        mServerConnection.sendMessage(String.valueOf(mCounter++));
    }


    //连接
    @Override
    protected void onResume() {
        super.onResume();
        mServerConnection.connect(this);
    }

    //断开连接
    @Override
    protected void onPause() {
        super.onPause();
        mServerConnection.disconnect();
    }


    //消息回调
    @Override
    public void onNewMessage(String message) {
        Log.e("zdh","---------------onNewMessage "+message);
    }

    //状态改变回调
    @Override
    public void onStatusChange(ServerConnection.ConnectionStatus status) {
        String statusMsg = (status == ServerConnection.ConnectionStatus.CONNECTED ? "连接" : "断开连接");
        Log.e("zdh","---------------onStatusChange " +statusMsg);
    }
}