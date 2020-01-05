package com.alsc.chat.fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

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

public class GroupChatFragment extends BaseFragment {

    private GroupMessageAdapter mAdapter;
    private UserBean mMyInfo;
    private GroupBean mGroup;
    private ArrayList<UserBean> mUsers;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_group_chat;
    }

    @Override
    protected void onViewCreated(View view) {
        EventBus.getDefault().register(this);
        mMyInfo = DataManager.getInstance().getUser();
        mGroup = (GroupBean) getArguments().getSerializable(Constants.BUNDLE_EXTRA);

        setText(R.id.tvTitle, mGroup.getName());
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        getAdapter().bindToRecyclerView(recyclerView);
        setViewsOnClickListener(R.id.btnSend);
        initMsgs();
    }

    private void initMsgs() {
        ArrayList<GroupMessageBean> list = DatabaseOperate.getInstance().getGroupMsg(mMyInfo.getUserId(), mGroup.getGroupId());
        getAdapter().setNewData(list);
        scrollBottom();
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
        if (v.getId() == R.id.btnSend) {
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
