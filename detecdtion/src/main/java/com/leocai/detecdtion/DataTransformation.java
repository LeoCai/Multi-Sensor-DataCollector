package com.leocai.detecdtion;

import com.leocai.detecdtion.blebrodcast.MixedSensorData;
import com.leocai.detecdtion.blebrodcast.ShakeParameters;
import com.leocai.detecdtion.transformation.MatrixUpdate;
import com.leocai.detecdtion.transformation.MatrixUtils;

/**
 * Created by leocai on 15-12-25.
 */
public class DataTransformation {
    private  double[][] newAcc6A;

    public DataTransformation(MixedSensorData selfMixedSensorData, ShakeParameters shakeParameters) {
//        double[][] accData = selfMixedSensorData.getLinearAccData();
//        double[][] gyrData = selfMixedSensorData.getGyrData();
//        double[][] initMatrix6A = ParameterLearning.getInitMatrix(gaccInit6A, initTheta6A);
//        MatrixUpdate matrixUpdate6A = new MatrixUpdate();
//
//        matrixUpdate6A.setCuMatrix(initMatrix6A);
//
//        newAcc6A = new double[0][];
//
//        int dataSize = 0;
//        for (int i = 0; i < dataSize; i++) {
//            double dt = 0.02;
//            double[][] cuMatrix6A = matrixUpdate6A.updateMatrixByGYR(selfMixedSensorData.getGyrData()[i], dt);
//            newAcc6A[i] = MatrixUtils.convertMatrixToVector(MatrixUtils.multiply(MatrixUtils.convertVectorToMatrix(selfMixedSensorData.getLinearAccData()[i]), cuMatrix6A));//TODO order
//        }

    }

    public MixedSensorData getTransformedData() {
        return null;
    }

//    public MixedSensorData getTransformedData() {
//        return newAcc6A;
//    }
}
