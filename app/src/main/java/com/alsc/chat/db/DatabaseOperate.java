package com.alsc.chat.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.alsc.chat.BaseApplication;
import com.alsc.chat.bean.MessageBean;

import java.util.ArrayList;

/**
 * Created by xiangwei.ma on 15-12-23.
 */


public class DatabaseOperate extends DBOperate {

    private static final String TAG = "DataManager";

    private static final String DB_NAME = "alsc.db";

    private static DatabaseOperate mDBManager = null;

    private static Context mContext;


    private DatabaseOperate(SQLiteDatabase db) {
        super(db);
    }

    public static void setContext(Context context) {
        mContext = context;
    }

    public static DatabaseOperate getInstance() {
        if (mDBManager == null) {
            synchronized (TAG) {
                if (mDBManager == null) {
                    DatabaseHelper databaseHelper = new DatabaseHelper(mContext, DB_NAME);
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

    public ArrayList<MessageBean> getUserChatMsg(long myId, long chatUserId) {
        String sql = String.format("select * from message where owerId=%d and ((fromId=%d and toId=%d) or (fromId=%d and toId=%d))",
                myId, myId, chatUserId, chatUserId, myId);
        ArrayList<MessageBean> list = mDBManager.getList(sql, MessageBean.class);
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }


    public ArrayList<MessageBean> getUserChatList(long myId) {
        String sql = String.format("select tag,fromId,toId,content,createTime from(select * from message where owerId=%d order by createTime desc) b group by b.tag", myId);
        ArrayList<MessageBean> list = mDBManager.getList(sql, MessageBean.class);
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

}
