package com.itsdf07.base;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @Description: 基础视图对UI的初始化封装
 * @Author itsdf07
 * @Date 2019/10/16
 */
public abstract class BaseActivity extends AppCompatActivity {
    public String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.getLayoutId() > 0) {
            if (MvpConfig.isShowMvpLog){
                Log.i(TAG, "onCreate->LayoutId:" + this.getLayoutId());
            }
            setContentView(this.getLayoutId());
        } else {
            Log.w(TAG, "请在getLayoutId中设置您的UI布局");
        }
        initView();
    }

    /**
     * 设置布局
     *
     * @return
     */
    public abstract int getLayoutId();

    /**
     * 初始化UI相关控件
     */
    public abstract void initView();

    public <T> T $(int resId) {
        return (T) findViewById(resId);
    }

    public <T> T $(int resId, View parent) {
        return (T) parent.findViewById(resId);
    }
}
