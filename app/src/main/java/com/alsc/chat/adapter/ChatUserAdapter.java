package com.alsc.chat.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.alsc.chat.R;
import com.alsc.chat.bean.MessageBean;
import com.alsc.chat.bean.UserBean;
import com.alsc.chat.fragment.ChatListFragment;
import com.alsc.chat.utils.Constants;
import com.alsc.chat.utils.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

public class ChatUserAdapter extends BaseQuickAdapter<ChatListFragment.ChatUser, BaseViewHolder> {

    private Context mContext;


    public ChatUserAdapter(Context context) {
        super(R.layout.item_chat_user);
        mContext = context;

    }

    @Override
    protected void convert(BaseViewHolder helper, ChatListFragment.ChatUser chatUser) {
        helper.setText(R.id.tvName, chatUser.chatUser.getNickName())
                .setText(R.id.tvMessage, chatUser.lastMsg.getContent())
                .setText(R.id.tvTime, Utils.longToDate2(chatUser.lastMsg.getCreateTime()));
        Utils.loadImage(mContext, R.mipmap.ic_launcher, Constants.BASE_URL + chatUser.chatUser.getAvatarUrl(), (ImageView) helper.getView(R.id.ivAvater));
    }
}
