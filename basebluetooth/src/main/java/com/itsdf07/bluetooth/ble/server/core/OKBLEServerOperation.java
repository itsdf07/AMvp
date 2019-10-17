package com.itsdf07.bluetooth.ble.server.core;


import com.itsdf07.bluetooth.ble.common.OKBLECharacteristicModel;
import com.itsdf07.bluetooth.ble.common.OKBLEServiceModel;

import java.util.List;

public class OKBLEServerOperation {
    public List<OKBLECharacteristicModel> characteristicModels;
    public OKBLEServiceModel serviceModel;
    public BLEServerOperationListener operationListener;


    public interface BLEServerOperationListener{

        /**
         * service UUID不合法
         */
        public static final int Operation_FAILED_Invalid_Service_UUID=1;

        /**
         * Characteristic UUID不合法
         */
        public static final int Operation_FAILED_Invalid_Characteristic_UUID=2;

        /**
         * BLE内部操作返回失败
         */
        public static final int Operation_FAILED_BLE_Failed=7;


        void onAddCharacteristicFailed(int errorCode, String errorMsg);
        void onAddCharacteristicSuccess();
    }
}
