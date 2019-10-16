package com.itsdf07.mvp;

import android.util.Log;

import com.itsdf07.base.mvp.BaseMvpActivity;

public class MainActivity extends BaseMvpActivity<MainPresenter> implements MainContracts.IMainView {

    @Override
    public MainPresenter initPresenter() {
        return new MainPresenter(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        Log.d(TAG, "初始化MainActivity...");
    }
}
