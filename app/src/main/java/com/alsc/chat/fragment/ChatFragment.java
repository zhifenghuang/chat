package com.alsc.chat.fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alsc.chat.R;
import com.alsc.chat.activity.BaseActivity;
import com.alsc.chat.adapter.MessageAdapter;
import com.alsc.chat.bean.FileBean;
import com.alsc.chat.bean.MessageBean;
import com.alsc.chat.bean.UserBean;
import com.alsc.chat.db.DatabaseOperate;
import com.alsc.chat.manager.DataManager;
import com.alsc.chat.utils.Constants;
import com.google.gson.Gson;
import com.upyun.library.common.Params;
import com.upyun.library.common.UploadEngine;
import com.upyun.library.listener.UpCompleteListener;
import com.upyun.library.listener.UpProgressListener;
import com.upyun.library.utils.UpYunUtils;
import com.zhangke.websocket.WebSocketHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import okhttp3.Response;

public class ChatFragment extends BaseFragment {

    private static final String TAG = "Chat";

    private MessageAdapter mAdapter;
    private UserBean mMyInfo;
    private UserBean mChatUser;

    protected boolean mHasMore;
    protected long mLastMsgTime;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_chat;
    }

    @Override
    protected void onViewCreated(View view) {
        EventBus.getDefault().register(this);
        mMyInfo = DataManager.getInstance().getUser();
        mChatUser = (UserBean) getArguments().getSerializable(Constants.BUNDLE_EXTRA);
        setText(R.id.tvLeft, mChatUser.getNickName());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);
        getAdapter().bindToRecyclerView(recyclerView);
        init(view);
    }

    protected void init(View view) {
        setViewVisible(R.id.tvLeft, R.id.ivRight);
        setImage(R.id.ivRight, R.drawable.chat_more_hor);
        setViewsOnClickListener(R.id.ivSend, R.id.ivRight, R.id.ivVoice, R.id.ivAdd);
        initMsgs();
        initEvent();
    }

    private void initEvent() {
        ((EditText) getView().findViewById(R.id.etChat)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fv(R.id.ivSend).setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    protected void initMsgs() {
        ArrayList<MessageBean> list = DatabaseOperate.getInstance().getUserChatMsg(mMyInfo.getUserId(), mChatUser.getContactId(), 0l, Constants.PAGE_NUM);
        Collections.reverse(list);
        int size = list.size();
        mHasMore = (size == Constants.PAGE_NUM);
        if (size > 0) {
            mLastMsgTime = list.get(0).getCreateTime();
        }
        getAdapter().setNewData(list);
        scrollBottom();

        if (!mHasMore) {
            return;
        }

        final RecyclerView recyclerView = fv(R.id.recyclerView);
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                final LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                    }

                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (mHasMore && manager.findFirstVisibleItemPosition() == 0) {
                            getMoreMessage();
                        }
                    }
                });
            }
        }, 200);

    }

    protected void getMoreMessage() {
        if (mHasMore) {
            ArrayList<MessageBean> list = DatabaseOperate.getInstance().getUserChatMsg(mMyInfo.getUserId(), mChatUser.getContactId(),
                    mLastMsgTime, Constants.PAGE_NUM);
            getAdapter().addData(0, list);
            //    int size = list.size();
            mHasMore = false;//(size == Constants.PAGE_NUM);
//            if (size > 0) {
//                mLastMsgTime = list.get(size - 1).getCreateTime();
//                getAdapter().addData(0, list);
//            }
        }

    }

    private MessageAdapter getAdapter() {
        if (mAdapter == null) {
            mAdapter = new MessageAdapter(getActivity(), mMyInfo, mChatUser);
        }
        return mAdapter;
    }

    @Override
    public void updateUIText() {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ivSend) {
            String text = getTextById(R.id.etChat);
            if (TextUtils.isEmpty(text.trim())) {
                return;
            }
            MessageBean msg = new MessageBean();
            msg.setCmd(2000);
            msg.setFromId(mMyInfo.getUserId());
            msg.setToId(mChatUser.getContactId());
            msg.setMsgType(1);
            msg.setContent(text);
            WebSocketHandler.getDefault().send(msg.toJson());
            setText(R.id.etChat, "");
            DatabaseOperate.getInstance().insert(msg);
            EventBus.getDefault().post(msg);
        } else if (id == R.id.ivRight) {

        } else if (id == R.id.ivAdd) {
            ((BaseActivity) getActivity()).showSelectPhotoTypeDialog();
        }
    }

    protected void sendImageMsg(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        MessageBean msg = new MessageBean();
        msg.setCmd(2000);
        msg.setFromId(mMyInfo.getUserId());
        msg.setToId(mChatUser.getContactId());
        msg.setMsgType(2);
        msg.setUrl(url);
        WebSocketHandler.getDefault().send(msg.toJson());
        setText(R.id.etChat, "");
        DatabaseOperate.getInstance().insert(msg);
        EventBus.getDefault().post(msg);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveMsg(MessageBean message) {
        if (getView() != null && message != null) {
            getAdapter().addData(message);
            scrollBottom();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveFile(FileBean fileBean) {
        if (getView() != null && fileBean != null) {
            formUpload(fileBean);
        }
    }

    private void scrollBottom() {
        if (getView() != null && getAdapter().getItemCount() > 0) {
            final RecyclerView recyclerView = getView().findViewById(R.id.recyclerView);
            recyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    recyclerView.smoothScrollToPosition(getAdapter().getItemCount() - 1);
                }
            }, 100);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }


    private void formUpload(FileBean fileBean) {
        File file = fileBean.getFile();
        if (file == null || !file.exists()) {
            return;
        }
        final Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put(Params.BUCKET, Constants.SPACE);
        paramsMap.put(Params.SAVE_KEY, String.format(Constants.IMAGE_SAVE_PATH, file.getName()));
        paramsMap.put(Params.CONTENT_LENGTH, file.length());
        paramsMap.put(Params.RETURN_URL, "httpbin.org/post");
        UpProgressListener progressListener = new UpProgressListener() {
            @Override
            public void onRequestProgress(long bytesWrite, long contentLength) {
                //            bp_form.setProgress((int) ((100 * bytesWrite) / contentLength));
            }
        };

        //结束回调，不可为空
        UpCompleteListener completeListener = new UpCompleteListener() {
            @Override
            public void onComplete(boolean isSuccess, Response response, Exception error) {
                try {
                    String result = null;
                    if (response != null) {
                        result = response.body().string();
                    } else if (error != null) {
                        result = error.toString();
                    }
                    if (isSuccess) {
                        try {
                            JSONObject object = new JSONObject(result);
                            sendImageMsg(object.optString("url"));
                        } catch (Exception e) {

                        }
                    }
                    Log.e("aaaaaaaaaa", "isSuccess:" + isSuccess + " result:" + result);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };
        UploadEngine.getInstance().formUpload(file, paramsMap, Constants.OPERATER, UpYunUtils.md5(Constants.PASSWORD), completeListener, progressListener);
    }
}
