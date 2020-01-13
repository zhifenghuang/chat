package com.alsc.chat.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alsc.chat.R;
import com.alsc.chat.activity.BaseActivity;
import com.alsc.chat.adapter.AddGroupAdapter;
import com.alsc.chat.adapter.GroupAdapter;
import com.alsc.chat.bean.GroupBean;
import com.alsc.chat.db.DatabaseOperate;
import com.alsc.chat.http.HttpMethods;
import com.alsc.chat.http.HttpObserver;
import com.alsc.chat.http.SubscriberOnNextListener;
import com.alsc.chat.manager.DataManager;
import com.alsc.chat.utils.Constants;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;

public class GroupListFragment extends BaseFragment {

    private GroupAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_group_list;
    }

    @Override
    protected void onViewCreated(View view) {
        setViewVisible(R.id.tvLeft);
        view.findViewById(R.id.topView).setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.color_25_2e_3f));
        setText(R.id.tvLeft, R.string.chat_group_chat);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        getAdapter().bindToRecyclerView(recyclerView);
        getAdapter().setNewData(DataManager.getInstance().getGroups());
        getGroups();
    }


    private GroupAdapter getAdapter() {
        if (mAdapter == null) {
            mAdapter = new GroupAdapter(getActivity());
            mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constants.BUNDLE_EXTRA, mAdapter.getItem(position));
                    gotoPager(GroupChatFragment.class, bundle);
                }
            });
        }
        return mAdapter;
    }

    @Override
    public void updateUIText() {

    }

    @Override
    public void onClick(View v) {
    }

    private void getGroups() {
        HttpMethods.getInstance().getGroups(1, 20, new HttpObserver(new SubscriberOnNextListener<ArrayList<GroupBean>>() {
            @Override
            public void onNext(ArrayList<GroupBean> list, String msg) {
                if (getView() == null || list == null) {
                    return;
                }
                getAdapter().setNewData(list);
                DataManager.getInstance().saveGroups(list);
            }
        }, getActivity(), false, (BaseActivity) getActivity()));
    }
}
