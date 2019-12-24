package com.alsc.chat.bean;

import com.google.gson.Gson;

public class BasicResponse<T> {
    private int code; // 返回的结果标志 200成功
    private String msg;
    private T result;

    public boolean isSuccess() {
        // TODO Auto-generated method stub
        return code == 200;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}

