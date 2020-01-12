package com.alsc.chat.fragment;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alsc.chat.R;
import com.alsc.chat.adapter.SelectFriendAdapter;
import com.alsc.chat.manager.DataManager;

public class SelectFriendFragment extends BaseFragment {

    private SelectFriendAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_select_friend;
    }

    @Override
    protected void onViewCreated(View view) {
        setViewVisible(R.id.tvLeft, R.id.btnRight);
        setText(R.id.tvLeft,R.string.chat_select_friend);
        setText(R.id.btnRight, getString(R.string.chat_ok_1, "0"));
        setBackground(R.id.btnRight, R.drawable.bg_chat_add_label);
        setViewsOnClickListener(R.id.btnRight);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        getAdapter().bindToRecyclerView(recyclerView);
        getAdapter().setNewData(DataManager.getInstance().getFriends());
    }

    private SelectFriendAdapter getAdapter() {
        if (mAdapter == null) {
            mAdapter = new SelectFriendAdapter(getActivity());
        }
        return mAdapter;
    }

    @Override
    public void updateUIText() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRight:
//                String groupName = getTextById(R.id.etName).trim();
//                if (TextUtils.isEmpty(groupName)) {
//                    return;
//                }
//                ArrayList<Long> list = getAdapter().getSelectUsers();
//                if (list.isEmpty()) {
//                    return;
//                }
//                list.add(DataManager.getInstance().getUser().getUserId());
//                HttpMethods.getInstance().createGroup(groupName, list, new HttpObserver(new SubscriberOnNextListener() {
//                    @Override
//                    public void onNext(Object o, String msg) {
//                        goBack();
//                    }
//                }, getActivity(), (BaseActivity) getActivity()));
                break;
        }
    }
}
