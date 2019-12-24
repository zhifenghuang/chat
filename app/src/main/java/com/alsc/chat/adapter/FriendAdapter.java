package com.alsc.chat.adapter;

import com.alsc.chat.R;
import com.alsc.chat.bean.UserBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

public class FriendAdapter extends BaseQuickAdapter<UserBean, BaseViewHolder> {
    public FriendAdapter(int layoutResId) {
        super(R.layout.item_friend);
    }

    @Override
    protected void convert(BaseViewHolder helper, UserBean item) {

    }
}
