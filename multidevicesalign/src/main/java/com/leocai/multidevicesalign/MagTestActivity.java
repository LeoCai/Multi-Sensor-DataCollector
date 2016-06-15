package com.leocai.multidevicesalign;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.leocai.publiclibs.PublicConstants;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 测试磁力计的类
 */
public class MagTestActivity extends AppCompatActivity implements SensorEventListener {

    private static final int MAX_MAG_SIZE = 50;
    Button btnStart;

    SensorManager sensorManager;
    private Sensor mSensorAcc;
    private Sensor mSensorMAG;
    private float[] mGravity;
    private float[] mGeomagnetic;
    List<Float> azimuts = new ArrayList<>();
    private boolean stopped = true;


    private EditText etX;
    private EditText etY;

    private FileWriter fileWriter;

    private TextView tvLog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mag_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorAcc = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorMAG = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this, mSensorAcc, (int) (PublicConstants.SENSOPR_PERIOD * 1000)); // 根据频率调整
        sensorManager.registerListener(this, mSensorMAG, (int) (PublicConstants.SENSOPR_PERIOD * 1000));
        btnStart = (Button) findViewById(R.id.btn_start);
        tvLog= (TextView)findViewById(R.id.tvLog);
        etX = (EditText) findViewById(R.id.etX);
        etY = (EditText) findViewById(R.id.etY);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stopped) {
                    btnStart.setText("STARTING...");
                    btnStart.setEnabled(false);
                    stopped = false;
                }
            }
        });
        try {
            fileWriter = new FileWriter(new File(Environment.getExternalStorageDirectory(), "MagTest.csv"),true);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;

        if (mGravity != null && mGeomagnetic != null && !stopped) {
            float R[] = new float[9];
            float I[] = new float[9];

            if (SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic)) {

                // orientation contains azimut, pitch and roll
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);

//                azimut = orientation[0];
                azimuts.add(orientation[0]);
                if(azimuts.size()==MAX_MAG_SIZE){
                    stopped = true;
                    double mean = 0;
                    for(float azimut:azimuts){
                        mean+=azimut;
                    }
                    mean/=azimuts.size();
                    mean = mean/Math.PI*180;
                    int x = Integer.parseInt(etX.getText().toString());
                    int y = Integer.parseInt(etY.getText().toString());
                    String log = x+","+y+","+mean+"\n";
                    tvLog.setText(log);
                    try {
                        fileWriter.write(log);
                        fileWriter.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    azimuts.clear();
                    btnStart.setText("START");
                    btnStart.setEnabled(true);

                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
