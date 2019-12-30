package com.alsc.chat.fragment;

import android.text.TextUtils;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alsc.chat.R;
import com.alsc.chat.adapter.MessageAdapter;
import com.alsc.chat.bean.MessageBean;
import com.alsc.chat.bean.UserBean;
import com.alsc.chat.manager.DataManager;
import com.alsc.chat.utils.Constants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        getAdapter().bindToRecyclerView(recyclerView);
        setViewsOnClickListener(R.id.btnSend);
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
            msg.setContent(text);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveMsg(MessageBean message) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
