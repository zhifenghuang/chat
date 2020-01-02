package com.alsc.chat.fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alsc.chat.R;
import com.alsc.chat.activity.BaseActivity;
import com.alsc.chat.adapter.AddGroupAdapter;
import com.alsc.chat.bean.UserBean;
import com.alsc.chat.http.HttpMethods;
import com.alsc.chat.http.HttpObserver;
import com.alsc.chat.http.SubscriberOnNextListener;
import com.alsc.chat.manager.DataManager;

import java.util.ArrayList;

public class AddGroupFragment extends BaseFragment {

    private AddGroupAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_add_group;
    }

    @Override
    protected void onViewCreated(View view) {
        setViewVisible(R.id.tvRight);
        setText(R.id.tvRight, "Done");
        setViewsOnClickListener(R.id.tvRight);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        getAdapter().bindToRecyclerView(recyclerView);
        getAdapter().setNewData(DataManager.getInstance().getFriends());
    }

    private AddGroupAdapter getAdapter() {
        if (mAdapter == null) {
            mAdapter = new AddGroupAdapter(getActivity());
        }
        return mAdapter;
    }

    @Override
    public void updateUIText() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvRight:
                String groupName=getTextById(R.id.etName).trim();
                if(TextUtils.isEmpty(groupName)){
                    return;
                }
                ArrayList<Long> list=getAdapter().getSelectUsers();
                if(list.isEmpty()){
                    return;
                }
                list.add(DataManager.getInstance().getUser().getUserId());
                HttpMethods.getInstance().createGroup(groupName,list,new HttpObserver(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o, String msg) {
                        goBack();
                    }
                },getActivity(),(BaseActivity)getActivity()));
                break;
        }
    }
}
