package com.alsc.chat.fragment;

import android.view.View;

import com.alsc.chat.R;

public class CreateLabelFragment extends BaseFragment {
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_label;
    }

    @Override
    protected void onViewCreated(View view) {
        setViewVisible(R.id.tvLeft, R.id.tvRight);
        setViewsOnClickListener(R.id.tvRight, R.id.btnAddLabel);
        setText(R.id.tvLeft, R.string.chat_all_labels);
        setText(R.id.tvRight, R.string.chat_create);
    }

    @Override
    public void updateUIText() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvRight:
                break;
            case R.id.btnAddLabel:
                break;
        }
    }

    private void reviewContact() {

    }
}
