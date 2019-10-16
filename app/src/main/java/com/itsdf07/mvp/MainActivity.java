package com.itsdf07.mvp;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.itsdf07.base.mvp.BaseMvpActivity;

public class MainActivity extends BaseMvpActivity<MainPresenter> implements MainContracts.IMainView, View.OnClickListener {
    private Button btnStartScanBLE, btnStopScanBLE;

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
        btnStartScanBLE = $(R.id.btn_intent_nf877_2_ble);
        btnStartScanBLE.setOnClickListener(this);
        btnStopScanBLE = $(R.id.btn_intent_wait_2_dev);
        btnStopScanBLE.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_intent_nf877_2_ble:
                Log.d(TAG, "正在前往NF877蓝牙BLE通讯");
                break;
            case R.id.btn_intent_wait_2_dev:
                Log.d(TAG, "待开发功能，敬请期待");
                break;
            default:
                break;
        }
    }
}
