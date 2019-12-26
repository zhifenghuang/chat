package com.alsc.chat.fragment;

import android.view.View;

import com.alsc.chat.R;
import com.alsc.chat.activity.BaseActivity;
import com.alsc.chat.http.HttpMethods;
import com.alsc.chat.http.HttpObserver;
import com.alsc.chat.http.SubscriberOnNextListener;

public class ApplyFragment extends BaseFragment {
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_chat_list;
    }

    @Override
    protected void onViewCreated(View view) {
        reviewContact();
    }

    @Override
    public void updateUIText() {

    }

    @Override
    public void onClick(View v) {

    }

    private void reviewContact() {
        HttpMethods.getInstance().reviewContact(new HttpObserver(new SubscriberOnNextListener() {
            @Override
            public void onNext(Object o, String msg) {

            }
        }, getActivity(), (BaseActivity) getActivity()));
    }
}
