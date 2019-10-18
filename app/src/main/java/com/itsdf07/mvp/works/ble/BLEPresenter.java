package com.itsdf07.mvp.works.ble;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.itsdf07.base.mvp.presenter.BaseMvpPresenter;
import com.itsdf07.bluetooth.ble.bean.BLEChannelSetting;
import com.itsdf07.bluetooth.ble.bean.BLEPublicSetting;
import com.itsdf07.bluetooth.ble.client.core.OKBLEDevice;
import com.itsdf07.bluetooth.ble.client.core.OKBLEDeviceImp;
import com.itsdf07.bluetooth.ble.client.core.OKBLEDeviceListener;
import com.itsdf07.bluetooth.ble.client.core.OKBLEOperation;
import com.itsdf07.bluetooth.ble.client.scan.BLEScanResult;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description:
 * @Author itsdf07
 * @Date 2019/10/17
 */
public class BLEPresenter extends BaseMvpPresenter<BLEContracts.IBLEView> implements BLEContracts.IBLEPresenter, OKBLEDeviceListener {
    public static final String UUIDWRITE = "0000ffe3-0000-1000-8000-00805f9b34fb";
    public static final String UUIDNOTIFY = "0000ffe2-0000-1000-8000-00805f9b34fb";
    public static final String EXTRA_BLEDEVICE = BLEActivity.class.getName() + ".EXTRA_BLEDEVICE";
    /**
     * 信道数
     */
    private static final int CHANNELS = 32;
    private BLEScanResult bleScanResult;

    private OKBLEDevice okbleDevice;

    private BLEPublicSetting blePublicSetting;
    /**
     * 32信道对应的独立信道协议,其中Integer = 0 时为公共协议数据
     */
    private HashMap<Integer, Object> bleChannelSettingHashMap = new HashMap<>();
    /**
     * 当前是否正在写数据
     */
    private boolean isDataWriting;
    /**
     * 与下位机的握手次数
     */
    private int handshakeNum = 0;

    /**
     * 发送的数据包个数
     */
    private int datasIndex2Write = 0;

    public BLEPresenter(BLEContracts.IBLEView view) {
        super(view);
    }

    @Override
    public void setBLEDevice(BLEScanResult device) {
        this.bleScanResult = device;
        okbleDevice = new OKBLEDeviceImp(getView().getSelfActivity(), bleScanResult);
        okbleDevice.addDeviceListener(this);

        //TODO 暂时在类实例时自带默认值，后续需要进行读取初始化
        blePublicSetting = new BLEPublicSetting();
        bleChannelSettingHashMap.put(0, blePublicSetting);
        /**
         *初始化信道
         */
        for (int i = 1; i <= CHANNELS; i++) {
            BLEChannelSetting bleChannelSetting = new BLEChannelSetting();
            bleChannelSetting.setChannelNum(i);
            bleChannelSetting.setTx2Send("400.12500");
            bleChannelSetting.setTx2Receive("400.12500");
            bleChannelSetting.setCtcss2Decode("D023N");
            bleChannelSetting.setCtcss2Encode("67.0");
            bleChannelSetting.setTransmitPower(1);
            bleChannelSetting.setScan(0);
            bleChannelSetting.setBandwidth(1);
            bleChannelSettingHashMap.put(i, bleChannelSetting);
        }
    }

    @Override
    public BLEScanResult getBLEDevice() {
        return bleScanResult;
    }

    @Override
    public void connectBLE() {
        okbleDevice.connect(true);
    }

    @Override
    public void disConnectBLE() {
        if (okbleDevice != null) {
            okbleDevice.disConnect(false);
            okbleDevice.remove();
        }
    }

    @Override
    public HashMap<Integer, Object> readDatas() {
        return null;
    }

    @Override
    public void writeDatas() {
        sendData(UUIDWRITE, BLEMhzUtils.handshakeProtocolHead());
    }

    @Override
    public BLEPublicSetting getBLEPublicSetting() {
        return (BLEPublicSetting) bleChannelSettingHashMap.get(0);
    }

    @Override
    public BLEChannelSetting getBLEChannelSetting(int channelNum) {
        return (BLEChannelSetting) bleChannelSettingHashMap.get(channelNum);
    }


    @Override
    public void onConnected(String deviceTAG) {
        Log.e(TAG, "onConnected->deviceTAG:" + deviceTAG);
        getView().updataBLEConnectStatus("已连接");
        final OKBLEOperation.OperationType[] operationType = new OKBLEOperation.OperationType[1];
//        Toast.makeText(BLEActivity.this, "通知打开中...", Toast.LENGTH_SHORT).show();
        okbleDevice.addNotifyOrIndicateOperation(UUIDNOTIFY, true, new OKBLEOperation.NotifyOrIndicateOperationListener() {
            @Override
            public void onNotifyOrIndicateComplete() {
                Log.e(TAG, "onNotifyOrIndicateComplete->通知已打开");
            }

            @Override
            public void onFail(int code, final String errMsg) {
                Log.e(TAG, "onFail->code:" + code + ",errMsg:" + errMsg);
            }

            @Override
            public void onExecuteSuccess(OKBLEOperation.OperationType type) {
                Log.e(TAG, "onExecuteSuccess->type:" + type.name());
                operationType[0] = type;
            }
        });
    }

    @Override
    public void onDisconnected(String deviceTAG) {
        Log.e(TAG, "onDisconnected->deviceTAG:" + deviceTAG);
        disConnectBLE();
    }

    @Override
    public void onReadBattery(String deviceTAG, int battery) {
        Log.e(TAG, "onReadBattery->deviceTAG:" + deviceTAG + ",battery:" + battery);
    }

    private int count = 0;
    byte[] readPublie = new byte[4];

    @Override
    public void onReceivedValue(String deviceTAG, String uuid, final byte[] value) {
        Log.e(TAG, "onReceivedValue->deviceTAG:" + deviceTAG + ",uuid:" + uuid + ",value:" + Arrays.toString(value));
        if (isDataWriting) {
            switch (handshakeNum) {//当前为第一次握手时下位机响应回来的信息:06
                case 1://
                    if (value[0] == (byte) 0x06) {
                        count++;
                        //TODO 发送 (byte) 0x02
                        sendData(UUIDWRITE, (byte) 0x02);
                    }
                    break;
                case 2://当前为第二次握手时下位机响应回来的信息:50 33 31 30 37 00 00 00
                    if (value.length == BLEMhzUtils.acceptHandshakeProtocol().length) {
                        count++;
                        boolean isMatch = true;
                        for (int i = 0; i < value.length; i++) {
                            if (value[i] != BLEMhzUtils.acceptHandshakeProtocol()[i]) {
                                isMatch = false;
                                break;
                            }
                        }
                        if (isMatch) {
                            // TODO 发送 (byte) 0x06
                            sendData(UUIDWRITE, (byte) 0x06);
                        }

                    }
                    break;
                case 3://当前为第三次握手时下位机响应回来的信息:06(本次为握手最后一步，往下即可开始写入数据了)
                    if (value[0] == (byte) 0x06) {
                        count = 0;
                        //TODO 开始发送第一个数据包:设置数据
                        Log.e(TAG, "onReceivedValue->正在发送公共协议数据");
                        datasIndex2Write = 0;
                        sendData(UUIDWRITE, getBLEPublicDataPackage(getBLEPublicSetting()));
                    }
                    break;
                default:
                    if (value[0] == (byte) 0x06) {
                        //TODO 开始发送第N+1个数据包:设置数据

                        if (datasIndex2Write >= 32) {
                            sendData(UUIDWRITE, (byte) 0x45);
                            datasIndex2Write = 0;
                            isDataWriting = false;
                        } else {
                            ++datasIndex2Write;
                            BLEChannelSetting bleChannelSetting = (BLEChannelSetting) bleChannelSettingHashMap.get(datasIndex2Write);
                            Log.e(TAG, "onReceivedValue->正在发送信道" + bleChannelSetting.getChannelNum() + "数据了");
                            sendData(UUIDWRITE, getChannelDataPackage(bleChannelSetting));
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void onWriteValue(String deviceTAG, String uuid, byte[] value, boolean success) {
        Log.e(TAG, "onWriteValue->deviceTAG:" + deviceTAG + ",uuid:" + uuid + ",value:" + Arrays.toString(value));
    }

    @Override
    public void onReadValue(String deviceTAG, String uuid, byte[] value, boolean success) {
        Log.e(TAG, "onReadValue->deviceTAG:" + deviceTAG + ",uuid:" + uuid + ",success:" + success + ",value:" + Arrays.toString(value));
    }

    @Override
    public void onNotifyOrIndicateComplete(String deviceTAG, String uuid, boolean enable, boolean success) {
        Log.e(TAG, "onNotifyOrIndicateComplete->deviceTAG:" + deviceTAG + ",uuid:" + uuid + ",success:" + success + ",enable:" + enable);
    }


    public void sendData(String uuid, byte data) {
        byte[] datas = new byte[1];
        datas[0] = data;
        sendData(uuid, datas);
    }

    public void sendData(String uuid, byte[] data) {
        okbleDevice.addWriteOperation(uuid, data, new OKBLEOperation.WriteOperationListener() {
            @Override
            public void onWriteValue(byte[] value) {
                Log.e(TAG, "onWriteValue->value:" + Arrays.toString(value));
            }

            @Override
            public void onFail(int code, String errMsg) {
                Log.e(TAG, "onFail->code:" + code + ",errMsg:" + errMsg);

            }

            @Override
            public void onExecuteSuccess(OKBLEOperation.OperationType type) {
                Log.e(TAG, "onExecuteSuccess->type:" + type.name());
            }
        });
    }


    private byte[] getBLEPublicDataPackage(BLEPublicSetting blePublicSetting) {
        byte[] datas = new byte[20];
        datas[0] = 0x57;
        datas[1] = 0x0A;
        datas[2] = 0x00;
        datas[3] = 0x10;

        datas[4] = (byte) blePublicSetting.getGps();
        datas[5] = (byte) blePublicSetting.getBluetoothStatus();
        datas[6] = (byte) blePublicSetting.getSquelch1();
        datas[7] = (byte) 0xFF;
        datas[8] = (byte) blePublicSetting.getVoiceLevel();
        datas[9] = (byte) blePublicSetting.getVoiceDelay();
        datas[10] = (byte) blePublicSetting.getScanType();
        datas[11] = (byte) blePublicSetting.getDisplayModel();
        datas[12] = (byte) blePublicSetting.getBeep();
        datas[13] = (byte) blePublicSetting.getVoice2Send();
        datas[14] = (byte) blePublicSetting.getTotTimeOut();
        datas[15] = (byte) blePublicSetting.getDisplayTime();
        datas[16] = (byte) blePublicSetting.getPowerMode();
        datas[17] = (byte) 0xFF;
        datas[18] = (byte) 0xFF;
        datas[19] = (byte) 0xFF;
        return datas;
    }


    private byte[] getChannelDataPackage(BLEChannelSetting bleChannelSetting) {
        byte[] recHz = tx2Hex(bleChannelSetting.getTx2Receive());
        byte[] sendHz = tx2Hex(bleChannelSetting.getTx2Send());
        byte[] decodeCtcDcs = converCtcDcs2DEC(bleChannelSetting.getCtcss2Decode());
        byte[] encodeCtcDcs = converCtcDcs2DEC(bleChannelSetting.getCtcss2Encode());
        byte[] datas = new byte[20];
        short address = (short) ((bleChannelSetting.getChannelNum() - 1) * 16);
        datas[0] = 0x57;
        datas[1] = (byte) (address >> 8);
        datas[2] = (byte) address;
        datas[3] = 0x10;

        datas[4] = recHz[0];
        datas[5] = recHz[1];
        datas[6] = recHz[2];
        datas[7] = recHz[3];
        datas[8] = sendHz[0];
        datas[9] = sendHz[1];
        datas[10] = sendHz[2];
        datas[11] = sendHz[3];
        datas[12] = encodeCtcDcs[0];
        datas[13] = encodeCtcDcs[1];
        datas[14] = decodeCtcDcs[0];
        datas[15] = decodeCtcDcs[1];
        datas[16] = (byte) ((byte) bleChannelSetting.getTransmitPower() + (((byte) bleChannelSetting.getBandwidth()) << 1));
        datas[17] = (byte) (((byte) bleChannelSetting.getBandwidth()) << 1);
        datas[18] = (byte) 0xFF;
        datas[19] = (byte) 0xFF;
        return datas;
    }


    /**
     * 信道频率数据
     * 把462.0125的转成发送格式为：50 12 20 46
     *
     * @param param 462.0125
     * @return
     */
    private byte[] tx2Hex(String param) {
        try {
            byte[] tx = new byte[4];
            long l = new Double(Double.valueOf(Double.valueOf(param).doubleValue() * 100000.0D).doubleValue()).longValue();
            param = l + "";
            if (param.length() != 8) {
                Log.d(TAG, "信道频率有误,param:" + param);
                return null;
            }
            tx[0] = Byte.parseByte(param.substring(6, 8));
            tx[1] = Byte.parseByte(param.substring(4, 6));
            tx[2] = Byte.parseByte(param.substring(2, 4));
            tx[3] = Byte.parseByte(param.substring(0, 2));
            return tx;
        } catch (Exception e) {
            Log.e(TAG, "e:", e);
        }
        return null;
    }

    /**
     * CTC/DCS 编解码转换成DEC数据
     * 如编解码 69.3 转成DEC数据：9306
     *
     * @param value 如 69.3
     * @return 9306
     */
    private byte[] converCtcDcs2DEC(String value) {
        byte[] dec = new byte[2];
        if (value.contains("I")) {//0xA800
            value = value.replace("D", "").replace("I", "");
            short decValue = (short) (Integer.valueOf(value, 8) + 0xA800);
            dec[0] = (byte) decValue;
            dec[1] = (byte) (decValue >> 8);
        } else if (value.contains("N")) {//0x2800
            value = value.replace("D", "").replace("N", "");
            short decValue = (short) (Integer.valueOf(value, 8) + 0x2800);
            dec[0] = (byte) decValue;
            dec[1] = (byte) (decValue >> 8);
        } else if (value.contains(".")) {
            if (!(value + "").matches("[0-9]*")) {
                dec[0] = (byte) 0xFF;
                dec[1] = (byte) 0xFF;
                return dec;
            }
            short decValue = (short) (Double.valueOf(value) * 10);
            dec[0] = (byte) decValue;
            dec[1] = (byte) (decValue >> 8);
        } else {
            dec[0] = (byte) 0xFF;
            dec[1] = (byte) 0xFF;
        }
        return dec;
    }
}
