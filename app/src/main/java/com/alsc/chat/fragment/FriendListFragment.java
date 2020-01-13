package com.alsc.chat.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alsc.chat.R;
import com.alsc.chat.activity.BaseActivity;
import com.alsc.chat.adapter.FriendAdapter;
import com.alsc.chat.bean.FriendItem;
import com.alsc.chat.bean.UserBean;
import com.alsc.chat.http.HttpMethods;
import com.alsc.chat.http.HttpObserver;
import com.alsc.chat.http.SubscriberOnNextListener;
import com.alsc.chat.manager.DataManager;
import com.alsc.chat.utils.Constants;
import com.alsc.chat.utils.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;

public class FriendListFragment extends BaseFragment {

    private FriendAdapter mAdapter;

    private ArrayList<UserBean> mFriendList;

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
        mFriendList = DataManager.getInstance().getFriends();
        getAdapter().setNewData(getNewList(mFriendList));
        if (mFriendList != null && !mFriendList.isEmpty()) {
            getFriendFromServer();
        }
    }

    private FriendAdapter getAdapter() {
        if (mAdapter == null) {
            mAdapter = new FriendAdapter(getActivity());
            mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    if (position == 0) {
                        //          gotoPager(ApplyFragment.class);
                    } else if (position == 1) {
            //            gotoPager(ApplyFragment.class);
                    } else if (position == 2) {
                        gotoPager(LabelFragment.class);
                    } else if (position == 3) {
                        gotoPager(GroupListFragment.class);
                    } else if (position == 4) {
              //          gotoPager(StarFriendFragment.class);
                    } else if (position > 5) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Constants.BUNDLE_EXTRA, mAdapter.getItem(position).getFriend());
                        gotoPager(UserInfoFragment.class, bundle);
                    }
                }
            });
        }
        return mAdapter;
    }

    public void onResume() {
        super.onResume();
        if (mFriendList == null || mFriendList.isEmpty()) {
            getFriendFromServer();
        }
    }

    @Override
    public void updateUIText() {
        UserBean userBean = DataManager.getInstance().getUser();
        if (userBean == null) {
            return;
        }
        Utils.loadImage(getActivity(),0,userBean.getAvatarUrl(),(ImageView)fv(R.id.ivMyAvatar));
        setText(R.id.tvMyName,userBean.getNickName());
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
                mFriendList = list;
                if (getView() != null) {
                    getAdapter().setNewData(getNewList(list));
                }
            }
        }, getActivity(), (BaseActivity) getActivity()));
    }

    private ArrayList<FriendItem> getNewList(ArrayList<UserBean> list) {
        ArrayList<FriendItem> newList = new ArrayList<>();
        FriendItem item = new FriendItem();
        item.setItemType(FriendItem.VIEW_TYPE_0);
        newList.add(item);
        item = new FriendItem();
        item.setItemType(FriendItem.VIEW_TYPE_1);
        item.setIconRes(R.drawable.chat_new_friend);
        item.setName(getString(R.string.chat_new_friend));
        newList.add(item);
        item = new FriendItem();
        item.setItemType(FriendItem.VIEW_TYPE_1);
        item.setIconRes(R.drawable.chat_label);
        item.setName(getString(R.string.chat_label));
        newList.add(item);
        item = new FriendItem();
        item.setItemType(FriendItem.VIEW_TYPE_1);
        item.setIconRes(R.drawable.chat_my_chat_group);
        item.setName(getString(R.string.chat_my_chat_groups));
        newList.add(item);
        item = new FriendItem();
        item.setItemType(FriendItem.VIEW_TYPE_1);
        item.setIconRes(R.drawable.chat_star);
        item.setName(getString(R.string.chat_star_friends));
        newList.add(item);
        item = new FriendItem();
        item.setItemType(FriendItem.VIEW_TYPE_2);
        newList.add(item);
        if (list != null) {
            for (UserBean bean : list) {
                item = new FriendItem();
                item.setItemType(FriendItem.VIEW_TYPE_3);
                item.setFriend(bean);
                newList.add(item);
            }
        }
        return newList;
    }
}
