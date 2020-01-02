package com.alsc.chat.adapter;

import android.content.Context;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.alsc.chat.R;
import com.alsc.chat.bean.UserBean;
import com.alsc.chat.utils.Constants;
import com.alsc.chat.utils.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;

public class AddGroupAdapter extends BaseQuickAdapter<UserBean, BaseViewHolder> {

    private Context mContext;

    public AddGroupAdapter(Context context) {
        super(R.layout.item_add_group);
        mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, final UserBean item) {
        helper.setGone(R.id.ivAvater, true)
                .setText(R.id.tvName, item.getNickName())
                .setChecked(R.id.checkSelect, item.isCheck());
        helper.setOnCheckedChangeListener(R.id.checkSelect, new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.e("aaaaaaa","isChecked: "+isChecked);
                item.setCheck(isChecked);
            }
        });
        Utils.loadImage(mContext, R.mipmap.ic_launcher, Constants.BASE_URL + item.getAvatarUrl(), (ImageView) helper.getView(R.id.ivAvater));
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
