package com.leocai.detecdtion.blebrodcast;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;

import com.leocai.detecdtion.R;
import com.leocai.detecdtion.SensorDataWithIndex;
import com.leocai.detecdtion.utils.BytesUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Set;
import java.util.UUID;

/**
 * Created by leocai on 15-12-24.
 */
public class BleDiscover extends Observable {
    private static final String TAG_BLE_DISCOVER = "BleDiscover";
    private String advData;
    private BluetoothLeScanner mBluetoothLeScanner;
    private Handler mHandler = new Handler();

    private double accData[];
    private double gyrData[];

    private byte[] svdata;

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (result == null
                    || result.getDevice() == null)
                return;
            Log.d(TAG_BLE_DISCOVER, result.getDevice().getAddress());

            StringBuilder builder = new StringBuilder(result.getDevice().getAddress());
//            byte[] svdata = result.getScanRecord().getServiceData(result.getScanRecord().getServiceUuids().get(0));
            Set<ParcelUuid> keset = result.getScanRecord().getServiceData().keySet();
            for (ParcelUuid pu : keset) {
                svdata = result.getScanRecord().getServiceData().get(pu);
                if (svdata == null) return;
//                builder.append("\n").append(new String(svdata, Charset.forName("UTF-8")));

//                if (BytesUtils.checkSizeType(svdata)) {
//                    int size = BytesUtils.getSizeFromBytes(svdata);
//                    if (accData == null) accData = new double[size];
//                    if (gyrData == null) gyrData = new double[size];
//                    setAdvData("" + size);
//                    Log.d(TAG_BLE_DISCOVER, "" + size);
//                } else {
//                    SensorDataWithIndex sensorDataWithIndex = BytesUtils.getSensorDataWithIndexFromBytes(svdata);
//                    if (accData != null)
//                        accData[sensorDataWithIndex.getIndex()] = sensorDataWithIndex.getAccVal();
//                    if (gyrData != null)
//                        gyrData[sensorDataWithIndex.getIndex()] = sensorDataWithIndex.getGyrVal();
//
//                    setAdvData("" + sensorDataWithIndex.toString());
//                    Log.d(TAG_BLE_DISCOVER, "" + sensorDataWithIndex.toString());
//                }
                setSvdata(svdata);

//
//                setChanged();
//                notifyObservers();
            }

        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e(TAG_BLE_DISCOVER, "Discovery onScanFailed: " + errorCode);
            super.onScanFailed(errorCode);
        }
    };
    private List<ScanFilter> filters = new ArrayList<>();
    private ScanSettings settings;

    private static BleDiscover instance;

    public static BleDiscover getInstance(Context context) {
        if (instance == null) {
            instance = new BleDiscover(context);
        }
        return instance;
    }

    private BleDiscover(Context context) {
        mBluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
        ScanFilter filter = new ScanFilter.Builder()
                .setServiceUuid(new ParcelUuid(UUID.fromString(context.getString(R.string.ble_uuid))))
                .build();
        filters.add(filter);
        settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();
    }

    public void discover() {
        mBluetoothLeScanner.startScan(filters, settings, mScanCallback);
    }

    public String getAdvData() {
        return advData;
    }

    public void setAdvData(String advData) {
        this.advData = advData;
    }

    public void stop() {
        mBluetoothLeScanner.stopScan(mScanCallback);
    }

    public double[] getAccData() {
        return accData;
    }

    public byte[] getSvdata() {
        return svdata;
    }

    public void setSvdata(byte[] svdata) {
        this.svdata = svdata;
        setChanged();
        notifyObservers(svdata);
    }
}
