package com.leocai.hellowear;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.leocai.publiclibs.BleConnection;
import com.leocai.publiclibs.ConnectedCallBack;
import com.leocai.publiclibs.PublicConstants;
import com.leocai.publiclibs.ShakeDetectorNew;
import com.leocai.publiclibs.ShakingData;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class WearActivity extends Activity implements Observer {

    private static final String TAG = "WearActivity";
    private SensorManager mSensorManager;
    private Sensor mSensorAcc;
    private Sensor mSensorGYR;
    ShakeDetectorNew shakeDetector;
    private TextView tvDevices;
    private TextView tvLog;
    private boolean sensorStarted;
    private BleConnection bleConnection;
    private OutputStream out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorGYR = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);

        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                tvDevices = (TextView) stub.findViewById(R.id.tv_devices);
                tvLog = (TextView) stub.findViewById(R.id.tv_log);
                stub.findViewById(R.id.btn_connect_mi).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        connectDevice(PublicConstants.addressMI_424);
                    }
                });
                stub.findViewById(R.id.btn_connect_sam).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        connectDevice(PublicConstants.addressSAM_419);
                    }
                });

                stub.findViewById(R.id.btn_start_sensor).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!sensorStarted) {
                            sensorStarted = true;
                            Toast.makeText(getApplicationContext(), "starting sensor", Toast.LENGTH_SHORT).show();
                            shakeDetector = new ShakeDetectorNew();
                            mSensorManager.registerListener(shakeDetector, mSensorAcc, (int) (PublicConstants.SENSOPR_PERIOD * 1000)); // 根据频率调整
                            mSensorManager.registerListener(shakeDetector, mSensorGYR, (int) (PublicConstants.SENSOPR_PERIOD * 1000));
                            shakeDetector.addObserver(WearActivity.this);
                            shakeDetector.startDetection();
                            ((Button) v).setText("STOPSENSOR");
                        } else {
                            stopSensor();
                            ((Button) v).setText("STATSENSOR");
                        }

                    }
                });

            }
        });
    }

    private void stopSensor() {
        sensorStarted = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "stop sensor", Toast.LENGTH_SHORT).show();
                mSensorManager.unregisterListener(shakeDetector, mSensorAcc);
                mSensorManager.unregisterListener(shakeDetector, mSensorGYR);
            }
        });
        shakeDetector.setStop(true);

    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onstop");
        stopSensor();
        try {
            closeSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        shakeDetector.setStop(true);
        stopSensor();
        try {
            closeSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeSocket() throws IOException {
        bleConnection.close();
    }

    private void connectDevice(String address) {
        bleConnection = new BleConnection();
        bleConnection.connect(address, new ConnectedCallBack() {
            @Override
            public void onConnected(InputStream in) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
                    }
                });
                WearActivity.this.out = bleConnection.getOut();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wear, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void update(Observable observable, Object data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Start Data Sending", Toast.LENGTH_LONG).show();
            }
        });
        List<ShakingData> shakingDataList = (List<ShakingData>) data;
        for (ShakingData shakingData : shakingDataList) {
            try {
                out.write(shakingData.getBytes());
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        stopSensor();


    }
}
