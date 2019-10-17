package com.itsdf07.mvp.works.ble;

import android.app.Activity;

import com.itsdf07.base.mvp.model.IBaseMvpModel;
import com.itsdf07.base.mvp.presenter.IBaseMvpPresenter;
import com.itsdf07.base.mvp.view.IBaseMvpView;
import com.itsdf07.bluetooth.LinkedHashMap;
import com.itsdf07.bluetooth.ble.client.scan.BLEScanResult;

import java.util.ArrayList;

/**
 * @Description:
 * @Author itsdf07
 * @Date 2019/10/16
 */
public interface ScanBLEContracts {
    interface IScanBLEView extends IBaseMvpView<Activity> {
        /**
         * 刷新整个适配器内容
         */
        public void notifyUpdata2Adapter();

        /**
         * 针对列表的某一项进行刷新
         *
         * @param index
         */
        void notifyUpdata2Item(int index);
    }

    interface IScanBLEPresenter extends IBaseMvpPresenter {
        LinkedHashMap<String, BLEScanResult> getBLEs();

        void startScan();

        void stopScan();
    }

    interface IScanBLEModel extends IBaseMvpModel {
    }
}
