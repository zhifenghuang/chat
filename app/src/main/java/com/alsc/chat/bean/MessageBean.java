package com.alsc.chat.bean;

import android.content.ContentValues;

import com.alsc.chat.db.DatabaseOperate;
import com.alsc.chat.db.IDBItemOperation;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.UUID;

public class MessageBean extends IDBItemOperation {

    private int cmd;
    private String messageId;
    private long fromId;
    private long toId;
    private int msgType;   //0表示文字消息，
    private String content;
    private String url;
    private Object fileInfo;
    private int status;
    private long expire;
    private long createTime;
    private int sendStatus;
    private int receiveStatus;

    public MessageBean() {
        messageId = UUID.randomUUID().toString();
        createTime = System.currentTimeMillis();
    }

    public boolean isMySendMsg(long myUserId) {
        return fromId == myUserId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(int sendStatus) {
        this.sendStatus = sendStatus;
    }

    public int getReceiveStatus() {
        return receiveStatus;
    }

    public void setReceiveStatus(int receiveStatus) {
        this.receiveStatus = receiveStatus;
    }

    public Object getFileInfo() {
        return fileInfo;
    }

    public void setFileInfo(Object fileInfo) {
        this.fileInfo = fileInfo;
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public long getFromId() {
        return fromId;
    }

    public void setFromId(long fromId) {
        this.fromId = fromId;
    }

    public long getToId() {
        return toId;
    }

    public void setToId(long toId) {
        this.toId = toId;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    @Override
    public String getPrimaryKeyName() {
        return "messageId";
    }

    @Override
    public String getTableName() {
        return "message";
    }

    public String toJson(){
        HashMap<String,Object> map=new HashMap<>();
        if(msgType==1){
            map.put("cmd",cmd);
            map.put("fromId",fromId);
            map.put("toId",toId);
            map.put("msgType",msgType);
            map.put("content",content);
            map.put("createTime",createTime);
        }
        return new Gson().toJson(map);
    }

    /**
     * 消息已读成功
     */
    public void sureReceiveStatus() {
        receiveStatus = 1;
        ContentValues contentValues = new ContentValues();
        contentValues.put(getPrimaryKeyName(), messageId);
        contentValues.put("receiveStatus", receiveStatus);
        DatabaseOperate.getInstance().update(this, contentValues);
    }

    /**
     * 消息已发送成功
     */
    public void sureSendMsg() {
        sendStatus = 1;
        ContentValues contentValues = new ContentValues();
        contentValues.put(getPrimaryKeyName(), messageId);
        contentValues.put("sendStatus", sendStatus);
        DatabaseOperate.getInstance().update(this, contentValues);
    }

    @Override
    public ContentValues getValues() {
        ContentValues values = new ContentValues();
        values.put("messageId", messageId);
        values.put("msgType", msgType);
        values.put("fromId", fromId);
        values.put("toId", toId);
        values.put("sendStatus", sendStatus);
        values.put("receiveStatus", receiveStatus);
        values.put("content", content);
        values.put("url", url);
        values.put("createTime", createTime);
        values.put("expire", expire);
        values.put("isdel", 0);
        return values;
    }
}
