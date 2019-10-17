package com.itsdf07.mvp;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.itsdf07.base.mvp.BaseMvpActivity;
import com.itsdf07.mvp.works.ble.ScanBLEActivity;

public class MainActivity extends BaseMvpActivity<MainPresenter> implements MainContracts.IMainView, View.OnClickListener {

    @Override
    public MainPresenter initPresenter() {
        return new MainPresenter(this);
    }

    @Override
    public void afterPresenter() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        Log.d(TAG, "初始化MainActivity...");
        ((Button) $(R.id.btn_intent_nf877_2_ble)).setOnClickListener(this);
        ((Button) $(R.id.btn_intent_wait_2_dev)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_intent_nf877_2_ble:
                Log.d(TAG, "正在前往NF877蓝牙BLE通讯");
                startActivity(new Intent(this, ScanBLEActivity.class));
                break;
            case R.id.btn_intent_wait_2_dev:
                Log.d(TAG, "待开发功能，敬请期待");
                break;
            default:
                break;
        }
    }
}
