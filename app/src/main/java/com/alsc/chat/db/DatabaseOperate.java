package com.alsc.chat.db;

import android.database.sqlite.SQLiteDatabase;
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
//                if (mDBManager == null) {
//                    DatabaseHelper databaseHelper = new DatabaseHelper(BaseApplication.getAppContext(), DB_NAME);
//                    SQLiteDatabase db = databaseHelper.getWritableDatabase();
//                    mDBManager = new DatabaseOperate(db);
//
//                    // ---------  升级数据库
//                    mDBManager.upgradeDb();
//                }
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



}
