package com.alsc.chat.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.alsc.chat.bean.BasicResponse;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 用于在Http请求开始时，自动显示一个ProgressDialog
 * 在Http请求结束是，关闭ProgressDialog
 */
public class HttpObserver<T> implements Observer<T>, ProgressCancelListener {

    private SubscriberOnNextListener mSubscriberOnNextListener;
    private OnHttpErrorListener mErrorListener;
    private boolean isShowDialog = true;
    private Context context;
    public static final int SHOW_PROGRESS_DIALOG = 1;
    public static final int DISMISS_PROGRESS_DIALOG = 2;

    public HttpObserver(SubscriberOnNextListener mSubscriberOnNextListener, Context context, OnHttpErrorListener errorListener) {
        this.mSubscriberOnNextListener = mSubscriberOnNextListener;
        this.context = context;
        this.mErrorListener = errorListener;
    }


    public HttpObserver(SubscriberOnNextListener mSubscriberOnNextListener, Context context, boolean isShowDialog) {
        this.mSubscriberOnNextListener = mSubscriberOnNextListener;
        this.context = context;
        this.isShowDialog = isShowDialog;
    }

    public HttpObserver(SubscriberOnNextListener mSubscriberOnNextListener, Context context, boolean isShowDialog, OnHttpErrorListener errorListener) {
        this.mSubscriberOnNextListener = mSubscriberOnNextListener;
        this.context = context;
        this.isShowDialog = isShowDialog;
        this.mErrorListener = errorListener;
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_PROGRESS_DIALOG:

                    break;
                case DISMISS_PROGRESS_DIALOG:

                    break;
            }
        }
    };

    @Override
    public void onSubscribe(Disposable d) {
        if (isShowDialog) {
            mHandler.obtainMessage(SHOW_PROGRESS_DIALOG).sendToTarget();
        }
    }

    /**
     * 对错误进行统一处理
     * 隐藏ProgressDialog
     *
     * @param e
     */
    @Override
    public void onError(Throwable e) {
       Log.e("aaaaaaaa", "errorCode: " + e.toString());
        try {
            dismissProgressDialog();
            if (mErrorListener != null) {
                mErrorListener.onConnectError(e);
            }
        }catch (Exception e1){

        }
    }

    @Override
    public void onComplete() {
        if (isShowDialog) {
            mHandler.obtainMessage(DISMISS_PROGRESS_DIALOG).sendToTarget();
        }
    }

    /**
     * 将onNext方法中的返回结果交给Activity或Fragment自己处理
     *
     * @param responseObj 创建Subscriber时的泛型类型
     */
    @Override
    public void onNext(Object responseObj) {
        if (responseObj != null) {
            BasicResponse basicResponse = (BasicResponse) responseObj;
            if (basicResponse.isSuccess()) {
                if (mSubscriberOnNextListener != null) {
                    Object data = basicResponse.getResult();
                    mSubscriberOnNextListener.onNext(data, basicResponse.getMsg());
                }
            } else {
                //处理errorCode
                onServerError(basicResponse.getCode(), basicResponse.getMsg());
            }
        }
    }

    public void onServerError(int errorCode, String errorMsg) {
        try {
            if (mErrorListener != null) {
                mErrorListener.onServerError(errorCode, errorMsg);
            }
        } catch (Exception e) {

        }
    }

    /**
     * 取消ProgressDialog的时候，取消对observable的订阅，同时也取消了http请求
     */
    @Override
    public void onCancelProgress() {
        stop();
    }

    public void stop() {
        if (isShowDialog) {
            mHandler.obtainMessage(DISMISS_PROGRESS_DIALOG).sendToTarget();
        }
    }

    /**
     * Check if there is any connectivity
     *
     * @param context
     * @return
     */
    public boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    public void showProgressDialog() {
        if (isShowDialog) {
            mHandler.obtainMessage(SHOW_PROGRESS_DIALOG).sendToTarget();
        }
    }

    public void dismissProgressDialog() {
        if (isShowDialog) {
            mHandler.obtainMessage(DISMISS_PROGRESS_DIALOG).sendToTarget();
        }
    }

}