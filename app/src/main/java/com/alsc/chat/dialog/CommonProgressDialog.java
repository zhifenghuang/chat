package com.alsc.chat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.alsc.chat.R;

/**
 * Created by Richard on 2018/7/31
 * 通用进度框
 * 可以自定义msg
 * 自定义主题需继承common_progress_dialog_theme_base
 */
public class CommonProgressDialog extends Dialog {


    public CommonProgressDialog(Context context) {
        this(context, R.style.common_progress_dialog_theme_base);
    }

    public CommonProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_common_progress_dialog);
    }
}
