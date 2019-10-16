package com.itsdf07.mvp;

import com.itsdf07.base.mvp.presenter.BaseMvpPresenter;

/**
 * @Description:
 * @Author itsdf07
 * @Date 2019/10/16
 */
public class MainPresenter extends BaseMvpPresenter<MainContracts.IMainView> implements MainContracts.IMainPresenter {
    MainModel mainModel;

    public MainPresenter(MainContracts.IMainView view) {
        super(view);
        this.mainModel = new MainModel();
    }
}
