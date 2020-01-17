package com.alsc.chat.bean;

import java.io.File;

public class FileBean {
    private int type;  //0表示图片，1表示语音，2表示视频
    private File file;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
