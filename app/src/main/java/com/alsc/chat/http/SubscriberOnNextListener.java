package com.alsc.chat.http;

public interface SubscriberOnNextListener<T> {
    void onNext(T t, String msg);
}
