package com.alsc.chat.adapter;

import android.content.Context;

import com.alsc.chat.R;
import com.alsc.chat.bean.UserBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

public class SearchContactAdapter extends BaseQuickAdapter<UserBean, BaseViewHolder> {

    private Context mContext;

    public SearchContactAdapter(Context context) {
        super(R.layout.item_friend);
        mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, UserBean item) {
        helper.setGone(R.id.ivAvater, true)
                .setText(R.id.tvName, item.getNickName());
    }
}
