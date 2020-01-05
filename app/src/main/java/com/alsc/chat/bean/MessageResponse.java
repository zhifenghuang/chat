package com.alsc.chat.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class MessageResponse<T> implements Serializable {
    private int cmd;
    private ArrayList<T> messages;

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public ArrayList<T> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<T> messages) {
        this.messages = messages;
    }
}
