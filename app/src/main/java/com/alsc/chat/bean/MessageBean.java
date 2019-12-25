package com.alsc.chat.bean;

import java.io.Serializable;

public class MessageBean implements Serializable {

    private int cmd;
    private long fromId;
    private long toId;
    private int msgType;
    private String content;
    private String url;
    private Object fileInfo;
    private int status;
    private long expire;

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
}
