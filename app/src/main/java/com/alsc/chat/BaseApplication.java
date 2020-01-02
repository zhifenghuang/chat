package com.alsc.chat;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.alsc.chat.bean.MessageBean;
import com.alsc.chat.bean.MessageResponse;
import com.alsc.chat.db.DBOperate;
import com.alsc.chat.db.DatabaseOperate;
import com.alsc.chat.http.HttpMethods;
import com.alsc.chat.manager.DataManager;
import com.alsc.chat.manager.Preferences;
import com.alsc.chat.utils.Constants;
import com.google.gson.Gson;
import com.zhangke.websocket.SimpleListener;
import com.zhangke.websocket.SocketListener;
import com.zhangke.websocket.WebSocketHandler;
import com.zhangke.websocket.WebSocketManager;
import com.zhangke.websocket.WebSocketSetting;
import com.zhangke.websocket.response.ErrorResponse;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        DatabaseOperate.setContext(this);
        DatabaseOperate.getInstance();
        HttpMethods.getInstance().setContext(this);
        Preferences.getInstacne().setContext(this);


        String token = DataManager.getInstance().getToken();
        if (!TextUtils.isEmpty(token)) {
            initWebSocket(token);
        }
    }

    public void initWebSocket(String token) {
        WebSocketSetting setting = new WebSocketSetting();
        //连接地址，必填，例如 wss://echo.websocket.org
        setting.setConnectUrl(String.format(Constants.CHAT_SOCKET_URL, token));//必填

        //设置连接超时时间
        setting.setConnectTimeout(15 * 1000);

        //设置心跳间隔时间
        setting.setConnectionLostTimeout(0);

        //设置断开后的重连次数，可以设置的很大，不会有什么性能上的影响
        setting.setReconnectFrequency(60);

        //网络状态发生变化后是否重连，
        //需要调用 WebSocketHandler.registerNetworkChangedReceiver(context) 方法注册网络监听广播
        setting.setReconnectWithNetworkChanged(true);

        //通过 init 方法初始化默认的 WebSocketManager 对象
        WebSocketManager manager = WebSocketHandler.init(setting);
        //启动连接
        manager.start();

        //注意，需要在 AndroidManifest 中配置网络状态获取权限
        //注册网路连接状态变化广播
        WebSocketHandler.registerNetworkChangedReceiver(this);

        WebSocketHandler.getDefault().addListener(socketListener);
    }

    private SocketListener socketListener = new SimpleListener() {
        @Override
        public void onConnected() {
            Log.e("aaaaaaaaa", "onConnected");
        }

        @Override
        public void onConnectFailed(Throwable e) {
            if (e != null) {
                Log.e("aaaaaaaaa", "onConnectFailed:" + e.toString());
            } else {
                Log.e("aaaaaaaaa", "onConnectFailed:null");
            }
        }

        @Override
        public void onDisconnect() {
            Log.e("aaaaaaaaa", "onDisconnect");
        }

        @Override
        public void onSendDataError(ErrorResponse errorResponse) {
            Log.e("aaaaaaaaa", "aaaaa:" + errorResponse.getDescription());
            errorResponse.release();
        }

        @Override
        public <T> void onMessage(String message, T data) {
            Log.e("aaaaaaaaa", "aaaaabbb:" + message);
            if (TextUtils.isEmpty(message)) {
                return;
            }
            try {
                JSONObject object = new JSONObject(message);
                int cmd = object.optInt("cmd");
                if (cmd == 2000) {
                    MessageBean bean = new Gson().fromJson(message, MessageBean.class);
                    DatabaseOperate.getInstance().insertOrUpdate(bean);
                    EventBus.getDefault().post(bean);
                } else if (cmd == 1000) {
//                    JSONObject jb = new JSONObject();
//                    jb.put("cmd", 1000);
//                    WebSocketHandler.getDefault().send(jb.toString());
                } else if (cmd == 1010 || cmd == 1020) {
                    MessageResponse response = new Gson().fromJson(message, MessageResponse.class);
                    if (response != null) {
                        ArrayList<MessageBean> list = response.getMessages();
                        if (list == null || list.isEmpty()) {
                            return;
                        }
                        switch (response.getCmd()) {
                            case 1010:  //离线群组消息
                                break;
                            case 1020:  //离线个人消息
                                for (MessageBean bean : list) {
                                    DatabaseOperate.getInstance().insertOrUpdate(bean);
                                }
                                break;

                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };
}
