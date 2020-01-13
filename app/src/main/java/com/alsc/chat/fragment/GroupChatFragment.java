package com.alsc.chat.fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alsc.chat.R;
import com.alsc.chat.adapter.GroupMessageAdapter;
import com.alsc.chat.bean.GroupBean;
import com.alsc.chat.bean.GroupMessageBean;
import com.alsc.chat.bean.MessageBean;
import com.alsc.chat.bean.UserBean;
import com.alsc.chat.db.DatabaseOperate;
import com.alsc.chat.manager.DataManager;
import com.alsc.chat.utils.Constants;
import com.zhangke.websocket.WebSocketHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;

public class GroupChatFragment extends ChatFragment {

    private GroupMessageAdapter mAdapter;
    private UserBean mMyInfo;
    private GroupBean mGroup;
    private ArrayList<UserBean> mUsers;


    @Override
    protected void onViewCreated(View view) {
        EventBus.getDefault().register(this);
        mMyInfo = DataManager.getInstance().getUser();
        mGroup = (GroupBean) getArguments().getSerializable(Constants.BUNDLE_EXTRA);
        setText(R.id.tvLeft, mGroup.getName());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);
        getAdapter().bindToRecyclerView(recyclerView);
        init(view);
    }

    @Override
    public void initMsgs() {
        ArrayList<GroupMessageBean> list = DatabaseOperate.getInstance().getGroupMsg(mMyInfo.getUserId(), mGroup.getGroupId(), 0l, Constants.PAGE_NUM);
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

    @Override
    public void getMoreMessage() {
        if (mHasMore) {
            ArrayList<GroupMessageBean> list = DatabaseOperate.getInstance().getGroupMsg(mMyInfo.getUserId(), mGroup.getGroupId(), mLastMsgTime, Constants.PAGE_NUM);
//            int size = list.size();
            getAdapter().addData(0, list);
            mHasMore = false;//(size == Constants.PAGE_NUM);
//            if (size > 0) {
//                mLastMsgTime = list.get(size - 1).getCreateTime();
//                getAdapter().addData(0, list);
//            }
        }

    }

    private GroupMessageAdapter getAdapter() {
        if (mAdapter == null) {
            mAdapter = new GroupMessageAdapter(getActivity(), mMyInfo, mUsers);
        }
        return mAdapter;
    }

    @Override
    public void updateUIText() {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivSend) {
            String text = getTextById(R.id.etChat);
            if (TextUtils.isEmpty(text.trim())) {
                return;
            }
            GroupMessageBean msg = new GroupMessageBean();
            msg.setCmd(2100);
            msg.setFromId(mMyInfo.getUserId());
            msg.setGroupId(mGroup.getGroupId());
            msg.setMsgType(1);
            msg.setContent(text);
            WebSocketHandler.getDefault().send(msg.toJson());
            setText(R.id.etChat, "");
            DatabaseOperate.getInstance().insert(msg);
            EventBus.getDefault().post(msg);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveMsg(GroupMessageBean message) {
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
