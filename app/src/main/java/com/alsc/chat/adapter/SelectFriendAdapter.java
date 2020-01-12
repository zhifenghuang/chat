package com.alsc.chat.adapter;

import android.content.Context;
import android.widget.CompoundButton;
import com.alsc.chat.R;
import com.alsc.chat.bean.UserBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;

public class SelectFriendAdapter extends BaseQuickAdapter<UserBean, BaseViewHolder> {

    private Context mContext;

    public SelectFriendAdapter(Context context) {
        super(R.layout.item_select_friend);
        mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, final UserBean item) {
        helper.setText(R.id.tvName, item.getNickName())
                .setChecked(R.id.checkFriend, item.isCheck());
        helper.setOnCheckedChangeListener(R.id.checkFriend, new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item.setCheck(isChecked);
            }
        });
    }

    public ArrayList<Long> getSelectUsers() {
        ArrayList<Long> list = new ArrayList<>();
        for (UserBean bean : getData()) {
            if (bean.isCheck()) {
                list.add(bean.getContactId());
            }
        }
        return list;
    }
}
