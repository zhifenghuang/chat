package com.alsc.chat.http;

/**
 * Created by gigabud on 17-12-22.
 */

import android.text.TextUtils;

import com.alsc.chat.bean.BasicResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpClientManager {
    private static OkHttpClientManager mInstance;
    private OkHttpClient mOkHttpClient;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClientManager() {
        mOkHttpClient = new OkHttpClient();
    }

    public static OkHttpClientManager getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpClientManager.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpClientManager();
                }
            }
        }
        return mInstance;
    }

    public void okhttpPost(String url, String json, final Callback callback) {
        RequestBody body = RequestBody.create(JSON, json);
        final Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        final Call call = mOkHttpClient.newCall(request);
        call.enqueue(callback);
    }

    public void okhttpGet(String url, final Callback callback) {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        final Call call = mOkHttpClient.newCall(request);
        call.enqueue(callback);
    }


    public void getServerUrl(String url) {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        final Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    if (response.body() == null) {
                        return;
                    }
                    String resStr = response.body().string();
                    if (TextUtils.isEmpty(resStr)) {
                        return;
                    }
                    BasicResponse<HashMap<String, String>> basicResponse = new Gson().fromJson(resStr, new TypeToken<BasicResponse<HashMap<String, String>>>() {
                    }.getType());
                    HashMap<String, String> map = basicResponse.getResult();
                    if (map == null) {
                        return;
                    }

                } catch (Exception e) {

                }
            }
        });
    }


}
