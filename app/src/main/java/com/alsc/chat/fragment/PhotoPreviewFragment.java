package com.alsc.chat.fragment;

import android.Manifest;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import com.alsc.chat.R;
import com.alsc.chat.activity.BaseActivity;
import com.alsc.chat.bean.FileBean;
import com.alsc.chat.utils.BitmapUtil;
import com.alsc.chat.utils.Constants;
import com.alsc.chat.utils.PermissionUtils;
import com.alsc.chat.utils.Utils;
import com.alsc.chat.view.ShowPicView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

/**
 * Created by gigabud on 16-6-21.
 */
public class PhotoPreviewFragment extends BaseFragment {

    private Bitmap mBmp;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_photo_preview;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void onViewCreated(View view) {
        setViewsOnClickListener(R.id.tvCancel, R.id.tvOk);
        String filePath = getArguments().getString(Constants.BUNDLE_EXTRA);
        mBmp = BitmapUtil.getBitmapFromFile(filePath, getDisplaymetrics().widthPixels, getDisplaymetrics().heightPixels);
        ShowPicView showPicView = view.findViewById(R.id.ivShowPic);
        showPicView.setImageBitmap(mBmp, true);

    }

    public void onStart() {
        super.onStart();
        //   ((BaseActivity) getActivity()).setScreenFull(true);
    }

    @Override
    public void updateUIText() {

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tvCancel) {
            getActivity().finish();
        } else if (id == R.id.tvOk) {
            if (!PermissionUtils.isGrantPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ((BaseActivity) getActivity()).requestPermission(0,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                return;
            }
            FileBean bean = new FileBean();
            bean.setType(0);
            bean.setFile(new File(Utils.saveJpeg(mBmp, getActivity())));
            EventBus.getDefault().post(bean);
            mBmp.recycle();
            mBmp = null;
            getActivity().finish();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mBmp != null && !mBmp.isRecycled()) {
            mBmp.recycle();
        }
        mBmp = null;
    }

}
