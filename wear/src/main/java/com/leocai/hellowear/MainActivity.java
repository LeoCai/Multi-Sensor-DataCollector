package com.leocai.hellowear;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;

import java.util.Arrays;

public class MainActivity extends Activity implements SensorEventListener {

    private TextView mTextView;
    private SensorManager mSensorManager;
    private Sensor mSensorAcc;
    private Sensor mSensorGYR;
    private long timestamp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorGYR = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        mSensorManager.registerListener(this, mSensorAcc, SensorManager.SENSOR_DELAY_FASTEST);
//        mSensorManager.registerListener(this, mSensorGYR, SensorManager.SENSOR_DELAY_FASTEST);


    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //event.sensor
        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            dealAcc(event);
        } else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            dealGYR(event);
        }
    }

    private void dealGYR(SensorEvent event) {
        double[] gyrdata = new double[]{event.values[0], event.values[1], event.values[2]};
        double dt = 1.0 * (event.timestamp - timestamp) / 1000000000;
        if (mTextView != null) {
            mTextView.setText(Arrays.toString(gyrdata));
            timestamp = event.timestamp;
        }

    }



    private void dealAcc(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
