package com.alsc.chat.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.alsc.chat.R;
import com.alsc.chat.bean.UserBean;
import com.alsc.chat.utils.Constants;
import com.alsc.chat.utils.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

public class FriendAdapter extends BaseQuickAdapter<UserBean, BaseViewHolder> {

    private Context mContext;

    public FriendAdapter(Context context) {
        super(R.layout.item_friend);
        mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, UserBean item) {
        int position = helper.getAdapterPosition();
        if (position < 3) {
            helper.setGone(R.id.ivAvater, false);
            if (position == 0) {
                helper.setText(R.id.tvName, "新的朋友");
            } else if (position == 1) {
                helper.setText(R.id.tvName, "寻找好友");
            } else if (position == 2) {
                helper.setText(R.id.tvName, "群聊");
            }
        } else {
            helper.setGone(R.id.ivAvater, true)
                    .setText(R.id.tvName, item.getNickName());
            Utils.loadImage(mContext, R.mipmap.ic_launcher, Constants.BASE_URL + item.getAvatarUrl(), (ImageView) helper.getView(R.id.ivAvater));
        }
    }
}
