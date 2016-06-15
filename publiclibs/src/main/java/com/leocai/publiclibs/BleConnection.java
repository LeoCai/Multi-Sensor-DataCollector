package com.leocai.publiclibs;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by leocai on 16-1-10.
 * 蓝牙连接客户端
 *
 */
public class BleConnection {

    private BluetoothSocket bleSocket;
    private BluetoothAdapter mAdapter;
    private InputStream in;
    private OutputStream out;

    public BleConnection(){
        mAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void connect(String address, final ConnectedCallBack connectedCallBack) {
        try {
            BluetoothDevice device = mAdapter.getRemoteDevice(address);
            bleSocket = device.createInsecureRfcommSocketToServiceRecord(PublicConstants.WEAR_UUID_INSECURE);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        bleSocket.connect();
                        setIn(bleSocket.getInputStream());
                        setOut(bleSocket.getOutputStream());
                        connectedCallBack.onConnected(in);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void close() throws IOException {
        bleSocket.close();
    }


    public void setIn(InputStream in) {
        this.in = in;
    }

    public InputStream getIn() {
        return in;
    }

    public void setOut(OutputStream out) {
        this.out = out;
    }

    public OutputStream getOut() {
        return out;
    }
}
