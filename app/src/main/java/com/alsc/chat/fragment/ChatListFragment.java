package com.alsc.chat.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alsc.chat.R;
import com.alsc.chat.activity.BaseActivity;
import com.alsc.chat.adapter.ChatUserAdapter;
import com.alsc.chat.bean.MessageBean;
import com.alsc.chat.bean.UserBean;
import com.alsc.chat.db.DatabaseOperate;
import com.alsc.chat.http.HttpMethods;
import com.alsc.chat.http.HttpObserver;
import com.alsc.chat.http.SubscriberOnNextListener;
import com.alsc.chat.manager.DataManager;
import com.alsc.chat.utils.Constants;
import com.chad.library.adapter.base.BaseQuickAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class ChatListFragment extends BaseFragment {

    ArrayList<ChatUser> mChatUserList;
    private ChatUserAdapter mAdapter;
    private ArrayList<UserBean> mFriendList;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_chat_list;
    }

    @Override
    protected void onViewCreated(View view) {
        EventBus.getDefault().register(this);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        getAdapter().bindToRecyclerView(recyclerView);
        mChatUserList = new ArrayList<>();
        mFriendList = DataManager.getInstance().getFriends();
    }

    private ChatUserAdapter getAdapter() {
        if (mAdapter == null) {
            mAdapter = new ChatUserAdapter(getActivity());
            mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constants.BUNDLE_EXTRA, mAdapter.getItem(position).chatUser);
                    gotoPager(ChatFragment.class, bundle);
                }
            });
        }
        return mAdapter;
    }

    private void getChatUserList() {
        UserBean myInfo = DataManager.getInstance().getUser();
        mChatUserList = new ArrayList<>();
        ArrayList<MessageBean> list = DatabaseOperate.getInstance().getUserChatList(myInfo.getUserId());
        if (list != null && !list.isEmpty()) {
            for (MessageBean bean : list) {
                for (UserBean friend : mFriendList) {
                    if (friend.getContactId() == bean.getToId()
                            || friend.getContactId() == bean.getFromId()) {
                        ChatUser chatUser = new ChatUser();
                        chatUser.chatUser = friend;
                        chatUser.lastMsg = bean;
                        mChatUserList.add(chatUser);
                        break;
                    }
                }
            }
        }
        getAdapter().setNewData(mChatUserList);
    }

    @Override
    public void updateUIText() {

    }

    @Override
    public void onClick(View v) {

    }

    public void onResume() {
        super.onResume();
        if (mFriendList != null && !mFriendList.isEmpty()) {
            getChatUserList();
        } else {
            getFriendFromServer();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveMsg(MessageBean message) {
        if (getView() != null && message != null) {
            for (ChatUser user : mChatUserList) {
                if (user.chatUser.getContactId() == message.getFromId()
                        || user.chatUser.getContactId() == message.getToId()) {
                    user.lastMsg = message;
                    getAdapter().notifyDataSetChanged();
                    return;
                }
            }
            getChatUserList();
        }
    }

    public static class ChatUser {
        public UserBean chatUser;
        public MessageBean lastMsg;
    }

    private void getFriendFromServer() {
        UserBean userBean = DataManager.getInstance().getUser();
        if (userBean == null) {
            return;
        }
        HttpMethods.getInstance().getFriends(new HttpObserver(new SubscriberOnNextListener<ArrayList<UserBean>>() {
            @Override
            public void onNext(ArrayList<UserBean> list, String msg) {
                DataManager.getInstance().saveFriends(list);
                mFriendList = list;
                if (getView() != null && mFriendList != null && !mFriendList.isEmpty()) {
                    getChatUserList();
                }
            }
        }, getActivity(), (BaseActivity) getActivity()));
    }
}
