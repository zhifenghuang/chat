package com.alsc.chat.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.alsc.chat.R;
import com.alsc.chat.bean.MessageBean;
import com.alsc.chat.bean.UserBean;
import com.alsc.chat.utils.Constants;
import com.alsc.chat.utils.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

public class MessageAdapter extends BaseQuickAdapter<MessageBean, BaseViewHolder> {

    private Context mContext;
    private UserBean mMyInfo;
    private UserBean mChatUser;

    public MessageAdapter(Context context, UserBean myInfo, UserBean chatUser) {
        super(R.layout.item_message);
        mContext = context;
        mMyInfo = myInfo;
        mChatUser = chatUser;
    }

    @Override
    protected void convert(BaseViewHolder helper, MessageBean item) {
        if (item.isMySendMsg(mMyInfo.getUserId())) {
            helper.setGone(R.id.llLeft, false);
            helper.setGone(R.id.rlRight, true);
            helper.setText(R.id.tvRight, item.getContent());
            Utils.loadImage(mContext, 0, mMyInfo.getAvatarUrl(), (ImageView) helper.getView(R.id.ivRight));

        } else {
            helper.setGone(R.id.llLeft, true);
            helper.setGone(R.id.rlRight, false);
            helper.setText(R.id.tvLeft, item.getContent());
            Utils.loadImage(mContext, 0, mChatUser.getAvatarUrl(), (ImageView) helper.getView(R.id.ivLeft));
        }
    }
}
