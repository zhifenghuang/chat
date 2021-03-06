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

public class ChatUserAdapter extends BaseQuickAdapter<ChatListFragment.ChatBean, BaseViewHolder> {

    private Context mContext;


    public ChatUserAdapter(Context context) {
        super(R.layout.item_chat_user);
        mContext = context;

    }

    @Override
    protected void convert(BaseViewHolder helper, ChatListFragment.ChatBean chatBean) {

        if (chatBean.chatUser != null) {
            helper.setText(R.id.tvName, chatBean.chatUser.getNickName())
                    .setText(R.id.tvMessage, chatBean.lastMsg.getContent());
            Utils.loadImage(mContext, 0, chatBean.chatUser.getAvatarUrl(), (ImageView) helper.getView(R.id.ivAvatar));
        } else {
            helper.setText(R.id.tvName, chatBean.group.getName())
                    .setText(R.id.tvMessage, chatBean.lastGroupMsg.getContent());
            Utils.loadImage(mContext, 0, chatBean.group.getIcon(), (ImageView) helper.getView(R.id.ivAvatar));
        }
    }
}
