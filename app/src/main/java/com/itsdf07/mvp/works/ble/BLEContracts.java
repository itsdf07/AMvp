package com.itsdf07.mvp.works.ble;

import android.app.Activity;

import com.itsdf07.base.mvp.model.IBaseMvpModel;
import com.itsdf07.base.mvp.presenter.IBaseMvpPresenter;
import com.itsdf07.base.mvp.view.IBaseMvpView;
import com.itsdf07.bluetooth.ble.bean.BLEChannelSetting;
import com.itsdf07.bluetooth.ble.bean.BLEPublicSetting;
import com.itsdf07.bluetooth.ble.client.scan.BLEScanResult;

import java.util.HashMap;

/**
 * @Description:
 * @Author itsdf07
 * @Date 2019/10/17
 */
public interface BLEContracts {
    interface IBLEView extends IBaseMvpView<Activity> {
        void updataBLEConnectStatus(String content);
    }

    interface IBLEPresenter extends IBaseMvpPresenter {
        void setBLEDevice(BLEScanResult device);

        BLEScanResult getBLEDevice();

        void connectBLE();

        void disConnectBLE();

        /**
         * 读取设备数据
         *
         * @return 包含公共协议BLEPublicSetting以及BLEChannelSetting
         */
        HashMap<Integer, Object> readDatas();

        /**
         * 设备写入数据
         */
        void writeDatas();


        /**
         * 获取公共协议对象
         *
         * @return
         */
        BLEPublicSetting getBLEPublicSetting();

        /**
         * 获取信道对象
         *
         * @param channelNum 信道值，1-32（注意，不是索引）
         * @return
         */
        BLEChannelSetting getBLEChannelSetting(int channelNum);
    }

    interface IBLEModel extends IBaseMvpModel {
    }
}
