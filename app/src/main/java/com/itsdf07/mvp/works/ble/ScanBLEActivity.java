package com.itsdf07.mvp.works.ble;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.itsdf07.base.mvp.BaseMvpActivity;
import com.itsdf07.mvp.BLE2Activity;
import com.itsdf07.mvp.R;


/**
 * @Description: 蓝牙BLE扫描界面
 * @Author itsdf07
 * @Date 2019/10/16
 */
public class ScanBLEActivity extends BaseMvpActivity<ScanBLEPresenter> implements ScanBLEContracts.IScanBLEView, View.OnClickListener {
    private Button btnStartScanBLE, btnStopScanBLE;
    private ListView lvBLEs;
    public BLEAdapter bleAdapter;

    @Override
    public ScanBLEPresenter initPresenter() {
        return new ScanBLEPresenter(this);
    }

    @Override
    public void afterPresenter() {
        lvBLEs.setAdapter(bleAdapter);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_scan_ble;
    }

    @Override
    public void initView() {
        btnStartScanBLE = $(R.id.btn_start_scan_ble);
        btnStartScanBLE.setOnClickListener(this);
        btnStopScanBLE = $(R.id.btn_stop_scan_ble);
        btnStopScanBLE.setOnClickListener(this);

        lvBLEs = $(R.id.lv_bles);
        bleAdapter = new BLEAdapter();
        lvBLEs.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                presenter.stopScan();
                Intent intent = new Intent(ScanBLEActivity.this, BLE2Activity.class);
                intent.putExtra(BLE2Activity.EXTRA_BLEDEVICE, presenter.getBLEs().get(position));
                startActivity(intent);
                return true;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.stopScan();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_scan_ble:
                presenter.startScan();
                break;
            case R.id.btn_stop_scan_ble:
                presenter.stopScan();
                break;

            default:
                break;
        }
    }


    //-----------------------------------------
    private class BLEAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return presenter.getBLEs() == null ? 0 : presenter.getBLEs().size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewholder;
            if (convertView == null) {
                viewholder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.item_bluetooth_device, null);
                convertView.setTag(viewholder);
                viewholder.tv_ble_name = convertView.findViewById(R.id.tv_ble_name);
                viewholder.tv_ble_mac = convertView.findViewById(R.id.tv_ble_mac);
                viewholder.tv_ble_rssi = convertView.findViewById(R.id.tv_ble_rssi);
            } else {
                viewholder = (ViewHolder) convertView.getTag();
            }
            viewholder.tv_ble_name.setText(presenter.getBLEs().get(position).getBluetoothDevice().getName());
            viewholder.tv_ble_mac.setText(presenter.getBLEs().get(position).getBluetoothDevice().getAddress());
            viewholder.tv_ble_rssi.setText(presenter.getBLEs().get(position).getRssi() + "");
            return convertView;
        }

        public void updatePosition(int posi) {
            int visibleFirstPosi = lvBLEs.getFirstVisiblePosition();
            int visibleLastPosi = lvBLEs.getLastVisiblePosition();
            if (posi >= visibleFirstPosi && posi <= visibleLastPosi) {
                View view = lvBLEs.getChildAt(posi - visibleFirstPosi);
                ViewHolder holder = (ViewHolder) view.getTag();
            }
        }

        class ViewHolder {
            TextView tv_ble_name;
            TextView tv_ble_mac;
            TextView tv_ble_rssi;
        }

    }

    @Override
    public void notifyUpdata2Adapter() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bleAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void notifyUpdata2Item(final int index) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bleAdapter.updatePosition(index);
//                bleAdapter.notifyDataSetChanged();
            }
        });

    }
}
