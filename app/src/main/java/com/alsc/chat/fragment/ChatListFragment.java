package com.alsc.chat.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alsc.chat.R;
import com.alsc.chat.activity.BaseActivity;
import com.alsc.chat.adapter.ChatUserAdapter;
import com.alsc.chat.bean.GroupBean;
import com.alsc.chat.bean.GroupMessageBean;
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

    private ArrayList<ChatBean> mChatList;
    private ChatUserAdapter mAdapter;
    private ArrayList<UserBean> mFriendList;
    private ArrayList<GroupBean> mGroupList;

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
        mChatList = new ArrayList<>();
        mFriendList = DataManager.getInstance().getFriends();
        mGroupList = DataManager.getInstance().getGroups();
    }

    private ChatUserAdapter getAdapter() {
        if (mAdapter == null) {
            mAdapter = new ChatUserAdapter(getActivity());
            mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    Bundle bundle = new Bundle();
                    ChatBean bean = getAdapter().getItem(position);
                    if (bean.chatUser != null) {
                        bundle.putSerializable(Constants.BUNDLE_EXTRA, bean.chatUser);
                        gotoPager(ChatFragment.class, bundle);
                    } else {
                        bundle.putSerializable(Constants.BUNDLE_EXTRA, bean.group);
                        gotoPager(GroupChatFragment.class, bundle);
                    }
                }
            });
        }
        return mAdapter;
    }

    private void initChatList() {
        mChatList.clear();
        UserBean myInfo = DataManager.getInstance().getUser();
        ArrayList<MessageBean> list = DatabaseOperate.getInstance().getUserChatList(myInfo.getUserId());
        if (list != null && !list.isEmpty()) {
            for (MessageBean bean : list) {
                for (UserBean friend : mFriendList) {
                    if (friend.getContactId() == bean.getToId()
                            || friend.getContactId() == bean.getFromId()) {
                        ChatBean chatBean = new ChatBean();
                        chatBean.chatUser = friend;
                        chatBean.lastMsg = bean;
                        mChatList.add(chatBean);
                        break;
                    }
                }
            }
        }
        initGroupChatList(myInfo.getUserId());
        getAdapter().setNewData(mChatList);
    }

    private void initGroupChatList(long myId) {
        ArrayList<GroupMessageBean> list = DatabaseOperate.getInstance().getChatGroupList(myId);
        if (list != null && !list.isEmpty()) {
            for (GroupMessageBean bean : list) {
                for (GroupBean group : mGroupList) {
                    if (group.getGroupId() == bean.getGroupId()) {
                        ChatBean chatBean = new ChatBean();
                        chatBean.group = group;
                        chatBean.lastGroupMsg = bean;
                        mChatList.add(chatBean);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void updateUIText() {

    }

    @Override
    public void onClick(View v) {

    }

    public void onResume() {
        super.onResume();
        if (mFriendList == null || mFriendList.isEmpty()) {
            getFriendFromServer();
        }

        if (mGroupList == null || mGroupList.isEmpty()) {
            getGroupFromServer();
        }
        initChatList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveMsg(MessageBean message) {
        if (getView() != null && message != null) {
            for (ChatBean chatBean : mChatList) {
                if (chatBean.chatUser == null) {
                    continue;
                }
                if (chatBean.chatUser.getContactId() == message.getFromId()
                        || chatBean.chatUser.getContactId() == message.getToId()) {
                    chatBean.lastMsg = message;
                    mChatList.remove(chatBean);
                    mChatList.add(0, chatBean);
                    getAdapter().notifyDataSetChanged();
                    return;
                }
            }
            initChatList();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveMsg(GroupMessageBean message) {
        if (getView() != null && message != null) {
            for (ChatBean chatBean : mChatList) {
                if (chatBean.group == null) {
                    continue;
                }
                if (chatBean.group.getGroupId() == message.getGroupId()) {
                    chatBean.lastGroupMsg = message;
                    mChatList.remove(chatBean);
                    mChatList.add(0, chatBean);
                    getAdapter().notifyDataSetChanged();
                    return;
                }
            }
            initChatList();
        }
    }

    public static class ChatBean {
        public UserBean chatUser;
        public GroupBean group;
        public MessageBean lastMsg;
        public GroupMessageBean lastGroupMsg;
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
                    initChatList();
                }
            }
        }, getActivity(), (BaseActivity) getActivity()));
    }

    private void getGroupFromServer() {
        UserBean userBean = DataManager.getInstance().getUser();
        if (userBean == null) {
            return;
        }
        HttpMethods.getInstance().getGroups(1, 20,
                new HttpObserver(new SubscriberOnNextListener<ArrayList<GroupBean>>() {
                    @Override
                    public void onNext(ArrayList<GroupBean> list, String msg) {
                        DataManager.getInstance().saveGroups(list);
                        mGroupList = list;
                        if (getView() != null && mGroupList != null && !mGroupList.isEmpty()) {
                            initChatList();
                        }
                    }
                }, getActivity(), (BaseActivity) getActivity()));
    }
}
