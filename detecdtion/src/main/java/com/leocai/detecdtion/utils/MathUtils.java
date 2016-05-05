package com.leocai.detecdtion.utils;

import com.leocai.publiclibs.ShakingData;

import java.util.List;

/**
 * Created by leocai on 16-5-3.
 */
public class MathUtils {
    public static double[] norm(double[] vector) {
        double sum = 0;
        for (double d : vector) {
            sum += Math.pow(d, 2);
        }
        sum = Math.sqrt(sum);
        double[] newVector = new double[vector.length];
        for (int i = 0; i < newVector.length; i++) {
            newVector[i] = vector[i] / sum;
        }
        return newVector;
    }

    public static double[] crossProduct(double[] a, double[] b) {
        return new double[]{a[1] * b[2] - a[2] * b[1], a[2] * b[0] - a[0] * b[2], a[0] * b[1] - a[1] * b[0]};
    }

    public static double[] convertFromSphericalToCardinal(double r, double fi, double theta) {
        return new double[]{r * Math.sin(fi) * Math.cos(theta), r * Math.sin(fi) * Math.sin(theta), r * Math.cos(fi)};
    }

    //TODO correlation
    public static double[] getCors(List<ShakingData> data1, List<ShakingData> data2, int len) {
        double[] mean1 = new double[3], mean2 = new double[3], sd1 = new double[3], sd2 = new double[3], cor = new double[3];
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < 3; j++) {
                mean1[j] += data1.get(i).getConvertedData()[j];
                mean2[j] += data2.get(i).getConvertedData()[j];
            }
        }
        for (int i = 0; i < 3; i++) {
            mean1[i] /= len;
            mean2[i] /= len;
        }
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < 3; j++) {
                cor[j] += (data1.get(i).getConvertedData()[j] - mean1[j]) * (data2.get(i).getConvertedData()[j] - mean2[j]);
                sd1[j] += Math.pow(data1.get(i).getConvertedData()[j] - mean1[j], 2);
                sd2[j] += Math.pow(data2.get(i).getConvertedData()[j] - mean2[j], 2);
            }
        }
        for (int i = 0; i < 3; i++) {
            sd1[i] = Math.sqrt(sd1[i] / len);
            sd2[i] = Math.sqrt(sd2[i] / len);
            cor[i] /= len;
        }
        for (int i = 0; i < 3; i++) {
            cor[i] /= (sd1[i] * sd2[i]);
        }
        return cor;
    }

}
