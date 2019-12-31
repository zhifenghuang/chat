package com.alsc.chat.fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alsc.chat.R;
import com.alsc.chat.adapter.MessageAdapter;
import com.alsc.chat.bean.MessageBean;
import com.alsc.chat.bean.UserBean;
import com.alsc.chat.db.DatabaseOperate;
import com.alsc.chat.manager.DataManager;
import com.alsc.chat.utils.Constants;
import com.google.gson.Gson;
import com.zhangke.websocket.WebSocketHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatFragment extends BaseFragment {

    private MessageAdapter mAdapter;
    private UserBean mMyInfo;
    private UserBean mChatUser;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_chat;
    }

    @Override
    protected void onViewCreated(View view) {
        EventBus.getDefault().register(this);
        mMyInfo = DataManager.getInstance().getUser();
        mChatUser = (UserBean) getArguments().getSerializable(Constants.BUNDLE_EXTRA);

        setText(R.id.tvTitle, mChatUser.getNickName());
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        getAdapter().bindToRecyclerView(recyclerView);
        setViewsOnClickListener(R.id.btnSend);
        initMsgs();
    }

    private void initMsgs() {
        ArrayList<MessageBean> list = DatabaseOperate.getInstance().getUserChatMsg(mMyInfo.getUserId(), mChatUser.getContactId());
        getAdapter().setNewData(list);
        scrollBottom();
//        if(list==null || list.isEmpty()){
//            return;
//        }
//        ArrayList<String> msgIds=new ArrayList<>();
//        for(MessageBean bean:list){
//            if(bean.getReceiveStatus()==0){
//                msgIds.add(bean.getMessageId());
//                bean.sureReceiveStatus();
//            }
//        }
//        HashMap<String,Object> map=new HashMap<>();
//        map.put("cmd",2001);
//        map.put("messageId",msgIds);
//        Log.e("aaaaaaaa",new Gson().toJson(map));
//        WebSocketHandler.getDefault().send(new Gson().toJson(map));
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
        if (v.getId() == R.id.btnSend) {
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
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveMsg(MessageBean message) {
        if (getView() != null && message != null) {
            getAdapter().addData(message);
            scrollBottom();
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
}
