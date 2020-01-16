package com.alsc.chat.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.alsc.chat.BaseApplication;
import com.alsc.chat.bean.GroupBean;
import com.alsc.chat.bean.GroupMessageBean;
import com.alsc.chat.bean.MessageBean;
import com.alsc.chat.manager.ConfigManager;

import java.util.ArrayList;

/**
 * Created by xiangwei.ma on 15-12-23.
 */


public class DatabaseOperate extends DBOperate {

    private static final String TAG = "DataManager";

    private static final String DB_NAME = "alsc.db";

    private static DatabaseOperate mDBManager = null;


    private DatabaseOperate(SQLiteDatabase db) {
        super(db);
    }


    public static DatabaseOperate getInstance() {
        if (mDBManager == null) {
            synchronized (TAG) {
                if (mDBManager == null) {
                    DatabaseHelper databaseHelper = new DatabaseHelper(ConfigManager.getInstance().getContext(), DB_NAME);
                    SQLiteDatabase db = databaseHelper.getWritableDatabase();
                    mDBManager = new DatabaseOperate(db);

                    // ---------  升级数据库
                    mDBManager.upgradeDb();
                }
            }
        }
        return mDBManager;
    }

    public static void reset() {
        mDBManager = null;
    }

    public void upgradeDb() {

    }

    public <T extends IDBItemOperation> ArrayList<T> getAll(String tableName, Class cls) {
        String sql = "select * from " + tableName + " where isdel=0";
        ArrayList<T> list = mDBManager.getList(sql, cls);
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    public ArrayList<MessageBean> getUserChatMsg(long myId, long chatUserId, long time, int limit) {
        String sql;
        if (time == 0l) {
            sql = String.format("select * from message where owerId=%d and ((fromId=%d and toId=%d) or (fromId=%d and toId=%d)) order by createTime desc limit 0,%d",
                    myId, myId, chatUserId, chatUserId, myId, limit);
        } else {
            sql = String.format("select * from message where owerId=%d and createTime<%d and ((fromId=%d and toId=%d) or (fromId=%d and toId=%d)) order by createTime desc",
                    myId, time, myId, chatUserId, chatUserId, myId, limit);
        }
        ArrayList<MessageBean> list = mDBManager.getList(sql, MessageBean.class);
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    public ArrayList<GroupMessageBean> getGroupMsg(long myId, long groupId, long time, int limit) {
        String sql;
        if (time == 0l) {
            sql = String.format("select * from group_message where owerId=%d and groupId=%d order by createTime desc limit 0,%d",
                    myId, groupId, limit);
        } else {
            sql = String.format("select * from group_message where owerId=%d and groupId=%d and createTime<%d order by createTime desc",
                    myId, groupId, time);
        }
        ArrayList<GroupMessageBean> list = mDBManager.getList(sql, GroupMessageBean.class);
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }


    public ArrayList<MessageBean> getUserChatList(long myId) {
        String sql = String.format("select tag,fromId,toId,content,createTime from message group by tag", myId);
        ArrayList<MessageBean> list = mDBManager.getList(sql, MessageBean.class);
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    public ArrayList<GroupMessageBean> getChatGroupList(long myId) {
        String sql = String.format("select groupId,fromId,content,createTime from group_message group by groupId", myId);
        ArrayList<GroupMessageBean> list = mDBManager.getList(sql, GroupMessageBean.class);
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

}
