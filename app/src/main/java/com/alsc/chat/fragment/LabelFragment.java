package com.alsc.chat.fragment;

import android.view.View;

import com.alsc.chat.R;
import com.alsc.chat.activity.BaseActivity;
import com.alsc.chat.http.HttpMethods;
import com.alsc.chat.http.HttpObserver;
import com.alsc.chat.http.SubscriberOnNextListener;

public class LabelFragment extends BaseFragment {
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_label;
    }

    @Override
    protected void onViewCreated(View view) {
        setViewVisible(R.id.tvLeft, R.id.btnRight);
        setViewsOnClickListener(R.id.btnRight, R.id.btnAddLabel);
        setText(R.id.tvLeft, R.string.chat_all_labels);
        setText(R.id.btnRight, R.string.chat_create);
    }

    @Override
    public void updateUIText() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRight:
            case R.id.btnAddLabel:
                gotoPager(SelectFriendFragment.class);
                break;
        }
    }
}
