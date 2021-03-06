package com.alsc.chat.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import com.alsc.chat.R;
import com.alsc.chat.fragment.BaseFragment;
import com.alsc.chat.fragment.ChatListFragment;
import com.alsc.chat.fragment.FriendListFragment;
import com.alsc.chat.fragment.LoginFragment;
import com.alsc.chat.manager.DataManager;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {

    private ArrayList<BaseFragment> mBaseFragment;
    private BaseFragment mCurrentFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFragments();
        initViews();
    }

    private void initFragments() {
        mBaseFragment = new ArrayList<>();
        mBaseFragment.add(new ChatListFragment());
        mBaseFragment.add(new FriendListFragment());
    }

    private void initViews() {
        switchFragment(mBaseFragment.get(0));
        resetBottomBar(0);
        LinearLayout llBottom = findViewById(R.id.llBottom);
        int count = llBottom.getChildCount();
        View itemView;
        for (int i = 0; i < count; ++i) {
            itemView = llBottom.getChildAt(i);
            itemView.setTag(i);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int tag = (int) view.getTag();
                    switchFragment(mBaseFragment.get(tag));
                    resetBottomBar(tag);
                }
            });
        }
    }

    public void onResume() {
        super.onResume();
        if (DataManager.getInstance().getUser() == null) {
            gotoPager(LoginFragment.class);
        }
    }

    private void resetBottomBar(int currentPos) {
        LinearLayout llBottom = findViewById(R.id.llBottom);
        int count = llBottom.getChildCount();
        ViewGroup itemView;
        for (int i = 0; i < count; ++i) {
            itemView = (ViewGroup) llBottom.getChildAt(i);
            (((ImageView) itemView.getChildAt(0))).setImageResource(getResIdByIndex(i, currentPos == i));
        }
    }

    private int getResIdByIndex(int index, boolean isCheck) {
        int id = 0;
        switch (index) {
            case 0:
                id = isCheck ? R.drawable.chat_message_selected : R.drawable.chat_message_unselected;
                break;
            case 1:
                id = isCheck ? R.drawable.chat_friend_selected : R.drawable.chat_friend_unselected;
                break;
        }
        return id;
    }


    /**
     * @param to 马上要切换到的Fragment，一会要显示
     */
    private void switchFragment(BaseFragment to) {
        if (mCurrentFragment != to) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if (!to.isAdded()) {
                if (mCurrentFragment != null) {
                    ft.hide(mCurrentFragment);
                }
                ft.add(R.id.fl, to).commit();
            } else {
                if (mCurrentFragment != null) {
                    ft.hide(mCurrentFragment);
                }
                ft.show(to).commit();
            }
        }
        mCurrentFragment = to;
    }
}
