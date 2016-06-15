package com.leocai.publiclibs.multidecicealign;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import com.leocai.publiclibs.PublicConstants;

import java.util.Observer;

/**
 * 传感器管理类
 * 用于启动传感器，启动监听，传递事件给观察者
 * Created by leocai on 16-1-15.
 */
public class MySensorManager {
    private SensorManager mSensorManager;
    private Sensor mSensorAcc;
    private Sensor mSensorGYR;
    SensorDataWriter sensorDataWriter ;
    /**
     * 用于写文件的类
     */
    SensorGlobalWriter sensorGlobalWriter;
    private Sensor mSensorMAG;

    public MySensorManager(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensorAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorGYR = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorMAG = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public void startSensor() {
        sensorGlobalWriter = new SensorGlobalWriter();
        mSensorManager.registerListener(sensorGlobalWriter, mSensorAcc, (int) (PublicConstants.SENSOPR_PERIOD * 1000)); // 根据频率调整
        mSensorManager.registerListener(sensorGlobalWriter, mSensorGYR, (int) (PublicConstants.SENSOPR_PERIOD * 1000));
        mSensorManager.registerListener(sensorGlobalWriter, mSensorMAG, (int) (PublicConstants.SENSOPR_PERIOD * 1000));
    }

    public void startDetection() {
        sensorGlobalWriter.startDetection();
    }

    public void stop() {
        mSensorManager.unregisterListener(sensorGlobalWriter, mSensorAcc);
        mSensorManager.unregisterListener(sensorGlobalWriter, mSensorGYR);
        mSensorManager.unregisterListener(sensorGlobalWriter, mSensorMAG);
        sensorGlobalWriter.close();
    }

    public void setFileName(String fileName){
        sensorGlobalWriter.setFileName(fileName);
    }

    public void addObserver(Observer observer){
        sensorGlobalWriter.addObserver(observer);
    }
}
