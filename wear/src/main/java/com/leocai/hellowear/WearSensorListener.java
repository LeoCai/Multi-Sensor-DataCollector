package com.leocai.hellowear;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by leocai on 15-11-17.
 */
public class WearSensorListener implements SensorEventListener {

    private final BleActivity bleActivity;
    private  FileWriter fileWriter;
    private long startTime = System.nanoTime();
    private int accCount = 0;
    private int gyrCount = 0;


    public WearSensorListener(BleActivity bleActivity) {
        this.bleActivity = bleActivity;


    }

    public void initFile(String fileName)  {
        File file = new File(Environment.getExternalStorageDirectory(),fileName);
        try {
            fileWriter = new FileWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        long dt = event.timestamp - startTime;
        String msg = "G"+","+event.values[0] + "," + event.values[1] + "," + event.values[2]+","+dt+"\n";
        try {
            fileWriter.write(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        bleActivity.sendMsg();
        gyrCount++;
        showCount();
    }

    private void dealAcc(SensorEvent event) {
        long dt = event.timestamp - startTime;
        String msg = "A"+","+event.values[0] + "," + event.values[1] + "," + event.values[2]+","+dt+"\n";
        try {
            fileWriter.write(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        bleActivity.sendMsg();
        accCount++;
        showCount();
    }

    private void showCount(){
//        String msg = "A:"+accCount+" G:"+gyrCount;
//        bleActivity.setInfo(msg);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void close() {
        try {
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getCountInfo() {
        return "A:"+accCount+" G:"+gyrCount;
    }
}
