package com.leocai.multidevicesalign;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.leocai.publiclibs.multidecicealign.SensorDataWriter;
import com.leocai.publiclibs.multidecicealign.SensorGlobalWriter;

@Deprecated
public class MainActivity extends AppCompatActivity {

    private SensorManager mSensorManager;
    private Sensor mSensorAcc;
    private Sensor mSensorGYR;
    SensorGlobalWriter sensorGlobalWriter = new SensorGlobalWriter("SensorGlobal.txt");
    private boolean stopped = true;
    private Sensor mSensorMAG;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorGYR = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorMAG = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);


        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stopped){
                    ((Button)v).setText("STOP");
                    stopped = false;
                    mSensorManager.registerListener(sensorGlobalWriter, mSensorAcc, (int) (CollectorConfig.SENSOPR_PERIOD * 1000)); // 根据频率调整
                    mSensorManager.registerListener(sensorGlobalWriter, mSensorGYR, (int) (CollectorConfig.SENSOPR_PERIOD * 1000));
                    mSensorManager.registerListener(sensorGlobalWriter, mSensorMAG, (int) (CollectorConfig.SENSOPR_PERIOD * 1000));
                    sensorGlobalWriter.startDetection();
                }else{
                    ((Button)v).setText("START");
                    stop();
                }

            }
        });
    }

    private void stop() {
        mSensorManager.unregisterListener(sensorGlobalWriter, mSensorAcc);
        mSensorManager.unregisterListener(sensorGlobalWriter, mSensorGYR);
        mSensorManager.unregisterListener(sensorGlobalWriter, mSensorMAG);
        sensorGlobalWriter.close();
        stopped = true;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stop();
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
}
