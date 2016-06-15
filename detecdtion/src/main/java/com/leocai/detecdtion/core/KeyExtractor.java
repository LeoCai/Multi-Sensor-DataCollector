package com.leocai.detecdtion.core;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import com.leocai.detecdtion.transformation.MatrixUpdate;
import com.leocai.detecdtion.transformation.MatrixUtils;
import com.leocai.detecdtion.utils.MathUtils;
import com.leocai.publiclibs.ShakeDetector;
import com.leocai.publiclibs.ShakingData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

/**
 * Created by leocai on 15-12-31.
 */
public abstract class KeyExtractor extends Observable implements SensorEventListener, Observer {

    protected static final double ALPHA_DEFAULT = 0.1;

    protected static final int TRAINNING_SIZE = 10;
    private static final String TAG = "KeyExtractor";
    private static final int EXCUTION_CONSECUTIVE_NUM = 3;

    private boolean master;
    private int misMatch;

    private String logInfo = "";

    public enum ExtractorStates {
        LISTEN, TRAIN_RECEIEVED, ACCEPTED, CONNECTING, CONNECTED, TRAINDATA_SENDED, PARAMETER_SENDED, PARAMETER_RECEIEVED, TRAIN
    }

    private ExtractorStates states = ExtractorStates.LISTEN;

    InputStream in;
    OutputStream out;

    protected static final String NAME_SECURE = "BluetoothChatSecure";
    // Unique UUID for this application
    protected static final UUID MY_UUID_INSECURE =
            UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

    private ShakeDetector shakeDetector;

    List<ShakingData> transformByParameter(double[][] initMatrix, List<ShakingData> shakingDatas, int len) {
        MatrixUpdate matrixUpdate6A = new MatrixUpdate();
        matrixUpdate6A.setCuMatrix(initMatrix);
        ShakingData firstData = shakingDatas.get(0);
        double[][] firstLineaAccMatrix = MatrixUtils.convertVectorToMatrix(firstData.getLinearAccData());
        double[] firstConvertedData = MatrixUtils.convertMatrixToVector(MatrixUtils.multiply(initMatrix, firstLineaAccMatrix));
        firstData.setConvertedData(firstConvertedData);
        for (int i = 1; i < len; i++) {
            //TODO dt
            ShakingData shakingData = shakingDatas.get(i);
            double[][] cuMatrix6A = matrixUpdate6A.updateMatrixByGYR(shakingData.getGyrData(), shakingData.getDt());
            double[][] lineaAccMatrix = MatrixUtils.convertVectorToMatrix(shakingData.getLinearAccData());
            double[] convertedData = MatrixUtils.convertMatrixToVector(MatrixUtils.multiply(cuMatrix6A, lineaAccMatrix));//TODO order
            shakingData.setConvertedData(convertedData);
        }
        return shakingDatas;
    }

    /**
     * leval-crossing algorithm
     * if data>q+, then 1
     * if data<q-, then 0
     * else, 2(other state)
     * find consecutive bits (cout>=m) to generate excurtions,
     * generate corresponding indexs,
     * send indexes to Bob,check corresponding excurtions,
     * send back new indexes to Alice
     * @param shakingDatas
     * @param alpha
     * @return
     */
    public ShakeBits generateBits(List<ShakingData> shakingDatas,double alpha){
        int m = EXCUTION_CONSECUTIVE_NUM;
        double []connectedDatas = connectDatas(shakingDatas);
        double []deltaMean = getDeltaMean(connectedDatas);
        byte []tempBits = generateTempBits(connectedDatas, deltaMean, alpha);
        return bitsByCooperate(tempBits,m);
//        ExcurtionsWithIndexes excurtions = computeExcurtions(tempBits, m);
//        int []indexesFromBob = askIndexesFromBob(excurtions.getIndexes());
//        return getResultsBits(tempBits, indexesFromBob);
    }

    protected abstract ShakeBits bitsByCooperate(byte[] tempBits, int m);

    private byte[] generateTempBits(double []connectedDatas,double[] deltaMean, double alpha) {
        double mean = deltaMean[0],delta = deltaMean[1];
        int len = connectedDatas.length;
        double q_plus = mean + alpha*delta,q_minus = mean - alpha*delta;
        byte []tempBits = new byte[len];
        for (int i = 0; i < len; i++) {
            double data = connectedDatas[i];
            byte bit = 2;
            if(data>q_plus){
                bit = 1;
            }else if(data<q_minus){
                bit = 0;
            }
            tempBits[i] = bit;
        }
        return tempBits;
    }

    private double[] getDeltaMean(double[] connectedDatas) {
        double mean =0, delta = 0;
        int len = connectedDatas.length;
        for (int i = 0; i < len; i++) {
            mean += connectedDatas[i];
        }
        mean /= len;
        for (int i = 0; i < len; i++) {
            delta += Math.pow(connectedDatas[i]-mean,2);
        }
        delta = Math.sqrt(delta/len);
        return new double[]{mean,delta};
    }

    private double[] connectDatas(List<ShakingData> shakingDatas) {
        int len = shakingDatas.size();
        double []connectedData = new double[len*3];
        for (int i = 0; i < len; i++) {
            double []cvData = shakingDatas.get(i).getConvertedData();
            for (int j = 0; j < 3; j++) {
                connectedData[j*len+i] = cvData[j];
            }
        }
        return connectedData;
    }


    private ShakeBits generateBits(List<ShakingData> shakingDatas) {
        double alpha = 0.3;
        int windowSize = 5, excursion = 3;
        List<List<Byte>> bits = new ArrayList<>();
        List<List<Byte>> finalBits = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            bits.add(new ArrayList<Byte>());
            finalBits.add(new ArrayList<Byte>());
        }
        List<Byte> finalBitsArray = new ArrayList<>();

        for (int start = 0; start <= shakingDatas.size() - windowSize; start += windowSize) {
            int end = start + windowSize;
            //compute mean, sd
            double[] mean = new double[3], sd = new double[3];
            for (int i = start; i < end; i++) {
                double[] cvDatas = shakingDatas.get(i).getConvertedData();
                for (int dimension = 0; dimension < cvDatas.length; dimension++) {
                    mean[dimension] += cvDatas[dimension];
                }
            }
            for (int dimension = 0; dimension < 3; dimension++) {
                mean[dimension] /= windowSize;
            }
            for (int i = start; i < end; i++) {
                double[] cvDatas = shakingDatas.get(i).getConvertedData();
                for (int dimension = 0; dimension < cvDatas.length; dimension++) {
                    sd[dimension] += Math.pow(cvDatas[dimension] - mean[dimension], 2);
                }
            }
            for (int dimension = 0; dimension < 3; dimension++) {
                sd[dimension] = Math.sqrt(sd[dimension] / windowSize);
            }


            for (int i = start; i < end; i++) {
                Log.d(TAG, "start:" + start + ",end:" + end);
                double[] cvDatas = shakingDatas.get(i).getConvertedData();
                for (int dimension = 0; dimension < cvDatas.length; dimension++) {
//                    List<Byte> bitsArray = bits.get(dimension);
                    if (cvDatas[dimension] >= mean[dimension] + alpha * sd[dimension]) {
                        finalBitsArray.add((byte) 0);
                        finalBitsArray.add((byte) 1);
                    } else if (cvDatas[dimension] <= mean[dimension] - alpha * sd[dimension]) {
                        finalBitsArray.add((byte) 0);
                        finalBitsArray.add((byte) 0);
                    } else {
                        finalBitsArray.add((byte) 1);
                        finalBitsArray.add((byte) 1);
                    }
//                    bits.get(dimension).add((byte) cvDatas[dimension]);
                }
            }
//            for (int dimension = 0; dimension < 3; dimension++) {
//                List<Byte> bitsArray = bits.get(dimension);
//                byte cuB = bitsArray.get(0);
//                int consetiveNum = 0;
//                for (byte b : bitsArray) {
//                    finalBitsArray.a
////                    if (b != cuB) {
////                        if (cuB != 2 && consetiveNum >= excursion) {
////                            finalBitsArray.add(cuB);
////                        }
////                        cuB = b;
////                        consetiveNum = 1;
////                    } else consetiveNum++;
//                }
//            }
            //quantazation
        }
        return new ShakeBits(finalBitsArray);
    }

    public void startReconcilation(ShakeBits shakeBits, ReconcilationEndCallBack reconcilationEndCallBack) {
        List<Byte> bitsList = shakeBits.getBits();
        byte[] bits = new byte[bitsList.size()];
        for (int i = 0; i < bitsList.size(); i++) {
            bits[i] = bitsList.get(i);
        }
        int iterNum = 20;
        int blockNum = 16;
        int bitsSize = bits.length;
        int start = -1, end = bits.length - 2;
        for (int i = 0; i < iterNum; i++) {
            //Reorder
            start++;
            end++;
            //Seperate into blocks
            //for each block
            for (int j = 0; j < blockNum; j++) { //except last block
                //  if(find) do binary and corect
                int subStart = start + j * bitsSize / blockNum;
                int subEnd;
                if (j != blockNum - 1)
                    subEnd = start + (j + 1) * bitsSize / blockNum - 1;
                else subEnd = end; //lastblock
                try {
                    if (!compareParity(bits, subStart, subEnd)) {
                        // Binary and slave correct
                        while (subStart != subEnd) {
                            int subMid = (subStart + subEnd) / 2;
                            if (!compareParity(bits, subStart, subMid))
                                subEnd = subMid;
                            else
                                subStart = subMid + 1;
                        }
                        misMatch++;
                        if (!isMaster())
                            bits[subStart % bitsSize] = (byte) (1 - bits[subStart % bitsSize]); // correct
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //TODO last block
        }
        for (int i = 0; i < bitsSize; i++) {
            bitsList.set(i, bits[i]);
        }
        reconcilationEndCallBack.onReconcilationEnd(bitsList, 1.0 * misMatch / bitsSize);
        List<Byte> finalSecretBits = extractRandomness(bitsList);
    }

    private List<Byte> extractRandomness(List<Byte> bitsList) {
        RandomnessExtractor randomnessExtractor = new  RandomnessExtractor(bitsList);
        return randomnessExtractor.getKey(bitsList);
    }


    protected abstract boolean compareParity(byte[] bits, int subStart, int subMid) throws IOException;

    public void onReconcilationEnd(ShakeKey shakeKey) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        shakeDetector.onSensorChanged(event);
    }

    public double[][] getInitMatrix(double[] gaccInit, double initTheta) {
        double gaccNorm[] = MathUtils.norm(gaccInit);
        double gx = gaccNorm[0], gy = gaccNorm[1], gz = gaccNorm[2];
        double r = 1;
        double fi = Math.PI / 2 - Math.atan2(Math.sqrt(Math.pow(gx, 2) + Math.pow(gy, 2)), gz);
        double xInit[] = MathUtils.convertFromSphericalToCardinal(r, fi, initTheta);
        double[] yInit = MathUtils.crossProduct(gaccNorm, xInit);
        yInit = MathUtils.norm(yInit);
        xInit = MathUtils.crossProduct(gaccNorm, yInit);
        xInit = MathUtils.norm(xInit);
        double[][] initMatrix = new double[][]{
                {gx, gy, gz},
                {xInit[0], xInit[1], xInit[2]},
                {yInit[0], yInit[1], yInit[2]}
        };
        return initMatrix;
    }

    public InputStream getIn() {
        return in;
    }

    public void setIn(InputStream in) {
        this.in = in;
    }

    public OutputStream getOut() {
        return out;
    }

    public void setOut(OutputStream out) {
        this.out = out;
    }

    public abstract void onGetShakingDatas(List<ShakingData> shakingDatas);

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void update(Observable observable, Object data) {
        onGetShakingDatas((List<ShakingData>) data);
    }

    //TODO order theta
    //TODO init Index
    public double[] iterTheta(double initThetaMaster, double initThetaSlave, List<ShakingData> slaveTrainDatas, List<ShakingData> masterDatas) {
        double[][] initMatrixSlave = getInitMatrix(slaveTrainDatas.get(0).getGravityAccData(), initThetaSlave);
//        LogMatrix("SlaveInitMatrix",initMatrixSlave);
        List<ShakingData> dataSlave = transformByParameter(initMatrixSlave, slaveTrainDatas, slaveTrainDatas.size());
        double[][] initMatrixMaster = getInitMatrix(masterDatas.get(0).getGravityAccData(), initThetaMaster);
//        LogMatrix("MasterInitMatrix",initMatrixMaster);
        List<ShakingData> dataMaster = transformByParameter(initMatrixMaster, masterDatas, slaveTrainDatas.size());

        StringBuilder infoTrain = new StringBuilder("Train:\n");
        StringBuilder infoMaster = new StringBuilder("Master:\n");
        for (int i = 0; i < dataSlave.size(); i++) {
            infoTrain.append(Arrays.toString(dataSlave.get(i).getConvertedData()));
            infoTrain.append("\n");
            infoMaster.append(Arrays.toString(dataMaster.get(i).getConvertedData()));
            infoMaster.append("\n");
        }
//        Log.d(TAG,infoTrain.toString());
//        Log.d(TAG, infoMaster.toString());

        return MathUtils.getCors(dataSlave, dataMaster, dataSlave.size());
    }

    private void LogMatrix(String head, double[][] initMatrixSlave) {
        StringBuilder stringBuilder = new StringBuilder(head);
        stringBuilder.append("\n");
        for (double[] line : initMatrixSlave) {
            stringBuilder.append(Arrays.toString(line));
            stringBuilder.append("\n");
        }
        Log.d(TAG, stringBuilder.toString());

    }

    public boolean isMaster() {
        return master;
    }

    public void setMaster(boolean master) {
        this.master = master;
        customNotify();
    }

    public String getLogInfo() {
        return logInfo;
    }

    public void setLogInfo(String logInfo) {
        this.logInfo = logInfo;
        customNotify();
    }

    public ExtractorStates getStates() {
        return states;
    }

    public void setStates(ExtractorStates states) {
        this.states = states;
        customNotify();
    }

    private void customNotify() {
        String data = (isMaster() ? "Master" : "Slave") + ": " + states.toString() + "\n" + logInfo;
        setChanged();
        notifyObservers(data);
    }

    public void setShakeDetector(ShakeDetector shakeDetector) {
        this.shakeDetector = shakeDetector;
        shakeDetector.addObserver(this);
    }

    public ShakeDetector getShakeDetector() {
        return shakeDetector;
    }
}
