package com.itsdf07.mvp.works.ble;

import android.util.Log;
import android.widget.Toast;

import com.itsdf07.base.mvp.presenter.BaseMvpPresenter;
import com.itsdf07.bluetooth.LinkedHashMap;
import com.itsdf07.bluetooth.ble.client.scan.BLEScanResult;
import com.itsdf07.bluetooth.ble.client.scan.DeviceScanCallBack;
import com.itsdf07.bluetooth.ble.client.scan.OKBLEScanManager;

/**
 * @Description:
 * @Author itsdf07
 * @Date 2019/10/16
 */
public class ScanBLEPresenter extends BaseMvpPresenter<ScanBLEContracts.IScanBLEView> implements ScanBLEContracts.IScanBLEPresenter {
    private OKBLEScanManager scanManager;
    /**
     * 扫描出来的蓝牙设备集合
     */
    private LinkedHashMap<String, BLEScanResult> scanedResults;

    public ScanBLEPresenter(ScanBLEContracts.IScanBLEView view) {
        super(view);
        scanedResults = new LinkedHashMap<>();
        scanManager = new OKBLEScanManager(view.getSelfActivity());
        scanManager.setScanCallBack(scanCallBack);
    }

    @Override
    public LinkedHashMap<String, BLEScanResult> getBLEs() {
        return scanedResults;
    }

    @Override
    public void startScan() {
        scanManager.startScan();
    }

    @Override
    public void stopScan() {
        scanManager.stopScan();
    }


    DeviceScanCallBack scanCallBack = new DeviceScanCallBack() {
        @Override
        public void onBLEDeviceScan(BLEScanResult device, int rssi) {
            Log.d(TAG, "onBLEDeviceScan->device:" + device.toString());
            int value[] = scanedResults.put(device.getMacAddress(), device);

            if (value[1] == 1) {
                //这是新增数据,
                getView().notifyUpdata2Adapter();
            } else {
                //这是重复数据,刷新rssi
                int index = value[0];
                getView().notifyUpdata2Item(index);
            }
        }

        @Override
        public void onFailed(int code) {
            Log.e(TAG, "onFailed->code:" + code);
            switch (code) {
                case DeviceScanCallBack.SCAN_FAILED_BLE_NOT_SUPPORT:
                    Toast.makeText(getView().getSelfActivity(), "该设备不支持BLE", Toast.LENGTH_SHORT).show();
//                    refreshLayout.finishRefresh(false);
                    break;
                case DeviceScanCallBack.SCAN_FAILED_BLUETOOTH_DISABLE:
                    Toast.makeText(getView().getSelfActivity(), "请打开手机蓝牙", Toast.LENGTH_SHORT).show();
//                    refreshLayout.finishRefresh(false);
                    break;
                case DeviceScanCallBack.SCAN_FAILED_LOCATION_PERMISSION_DISABLE:
                    Toast.makeText(getView().getSelfActivity(), "请授予位置权限以扫描周围的蓝牙设备", Toast.LENGTH_SHORT).show();
//                    refreshLayout.finishRefresh(false);
                    break;
                case DeviceScanCallBack.SCAN_FAILED_LOCATION_PERMISSION_DISABLE_FOREVER:
//                    refreshLayout.finishRefresh(false);
                    Toast.makeText(getView().getSelfActivity(), "位置权限被您永久拒绝,请在设置里授予位置权限以扫描周围的蓝牙设备", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onStartSuccess() {
            Log.e(TAG, "onStartSuccess->...");
//            refreshLayout.finishRefresh();
        }
    };
}
