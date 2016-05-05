package com.leocai.hellowear;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.leocai.publiclibs.PublicConstants;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

public class BleActivity extends Activity {

    private static final String TAG = "BluetoothChatFragment";

    private TextView tvDevices;
    private TextView tvLog;

    /**
     * Member object for the chat services
     */
    private BluetoothChatService mChatService = null;

    /**
     * Member fields
     */
    private BluetoothAdapter mBtAdapter;
    private String address = "7C:1D:D9:7D:56:B8";
    private SensorManager mSensorManager;
    private Sensor mSensorAcc;
    private Sensor mSensorGYR;
    private WearSensorListener sensorLisener;
    private boolean sensorStarted;

    private BleFileTransfer bleFileTransfer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);

        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                tvDevices = (TextView) stub.findViewById(R.id.tv_devices);
                tvLog = (TextView) stub.findViewById(R.id.tv_log);
                Button btnScan = (Button) stub.findViewById(R.id.btn_scan);
                Button btnConnectMi = (Button) stub.findViewById(R.id.btn_connect_mi);
                btnScan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), "scanning...", Toast.LENGTH_SHORT).show();
                        doDiscovery();

                    }
                });
                btnConnectMi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        connectDevice(PublicConstants.addressMI);
                    }
                });
                stub.findViewById(R.id.btn_connect_sam).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        connectDevice(PublicConstants.addressSum);
                    }
                });

                stub.findViewById(R.id.btn_start_sensor).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!sensorStarted) {
                            sensorStarted = true;
                            sensorLisener.initFile(Constants.fileLogName);
                            Toast.makeText(getApplicationContext(), "starting sensor", Toast.LENGTH_SHORT).show();
                            mSensorManager.registerListener(sensorLisener, mSensorAcc, SensorManager.SENSOR_DELAY_FASTEST);
                            mSensorManager.registerListener(sensorLisener, mSensorGYR, SensorManager.SENSOR_DELAY_FASTEST);
                            ((Button) v).setText("STOPSENSOR");
                        } else {
                            sensorStarted = false;
                            Toast.makeText(getApplicationContext(), "stop sensor", Toast.LENGTH_SHORT).show();
                            mSensorManager.unregisterListener(sensorLisener, mSensorAcc);
                            mSensorManager.unregisterListener(sensorLisener, mSensorGYR);


                            ((Button) v).setText(sensorLisener.getCountInfo());
                            sensorLisener.close();
                        }
                    }
                });
                stub.findViewById(R.id.btn_sendFile).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                        bleFileTransfer = new BleFileTransfer();
                        ((Button)v).setText("Sending....");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                bleFileTransfer.transferFile(BleActivity.this,mChatService,Constants.fileLogName);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((Button)v).setText("Finish:"+bleFileTransfer.getSize());
                                    }
                                });
                            }
                        }).start();
                    }
                });

            }
        });
        initBluetooth();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorGYR = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorLisener = new WearSensorListener(this);


    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorLisener.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorLisener.close();
    }

    private void initBluetooth() {
        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);
    }

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Context activity = getApplicationContext();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
//                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
//                            mConversationArrayAdapter.clear();
                            Toast.makeText(getApplicationContext(), "connected!", Toast.LENGTH_SHORT).show();
                            sendMsg("Hello From Wear!");
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
//                            setStatus(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
//                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
//                    mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
//                    mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    /*
      Name of the connected device
     */
                    String mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != activity) {
                        Toast.makeText(activity, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };


    /**
     * Establish connection with other divice
     */
    private void connectDevice(String address) {
//        // Get the device MAC address
        BluetoothDevice device = mBtAdapter.getRemoteDevice(address);
//        // Attempt to connect to the device
        mChatService.connect(device, true);
    }

    /**
     * The BroadcastReceiver that listens for discovered devices and changes the title when
     * discovery is finished
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvDevices.append(device.getName() + device.getAddress() + "\n");
                    }
                });
                // If it's already paired, skip it, because it's been listed already

                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "finish scan...", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
    };

    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {
        Log.d(TAG, "doDiscovery()");

        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        mBtAdapter.startDiscovery();
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    public void sendMsg(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(getApplicationContext(), "Not Connected!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

        }
    }

    public void setInfo(String msg) {
        tvLog.setText(msg);
    }
}
