package com.alsc.chat.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.alsc.chat.R;
import com.alsc.chat.bean.GroupMessageBean;
import com.alsc.chat.bean.MessageBean;
import com.alsc.chat.bean.UserBean;
import com.alsc.chat.utils.Constants;
import com.alsc.chat.utils.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;

public class GroupMessageAdapter extends BaseQuickAdapter<GroupMessageBean, BaseViewHolder> {

    private Context mContext;
    private UserBean mMyInfo;
    private ArrayList<UserBean> mGroupUsers;

    public GroupMessageAdapter(Context context, UserBean myInfo,ArrayList<UserBean> groupUsers) {
        super(R.layout.item_message);
        mContext = context;
        mMyInfo = myInfo;
        mGroupUsers = groupUsers;
    }

    @Override
    protected void convert(BaseViewHolder helper, GroupMessageBean item) {
        if (item.isMySendMsg(mMyInfo.getUserId())) {
            helper.setGone(R.id.llLeft, false);
            helper.setGone(R.id.rlRight, true);
    //        Utils.loadImage(mContext, R.mipmap.ic_launcher, "", (ImageView) helper.getView(R.id.ivRight));
            helper.setText(R.id.tvRight, item.getContent());
        } else {
            helper.setGone(R.id.llLeft, true);
            helper.setGone(R.id.rlRight, false);
      //      Utils.loadImage(mContext, R.mipmap.ic_launcher, "", (ImageView) helper.getView(R.id.ivLeft));
            helper.setText(R.id.tvLeft, item.getContent());
        }
    }
}
