package com.alsc.chat.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.alsc.chat.R;
import com.alsc.chat.bean.GroupBean;
import com.alsc.chat.utils.Utils;
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
                .setGone(R.id.line, helper.getAdapterPosition() != getItemCount() - 1);
        Utils.loadImage(mContext,0,item.getIcon(),(ImageView) helper.getView(R.id.ivAvatar));
    }
}
