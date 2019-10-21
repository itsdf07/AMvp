package com.itsdf07.mvp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.itsdf07.alog.ALog;
import com.itsdf07.bluetooth.ble.bean.BLEChannelSetting;
import com.itsdf07.bluetooth.ble.bean.BLEPublicSetting;
import com.itsdf07.bluetooth.ble.client.core.OKBLEDevice;
import com.itsdf07.bluetooth.ble.client.core.OKBLEDeviceImp;
import com.itsdf07.bluetooth.ble.client.core.OKBLEDeviceListener;
import com.itsdf07.bluetooth.ble.client.core.OKBLEOperation;
import com.itsdf07.bluetooth.ble.client.scan.BLEScanResult;
import com.itsdf07.mvp.works.ble.BLEMhzUtils;

import java.util.Arrays;
import java.util.HashMap;

public class BLE2Activity extends AppCompatActivity implements
        View.OnClickListener,
        OKBLEDeviceListener,
        AdapterView.OnItemSelectedListener {
    private static final String TAG = "BLE2Activity";
    public static final String EXTRA_BLEDEVICE = BLE2Activity.class.getName() + ".EXTRA_BLEDEVICE";
    public static final String UUIDWRITE = "0000ffe3-0000-1000-8000-00805f9b34fb";
    public static final String UUIDNOTIFY = "0000ffe2-0000-1000-8000-00805f9b34fb";

    private boolean isDataWriting = false;//当前是否正在写数据

    private int handshakeNum = 0;


    private BLEScanResult bleScanResult;
    OKBLEDevice okbleDevice;

    private BLEPublicSetting blePublicSetting = new BLEPublicSetting();
    /**
     * 16信道对应的独立信道协议
     */
    private HashMap<Integer, BLEChannelSetting> bleChannelSettingHashMap = new HashMap<>();

    private TextView tvConnectStatus;
    private Button btnMhzWrite;
    private Button btnMhzRead;

    private EditText etCtcss2Accept;//接收频率
    private EditText etCtcss2Send;//发送频率

    //发送的数据包个数
    private int packageDataIndex = 0;


    /**
     * gps:0-OFF,1-Automatic Mode,2-Manual Mode
     */
    private Spinner spGps;
    /**
     * 蓝牙开关:0-OFF,1-ON
     */
    private Spinner spBluetoothStatus;
    /**
     * 静噪1:0~9
     */
    private Spinner spSquelch1;
    /**
     * 声控等级:0-OFF,1~9
     */
    private Spinner spVoiceLevel;
    /**
     * 声控延时[毫秒]:0~5->500,1000,1500,2000,2500,3000
     */
    private Spinner spVoiceDelay;
    /**
     * 扫描模式:0-TO,1-CO
     */
    private Spinner spSscanType;

    /**
     * 显示模式:0-Black and white,1-Colorful
     */
    private Spinner spDisplayModel;

    /**
     * BEEP声:0-OFF,1-ON
     */
    private Spinner spBeep;

    /**
     * 发射提示音:0-OFF,1-ON
     */
    private Spinner spVoice2Send;

    /**
     * TOT超时:0-OFF,1~12->15s-180s,每15s一个选项,共12
     */
    private Spinner spTotTimeOut;

    /**
     * 屏保时间:0-OFF,1~30->5s-150s,每5s一个选项,共30
     */
    private Spinner spDisplayTime;

    /**
     * 省电模式:0-OFF,1-ON
     */
    private Spinner spPowerMode;


    /**
     * 信道选择
     */
    private Spinner spXdxz;

    /**
     * CTC/DCS解码
     */
    private Spinner spCtcss2Decode;
    /**
     * CTC/DCS编码
     */
    private Spinner spCtcss2Encode;
    /**
     * 扫描添加
     */
    private Spinner spScan;
    /**
     * 带宽
     */
    private Spinner spBandWidth;
    /**
     * 发射功率
     */
    private Spinner spTransmitPower;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (okbleDevice != null) {
            okbleDevice.disConnect(false);
            okbleDevice.remove();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ALog.eTag(TAG, "..............................................");
        setContentView(R.layout.activity_ble_channel);
        bleScanResult = getIntent().getParcelableExtra(EXTRA_BLEDEVICE);

        initView();

        okbleDevice = new OKBLEDeviceImp(this, bleScanResult);

        okbleDevice.addDeviceListener(this);
        okbleDevice.connect(true);
        initBleChannelSettingHashMap();
    }

    private void initView() {
        ((TextView) findViewById(R.id.tv_ble_info)).setText(getString(R.string.string_public_information) + "　" +
                bleScanResult.getBluetoothDevice().getName() + "->" + bleScanResult.getBluetoothDevice().getAddress());

        tvConnectStatus = findViewById(R.id.tv_connect_status);
        tvConnectStatus.setText("连接中...");

        btnMhzWrite = findViewById(R.id.btn_writeHz);
        btnMhzWrite.setOnClickListener(this);
        btnMhzRead = findViewById(R.id.btn_readHz);
        btnMhzRead.setOnClickListener(this);

        etCtcss2Accept = findViewById(R.id.et_rx);
        etCtcss2Send = findViewById(R.id.et_tx);


        /**
         * gps:0-OFF,1-Automatic Mode,2-Manual Mode
         */
        spGps = findViewById(R.id.sp_gps);
        spBluetoothStatus = findViewById(R.id.sp_bluetooth_status);
        spSquelch1 = findViewById(R.id.sp_squelch1);
        spVoiceLevel = findViewById(R.id.sp_voice_level);
        spVoiceDelay = findViewById(R.id.sp_voice_delay);
        spSscanType = findViewById(R.id.sp_scan_type);
        spDisplayModel = findViewById(R.id.sp_display_model);
        spBeep = findViewById(R.id.sp_beep);
        spVoice2Send = findViewById(R.id.sp_voice2send);
        spTotTimeOut = findViewById(R.id.sp_tot_timeout);
        spDisplayTime = findViewById(R.id.sp_display_time);
        spPowerMode = findViewById(R.id.sp_power_model);
        spXdxz = findViewById(R.id.sp_xdxz);
        spCtcss2Decode = findViewById(R.id.sp_ctcss2Decode);
        spCtcss2Encode = findViewById(R.id.sp_ctcss2Encode);
        spScan = findViewById(R.id.sp_sacn);
        spBandWidth = findViewById(R.id.sp_bandwidth);
        spTransmitPower = findViewById(R.id.sp_transmitpower);

        spGps.setOnItemSelectedListener(this);
        spBluetoothStatus.setOnItemSelectedListener(this);
        spSquelch1.setOnItemSelectedListener(this);
        spVoiceLevel.setOnItemSelectedListener(this);
        spVoiceDelay.setOnItemSelectedListener(this);
        spSscanType.setOnItemSelectedListener(this);
        spDisplayModel.setOnItemSelectedListener(this);
        spBeep.setOnItemSelectedListener(this);
        spVoice2Send.setOnItemSelectedListener(this);
        spTotTimeOut.setOnItemSelectedListener(this);
        spDisplayTime.setOnItemSelectedListener(this);
        spPowerMode.setOnItemSelectedListener(this);
        spXdxz.setOnItemSelectedListener(this);
        spCtcss2Decode.setOnItemSelectedListener(this);
        spCtcss2Encode.setOnItemSelectedListener(this);
        spScan.setOnItemSelectedListener(this);
        spBandWidth.setOnItemSelectedListener(this);
        spTransmitPower.setOnItemSelectedListener(this);
    }

    private void initBleChannelSettingHashMap() {
        for (int i = 0; i < 32; i++) {
            BLEChannelSetting bleChannelSetting = new BLEChannelSetting();
            bleChannelSetting.setChannelNum(i + 1);
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

    public void sendData(String uuid, byte data) {
        byte[] datas = new byte[1];
        datas[0] = data;
        sendData(uuid, datas);
    }

    public void sendData(String uuid, byte[] data) {

        okbleDevice.addWriteOperation(uuid, data, new OKBLEOperation.WriteOperationListener() {
            @Override
            public void onWriteValue(byte[] value) {
                ALog.eTag(TAG, "value:%s", Arrays.toString(value));
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                    }
                });

            }

            @Override
            public void onFail(int code, String errMsg) {
                ALog.eTag(TAG, "code:%s,errMsg:%s", code, errMsg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });

            }

            @Override
            public void onExecuteSuccess(OKBLEOperation.OperationType type) {
                ALog.eTag(TAG, "type:%s", type);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_writeHz:
                btnMhzWrite.setEnabled(false);
                Toast.makeText(this, "正在写数据，请稍等", Toast.LENGTH_SHORT).show();
                isDataWriting = true;
                handshakeNum = 1;
                ALog.eTag(TAG, "isDataWriting:%s,handshakeNum:%s,value:%s", isDataWriting, handshakeNum, Arrays.toString(BLEMhzUtils.handshakeProtocolHead()));
                sendData(UUIDWRITE, BLEMhzUtils.handshakeProtocolHead());
                break;
            case R.id.btn_readHz:
                isDataWriting = false;
                handshakeNum = 1;
                ALog.eTag(TAG, "isDataWriting::%s,handshakeNum::%s,value::%s", isDataWriting, handshakeNum, Arrays.toString(BLEMhzUtils.handshakeProtocolHead()));
                sendData(UUIDWRITE, BLEMhzUtils.handshakeProtocolHead());
                break;
            default:
                break;
        }
    }

    byte[] readPublie = new byte[4];

    @Override
    public void onConnected(String deviceTAG) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvConnectStatus.setText("已连接");
            }
        });
        ALog.eTag(TAG, "deviceTAG:%s", deviceTAG);
        final OKBLEOperation.OperationType[] operationType = new OKBLEOperation.OperationType[1];
//        Toast.makeText(BLEActivity.this, "通知打开中...", Toast.LENGTH_SHORT).show();
        okbleDevice.addNotifyOrIndicateOperation(UUIDNOTIFY, true, new OKBLEOperation.NotifyOrIndicateOperationListener() {
            @Override
            public void onNotifyOrIndicateComplete() {
                ALog.eTag(TAG, "通知已打开");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Toast.makeText(BLEActivity.this, "通知已打开", Toast.LENGTH_SHORT).show();
                        //addLog("onNotifyOrIndicateComplete");
                        if (operationType[0] == OKBLEOperation.OperationType.OperationType_Enable_Indicate) {
                            //  btn_indicate.setText("Indication enabled");
                            //  btn_indicate.setEnabled(false);
                        } else if (operationType[0] == OKBLEOperation.OperationType.OperationType_Enable_Notify) {
                            // btn_notify.setText("Notification enabled");
                            // btn_notify.setEnabled(false);
                        }
                    }
                });
            }

            @Override
            public void onFail(int code, final String errMsg) {
                ALog.eTag(TAG, "code:%s,errMsg:%s", code, errMsg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }

            @Override
            public void onExecuteSuccess(OKBLEOperation.OperationType type) {
                ALog.eTag(TAG, "type:%s", type.name());
                operationType[0] = type;
            }
        });
    }

    @Override
    public void onDisconnected(String deviceTAG) {
        ALog.eTag(TAG, "deviceTAG:%s", deviceTAG);
        if (okbleDevice != null) {
            okbleDevice.disConnect(false);
            okbleDevice.remove();
        }
    }

    @Override
    public void onReadBattery(String deviceTAG, int battery) {
        ALog.eTag(TAG, "deviceTAG:%s,battery:%s", deviceTAG, battery);
    }


    @Override
    public void onReceivedValue(String deviceTAG, String uuid, final byte[] value) {
        ALog.eTag(TAG, "onReceivedValue->isDataWriting:%s,handshakeNum:%s,deviceTAG:%s,uuid:%s,value:%s",
                isDataWriting, handshakeNum, deviceTAG, uuid, Arrays.toString(value));
        if (isDataWriting) {
            if (handshakeNum == 1) {
                if (value[0] == (byte) 0x06) {
                    handshakeNum++;
                    //TODO 发送 (byte) 0x02
                    sendData(UUIDWRITE, (byte) 0x02);
                }
            } else if (handshakeNum == 2) {
                if (value.length == BLEMhzUtils.acceptHandshakeProtocol().length) {
                    handshakeNum++;
                    boolean isMatch = true;
                    for (int i = 0; i < value.length; i++) {
                        if (value[i] != BLEMhzUtils.acceptHandshakeProtocol()[i]) {
                            isMatch = false;
                            break;
                        }
                    }
                    if (isMatch) {
                        Log.e(TAG, "onReceivedValue->握手成功....");
                        // TODO 发送 (byte) 0x06
                        sendData(UUIDWRITE, (byte) 0x06);
                    }

                }
            } else if (handshakeNum == 3) {
                if (value[0] == (byte) 0x06) {
                    handshakeNum = 0;
                    //TODO 开始发送第一个数据包:设置数据
                    Log.e(TAG, "onReceivedValue->handshakeNum == 3:握手成功，可以开始发送公共协议数据了");
                    sendData(UUIDWRITE, getBLEPublicDataPackage(blePublicSetting));
                }
            } else {
                if (value[0] == (byte) 0x06) {

                    //TODO 开始发送第N+1个数据包:设置数据
                    Log.e(TAG, "onReceivedValue->handshakeNum == 3:握手成功，可以开始发送第" + packageDataIndex + "个信道数据了");
                    if (packageDataIndex >= 32) {
                        sendData(UUIDWRITE, (byte) 0x45);
                        packageDataIndex = 0;
                        isDataWriting = false;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btnMhzWrite.setEnabled(true);
                            }
                        });
                    } else {
                        sendData(UUIDWRITE, getChannelDataPackage(bleChannelSettingHashMap.get(packageDataIndex)));
                        packageDataIndex++;
                    }
                }
            }
        } else {
            if (handshakeNum == 1) {
                if (value[0] == (byte) 0x06) {
                    handshakeNum++;
                    //TODO 发送 (byte) 0x02
                    sendData(UUIDWRITE, (byte) 0x02);
                }
            } else if (handshakeNum == 2) {
                if (value.length == BLEMhzUtils.acceptHandshakeProtocol().length) {
                    handshakeNum++;
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
            } else if (handshakeNum == 3) {
                if (value[0] == (byte) 0x06) {
                    handshakeNum = 0;
                    //TODO 开始发送第一个数据包:设置数据
                    Log.e(TAG, "onReceivedValue->handshakeNum == 3:握手成功，可以开始发送公共协议数据了");
                    readPublie[0] = 0x52;
                    readPublie[1] = 0x0A;
                    readPublie[2] = 0x00;
                    readPublie[3] = 0x10;
                    sendData(UUIDWRITE, readPublie);
                }
            } else {
                if (value[0] == (byte) 0x57
                        && value[1] == readPublie[1]
                        && value[2] == readPublie[2]
                        && value[3] == readPublie[3]) {
                    sendData(UUIDWRITE, (byte) 0x06);
                } else if (value[0] == (byte) 0x06) {
                    sendData(UUIDWRITE, (byte) 0x45);
                    isDataWriting = false;
                }
            }
        }
    }

    @Override
    public void onWriteValue(String deviceTAG, String uuid, byte[] value, boolean success) {
        ALog.eTag(TAG, "deviceTAG:%s,uuid:%s,success:%s,value:%s", deviceTAG, uuid, success, Arrays.toString(value));
    }

    @Override
    public void onReadValue(String deviceTAG, String uuid, byte[] value, boolean success) {
        ALog.eTag(TAG, "deviceTAG:%s,uuid:%s,success:%s,value:%s", deviceTAG, uuid, success, Arrays.toString(value));
    }

    @Override
    public void onNotifyOrIndicateComplete(String deviceTAG, String uuid, boolean enable, boolean success) {
        ALog.eTag(TAG, "deviceTAG:%s,uuid:%s,success:%s,enable:%s", deviceTAG, uuid, success, enable);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ALog.eTag(TAG, "position:%s,id:%s", position, id);
        if (null == blePublicSetting) {
            Log.w(TAG, "blePublicSetting is null");
            return;
        }
        switch (parent.getId()) {
            case R.id.sp_gps:
                blePublicSetting.setGps(spGps.getSelectedItemPosition());
                break;
            case R.id.sp_bluetooth_status:
//                Log.d("SPPPP", "position:" + position + ",value:" + spBluetoothStatus.getSelectedItem().toString());
//                spBluetoothStatus.setSelection(position,true);
                blePublicSetting.setBluetoothStatus(spBluetoothStatus.getSelectedItemPosition());
                break;
            case R.id.sp_squelch1:
                blePublicSetting.setSquelch1(spSquelch1.getSelectedItemPosition());
                break;
            case R.id.sp_voice_level:
                blePublicSetting.setVoiceLevel(spVoiceLevel.getSelectedItemPosition());
                break;
            case R.id.sp_voice_delay:
                blePublicSetting.setVoiceDelay((spVoiceDelay.getSelectedItemPosition()));
                break;
            case R.id.sp_scan_type:
                blePublicSetting.setScanType(spSscanType.getSelectedItemPosition());
                break;
            case R.id.sp_display_model:
                blePublicSetting.setDisplayModel(spDisplayModel.getSelectedItemPosition());
                break;
            case R.id.sp_beep:
                blePublicSetting.setBeep(spBeep.getSelectedItemPosition());
                break;
            case R.id.sp_voice2send:
                blePublicSetting.setVoice2Send(spVoice2Send.getSelectedItemPosition());
                break;
            case R.id.sp_tot_timeout:
                blePublicSetting.setTotTimeOut(spTotTimeOut.getSelectedItemPosition());
                break;
            case R.id.sp_display_time:
                blePublicSetting.setDisplayTime(spDisplayTime.getSelectedItemPosition());
                break;
            case R.id.sp_power_model:
                blePublicSetting.setPowerMode(spPowerMode.getSelectedItemPosition());
                break;
            case R.id.sp_xdxz:
                break;
            case R.id.sp_ctcss2Decode:
                bleChannelSettingHashMap.get(spXdxz.getSelectedItemPosition()).setCtcss2Decode(spCtcss2Decode.getSelectedItem().toString());
                break;
            case R.id.sp_ctcss2Encode:
                bleChannelSettingHashMap.get(spXdxz.getSelectedItemPosition()).setCtcss2Decode(spCtcss2Encode.getSelectedItem().toString());
                break;
            case R.id.sp_sacn:
                bleChannelSettingHashMap.get(spXdxz.getSelectedItemPosition()).setScan(position);
                break;
            case R.id.sp_bandwidth:
                bleChannelSettingHashMap.get(spXdxz.getSelectedItemPosition()).setBandwidth(position);
                break;
            case R.id.sp_transmitpower:
                bleChannelSettingHashMap.get(spXdxz.getSelectedItemPosition() + 1).setTransmitPower(position);
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        ALog.eTag(TAG, "onNothingSelected................");
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
