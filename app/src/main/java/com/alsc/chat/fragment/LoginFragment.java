package com.alsc.chat.fragment;

import android.text.TextUtils;
import android.view.View;

import com.alsc.chat.BaseApplication;
import com.alsc.chat.R;
import com.alsc.chat.activity.BaseActivity;
import com.alsc.chat.bean.UserBean;
import com.alsc.chat.http.HttpMethods;
import com.alsc.chat.http.HttpObserver;
import com.alsc.chat.http.SubscriberOnNextListener;
import com.alsc.chat.manager.DataManager;

public class LoginFragment extends BaseFragment {
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_login;
    }

    @Override
    protected void onViewCreated(View view) {
        setViewsOnClickListener(R.id.btnLogin);
    }

    @Override
    public void updateUIText() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                String account = getTextById(R.id.etAccount);
                String password = getTextById(R.id.etPassword);
                if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) {
                    return;
                }
                HttpMethods.getInstance().login(account, password, new HttpObserver(new SubscriberOnNextListener<UserBean>() {
                    @Override
                    public void onNext(UserBean userBean, String msg) {
                        DataManager.getInstance().saveUser(userBean);
                        ((BaseApplication) getActivity().getApplication()).initWebSocket(userBean.getToken());
                        goBack();
                    }
                }, getActivity(), (BaseActivity) getActivity()));
                break;
        }
    }
}
