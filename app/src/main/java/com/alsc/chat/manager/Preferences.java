package com.alsc.chat.manager;

import android.content.SharedPreferences;
/**
 * Created by gigabud on 17-5-4.
 */

public class Preferences {
    private static final String TAG = "Preferences";
    private static Preferences mConfig = null;
    private SharedPreferences mSettings;

    public static Preferences getInstacne() {
        if (mConfig == null) {
            synchronized (TAG) {
                if (mConfig == null) {
                    mConfig = new Preferences();
                }
            }
        }
        return mConfig;
    }

    private Preferences() {
    }

    private SharedPreferences getSettings() {
        if (mSettings == null) {
            mSettings = ConfigManager.getInstance().getContext().getSharedPreferences("Config", 0);
        }
        return mSettings;
    }



    public void setValues(String key, String value) {
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void setValues(String key, boolean value) {
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void setValues(String key, long value) {
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public void setValues(String key, int value) {
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public String getValues(String key, String value) {
        return getSettings().getString(key, value);
    }

    public boolean getValues(String key, boolean defValue) {
        return getSettings().getBoolean(key, defValue);
    }

    public int getValues(String key, int defValue) {
        return getSettings().getInt(key, defValue);
    }

    public long getValues(String key, long defValue) {
        return getSettings().getLong(key, defValue);
    }

    public Integer getValues(String key, Integer defValue) {
        return getSettings().getInt(key, defValue);
    }

    public String getStringByKey(String key) {
        return getSettings().getString(key, "");
    }

    public boolean getBoolByKey(String key) {
        return getSettings().getBoolean(key, false);
    }

    public long getLongByKey(String key) {
        return getSettings().getLong(key, 0l);
    }

    public Integer getIntByKey(String key) {
        return getSettings().getInt(key, 0);
    }
}
