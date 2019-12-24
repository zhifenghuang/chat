package com.alsc.chat.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.alsc.chat.R;
import com.alsc.chat.fragment.BaseFragment;
import com.alsc.chat.fragment.MyDialogFragment;
import com.alsc.chat.http.HttpMethods;
import com.alsc.chat.http.OnHttpErrorListener;
import com.alsc.chat.manager.Preferences;
import com.alsc.chat.utils.NetUtil;
import com.alsc.chat.utils.Utils;
import org.json.JSONException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * 应用首界面
 */
public abstract class BaseActivity extends AppCompatActivity implements OnHttpErrorListener {

    private DisplayMetrics mDisplaymetrics;

    private boolean mIsFullScreen;

    private MyDialogFragment mErrorDialog;

    private static boolean isNotComeFromBG;  //改为静态的，防止多个Activity会调用背景到前景的方法


    private static ArrayList<String> mActivityNameList;  //当mActivityNameList size为0时表示到了后台
    private static final ArrayList<BaseActivity> mActivityList = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HttpMethods.getInstance().setContext(this);
        Preferences.getInstacne().setContext(this);
        /*
         * 这里判断是否从splashActivity过来，是的话当作从后台到前台处理
         */
        isNotComeFromBG = !isComeFromSplash();
        mActivityList.add(this);

    }

    public void showLoadingDialog() {
        showLoadingDialog("", null, true);
    }

    public void showLoadingDialog(String text) {
        showLoadingDialog(text, null, true);
    }

    /**
     * 显示Loading 页面， listener可为空
     *
     * @param strTitle
     * @param listener
     * @param isCancelByUser:用户是否可点击屏幕，或者Back键关掉对话框
     */
    public void showLoadingDialog(String strTitle, final DialogInterface.OnCancelListener listener, boolean isCancelByUser) {

    }


    /**
     * 关闭loading的页面
     */
    public void hideLoadingDialog() {

    }

    public void requestPermission(int permissionReqCode, String... permissions) {
        ArrayList<String> uncheckPermissions = null;
        for (String permission : permissions) {
            if (!Utils.isGrantPermission(this, permission)) {
                //进行权限请求
                if (uncheckPermissions == null) {
                    uncheckPermissions = new ArrayList<>();
                }
                uncheckPermissions.add(permission);
            }
        }
        if (uncheckPermissions != null && !uncheckPermissions.isEmpty()) {
            String[] array = new String[uncheckPermissions.size()];
            ActivityCompat.requestPermissions(this, uncheckPermissions.toArray(array), permissionReqCode);
        }
    }

    /**
     * 判断是否从splashActivity过来
     *
     * @return true将被当作从后台到前台处理
     */
    protected boolean isComeFromSplash() {
        return getIntent().getBooleanExtra("key_come_from_splash", false);
    }


    public DisplayMetrics getDisplaymetrics() {
        if (mDisplaymetrics == null) {
            mDisplaymetrics = new DisplayMetrics();
            ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(mDisplaymetrics);
        }
        return mDisplaymetrics;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mActivityNameList == null) {
            mActivityNameList = new ArrayList<>();
        }

        mActivityNameList.add(getClass().getName());

        if (!isNotComeFromBG) {// 这里表示是从后台到前台
            onFromBackground();
            // 重置
            isNotComeFromBG = true;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermission(0,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();

        if (mActivityNameList != null) {
            mActivityNameList.remove(getClass().getName());
        }
        if (isBackground()) {
            isNotComeFromBG = false;
            onToBackground();
        } else {
            isNotComeFromBG = true;
        }
    }


    /**
     * 用于在onStop后判断应用是否已经退到后台
     *
     * @return
     */
    private boolean isBackground() {
        return mActivityNameList == null || mActivityNameList.isEmpty();
    }


    public void showOneBtnDialog(final String title, final String msg, final String btnText) {
        final MyDialogFragment dialogFragment = new MyDialogFragment();
        dialogFragment.setLayout(R.layout.layout_one_btn_dialog);
        dialogFragment.setOnMyDialogListener(new MyDialogFragment.OnMyDialogListener() {
            @Override
            public void initView(View view) {
                if (TextUtils.isEmpty(title)) {
                    view.findViewById(R.id.tv1).setVisibility(View.GONE);
                } else {
                    ((TextView) view.findViewById(R.id.tv1)).setText(title);
                }
                ((TextView) view.findViewById(R.id.tv2)).setText(msg);
                ((TextView) view.findViewById(R.id.btn2)).setText(btnText);
                dialogFragment.setDialogViewsOnClickListener(view, R.id.btn2);
            }

            @Override
            public void onViewClick(int viewId) {

            }
        });
        dialogFragment.show(getSupportFragmentManager(), "MyDialogFragment");
    }

    public void errorCodeDo(final int errorCode, final String message) {
        if (!TextUtils.isEmpty(message)) {
            mErrorDialog = new MyDialogFragment();
            mErrorDialog.setLayout(R.layout.layout_one_btn_dialog);
            mErrorDialog.setOnMyDialogListener(new MyDialogFragment.OnMyDialogListener() {
                @Override
                public void initView(View view) {
                    view.findViewById(R.id.tv1).setVisibility(View.GONE);
                    ((TextView) view.findViewById(R.id.tv2)).setText(message);
                    ((TextView) view.findViewById(R.id.btn2)).setText("Ok");
                    mErrorDialog.setDialogViewsOnClickListener(view, R.id.btn2);
                }

                @Override
                public void onViewClick(int viewId) {

                }
            });
            mErrorDialog.show(getSupportFragmentManager(), "MyDialogFragment");

            mErrorDialog.setOnDismiss(new MyDialogFragment.IDismissListener() {
                @Override
                public void onDismiss() {

                }
            });
        }
    }

    /**
     * 页面跳转，如果返回true,则基类已经处理，否则没有处理
     *
     * @param pagerClass
     * @return
     */
    public void gotoPager(Class<?> pagerClass) {
        gotoPager(pagerClass, null);
    }


    /**
     * 页面跳转，如果返回true,则基类已经处理，否则没有处理
     *
     * @param pagerClass
     * @param bundle
     * @return
     */
    public void gotoPager(Class<?> pagerClass, Bundle bundle) {

        if (Activity.class.isAssignableFrom(pagerClass)) {
            Intent intent = new Intent(this, pagerClass);
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            startActivity(intent);
        } else {
            String name = pagerClass.getName();
            Intent intent = new Intent(this, EmptyActivity.class);
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            intent.putExtra("FRAGMENT_NAME", name);
            startActivity(intent);
        }
    }


    /**
     * 根据name获取fragment
     *
     * @param name
     * @return
     */
    public BaseFragment getFragmentByName(String name) {
        BaseFragment fragment = (BaseFragment) getSupportFragmentManager()
                .findFragmentByTag(name);
        if (fragment == null) {
            fragment = (BaseFragment) Fragment.instantiate(this, name);
        }
        return fragment;
    }

    /**
     * 返回，如果stack中还有Fragment的话，则返回stack中的fragment，否则 finish当前的Activity
     */
    public void goBack() {

        getSupportFragmentManager().executePendingTransactions();
        int nSize = getSupportFragmentManager().getBackStackEntryCount();
        if (nSize > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }


    public BaseFragment getVisibleFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        @SuppressLint("RestrictedApi") List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments == null) {
            return null;
        }
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment instanceof BaseFragment && fragment.isVisible())
                return (BaseFragment) fragment;
        }
        return null;
    }

    protected void onToBackground() {

    }

    protected void onFromBackground() {
    }


    public void onResume() {
        super.onResume();
    }

    public void onDestroy() {
        super.onDestroy();
        mActivityList.remove(this);
    }

    public void finishAllOtherActivity() {
        for (BaseActivity activity : mActivityList) {
            if (!(activity instanceof MainActivity)) {
                activity.finish();
            }
        }
    }

    public void finishAllActivity() {
        for (BaseActivity activity : mActivityList) {
            activity.finish();
        }
    }


    @Override
    public void onConnectError(Throwable e) {
        if (!NetUtil.isConnected(this)) {

        } else if (e instanceof UnknownHostException
                || e instanceof JSONException
                || e instanceof retrofit2.HttpException) {

        } else if (e instanceof SocketTimeoutException
                || e instanceof ConnectException
                || e instanceof TimeoutException) {

        } else {

        }
    }


    @Override
    public synchronized void onServerError(int errorCode, String errorMsg) {
        //   stopHttpLoad();
        errorCodeDo(errorCode, errorMsg);
        if (!TextUtils.isEmpty(errorMsg)) {
            showToast(errorMsg);
        }
    }

    public void onBackClick(View view) {
        goBack();
    }

    public void setScreenFull(boolean isFull) {
        if (mIsFullScreen == isFull) {
            return;
        }

        if (isFull) {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(params);
            mIsFullScreen = true;
        } else {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(params);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            mIsFullScreen = false;
        }
    }


    public void showToast(int textId) {
        Toast.makeText(this, getString(textId), Toast.LENGTH_LONG).show();
    }

    public void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    }

}
