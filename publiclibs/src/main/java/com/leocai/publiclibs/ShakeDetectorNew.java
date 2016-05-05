package com.leocai.publiclibs;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

/**
 * Created by leocai on 15-12-21.
 */
public class ShakeDetectorNew extends Observable implements SensorEventListener {
    public static final int THREHOLD_ACC = 4;
    public static final int MAX_POINT_SIZE = 50;
    private static final int WINDOW_SIZE = 10;

    public static volatile boolean stop;
    private boolean shakeStart;

    private long preTimestamp;

    List<ShakingData> window = new LinkedList<>();
    private int windowPeekNum;
    private List<ShakingData> shakingDatasBuffer = new ArrayList<>();
    private ShakingData cuShakingData = new ShakingData();

    private double[] gravity = new double[3];//TODO 初始值
    private double[] linear_acceleration = new double[3];


    private static final String TAG = "SHAKEDETECTORNew";

    public ShakeDetectorNew() {
        cuShakingData.setLinearAccData(null);
        cuShakingData.setGyrData(null);
        cuShakingData.setDt(0);
        cuShakingData.setIndex(0);
        cuShakingData.setTimeStamp(0);
    }

    public void startDetection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (!stop) {
                    if (cuShakingData.getLinearAccData() == null) continue;
                    try {
                        Thread.sleep(PublicConstants.SENSOPR_PERIOD);
                        addToWindow();//加入窗口
                        if (shakeStart) {
                            int bufferSize = shakingDatasBuffer.size();

                            addPointToBuffer();
                            Log.d(TAG, "addPointToBuffer");

                            if (bufferSize >= MAX_POINT_SIZE) {
                                stop = true;
                                dealBuffer();
                                Log.d(TAG, "hasBuffer");
                                shakingDatasBuffer.clear();
                                shakeStart = false;
                            }
                        } else {
                            shakeStart = checkMultiScopeByWindow();
                            if (shakeStart) Log.d(TAG, "shakeStart");

                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            final double alpha = 0.8;

            // Isolate the force of gravity with the low-pass filter.
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            // Remove the gravity contribution with the high-pass filter.
            linear_acceleration[0] = event.values[0] - gravity[0];
            linear_acceleration[1] = event.values[1] - gravity[1];
            linear_acceleration[2] = event.values[2] - gravity[2];
            cuShakingData.setLinearAccData(linear_acceleration);
            cuShakingData.setGravityAccData(gravity);
            cuShakingData.setTimeStamp(event.timestamp);
            if (preTimestamp != 0)
                cuShakingData.setDt(1.0 * (event.timestamp - preTimestamp) / 1000000000);
            preTimestamp = event.timestamp;
        } else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            cuShakingData.setGyrData(new double[]{event.values[0], event.values[1], event.values[2]});
        }
    }

    private void dealBuffer() {
        List<ShakingData> newBuffer = new ArrayList<>();
        for (int i = 0; i < shakingDatasBuffer.size(); i++) {
            newBuffer.add(new ShakingData(shakingDatasBuffer.get(i)));
        }
        setShakingDatasBuffer(newBuffer);
    }

    private void addPointToBuffer() {//remeber to copy
        shakingDatasBuffer.add(window.get(window.size() - 1));
    }

    private void addWindowToBuffer() {
        for (int i = 0; i < window.size(); i++) {
            shakingDatasBuffer.add(window.get(i));
        }
    }

    private boolean checkMultiScopeByWindow() {
        return (window.size() == WINDOW_SIZE && (1.0 * windowPeekNum / WINDOW_SIZE > 0.4));
    }

    private void addToWindow() {
        int windowSize = window.size();
        if (windowSize >= WINDOW_SIZE) { //remove head
            if (window.get(0).getResultantAccData() > THREHOLD_ACC) windowPeekNum--;
            window.remove(0);
        }
        ShakingData newP = new ShakingData(cuShakingData);
        window.add(newP);
        if (newP.getResultantAccData() > THREHOLD_ACC)
            windowPeekNum++;
    }

    public void setShakingDatasBuffer(List<ShakingData> shakingDatasBuffer) {
        this.shakingDatasBuffer = shakingDatasBuffer;
        setChanged();
        notifyObservers(shakingDatasBuffer);
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


}
