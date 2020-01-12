package com.alsc.chat.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.alsc.chat.R;
import com.alsc.chat.bean.FriendItem;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;

public class FriendAdapter extends BaseMultiItemQuickAdapter<FriendItem, BaseViewHolder> {

    private Context mContext;

    public FriendAdapter(Context context) {
        super(new ArrayList<FriendItem>());
        addItemType(FriendItem.VIEW_TYPE_0, R.layout.item_friend_0);
        addItemType(FriendItem.VIEW_TYPE_1, R.layout.item_friend_1);
        addItemType(FriendItem.VIEW_TYPE_2, R.layout.item_friend_2);
        addItemType(FriendItem.VIEW_TYPE_3, R.layout.item_friend_3);
        mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, FriendItem item) {
        switch (helper.getItemViewType()) {
            case FriendItem.VIEW_TYPE_0:
                break;
            case FriendItem.VIEW_TYPE_1:
                helper.setImageResource(R.id.ivIcon,item.getIconRes())
                        .setText(R.id.tvName,item.getName());
                break;
            case FriendItem.VIEW_TYPE_2:
                break;
            case FriendItem.VIEW_TYPE_3:
                helper.setText(R.id.tvName,item.getFriend().getNickName());
                break;
        }
    }
}
