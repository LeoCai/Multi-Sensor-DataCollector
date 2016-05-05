package com.leocai.detecdtion.core;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.leocai.publiclibs.PublicConstants;
import com.leocai.publiclibs.ShakingData;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * ReadDataFrom Wear
 * Created by leocai on 16-1-11.
 */
public class WearDataGetter extends Observable {

    private static final String TAG = "WearDataGetter";
    private  BluetoothServerSocket mmServerSocket;

    public WearDataGetter() {
        BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();

        try {
            mmServerSocket = mAdapter.listenUsingInsecureRfcommWithServiceRecord(PublicConstants.NAME_WEAR_DATA,
                    PublicConstants.WEAR_UUID_INSECURE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startListen(final ShakingDataGetterCallback shakingDataGetterCallback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG,"Ble Listening...");
                    BluetoothSocket socket = mmServerSocket.accept();
                    Log.d(TAG,"Ble Connected");
                    InputStream in = socket.getInputStream();
                    byte[] buffer = new ShakingData().getBytes();
                    List<ShakingData> shakingDatas = new ArrayList<>();
                    for (int i = 0; i < PublicConstants.SHAKING_DATA_SIZE; i++) {
                        in.read(buffer);
                        ShakingData shakingData = new ShakingData(buffer);
                        shakingDatas.add(shakingData);
                    }
                    in.close();
                    socket.close();
                    shakingDataGetterCallback.onGetShakingDatas(shakingDatas);
                    setChanged();
                    notifyObservers(shakingDatas);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
