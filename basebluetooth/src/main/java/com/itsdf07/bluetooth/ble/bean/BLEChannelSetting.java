package com.itsdf07.bluetooth.ble.bean;

/**
 * @Description ：信道协议
 * @Author itsdf07
 * @Time 2017/12/8
 */

public class BLEChannelSetting {

    /**
     * 信道的值+1
     */
    private long id = 0L;
    /**
     * 信道
     */
    private int channelNum = 1;
    /**
     * 发射频率
     */
    private String tx2Send = "";

    /**
     * 接收频率
     */
    private String tx2Receive = "";//462.0125
    /**
     * 亚音频解码（CTC/DCS解码）
     */
    private String ctcss2Decode = "";
    /**
     * 亚音频编码（CTC/DCS编码）
     */
    private String ctcss2Encode = "";

    /**
     * 添加扫描
     */
    private int scan = 0;
    /**
     * [通信] 频带宽度
     */
    private int bandwidth = 0;
    /**
     * 发射功率
     */
    private int transmitPower = 0;
    /**
     * 繁忙锁定
     */
    private int busyLock = 0;
    /**
     * 扰频
     */
    private int scramble = 0;
    /**
     * 压扩
     */
    private int companding = 0;
    /**
     * 特殊信令
     */
    private int specialSignaling = 0;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getChannelNum() {
        return channelNum;
    }

    public void setChannelNum(int channelNum) {
        this.channelNum = channelNum;
    }

    public String getTx2Send() {
        return tx2Send;
    }

    public void setTx2Send(String tx2Send) {
        if (tx2Send.toUpperCase().equals("FFF.FFFF")) {
            tx2Send = "";
        }
        this.tx2Send = tx2Send;
    }

    public String getTx2Receive() {
        return tx2Receive;
    }

    public void setTx2Receive(String tx2Receive) {
        if (tx2Receive.toUpperCase().equals("FFF.FFFF")) {
            tx2Receive = "";
        }
        this.tx2Receive = tx2Receive;
    }

    public String getCtcss2Decode() {
        return ctcss2Decode;
    }

    public void setCtcss2Decode(String ctcss2Decode) {
        this.ctcss2Decode = ctcss2Decode;
    }

    public String getCtcss2Encode() {
        return ctcss2Encode;
    }

    public void setCtcss2Encode(String ctcss2Encode) {
        this.ctcss2Encode = ctcss2Encode;
    }

    public int getScan() {
        return scan;
    }

    public void setScan(int scan) {
        this.scan = scan;
    }

    public int getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(int bandwidth) {
        this.bandwidth = bandwidth;
    }

    public int getTransmitPower() {
        return transmitPower;
    }

    public void setTransmitPower(int transmitPower) {
        this.transmitPower = transmitPower;
    }

    public int getBusyLock() {
        return busyLock;
    }

    public void setBusyLock(int busyLock) {
        this.busyLock = busyLock;
    }

    public int getScramble() {
        return scramble;
    }

    public void setScramble(int scramble) {
        this.scramble = scramble;
    }

    public int getCompanding() {
        return companding;
    }

    public void setCompanding(int companding) {
        this.companding = companding;
    }

    public int getSpecialSignaling() {
        return specialSignaling;
    }

    public void setSpecialSignaling(int specialSignaling) {
        this.specialSignaling = specialSignaling;
    }

    @Override
    public String toString() {
        return "信道:" + channelNum
                + ",发射频率:" + tx2Send + ",接收频率:" + tx2Receive
                + ",CTC/DCS解码:" + ctcss2Decode + ",CTC/DCS编码:" + ctcss2Encode
                + ",是否添加扫描:" + scan + ",带宽:" + bandwidth + ",发射功率:" + transmitPower + ",是否繁忙锁定:" + busyLock
                + ",是否扰频:" + scramble + ",是否压扩:" + companding + ",是否特殊信令:" + specialSignaling;
    }
}
