package com.alsc.chat.http;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.alsc.chat.bean.UserBean;
import com.alsc.chat.manager.DataManager;
import com.alsc.chat.utils.Constants;
import com.alsc.chat.utils.NetUtil;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import org.apache.http.conn.ConnectTimeoutException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
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
        return Constants.BASE_URL + "/";
    }

    /**
     * 登录
     *
     * @param account  账号
     * @param password 密码
     * @param observer
     */
    public void login(String account, String password, HttpObserver observer) {
        HttpService httpService = mRetrofit.create(HttpService.class);
        Observable observable = httpService.login(RequestBody.create(MediaType.parse("text/plain"), account)
                , RequestBody.create(MediaType.parse("text/plain"), password));
        toSubscribe(observable, observer);
    }

    /**
     * 联系人列表
     *
     * @param observer
     */
    public void getFriends(HttpObserver observer) {
        HttpService httpService = mRetrofit.create(HttpService.class);
        Observable observable = httpService.getFriends();
        toSubscribe(observable, observer);
    }


    /**
     * 申请添加联系人
     *
     * @param contactId 添加的联系人Id
     * @param memo      备注
     * @param remark    申请留言
     * @param observer
     */
    public void addContact(String contactId, String memo, String remark, HttpObserver observer) {
        HttpService httpService = mRetrofit.create(HttpService.class);
        Observable observable = httpService.addContact(RequestBody.create(MediaType.parse("text/plain"), contactId)
                , RequestBody.create(MediaType.parse("text/plain"), memo)
                , RequestBody.create(MediaType.parse("text/plain"), remark));
        toSubscribe(observable, observer);
    }

    /**
     * 查找指定联系人
     *
     * @param mobile   用户名
     * @param observer
     */
    public void searchContact(String mobile, HttpObserver observer) {
        HttpService httpService = mRetrofit.create(HttpService.class);
        Observable observable = httpService.searchContact(RequestBody.create(MediaType.parse("text/plain"), mobile));
        toSubscribe(observable, observer);
    }

    /**
     * 同意或拒绝添加联系人
     *
     * @param contactId 申请用户ID
     * @param status    1同意2拒绝3忽略
     * @param memo      备注信息
     * @param observer
     */
    public void replayContact(String contactId, String status, String memo, HttpObserver observer) {
        HttpService httpService = mRetrofit.create(HttpService.class);
        Observable observable = httpService.replayContact(RequestBody.create(MediaType.parse("text/plain"), contactId)
                , RequestBody.create(MediaType.parse("text/plain"), status)
                , RequestBody.create(MediaType.parse("text/plain"), memo));
        toSubscribe(observable, observer);
    }

    /**
     * 待处理联系人申请列表
     *
     * @param observer
     */
    public void reviewContact(HttpObserver observer) {
        HttpService httpService = mRetrofit.create(HttpService.class);
        Observable observable = httpService.reviewContact();
        toSubscribe(observable, observer);
    }

    /**
     * 退出群
     *
     * @param groupId  群ID
     * @param observer
     */
    public void exitGroup(String groupId, HttpObserver observer) {
        HttpService httpService = mRetrofit.create(HttpService.class);
        Observable observable = httpService.exitGroup(RequestBody.create(MediaType.parse("text/plain"), groupId));
        toSubscribe(observable, observer);
    }

    /**
     * 获取群好友列表
     *
     * @param contactId 群ID
     * @param observer
     */
    public void getGroupUsers(String contactId, HttpObserver observer) {
        HttpService httpService = mRetrofit.create(HttpService.class);
        Observable observable = httpService.getGroupUsers(RequestBody.create(MediaType.parse("text/plain"), contactId));
        toSubscribe(observable, observer);
    }

    /**
     * 修改群聊信息
     *
     * @param contactId    群ID
     * @param name         群名称
     * @param ownerId      群所有者ID（默认0为不修改)
     * @param notice       群公告
     * @param introduction 群介绍
     * @param observer
     */
    public void updateGroup(String contactId, String name, String ownerId, String notice, String introduction, HttpObserver observer) {
        HttpService httpService = mRetrofit.create(HttpService.class);
        Observable observable = httpService.updateGroup(RequestBody.create(MediaType.parse("text/plain"), contactId)
                , RequestBody.create(MediaType.parse("text/plain"), name)
                , RequestBody.create(MediaType.parse("text/plain"), ownerId)
                , RequestBody.create(MediaType.parse("text/plain"), notice)
                , RequestBody.create(MediaType.parse("text/plain"), introduction));
        toSubscribe(observable, observer);
    }

    /**
     * 创建群聊
     *
     * @param name     群聊名称
     * @param users
     * @param observer
     */
    public void createGroup(String name, ArrayList<Long> users, HttpObserver observer) {
        HttpService httpService = mRetrofit.create(HttpService.class);
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("userIds", users);
        Observable observable = httpService.createGroup(map);
        toSubscribe(observable, observer);
    }


    private <T> void toSubscribe(Observable<T> o, HttpObserver s) {
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
