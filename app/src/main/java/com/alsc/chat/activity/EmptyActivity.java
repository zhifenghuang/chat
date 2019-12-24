package com.alsc.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.alsc.chat.R;
import com.alsc.chat.fragment.BaseFragment;


/**
 * 空的Activty，会替换你传进来的Fragment,当Activity存在时使用存在的Activity，整个程序中只有一个该Activity
 */
public class EmptyActivity extends BaseActivity {

    private boolean mCanBackFinish = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);
        mCanBackFinish = true;
        Intent intent = getIntent();
        String fragmentName = intent.getStringExtra("FRAGMENT_NAME");
        BaseFragment fragment = (BaseFragment) Fragment.instantiate(this,
                fragmentName);
        Bundle b = intent.getExtras();
        fragment.setArguments(b);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.container, fragment, fragmentName);
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
    }


    @Override
    protected void onFromBackground() {
        super.onFromBackground();

    }

    public void setCanBackFinish(boolean canBackFinish) {
        mCanBackFinish = canBackFinish;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!mCanBackFinish) {
                return false;
            }
            if (getVisibleFragment() == null
                    || getVisibleFragment().getActivity() == null) {
                finish();
            } else {
                getVisibleFragment().goBack();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

}

