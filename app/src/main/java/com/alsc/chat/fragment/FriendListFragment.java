package com.alsc.chat.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alsc.chat.R;
import com.alsc.chat.activity.BaseActivity;
import com.alsc.chat.adapter.FriendAdapter;
import com.alsc.chat.bean.UserBean;
import com.alsc.chat.http.HttpMethods;
import com.alsc.chat.http.HttpObserver;
import com.alsc.chat.http.SubscriberOnNextListener;
import com.alsc.chat.manager.DataManager;
import com.alsc.chat.utils.Constants;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;

public class FriendListFragment extends BaseFragment {

    private FriendAdapter mAdapter;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_friend_list;
    }

    @Override
    protected void onViewCreated(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        getAdapter().bindToRecyclerView(recyclerView);
        getAdapter().setNewData(getNewList(DataManager.getInstance().getFriends()));
    }

    private FriendAdapter getAdapter() {
        if (mAdapter == null) {
            mAdapter = new FriendAdapter(getActivity());
            mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    if (position == 0) {
                        gotoPager(ApplyFragment.class);
                    } else if (position == 1) {
                        gotoPager(SearchContactFragment.class);
                    } else if (position == 2) {
                        gotoPager(GroupListFragment.class);
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Constants.BUNDLE_EXTRA, mAdapter.getItem(position));
                        gotoPager(UserInfoFragment.class, bundle);
                    }
                }
            });
        }
        return mAdapter;
    }

    public void onResume() {
        super.onResume();
        if (getAdapter().getItemCount() <= 3) {
            getFriendFromServer();
        }
    }

    @Override
    public void updateUIText() {

    }

    @Override
    public void onClick(View v) {

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
                if (getView() != null) {
                    getAdapter().setNewData(getNewList(list));
                }
            }
        }, getActivity(), (BaseActivity) getActivity()));
    }

    private ArrayList<UserBean> getNewList(ArrayList<UserBean> list) {
        ArrayList<UserBean> newList = new ArrayList<>();
        newList.add(null);
        newList.add(null);
        newList.add(null);
        if (list != null) {
            newList.addAll(list);
        }
        return newList;
    }
}
