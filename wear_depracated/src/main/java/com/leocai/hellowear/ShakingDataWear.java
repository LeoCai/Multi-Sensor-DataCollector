package com.leocai.hellowear;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by leocai on 15-12-31.
 */
public class ShakingDataWear implements Serializable,Cloneable {

    private static final long serialVersionUID = -6091530420906090649L;

    private int index;
    private double linearAccData[];
    private double gyrData[];
    private long timeStamp;
    private double dt;
    private double[] gravityAccData;
    private double resultantAccData;
    private double[] convertedData;

    public ShakingDataWear(double[] linearAccData, double[] gyrData, int index, double dt) {
        this.linearAccData = linearAccData;
        this.gyrData = gyrData;
        this.index = index;
        this.dt = dt;
    }

    public ShakingDataWear(byte[] sdBuffer) {

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(sdBuffer);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        this.linearAccData = new double[3];
        this.gravityAccData = new double[3];
        this.gyrData = new double[3];
        try {
            this.index = dataInputStream.readInt();
            for (int i = 0; i < 3; i++) {
                this.linearAccData[i] = dataInputStream.readDouble();
                this.gyrData[i] = dataInputStream.readDouble();
                this.gravityAccData[i] = dataInputStream.readDouble();
            }
            this.resultantAccData = dataInputStream.readDouble();
            this.timeStamp = dataInputStream.readLong();
            this.dt = dataInputStream.readDouble();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public ShakingDataWear(ShakingDataWear shakingData) {
        copy(shakingData);
    }

    public byte[] getBytes() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream dataInputStream = new DataOutputStream(outputStream);
        try {
            dataInputStream.writeInt(this.index);
            for (int i = 0; i < 3; i++) {
                dataInputStream.writeDouble(this.linearAccData[i]);
                dataInputStream.writeDouble(this.gyrData[i]);
                dataInputStream.writeDouble(this.gravityAccData[i]);
            }
            dataInputStream.writeDouble(this.resultantAccData);
            dataInputStream.writeLong(this.timeStamp);
            dataInputStream.writeDouble(this.dt);
            dataInputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }

    public ShakingDataWear() {
        Random random = new Random(System.currentTimeMillis());
        linearAccData = new double[]{random.nextDouble()*10, random.nextDouble()*10, random.nextDouble()*10};
        gravityAccData = new double[]{random.nextDouble()*10, random.nextDouble()*10, random.nextDouble()*10};
        convertedData = new double[]{random.nextDouble()*10, random.nextDouble()*10, random.nextDouble()*10};
        gyrData = new double[]{random.nextDouble()*5, random.nextDouble()*5, random.nextDouble()*5};
        index = random.nextInt(100);
        timeStamp = random.nextLong();
        dt = random.nextDouble();
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public double[] getLinearAccData() {
        return linearAccData;
    }

    public void setLinearAccData(double[] linearAccData) {
        this.linearAccData = linearAccData;
        if(linearAccData == null) return;
        this.resultantAccData = Math.sqrt(Math.pow(linearAccData[0], 2)
                + Math.pow(linearAccData[1], 2)
                + Math.pow(linearAccData[2], 2));
    }

    public double[] getGyrData() {
        return gyrData;
    }

    public void setGyrData(double[] gyrData) {
        this.gyrData = gyrData;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public double getDt() {
        return dt;
    }

    public void setDt(double dt) {
        this.dt = dt;
    }

    @Override
    public String toString() {
        return "ShakingDataWear{" +
                "linearAccData=" + Arrays.toString(linearAccData) +
                ", gyrData=" + Arrays.toString(gyrData) +
                ", dt=" + dt +
                ", gravityAccData=" + Arrays.toString(gravityAccData) +
                ", resultantAccData=" + resultantAccData +
                ", convertedData=" + Arrays.toString(convertedData) +
                '}';
    }

    public void setGravityAccData(double[] gravityAccData) {
        this.gravityAccData = gravityAccData;
    }

    public double[] getGravityAccData() {
        return gravityAccData;
    }

    public double getResultantAccData() {
        return resultantAccData;
    }

    public void setResultantAccData(double resultantAccData) {
        this.resultantAccData = resultantAccData;
    }

    @Override
    public ShakingDataWear clone() throws CloneNotSupportedException {
        return (ShakingDataWear)super.clone();
    }

    public void copy(ShakingDataWear cuShakingData) {
        this.index = cuShakingData.index;
        this.gyrData = Arrays.copyOf(cuShakingData.gyrData,3);
        this.linearAccData = Arrays.copyOf(cuShakingData.linearAccData,3);
        this.gravityAccData =  Arrays.copyOf(cuShakingData.gravityAccData,3);
        this.timeStamp  =  cuShakingData.timeStamp;
        this.dt = cuShakingData.dt;
        this.resultantAccData = cuShakingData.resultantAccData;
    }

    public void setConvertedData(double[] convertedData) {
        this.convertedData = convertedData;
    }

    public double[] getConvertedData() {
        return convertedData;
    }
}
