package com.alsc.chat.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class MessageResponse implements Serializable {
    private int cmd;
    private ArrayList<MessageBean> messages;

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public ArrayList<MessageBean> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<MessageBean> messages) {
        this.messages = messages;
    }
}
