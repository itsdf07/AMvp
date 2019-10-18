package com.itsdf07.base.mvp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.itsdf07.alog.ALog;
import com.itsdf07.base.BaseActivity;
import com.itsdf07.base.MvpConfig;
import com.itsdf07.base.mvp.presenter.IBaseMvpPresenter;
import com.itsdf07.base.mvp.view.IBaseMvpView;


/**
 * @Description: MVP基类针对常用业务、交互进行封装
 * @Author itsdf07
 * @Date 2019/10/16
 */
public abstract class BaseMvpActivity<P extends IBaseMvpPresenter> extends BaseActivity implements IBaseMvpView<Activity> {
    public P presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = initPresenter();
        afterPresenter();
    }

    @Override
    protected void onDestroy() {
        if (MvpConfig.isShowMvpLog) {
            ALog.iTag(TAG, "...");
        }
        super.onDestroy();
        /**
         * 在生命周期结束时，将presenter与view之间的联系断开，防止出现内存泄露
         */
        if (null != presenter) {
            presenter.detachView();
        }
    }

    public abstract P initPresenter();

    public abstract void afterPresenter();

    @Override
    public void showLoading() {
        if (MvpConfig.isShowMvpLog) {
            ALog.iTag(TAG, "...");
        }
    }

    @Override
    public void showLoading(String content) {
        if (MvpConfig.isShowMvpLog) {
            ALog.iTag(TAG, "content:%s", content);
        }
        if (null == content) {
            ALog.wTag(TAG, "showLoading 的显示内容为null，将为您显示默认内容...");
            showLoading();
            return;
        }
    }

    @Override
    public void hideLoading() {
        if (MvpConfig.isShowMvpLog) {
            ALog.iTag(TAG, "...");
        }
    }

    @Override
    public void showToast(String content) {
        if (MvpConfig.isShowMvpLog) {
            ALog.iTag(TAG, "content:%s", content);
        }
        if (null == content) {
            ALog.wTag(TAG, "showToast 的显示内容为null，将主动为您置为\"\"");
            content = "";
        }
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Activity getSelfActivity() {
        return this;
    }
}
