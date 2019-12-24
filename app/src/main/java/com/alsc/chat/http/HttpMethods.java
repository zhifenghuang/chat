package com.alsc.chat.http;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.alsc.chat.manager.DataManager;
import com.alsc.chat.utils.NetUtil;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import org.apache.http.conn.ConnectTimeoutException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by gigabud on 17-5-3.
 */

public class HttpMethods {

    private static final String TAG = "HttpMethods";
    private Retrofit mRetrofit;
    private static final int DEFAULT_TIMEOUT = 10;
    private static HttpMethods INSTANCE;

    private Context mContext;

    private HttpMethods() {
        //手动创建一个OkHttpClient并设置超时时间
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.i(TAG, message);
            }
        });


        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request request;
                if (!TextUtils.isEmpty(DataManager.getInstance().getToken())) {
                    Log.e("aaaaaaaa", "token: " + DataManager.getInstance().getToken());
                    request = chain.request()
                            .newBuilder()
                            .addHeader("Authorization", DataManager.getInstance().getToken())
                            .build();
                } else {
                    request = chain.request()
                            .newBuilder()
                            .build();
                }
                return chain.proceed(request);
            }
        };

        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .addInterceptor(loggingInterceptor);
        mRetrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
                        .setLenient()
                        .create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(getBaseUrl())
                .build();
    }

    public static HttpMethods getInstance() {
        if (INSTANCE == null) {
            synchronized (TAG) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpMethods();
                }
            }
        }
        return INSTANCE;
    }

    public void setContext(Context context) {
        if (mContext == null) {
            mContext = context.getApplicationContext();
        }
    }

    private String getBaseUrl() {
        return "http://118.178.16.240/";
    }

    public void login(String account, String password, HttpObserver observer) {
        HttpService httpService = mRetrofit.create(HttpService.class);
        Observable observable = httpService.login(RequestBody.create(MediaType.parse("text/plain"), account)
                , RequestBody.create(MediaType.parse("text/plain"), password));
        toSubscribe(observable, observer);
    }

    public void getFriends(HttpObserver observer) {
        HttpService httpService = mRetrofit.create(HttpService.class);
        Observable observable = httpService.getFriends();
        toSubscribe(observable, observer);
    }


    private <T> void toSubscribe(Observable<T> o, HttpObserver<T> s) {
        o.retry(2, new Predicate<Throwable>() {
            @Override
            public boolean test(@NonNull Throwable throwable) throws Exception {
                return NetUtil.isConnected(mContext) &&
                        (throwable instanceof SocketTimeoutException ||
                                throwable instanceof ConnectException ||
                                throwable instanceof ConnectTimeoutException ||
                                throwable instanceof TimeoutException);
            }
        }).subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }

}
