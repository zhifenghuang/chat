package com.alsc.chat.fragment;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.alsc.chat.R;
import com.alsc.chat.activity.BaseActivity;
import com.alsc.chat.adapter.SearchContactAdapter;
import com.alsc.chat.http.HttpMethods;
import com.alsc.chat.http.HttpObserver;
import com.alsc.chat.http.SubscriberOnNextListener;
import com.alsc.chat.utils.Constants;
import com.chad.library.adapter.base.BaseQuickAdapter;

public class SearchContactFragment extends BaseFragment {

    private SearchContactAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search_contact;
    }

    @Override
    protected void onViewCreated(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        getAdapter().bindToRecyclerView(recyclerView);
        EditText et = view.findViewById(R.id.etContactPhone);
        et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    String text = textView.getText().toString();
                    searchContact(text);
                }
                return false;
            }
        });
    }

    private SearchContactAdapter getAdapter() {
        if (mAdapter == null) {
            mAdapter = new SearchContactAdapter(getActivity());
            mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constants.BUNDLE_EXTRA, mAdapter.getItem(position));
                    gotoPager(UserInfoFragment.class, bundle);
                }
            });
        }
        return mAdapter;
    }

    @Override
    public void updateUIText() {

    }

    @Override
    public void onClick(View v) {

    }

    private void searchContact(String text) {
        HttpMethods.getInstance().searchContact(text, new HttpObserver(new SubscriberOnNextListener() {
            @Override
            public void onNext(Object o, String msg) {

            }
        }, getActivity(), (BaseActivity) getActivity()));
    }
}
