package com.alsc.chat.manager;

import android.content.Context;
import android.text.TextUtils;

import com.alsc.chat.bean.UserBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class DataManager {

    private static final String TAG = "DataManager";
    private static DataManager mDataManager;

    private UserBean mMyInfo;

    private DataManager() {

    }

    public static DataManager getInstance() {
        if (mDataManager == null) {
            synchronized (TAG) {
                if (mDataManager == null) {
                    mDataManager = new DataManager();
                }
            }
        }
        return mDataManager;
    }

    public void saveUser(UserBean userBean) {
        mMyInfo = userBean;
        Preferences.getInstacne().setValues("user", new Gson().toJson(userBean));
    }

    public UserBean getUser() {
        if (mMyInfo == null) {
            String str = Preferences.getInstacne().getValues("user", "");
            if (TextUtils.isEmpty(str)) {
                return null;
            }
            mMyInfo = new Gson().fromJson(str, UserBean.class);
        }
        return mMyInfo;
    }

    public String getToken() {
        return getUser() == null ? null : getUser().getToken();
    }

    public void saveFriends(ArrayList<UserBean> list) {
        Preferences.getInstacne().setValues("friends", list == null ? "" : new Gson().toJson(list));
    }

    public ArrayList<UserBean> getFriends() {
        String str = Preferences.getInstacne().getValues("friends", "");
        if (TextUtils.isEmpty(str)) {
            return new ArrayList<>();
        }
        return new Gson().fromJson(str, new TypeToken<ArrayList<UserBean>>() {
        }.getType());
    }
}
