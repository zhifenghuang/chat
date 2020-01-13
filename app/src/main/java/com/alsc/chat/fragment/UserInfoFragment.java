package com.alsc.chat.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.alsc.chat.R;
import com.alsc.chat.bean.UserBean;
import com.alsc.chat.utils.Constants;
import com.alsc.chat.utils.Utils;

public class UserInfoFragment extends BaseFragment {

    private UserBean mUserInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_user_info;
    }

    @Override
    protected void onViewCreated(View view) {
        mUserInfo = (UserBean) getArguments().getSerializable(Constants.BUNDLE_EXTRA);
        setText(R.id.tvNick, mUserInfo.getNickName());
        setText(R.id.tvId,"ID:"+mUserInfo.getContactId());
        setViewsOnClickListener(R.id.tvAddMemo, R.id.tvMore,R.id.llSendMsg);
        Utils.loadImage(getActivity(), R.mipmap.ic_launcher, mUserInfo.getAvatarUrl(), (ImageView) view.findViewById(R.id.ivAvatar));
    }

    @Override
    public void updateUIText() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvAddMemo:
                break;
            case R.id.tvMore:
                break;
            case R.id.llSendMsg:
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.BUNDLE_EXTRA, mUserInfo);
                gotoPager(ChatFragment.class, bundle);
                goBack();
                break;
        }
    }
}
