package com.leocai.detecdtion.blebrodcast;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;

import com.leocai.detecdtion.R;
import com.leocai.detecdtion.utils.BytesUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by leocai on 15-12-24.
 */
public class BleAdvertise {


    private final BluetoothLeAdvertiser advertiser;
    private final AdvertiseSettings settings;
    private AdvertiseData data;
    private final AdvertiseCallback advertisingCallback;
    private Context context;

    private static BleAdvertise ourInstance ;

    public static BleAdvertise getInstance(Context context) {
        if(ourInstance==null){
            ourInstance = new BleAdvertise(context);
        }
        return ourInstance;
    }

    private BleAdvertise(final Context context) {
        this.context = context;
        advertiser = BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();
        settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                .setConnectable(false)
                .build();
        ParcelUuid pUuid = new ParcelUuid(UUID.fromString(context.getString(R.string.ble_uuid)));
        ParcelUuid pUuid2 = new ParcelUuid(UUID.fromString(context.getString(R.string.ble_uuid2)));


        "asdddsss".getBytes(Charset.forName("UTF-8"));
        data = new AdvertiseData.Builder()
                .setIncludeTxPowerLevel(false)
                .addServiceUuid(pUuid)
//                .addServiceUuid(pUuid2)
                .addServiceData(pUuid, new byte[6])
//                .addServiceData(pUuid2,new byte[6])
                .build();

        advertisingCallback = new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
            }

            @Override
            public void onStartFailure(int errorCode) {
                Log.e("BLE", "Advertising onStartFailure: " + errorCode);

                Toast.makeText(context, "Advertising onStartFailure: " + errorCode, Toast.LENGTH_LONG).show();
                super.onStartFailure(errorCode);
            }
        };

    }

    public void advertiseSensorData(final List<Double> bufferAcc, final List<Double> bufferGyr) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 2; i++) {
                    int size = bufferAcc.size();
                    sendHead(size);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    for (int index = 0; index < bufferAcc.size(); index++) {
                        try {
                            sendSingleBuffer(index, bufferAcc.get(index),bufferGyr.get(index));
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }).start();

    }


    private void sendSingleBuffer(int index, Double accVal, Double gyrVal) {
        byte[] intBytes = BytesUtils.intShortToBytes(index);
        byte[] accBytes = BytesUtils.doubleToBytes(accVal);
        byte[] gyrBytes = BytesUtils.doubleToBytes(gyrVal);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(intBytes);
            outputStream.write(accBytes);
            outputStream.write(gyrBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte finalBytes[] = outputStream.toByteArray();
        advertiseData(finalBytes);
    }

    private void sendHead(int size) {
        byte[] intBytes = BytesUtils.intShortToBytes(size);
        byte[] bytes =new byte[2];
        bytes[0] = 0xF;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(intBytes);
            outputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte finalBytes[] = outputStream.toByteArray();
        advertiseData(finalBytes);
    }

    public void advertiseData(byte[] sdata) {
        ParcelUuid pUuid = new ParcelUuid(UUID.fromString(context.getString(R.string.ble_uuid)));
        data = new AdvertiseData.Builder()
                .setIncludeTxPowerLevel(false)
                .addServiceUuid(pUuid)
//                .addServiceUuid(pUuid2)
                .addServiceData(pUuid, sdata)
//                .addServiceData(pUuid2,new byte[6])
                .build();
        advertiser.stopAdvertising(advertisingCallback);
        advertiser.startAdvertising(settings, data, advertisingCallback);

    }

    public void advertise() {
//        advertiser.startAdvertising(settings, data, advertisingCallback);
        List<Double> bufferAcc = new ArrayList<>();
        double ba[] = new double[]{1.2, 3.5, 5.7, 6.6, 9.9, 10.2, 20.3, 5.2, 6.4, 8.4, 2.7, 4.6, 9.2};
        for (double d : ba) {
            bufferAcc.add(d);
        }
        List<Double> bufferGyr = new ArrayList<>();
        for (double d : ba) {
            bufferGyr.add((d+1));
        }
        advertiseSensorData(bufferAcc,bufferGyr);
    }


    public void stop() {

        advertiser.stopAdvertising(advertisingCallback);
    }
}
