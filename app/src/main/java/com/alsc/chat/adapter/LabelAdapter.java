package com.alsc.chat.adapter;

import android.content.Context;

import com.alsc.chat.R;
import com.alsc.chat.bean.LabelBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

public class LabelAdapter extends BaseQuickAdapter<LabelBean, BaseViewHolder> {

    private Context mContext;


    public LabelAdapter(Context context) {
        super(R.layout.item_chat_user);
        mContext = context;

    }

    @Override
    protected void convert(BaseViewHolder helper, LabelBean item) {

    }
}
