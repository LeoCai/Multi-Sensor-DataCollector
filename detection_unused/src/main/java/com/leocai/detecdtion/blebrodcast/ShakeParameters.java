package com.leocai.detecdtion.blebrodcast;

import com.leocai.detecdtion.utils.BytesUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

/**
 * Created by leocai on 15-12-28.
 */
public class ShakeParameters {

    private double maxMean;
    private double initThetaMaster;

    public ShakeParameters() {
        Random random = new Random(System.currentTimeMillis());
        initThetaMaster = random.nextDouble();
        initThetaSlave = random.nextDouble();
    }

    public ShakeParameters(double initThetaMaster, double initThetaSlave, double maxMean) {
        this.initThetaMaster=initThetaMaster;
        this.initThetaSlave = initThetaSlave;
        this.maxMean = maxMean;

    }

    public double getInitThetaSlave() {
        return initThetaSlave;
    }

    public void setInitThetaSlave(double initThetaSlave) {
        this.initThetaSlave = initThetaSlave;
    }

    private double initThetaSlave;

    public double getInitThetaMaster() {
        return initThetaMaster;
    }

    public void setInitThetaMaster(double initThetaMaster) {
        this.initThetaMaster = initThetaMaster;
    }

    public ShakeParameters(double initThetaMaster) {
        this.initThetaMaster = initThetaMaster;
    }

    public static ShakeParameters parse(byte[] data) {
        return null;
    }

    public byte[] getBytes() {
        return BytesUtils.doubleToBytes(initThetaMaster);
    }


    public ShakeParameters(double initThetaMaster, double initThetaSlave) {
        this.initThetaMaster = initThetaMaster;
        this.initThetaSlave = initThetaSlave;
    }

    public static ShakeParameters read(InputStream in) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(in);
        ShakeParameters shakeParameters = new ShakeParameters();
        shakeParameters.setInitThetaMaster(dataInputStream.readDouble());
        shakeParameters.setInitThetaSlave(dataInputStream.readDouble());
        return shakeParameters;
    }

    public static void send(OutputStream out, ShakeParameters shakeParameters) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(out);
        dataOutputStream.writeDouble(shakeParameters.getInitThetaMaster());
        dataOutputStream.writeDouble(shakeParameters.getInitThetaSlave());
        dataOutputStream.flush();
    }

    @Override
    public String toString() {
        return "ShakeParameters{" +
                "maxMean=" + maxMean +
                ", initThetaMaster=" + initThetaMaster +
                ", initThetaSlave=" + initThetaSlave +
                '}';
    }
}
