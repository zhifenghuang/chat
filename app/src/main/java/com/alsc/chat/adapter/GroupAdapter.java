package com.alsc.chat.adapter;

import android.content.Context;
import com.alsc.chat.R;
import com.alsc.chat.bean.GroupBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;


public class GroupAdapter extends BaseQuickAdapter<GroupBean, BaseViewHolder> {

    private Context mContext;

    public GroupAdapter(Context context) {
        super(R.layout.item_group);
        mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, GroupBean item) {
        helper.setText(R.id.tvName, item.getName())
                .setImageResource(R.id.ivAvater, R.mipmap.ic_launcher);
    }
}
