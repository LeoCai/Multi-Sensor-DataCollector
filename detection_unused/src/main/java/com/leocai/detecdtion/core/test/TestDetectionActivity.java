package com.leocai.detecdtion.core.test;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.leocai.detecdtion.R;
import com.leocai.detecdtion.ShakeBufferView;
import com.leocai.publiclibs.PublicConstants;
import com.leocai.publiclibs.ShakeDetector;


//Test Dection
public class TestDetectionActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensorAcc;
    private Sensor mSensorGYR;
    private ShakeDetector shakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorGYR = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        ShakeBufferView shakeBufferView = (ShakeBufferView) findViewById(R.id.buffer_view);
        shakeDetector = new ShakeDetector();
        shakeDetector.addObserver(shakeBufferView);
        mSensorManager.registerListener(this, mSensorAcc, (int) (PublicConstants.SENSOPR_PERIOD * 1000)); // 根据频率调整
        mSensorManager.registerListener(this, mSensorGYR, (int) (PublicConstants.SENSOPR_PERIOD * 1000));

    }

    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this, mSensorAcc);
        mSensorManager.unregisterListener(this, mSensorGYR);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        mSensorManager.unregisterListener(this, mSensorAcc);
        mSensorManager.unregisterListener(this, mSensorGYR);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    public void onSensorChanged(SensorEvent event) {
        shakeDetector.onSensorChanged(event);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
