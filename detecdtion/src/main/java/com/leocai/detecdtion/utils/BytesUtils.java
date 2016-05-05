package com.leocai.detecdtion.utils;

import com.leocai.detecdtion.SensorDataWithIndex;

/**
 * Created by leocai on 15-12-26.
 */
public class BytesUtils {

    public static byte[] intShortToBytes(int val) {
        byte[] barray = new byte[2];
        barray[0] = (byte) (val >> 8);
        barray[1] = (byte) (val & 0xFF);
        return barray;
    }

    public static byte[] doubleToBytes(double dval) {
        int val = (int) (dval * 100);
        return intShortToBytes(val);
    }

    public static boolean checkSizeType(byte[] bleBytes) {
        return bleBytes.length!=6;
    }

    public static int getShortIntFromBytes(byte[] sizeBytes,int index0, int index1) {
        int val0 = (sizeBytes[index0] & 0xF0) + (sizeBytes[index0] & 0x0F);
        int val1 = (sizeBytes[index1] & 0xF0) + (sizeBytes[index1] & 0x0F);
        return (val0 << 8) + val1;
    }

    public static int getSizeFromBytes(byte[] sizeBytes) {
        return getShortIntFromBytes(sizeBytes,0,1);
    }

    public static SensorDataWithIndex getSensorDataWithIndexFromBytes(byte[] dbytes) {
        int index = getShortIntFromBytes(dbytes,0,1);
        double accVal = 1.0 * getShortIntFromBytes(dbytes,2,3) / 100;
        double gyrVal = 1.0 * getShortIntFromBytes(dbytes,4,5) / 100;
        return new SensorDataWithIndex(index, accVal, gyrVal);
    }

}
