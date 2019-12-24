package com.alsc.chat.http;


import com.alsc.chat.bean.BasicResponse;
import com.alsc.chat.bean.UserBean;

import java.util.ArrayList;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface HttpService {

    @Multipart
    @POST("api/v1/passport/login")
    Observable<BasicResponse<UserBean>> login(@Part("loginAccount") RequestBody loginAccount, @Part("loginPassword") RequestBody loginPassword);


    @POST("api/v1/contact/list")
    Observable<BasicResponse<ArrayList<UserBean>>> getFriends();

    @POST("api/v1/contact/add")
    Observable<BasicResponse<UserBean>> addContact();

    @POST("api/v1/contact/search")
    Observable<BasicResponse<UserBean>> searchContact();

    @POST("api/v1/contact/reply")
    Observable<BasicResponse<UserBean>> replayContact();

    @POST("api/v1/contact/review")
    Observable<BasicResponse<UserBean>> reviewContact();
}
