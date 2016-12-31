package com.leocai.detecdtion;

import com.leocai.detecdtion.blebrodcast.MixedSensorData;
import com.leocai.detecdtion.blebrodcast.ShakeParameters;
import com.leocai.detecdtion.transformation.MatrixUpdate;
import com.leocai.detecdtion.transformation.MatrixUtils;
import com.leocai.detecdtion.utils.BytesUtils;

import java.util.Arrays;

/**
 * Created by leocai on 15-12-25.
 */
public class ParameterLearning {
    private ShakeParameters parameters;
    private double[][] accData88;
    private double[][] gyrData88;
    private int dataCount;
    private int size;

    private MixedSensorData selfMixedSensorData;
    private double theta88;

    double[] gaccInit6A;
    double[] gaccInit88;

    private MatrixUpdate matrixUpdate6A;
    private MatrixUpdate matrixUpdate88;
    private double dt;

    public ParameterLearning() {
        parameters = new ShakeParameters(1.5);
    }

    public ShakeParameters getParameters() {
        return parameters;
    }

    public void setParameters(ShakeParameters parameters) {
        this.parameters = parameters;
    }

    public void trainParameter() {
//        int iterNum = 100;
//        double initTheta6A = 0;
//        double maxMean = Double.MIN_VALUE;
//        double maxIndex = -1;
//        for (int i = 0; i < iterNum; i++) {
//            double initTheta88 = 2 * Math.PI / iterNum * i;
//            double cor[] = iterTheta(initTheta6A, initTheta88);
//            double meanCor = meanOfCor();
//            if (meanCor > maxMean) {
//                maxMean = meanCor;
//                maxIndex = i;
//            }
//        }
//        this.theta88 = 2 * Math.PI / iterNum * maxIndex;
    }

    private double meanOfCor() {
        return 0;
    }

    public static double[] iterTheta(double initTheta6A, double initTheta88,
                              double[] gaccInit6A, double[] gaccInit88, double dt,
                              MixedSensorData selfMixedSensorData,
                              double[][] accData88, double[][] gyrData88) {

        double[][] initMatrix6A = getInitMatrix(gaccInit6A, initTheta6A);
        double[][] initMatrix88 = getInitMatrix(gaccInit88, initTheta88);

        MatrixUpdate matrixUpdate6A = new MatrixUpdate();
        MatrixUpdate matrixUpdate88 = new MatrixUpdate();

        matrixUpdate6A.setCuMatrix(initMatrix6A);
        matrixUpdate88.setCuMatrix(initMatrix88);

        double[][] newAcc6A = new double[0][];
        double[][] newAcc88 = new double[0][];

        int dataSize = 0;
        for (int i = 0; i < dataSize; i++) {
            double[][] cuMatrix6A = matrixUpdate6A.updateMatrixByGYR(selfMixedSensorData.getGyrData()[i], dt);
            double[][] cuMatrix88 = matrixUpdate88.updateMatrixByGYR(gyrData88[i], dt);
            newAcc6A[i] = MatrixUtils.convertMatrixToVector(MatrixUtils.multiply(MatrixUtils.convertVectorToMatrix(selfMixedSensorData.getAccData()[i]), cuMatrix6A));//TODO order
            double[][] accDataMt = MatrixUtils.convertVectorToMatrix(accData88[i]);
            newAcc88[i] = MatrixUtils.convertMatrixToVector(MatrixUtils.multiply(accDataMt, cuMatrix88));//TODO order
        }
        return getAccCors(newAcc6A, newAcc88);
    }

    private static double[] getAccCors(double[][] newAcc6A, double[][] newAcc88) {
        return new double[0];
    }

    public static double[][] getInitMatrix(double[] gaccInit6A, double initTheta6A) {
        return new double[0][];
    }

    public boolean addData(byte[] data) {
//        if (size != 0 && dataCount >= size) return false;
//        if (BytesUtils.checkSizeType(data)) {
//            if (size == 0) {
//                size = BytesUtils.getSizeFromBytes(data);
//                if (accData88 == null) accData88 = new double[size];
//                if (gyrData88 == null) gyrData88 = new double[size];
//                Arrays.fill(accData88, -1);
//                Arrays.fill(gyrData88, -1);
//            }
//        } else if (size != 0) {
//            SensorDataWithIndex sensorDataWithIndex = BytesUtils.getSensorDataWithIndexFromBytes(data);
//            if (accData88 != null) {
//                if (accData88[sensorDataWithIndex.getIndex()] == -1) {
//                    accData88[sensorDataWithIndex.getIndex()] = sensorDataWithIndex.getAccVal();
//                    dataCount++;
//                }
//            }
//            if (gyrData88 != null)
//                gyrData88[sensorDataWithIndex.getIndex()] = sensorDataWithIndex.getGyrVal();
//            if (dataCount >= size) {
//                return false;
//            }
//        }
        return true;
    }

    public String getDataInfo() {
        return Arrays.toString(accData88) + "\n" + Arrays.toString(gyrData88);
    }
}
