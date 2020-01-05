package com.alsc.chat.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context, String strDBName) {
        super(context, strDBName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table message(" +   //消息表
                "messageId varchar(100) primary key," +
                "msgType int," +
                "fromId long," +
                "toId long," +
                "sendStatus int," +
                "receiveStatus int," +
                "content text," +
                "url text," +
                "createTime long," +
                "expire long," +
                "isdel byte," +
                "owerId long,"+  //消息所属id
                "tag varchar(100)"+  //两者id组合，用来做group by
                ")";
        db.execSQL(sql);
        sql = "create table group_message(" +   //消息表
                "messageId varchar(100) primary key," +
                "msgType int," +
                "fromId long," +
                "groupId long," +
                "sendStatus int," +
                "receiveStatus int," +
                "content text," +
                "url text," +
                "createTime long," +
                "expire long," +
                "isdel byte," +
                "owerId long"+  //消息所属id
                ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
