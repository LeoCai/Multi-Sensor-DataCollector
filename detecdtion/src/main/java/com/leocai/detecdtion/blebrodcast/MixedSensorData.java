package com.leocai.detecdtion.blebrodcast;

import java.util.List;

/**
 * Created by leocai on 15-12-28.
 */
public class MixedSensorData {

    double accData[][];
    double gyrData[][];

    public MixedSensorData(double[][] accData, double[][] gyrData) {
        this.accData = accData;
        this.gyrData = gyrData;
    }

    public double[][] getAccData() {
        return accData;
    }

    public void setAccData(double[][] accData) {
        this.accData = accData;
    }

    public double[][] getGyrData() {
        return gyrData;
    }

    public void setGyrData(double[][] gyrData) {
        this.gyrData = gyrData;
    }
}
