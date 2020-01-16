package com.alsc.chat.manager;

import android.content.Context;

public class ConfigManager {

    private static final String TAG = "ConfigManager";

    private static ConfigManager mConfigManager;

    private Context mContext;

    private ConfigManager() {

    }

    public static ConfigManager getInstance() {
        if (mConfigManager == null) {
            synchronized (TAG) {
                if(mConfigManager==null){
                    mConfigManager=new ConfigManager();
                }
            }
        }
        return mConfigManager;
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }
}
