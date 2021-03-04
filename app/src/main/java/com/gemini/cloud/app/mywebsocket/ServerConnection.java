package com.gemini.cloud.app.mywebsocket;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;


/**
 * @author zhudaihao
 * @date 2021-3-4
 * webSocket 通信实现
 */
public class ServerConnection {
    private static final String TAG = "zdh";
    private WebSocket mWebSocket;
    private OkHttpClient mClient;
    private String mServerUrl;
    private Handler mMessageHandler;
    private Handler mStatusHandler;
    private ServerListener mListener;


    //初始化ws
    public ServerConnection(String url) {
        mClient = new OkHttpClient.Builder()
                .readTimeout(3, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();

        mServerUrl = url;
    }

    //连接
    public void connect(ServerListener listener) {
        try {
            Request request = new Request.Builder().url(mServerUrl).build();
            mWebSocket = mClient.newWebSocket(request, new SocketListener());
            mListener = listener;
            mMessageHandler = new Handler(msg -> {
                mListener.onNewMessage((String) msg.obj);
                return true;
            });
            mStatusHandler = new Handler(msg -> {
                mListener.onStatusChange((ConnectionStatus) msg.obj);
                return true;
            });
        } catch (Exception e) {
            Log.e(TAG, "-------------e " + e);
        }

    }

    //断开连接
    public void disconnect() {
        mWebSocket.cancel();
        mListener = null;
        mMessageHandler.removeCallbacksAndMessages(null);
        mStatusHandler.removeCallbacksAndMessages(null);
    }


    //发送消息
    public void sendMessage(String message) {
        mWebSocket.send(message);
    }



    /**
     * ws连接回调
     */
    private class SocketListener extends WebSocketListener {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            Log.e(TAG, "-------------onOpen ");
            Message m = mStatusHandler.obtainMessage(0, ConnectionStatus.CONNECTED);
            mStatusHandler.sendMessage(m);
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            Log.e(TAG, "-------------onMessage ");
            Message m = mMessageHandler.obtainMessage(0, text);
            mMessageHandler.sendMessage(m);
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            Log.e(TAG, "-------------onClosed ");
            Message m = mStatusHandler.obtainMessage(0, ConnectionStatus.DISCONNECTED);
            mStatusHandler.sendMessage(m);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            Log.e(TAG, "-------------onFailure " + response);
            t.printStackTrace();
            disconnect();
        }
    }

    //回调结果
    public interface ServerListener {
        void onNewMessage(String message);
        void onStatusChange(ConnectionStatus status);
    }

    public enum ConnectionStatus {
        DISCONNECTED,
        CONNECTED
    }
}
