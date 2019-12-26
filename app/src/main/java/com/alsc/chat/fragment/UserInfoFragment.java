package com.alsc.chat.fragment;

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
        setText(R.id.tvMemo, TextUtils.isEmpty(mUserInfo.getMemo()) ? mUserInfo.getNickName() : mUserInfo.getMemo());
        setText(R.id.tvNick, mUserInfo.getNickName());
        setViewsOnClickListener(R.id.btnUpdateMemo, R.id.btnOpeartor);
        Utils.loadImage(getActivity(), 0, Constants.BASE_URL + mUserInfo.getAvatarUrl(), (ImageView) view.findViewById(R.id.ivAvater));
    }

    @Override
    public void updateUIText() {

    }

    @Override
    public void onClick(View v) {

    }
}
