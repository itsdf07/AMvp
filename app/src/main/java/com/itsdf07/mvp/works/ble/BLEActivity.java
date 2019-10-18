package com.itsdf07.mvp.works.ble;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.itsdf07.alog.ALog;
import com.itsdf07.base.mvp.BaseMvpActivity;
import com.itsdf07.bluetooth.ble.client.scan.BLEScanResult;
import com.itsdf07.mvp.R;


/**
 * @Description:
 * @Author itsdf07
 * @Date 2019/10/17
 */
public class BLEActivity extends BaseMvpActivity<BLEPresenter> implements BLEContracts.IBLEView,
        View.OnClickListener,
        AdapterView.OnItemSelectedListener {
    private TextView tvConnectStatus;
    private TextView tvDeviceInfo;
    private Button btnMhzWrite;
    private Button btnMhzRead;
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
    private Spinner spScanType;

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
    public BLEPresenter initPresenter() {
        return new BLEPresenter(this);
    }

    @Override
    public void afterPresenter() {
        presenter.setBLEDevice((BLEScanResult) getIntent().getParcelableExtra(BLEPresenter.EXTRA_BLEDEVICE));
        tvDeviceInfo.setText(getString(R.string.string_public_information) + "　" +
                presenter.getBLEDevice().getBluetoothDevice().getName() + "->" + presenter.getBLEDevice().getBluetoothDevice().getAddress());
        presenter.connectBLE();
    }

    @Override
    protected void onDestroy() {
        presenter.disConnectBLE();
        super.onDestroy();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_ble_channel;
    }

    @Override
    public void initView() {
        tvDeviceInfo = findViewById(R.id.tv_ble_info);

        tvConnectStatus = findViewById(R.id.tv_connect_status);
        tvConnectStatus.setText("连接中...");

        btnMhzWrite = findViewById(R.id.btn_writeHz);
        btnMhzWrite.setOnClickListener(this);
        btnMhzRead = findViewById(R.id.btn_readHz);
        btnMhzRead.setOnClickListener(this);


        /**
         * gps:0-OFF,1-Automatic Mode,2-Manual Mode
         */
        spGps = findViewById(R.id.sp_gps);
        spBluetoothStatus = findViewById(R.id.sp_bluetooth_status);
        spSquelch1 = findViewById(R.id.sp_squelch1);
        spVoiceLevel = findViewById(R.id.sp_voice_level);
        spVoiceDelay = findViewById(R.id.sp_voice_delay);
        spScanType = findViewById(R.id.sp_scan_type);
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
        spScanType.setOnItemSelectedListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_writeHz:
                presenter.writeDatas();
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ALog.eTag(TAG, "position:%s,id:%s", position, id);
        switch (parent.getId()) {
            case R.id.sp_gps:
                presenter.getBLEPublicSetting().setGps(position);
                break;
            case R.id.sp_bluetooth_status:
                presenter.getBLEPublicSetting().setBluetoothStatus(position);
                break;
            case R.id.sp_squelch1:
                presenter.getBLEPublicSetting().setSquelch1(position);
                break;
            case R.id.sp_voice_level:
                presenter.getBLEPublicSetting().setVoiceLevel(position);
                break;
            case R.id.sp_voice_delay:
                presenter.getBLEPublicSetting().setVoiceDelay(position);
                break;
            case R.id.sp_scan_type:
                presenter.getBLEPublicSetting().setScanType(position);
                break;
            case R.id.sp_display_model:
                presenter.getBLEPublicSetting().setDisplayModel(position);
                break;
            case R.id.sp_beep:
                presenter.getBLEPublicSetting().setBeep(position);
                break;
            case R.id.sp_voice2send:
                presenter.getBLEPublicSetting().setVoice2Send(position);
                break;
            case R.id.sp_tot_timeout:
                presenter.getBLEPublicSetting().setTotTimeOut(position);
                break;
            case R.id.sp_display_time:
                presenter.getBLEPublicSetting().setDisplayTime(position);
                break;
            case R.id.sp_power_model:
                presenter.getBLEPublicSetting().setPowerMode(position);
                break;
            case R.id.sp_xdxz:
                break;
            case R.id.sp_ctcss2Decode:
                presenter.getBLEChannelSetting(spXdxz.getSelectedItemPosition() + 1).setCtcss2Decode(spCtcss2Decode.getSelectedItem().toString());
                break;
            case R.id.sp_ctcss2Encode:
                presenter.getBLEChannelSetting(spXdxz.getSelectedItemPosition() + 1).setCtcss2Decode(spCtcss2Encode.getSelectedItem().toString());
                break;
            case R.id.sp_sacn:
                presenter.getBLEChannelSetting(spXdxz.getSelectedItemPosition() + 1).setScan(position);
                break;
            case R.id.sp_bandwidth:
                presenter.getBLEChannelSetting(spXdxz.getSelectedItemPosition() + 1).setBandwidth(position);
                break;
            case R.id.sp_transmitpower:
                presenter.getBLEChannelSetting(spXdxz.getSelectedItemPosition() + 1).setTransmitPower(position);
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        ALog.eTag(TAG,"...");
    }

    @Override
    public void updataBLEConnectStatus(final String content) {
        ALog.eTag(TAG,"content:%s",content);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvConnectStatus.setText(content);
            }
        });
    }
}
