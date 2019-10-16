package com.itsdf07.mvp;


import android.app.Activity;

import com.itsdf07.base.mvp.model.IBaseMvpModel;
import com.itsdf07.base.mvp.presenter.IBaseMvpPresenter;
import com.itsdf07.base.mvp.view.IBaseMvpView;

/**
 * @Description:
 * @Author itsdf07
 * @Date 2019/10/16
 */
public interface MainContracts {
    interface IMainView extends IBaseMvpView<Activity> {
    }

    interface IMainPresenter extends IBaseMvpPresenter {
    }

    interface IMainModel extends IBaseMvpModel {
    }
}
