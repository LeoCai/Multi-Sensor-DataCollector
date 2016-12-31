package com.leocai.hellowear;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;


import com.leocai.hellowear.utils.ApplyFuncMean;
import com.leocai.hellowear.utils.ApplyFuncPeak;
import com.leocai.hellowear.utils.ApplyFuncPeak2;
import com.leocai.hellowear.utils.RollApply;
import com.leocai.publiclibs.PublicConstants;
import com.leocai.publiclibs.ShakingData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;

/**
 * Created by leocai on 15-12-21.
 */
public class ShakeDetectorw extends Observable  implements SensorEventListener {
    private static final int PEAK_NUM = 5;
    public static final int THREHOLD_ACC = 3;
    private static final String TAG = "SHAKEDETECTOR";
    public static final int MAX_POINT_SIZE = 50;
    private boolean splitStarted;

    private static final int WINDOW_SIZE = 3;
    private ShakingData[] windowData = new ShakingData[WINDOW_SIZE];
    private long preTimestamp;

    {
        for (int i = 0; i < WINDOW_SIZE; i++) {
            windowData[i] = new ShakingData();
        }
    }

    private int startIndex;
    private int cuWindowNum;
    private int windowPeekNum;

    private double[] gravity = new double[3];//TODO 初始值
    private double[] linear_acceleration = new double[3];

    private int cuBufferIndex;
    //    private List<Double> shakeAccBuffer = new ArrayList<>();
    private List<ShakingData> shakingDatasBuffer = new ArrayList<>();

    private int historyNum;

    private ShakingData cuShakingData = new ShakingData();
    private static volatile boolean stop;
    public static volatile boolean sendingData;
    private List<Double> resultantantAccBuffer;

    public ShakeDetectorw() {
        cuShakingData.setLinearAccData(null);
        cuShakingData.setGyrData(null);
        cuShakingData.setDt(0);
        cuShakingData.setIndex(0);
        cuShakingData.setTimeStamp(0);
        Collections.synchronizedList(shakingDatasBuffer);
        startDetection();
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
                    if (sendingData||cuShakingData.getLinearAccData() == null) continue;
                    try {
                        Thread.sleep(PublicConstants.SENSOPR_PERIOD);
//                        Log.d(TAG,cuShakingData.getTimeStamp()+"");
                        addToWindow();//加入窗口
                        if (checkMultiScopeByWindow()) {
//                            Log.d(TAG, "windowPeekNum:"+windowPeekNum+",cuWindowNum：" + cuWindowNum +",historyNum:" + historyNum);
                           synchronized (shakingDatasBuffer){
                               if (shakingDatasBuffer.size()==0) {
                                   addWindowToBuffer();
                                   Log.d(TAG, "addWindowToBuffer");
                               } else {
                                   Log.d(TAG, "addPointToBuffer");

                                   addPointToBuffer();
//                                Log.d(TAG, "addPointToBuffer");
                               }
                           }

                            splitStarted = true;
                        } else if (splitStarted) {
                            splitStarted = false;
                            if (shakingDatasBuffer.size() >= MAX_POINT_SIZE) {//not enough points
                                if (sendingData) continue;
                                dealBuffer(PEAK_NUM);
                                Log.d(TAG, "hasBuffer");
                            }
                            synchronized (shakingDatasBuffer){
                                shakingDatasBuffer.clear();
                            }
                            Log.d(TAG, "clear");


                        } else {
                            splitStarted = false;
                            synchronized (shakingDatasBuffer) {
                                shakingDatasBuffer.clear();
                            }
                            Log.d(TAG, "clear");
                            Log.d(TAG, "else");
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
//            Log.d(TAG, "" + event.timestamp);
        } else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            cuShakingData.setGyrData(new double[]{event.values[0], event.values[1], event.values[2]});
//            Log.d(TAG, "" + event.timestamp);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    private void dealBuffer(int peakNum) {
        resultantantAccBuffer = new ArrayList<>();
        synchronized (shakingDatasBuffer){
            for (ShakingData shakingData : shakingDatasBuffer) {
                resultantantAccBuffer.add(shakingData.getResultantAccData());
            }
//        for (ShakingDataWear shakingData : shakingDatasBuffer) {
//            Log.d(TAG, shakingData.toString());
//        }
//        setShakingDatasBuffer(shakingDatasBuffer);
//            meanRoll();
//            peakRoll();
//            peakRoll2();
            shrinkPeak();
        }

    }

    private void meanRoll() {
        Log.d(TAG,"mean");
        resultantantAccBuffer = RollApply.rollApply(resultantantAccBuffer, 5, new ApplyFuncMean());
    }

    private void peakRoll() {
        Log.d(TAG,"peakRoll");
        resultantantAccBuffer = RollApply.rollApply(resultantantAccBuffer, 5, new ApplyFuncPeak());
    }

    private void peakRoll2() {
        Log.d(TAG,"peakRoll2");
        double meanPeak = 0;
        int count = 0;
        for (double b : resultantantAccBuffer) {
            if (b > 0) {
                meanPeak += b;
                count++;
            }

        }
        meanPeak /= count;
        resultantantAccBuffer = RollApply.rollApply(resultantantAccBuffer, 1, new ApplyFuncPeak2(meanPeak));
    }

    private void shrinkPeak() {
        Log.d(TAG,"shrinkPeak");
        List<ShakingData> newBuffer = new ArrayList<>();
        int start = -1, end = 0;
//        for (int i = 0; i < resultantantAccBuffer.size(); i++) {
//            if (resultantantAccBuffer.get(i) > 0) {
//                if (start == -1) start = i;
//                end = i;
//            }
//        }
        start = 0;
        if (start == -1||(start+MAX_POINT_SIZE) >= shakingDatasBuffer.size()) {
            Log.d(TAG, "not shake");
            return;
        }
        end = MAX_POINT_SIZE - 1;
        sendingData = true;
        stop = true;
        for (int i = start; i < start + MAX_POINT_SIZE; i++) {
            newBuffer.add(new ShakingData(shakingDatasBuffer.get(i)));
        }
//        for(ShakingDataWear shakingData:shakingDatasBuffer){
//            Log.d(TAG,shakingData.toString());
//        }
        Log.d(TAG, "send data");


        setShakingDatasBuffer(newBuffer);
//        shakingDatasBuffer = newBuffer;
//        setShakingDatasBuffer(shakingDatasBuffer);
    }

    private void addPointToBuffer() {//remeber to copy
        shakingDatasBuffer.add(new ShakingData(windowData[(cuWindowNum + startIndex) % WINDOW_SIZE]));
    }

    private void addWindowToBuffer() {
        for (int i = startIndex; i < (startIndex + cuWindowNum); i++)
            shakingDatasBuffer.add(windowData[i % WINDOW_SIZE]);
    }

    private boolean checkMultiScopeByWindow() {
        return (historyNum >= 20 && cuWindowNum >= WINDOW_SIZE && (1.0 * windowPeekNum / WINDOW_SIZE > 0.3));
    }

    private void addToWindow() {
        if (cuWindowNum >= WINDOW_SIZE) { //remove head
            if (windowData[startIndex].getResultantAccData() > THREHOLD_ACC) windowPeekNum--;
            startIndex = (startIndex + 1) % WINDOW_SIZE;
            cuWindowNum--;
        }

        //add new point
        ShakingData newP = windowData[(startIndex + cuWindowNum) % WINDOW_SIZE];
        newP.copy(cuShakingData);
//        Log.d(TAG,newP.toString());
        cuWindowNum++;
        if (newP.getResultantAccData() > THREHOLD_ACC)
            windowPeekNum++;
        historyNum++;
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


}
