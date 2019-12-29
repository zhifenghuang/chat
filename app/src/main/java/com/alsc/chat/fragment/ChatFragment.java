package com.alsc.chat.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.alsc.chat.R;
import com.alsc.chat.activity.MainActivity;
import com.zhangke.websocket.WebSocketHandler;

public class ChatFragment extends BaseFragment {
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_chat;
    }

    @Override
    protected void onViewCreated(View view) {
        setViewsOnClickListener(R.id.btnSend);
    }

    @Override
    public void updateUIText() {

    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btnSend){
            String text = getTextById(R.id.etChat);
            if (TextUtils.isEmpty(text)) {
                return;
            }
     //       WebSocketHandler.getDefault().send(text);
        }
    }
}
