package com.leocai.detecdtion;

/**
 * Created by leocai on 15-12-26.
 */
public class SensorDataWithIndex {
    private int index;
    private double accVal;
    private double gyrVal;

    public SensorDataWithIndex(int index, double accVal, double gyrVal) {
        this.index = index;
        this.accVal = accVal;
        this.gyrVal = gyrVal;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public double getAccVal() {
        return accVal;
    }

    public void setAccVal(double accVal) {
        this.accVal = accVal;
    }

    public double getGyrVal() {
        return gyrVal;
    }

    public void setGyrVal(double gyrVal) {
        this.gyrVal = gyrVal;
    }

    @Override
    public String toString() {
        return "SensorDataWithIndex{" +
                "index=" + index +
                ", accVal=" + accVal +
                ", gyrVal=" + gyrVal +
                '}';
    }
}
