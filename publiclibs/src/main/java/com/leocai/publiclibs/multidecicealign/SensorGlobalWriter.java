package com.leocai.publiclibs.multidecicealign;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Environment;
import android.util.Log;

import com.leocai.publiclibs.PublicConstants;
import com.leocai.publiclibs.ShakingData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;

/**
 * 监听传感器数据，用于将传感器数据写到文件中
 * 启动一个线程，一个传感器周期写一次数据
 *
 * Created by leocai on 15-12-21.
 */
public class SensorGlobalWriter extends Observable implements SensorEventListener {
    private static final String TAG = "SensorDataWriter";

    private long preTimestamp;

    private double[] gravity = new double[3];//TODO 初始值
    private double[] linear_acceleration = new double[3];

    protected ShakingData cuShakingData = new ShakingData();
    private volatile boolean stop;

    private OutputStream outputStream;
    private FileWriter fileWriter;

    public SensorGlobalWriter(String fileName) {
        cuShakingData.setLinearAccData(null);
        cuShakingData.setGyrData(null);
        cuShakingData.setDt(0);
        cuShakingData.setIndex(0);
        cuShakingData.setTimeStamp(0);
        try {
            fileWriter = new FileWriter(new File(Environment.getExternalStorageDirectory(), fileName));
            fileWriter.write(cuShakingData.getCSVHead());
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setFileName(String fileName){
        try {
            fileWriter = new FileWriter(new File(Environment.getExternalStorageDirectory(), fileName));
            fileWriter.write(cuShakingData.getCSVHead());
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SensorGlobalWriter() {
        cuShakingData.setLinearAccData(null);
        cuShakingData.setGyrData(null);
        cuShakingData.setDt(0);
        cuShakingData.setIndex(0);
        cuShakingData.setTimeStamp(0);
    }

    public void startDetection() {
        stop = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                while (!stop) {
                    if (cuShakingData.getLinearAccData() == null) continue;
//                    Log.d(TAG,"dection");
                    cuShakingData.transform();
                    notifyObservers(cuShakingData);
                    setChanged();
                    try {
                        Thread.sleep(PublicConstants.SENSOPR_PERIOD);
                        String info = cuShakingData.getCSV();
//                        Log.d(TAG, info);
                        fileWriter.write(info);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void close() {
        try {
            stop = true;
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            cuShakingData.setAccData(new double[]{event.values[0],event.values[1],event.values[2]});
            cuShakingData.setTimeStamp(new Date().getTime());
            if (preTimestamp != 0)
                cuShakingData.setDt(1.0 * (event.timestamp - preTimestamp) / 1000000000);
            preTimestamp = event.timestamp;
//            Log.d(TAG, "" + event.timestamp);
        } else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            cuShakingData.setGyrData(new double[]{event.values[0], event.values[1], event.values[2]});
//            Log.d(TAG, "" + event.timestamp);
        } else if(sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            cuShakingData.setMagnetData(new double[]{event.values[0], event.values[1], event.values[2]});
        } else if(sensor.getType() == Sensor.TYPE_GRAVITY){
            cuShakingData.setGravityAccData(new double[]{event.values[0], event.values[1], event.values[2]});
        } else if(sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            cuShakingData.setLinearAccData(new double[]{event.values[0], event.values[1], event.values[2]});
        }
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }


}
