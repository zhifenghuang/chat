package com.alsc.chat.fragment;

import android.view.View;

import com.alsc.chat.R;
import com.alsc.chat.http.HttpMethods;

public class GroupListFragment extends BaseFragment {
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_chat_list;
    }

    @Override
    protected void onViewCreated(View view) {
        setViewVisible(R.id.tvRight);
        setViewsOnClickListener(R.id.tvRight);
    }

    @Override
    public void updateUIText() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvRight:
                gotoPager(AddGroupFragment.class);
                break;
        }
    }

    private void getGroups() {
        //HttpMethods.getInstance().
    }
}
