package com.alsc.chat.fragment;

import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.alsc.chat.R;
import com.alsc.chat.adapter.FriendAdapter;
import com.alsc.chat.manager.DataManager;

public class SearchContactFragment extends BaseFragment {

    private FriendAdapter mAdapter;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search_contact;
    }

    @Override
    protected void onViewCreated(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        getAdapter().bindToRecyclerView(recyclerView);
        getAdapter().setNewData(DataManager.getInstance().getFriends());

        EditText et=view.findViewById(R.id.etContactPhone);
        et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_UNSPECIFIED) {

                }
                return false;
            }
        });
    }

    private FriendAdapter getAdapter() {
        if (mAdapter == null) {
            mAdapter = new FriendAdapter();
        }
        return mAdapter;
    }

    @Override
    public void updateUIText() {

    }

    @Override
    public void onClick(View v) {

    }
}
