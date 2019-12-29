package com.alsc.chat;

import android.app.Application;
import android.text.TextUtils;

import com.alsc.chat.manager.DataManager;
import com.alsc.chat.utils.Constants;
import com.zhangke.websocket.WebSocketHandler;
import com.zhangke.websocket.WebSocketManager;
import com.zhangke.websocket.WebSocketSetting;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        String token= DataManager.getInstance().getToken();
        if(!TextUtils.isEmpty(token)){
            initWebSocket(token);
        }
    }

    public void initWebSocket(String token){
        WebSocketSetting setting = new WebSocketSetting();
        //连接地址，必填，例如 wss://echo.websocket.org
        setting.setConnectUrl(Constants.CHAT_SOCKET_URL+token);//必填

        //设置连接超时时间
        setting.setConnectTimeout(15 * 1000);

        //设置心跳间隔时间
        setting.setConnectionLostTimeout(30);

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
    }
}
