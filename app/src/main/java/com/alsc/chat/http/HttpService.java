package com.alsc.chat.http;


import com.alsc.chat.bean.BasicResponse;
import com.alsc.chat.bean.UserBean;

import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface HttpService {

    @Multipart
    @POST("api/v1/passport/login")
    Observable<BasicResponse<UserBean>> login(@Part("loginAccount") RequestBody loginAccount,
                                              @Part("loginPassword") RequestBody loginPassword);

    @POST("api/v1/contact/list")
    Observable<BasicResponse<ArrayList<UserBean>>> getFriends();


    @Multipart
    @POST("api/v1/contact/add")
    Observable<BasicResponse<UserBean>> addContact(@Part("contactId") RequestBody contactId,
                                                   @Part("remark") RequestBody remark,
                                                   @Part("memo") RequestBody memo);

    @Multipart
    @POST("api/v1/contact/search")
    Observable<BasicResponse<UserBean>> searchContact(@Part("mobile") RequestBody mobile);

    @Multipart
    @POST("api/v1/contact/reply")
    Observable<BasicResponse<UserBean>> replayContact(@Part("contactId") RequestBody contactId,
                                                      @Part("status") RequestBody status,
                                                      @Part("memo") RequestBody memo);

    @POST("api/v1/contact/review")
    Observable<BasicResponse<ArrayList<UserBean>>> reviewContact();

    /*
    退出群
     */
    @Multipart
    @POST("api/v1/group/exitGroup")
    Observable<BasicResponse> exitGroup(@Part("groupId") RequestBody groupId);

    /*
    获取群好友列表
     */
    @Multipart
    @POST("api/v1/group/getGroupUsers")
    Observable<BasicResponse<UserBean>> getGroupUsers(@Part("groupId") RequestBody groupId);

    /*
    修改群聊信息
    */
    @Multipart
    @POST("api/v1/group/updateGroup")
    Observable<BasicResponse> updateGroup(@Part("groupId") RequestBody groupId,
                                                    @Part("name") RequestBody name,
                                                    @Part("ownerId") RequestBody ownerId,
                                                    @Part("notice") RequestBody notice,
                                                    @Part("introduction") RequestBody introduction);

    /*
    创建群
    */
    @POST("api/v1/group/create")
    Observable<BasicResponse<UserBean>> createGroup(@Body HashMap<String,Object> map);
}
