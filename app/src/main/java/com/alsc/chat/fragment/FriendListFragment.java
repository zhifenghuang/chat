package com.alsc.chat.fragment;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.alsc.chat.R;
import com.alsc.chat.activity.BaseActivity;
import com.alsc.chat.adapter.FriendAdapter;
import com.alsc.chat.bean.UserBean;
import com.alsc.chat.http.HttpMethods;
import com.alsc.chat.http.HttpObserver;
import com.alsc.chat.http.SubscriberOnNextListener;
import com.alsc.chat.manager.DataManager;

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
        getAdapter().bindToRecyclerView(recyclerView);
        getAdapter().setNewData(DataManager.getInstance().getFriends());
    }

    private FriendAdapter getAdapter() {
        if (mAdapter == null) {
            mAdapter = new FriendAdapter();
        }
        return mAdapter;
    }

    public void onResume() {
        super.onResume();
        if(getAdapter().getItemCount()==0) {
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
        HttpMethods.getInstance().getFriends(new HttpObserver(new SubscriberOnNextListener<ArrayList<UserBean>>() {
            @Override
            public void onNext(ArrayList<UserBean> list, String msg) {
                if (getView() != null) {
                    getAdapter().setNewData(list);
                }
                DataManager.getInstance().saveFriends(list);
            }
        }, getActivity(), (BaseActivity) getActivity()));
    }
}
