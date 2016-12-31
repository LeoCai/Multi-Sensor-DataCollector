package com.leocai.detecdtion;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

import com.leocai.publiclibs.ShakingData;
import com.leocai.publiclibs.utils.RollApply;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * Created by leocai on 15-12-21.
 */
@Deprecated
public class ShakeDetectorOld extends Observable   {
    private static final int PEAK_NUM = 15;
    public static final int THREHOLD_ACC = 4;
    private boolean splitStarted;

    private static final int WINDOW_SIZE = 50;
    private double[] windowAcc = new double[WINDOW_SIZE];
//    private ShakingData[] windowData = new ShakingData[WINDOW_SIZE];
    private int startIndex;
    private int cuWindowNum;
    private int windowPeekNum;

    private double[] gravity = new double[3];
    private double[] linear_acceleration = new double[3];

    private int cuBufferIndex;
    private List<Double> shakeAccBuffer = new ArrayList<>();
    private List<ShakingData> shakingDatas = new ArrayList<>();
    private boolean hasBuffer;

    private int historyNum;

    public ShakeDetectorOld() {
    }

    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            if (hasBuffer) return;
            dealAcc(event);
        } else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
//            dealGYR(event);
        }
    }

    private void dealAcc(SensorEvent event) {
        final double alpha = 0.8;

        // Isolate the force of gravity with the low-pass filter.
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        // Remove the gravity contribution with the high-pass filter.
        linear_acceleration[0] = event.values[0] - gravity[0];
        linear_acceleration[1] = event.values[1] - gravity[1];
        linear_acceleration[2] = event.values[2] - gravity[2];


        addToWindow();//加入窗口
//        windowAcc.add(Math.sqrt(Math.pow(linear_acceleration[0], 2)
//                + Math.pow(linear_acceleration[1], 2)
//                + Math.pow(linear_acceleration[2], 2)));
//        windowAcc.pop();
        if (checkMultiScopeByWindow()) {
            if (!splitStarted) {
                splitStarted = true;
                addWindowToBuffer();
            } else {
                addPointToBuffer();
            }
        } else if (splitStarted) {
            splitStarted = false;
            dealBuffer(PEAK_NUM);
            hasBuffer = true;

        } else {
            splitStarted = false;
        }


    }

    private void dealBuffer(int peakNum) {
        meanRoll();
        peakRoll();
        peakRoll2();
        shrinkPeak();
        setChanged();
        notifyObservers();
    }

    private void meanRoll() {
//        shakeAccBuffer = RollApply.rollApply(shakeAccBuffer, 5, new ApplyFuncMean());
    }

    private void peakRoll() {
//        shakeAccBuffer = RollApply.rollApply(shakeAccBuffer, 5, new ApplyFuncPeak());
    }

    private void peakRoll2() {
        double meanPeak = 0;
        int count = 0;
        for (double b : shakeAccBuffer) {
            if (b > 0) {
                meanPeak += b;
                count++;
            }
        }
        meanPeak /= count;
//        shakeAccBuffer = RollApply.rollApply(shakeAccBuffer, 1, new ApplyFuncPeak2(meanPeak));
    }

    private void shrinkPeak() {
        List<Double> newBuffer = new ArrayList<>();
        int start =-1,end = 0;
        for(int i=0;i<shakeAccBuffer.size();i++){
            if(shakeAccBuffer.get(i)>0){
                if(start == -1) start = i;
                end = i;
            }
        }
        for (int i = start; i <= end; i++) {
            newBuffer.add(shakeAccBuffer.get(i));
        }
        shakeAccBuffer = newBuffer;
    }

    private void addPointToBuffer() {
        shakeAccBuffer.add(windowAcc[(cuWindowNum + startIndex) % WINDOW_SIZE]);
    }

    private void addWindowToBuffer() {
        for (int i = startIndex; i < (startIndex + cuWindowNum); i++)
            shakeAccBuffer.add(windowAcc[i % WINDOW_SIZE]);
    }

    private boolean checkMultiScopeByWindow() {
        return (historyNum >= 20 && cuWindowNum >= WINDOW_SIZE && 1.0 * windowPeekNum / WINDOW_SIZE > 0.2);
    }

    private void addToWindow() {
        if (cuWindowNum >= WINDOW_SIZE) { //remove head
            startIndex = (startIndex + 1) % WINDOW_SIZE;
            cuWindowNum--;
            if (windowAcc[startIndex] > THREHOLD_ACC) windowPeekNum--;
        }

        //add new point
        windowAcc[(startIndex + cuWindowNum) % WINDOW_SIZE] = Math.sqrt(Math.pow(linear_acceleration[0], 2)
                + Math.pow(linear_acceleration[1], 2)
                + Math.pow(linear_acceleration[2], 2));
        cuWindowNum++;
        if (windowAcc[(startIndex + cuWindowNum - 1) % WINDOW_SIZE] > THREHOLD_ACC) windowPeekNum++;
        historyNum++;
    }

    public List<Double> getShakeAccBuffer() {
        return shakeAccBuffer;
    }

    public void setShakingDatas(List<ShakingData> shakingDatas) {
        this.shakingDatas = shakingDatas;
        setChanged();
        notifyObservers(shakingDatas);
    }
}
