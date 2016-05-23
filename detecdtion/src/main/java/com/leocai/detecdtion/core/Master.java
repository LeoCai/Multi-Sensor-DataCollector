package com.leocai.detecdtion.core;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.leocai.detecdtion.blebrodcast.ShakeParameters;
import com.leocai.detecdtion.utils.MathUtils;
import com.leocai.publiclibs.ShakingData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by leocai on 15-12-31.
 */
public class Master extends KeyExtractor {

    private static final String TAG = "Master";
    private List<ShakingData> shakingDatas;

    private BluetoothServerSocket mmServerSocket;

    private BluetoothSocket socket;

    public Master() {
        setMaster(true);
        BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();

        try {
            mmServerSocket = mAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME_SECURE,
                    MY_UUID_INSECURE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startListen(final AccecptedCallBack accecptedCallBack) {
        setStates(ExtractorStates.LISTEN);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = mmServerSocket.accept();
                    setStates(ExtractorStates.ACCEPTED);
                    setIn(socket.getInputStream());
                    setOut(socket.getOutputStream());
                    accecptedCallBack.onAccepted();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void close() {

    }

    @Override
    public void onGetShakingDatas(List<ShakingData> shakingDatas) {
        this.shakingDatas = shakingDatas;
        startListen(new AccecptedCallBack() {
            @Override
            public void onAccepted() {
                //receieve
                DataInputStream dataInputStream = new DataInputStream(in);
                List<ShakingData> trainingDatas = new ArrayList<>();
                for (int i = 0; i < TRAINNING_SIZE; i++) {
                    byte[] sdBuffer = new ShakingData().getBytes();
                    try {
                        dataInputStream.read(sdBuffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    trainingDatas.add(new ShakingData(sdBuffer));
                }
                StringBuilder logInfo = new StringBuilder();
                for (ShakingData sd : trainingDatas) {
                    logInfo.append(sd.toString());
                    logInfo.append("\n");
                }
                setLogInfo(logInfo.toString());
                onRecievingTrainDatas(trainingDatas);
            }
        });
    }

    public void onRecievingTrainDatas(List<ShakingData> trainDatas) {
        setStates(ExtractorStates.TRAIN_RECEIEVED);
        //trainData//TODO trainData and SendBackParameters
        ShakeParameters shakingParameters = trainParameters(trainDatas);//TODO uncomment
//        ShakeParameters shakingParameters = new ShakeParameters(9, 2);
        try {
            ShakeParameters.send(out, shakingParameters);
            setLogInfo(shakingParameters.toString());
            setStates(ExtractorStates.PARAMETER_SENDED);
        } catch (IOException e) {
            e.printStackTrace();
        }
        double[][] initMatrix = getInitMatrix(
                shakingDatas.get(0).getGravityAccData(),
                shakingParameters.getInitThetaMaster());
        List<ShakingData> convertedData = transformByParameter(initMatrix, shakingDatas, shakingDatas.size());
        double alpha = ALPHA_DEFAULT;
        ShakeBits shakeBits = generateBits(convertedData,alpha);
        Log.d(TAG,"convertedDataSize:"+convertedData.size());
        Log.d(TAG, "shakeBitsSize:" + shakeBits.getBits().size());
        startReconcilation(shakeBits, new ReconcilationEndCallBack() {
            @Override
            public void onReconcilationEnd(List<Byte> bitsList, double mismatchRate) {
                String logInfo = "";
                for (byte b : bitsList) {
                    logInfo += b;
                }
                logInfo += "\n";
                logInfo += "MismatchRate:" + mismatchRate;
                setLogInfo(logInfo);
            }
        });
    }


    /**
     * Important Core Function
     * test before:set shakingDatas
     * @param trainDatas
     * datas from slave
     * @return
     * shakeparameter
     */
    public ShakeParameters trainParameters(List<ShakingData> trainDatas) {
        for(ShakingData shakingData:trainDatas){
            shakingData.setConvertedData(shakingData.getLinearAccData());
        }
        for (int i = 0; i < trainDatas.size(); i++) {
            shakingDatas.get(i).setConvertedData(shakingDatas.get(i).getLinearAccData());
        }
        logTrainDatas(trainDatas);

        double[] corsBefore = MathUtils.getCors(trainDatas, shakingDatas, trainDatas.size());
        Log.d(TAG,"CorsBefore:" + Arrays.toString(corsBefore) +", Mean:" + meanOfCor(corsBefore));

        int iterNum = 20;
        double initThetaMaster = 0;
        double maxMean = Double.MIN_VALUE;
        double maxIndex = -1;
        for (int i = 0; i < iterNum; i++) {
            double initThetaSlave = 2 * Math.PI / iterNum * i;
            double cor[] = iterTheta(initThetaMaster, initThetaSlave, trainDatas, shakingDatas);
            double meanCor = meanOfCor(cor);
            Log.d(TAG,"IterateCors:" +Arrays.toString(cor) + ", Mean:" + meanCor);

            if (Math.abs(meanCor) > maxMean) {
                maxMean = Math.abs(meanCor);
                maxIndex = i;
            }
        }
        Log.d(TAG,"CorsAfter:" + maxMean);

        double thetaSlave = 2 * Math.PI / iterNum * maxIndex;
        return new ShakeParameters(initThetaMaster, thetaSlave,maxMean);
    }

    private void logTrainDatas(List<ShakingData> trainDatas) {
        StringBuilder infoTrain = new StringBuilder("Train:\n");
        StringBuilder infoMaster = new StringBuilder("Master:\n");
        for (int i = 0; i < trainDatas.size(); i++) {
            infoTrain.append(Arrays.toString(trainDatas.get(i).getConvertedData()));
            infoTrain.append("\n");
            infoMaster.append(Arrays.toString(shakingDatas.get(i).getConvertedData()));
            infoMaster.append("\n");
        }
        Log.d(TAG, infoTrain.toString());
        Log.d(TAG, infoMaster.toString());
    }

    private double meanOfCor(double cor[]) {
        double sum = 0;
        for (double c : cor) {
            sum += Math.abs(c);
        }
        return sum / cor.length;
    }


    public List<ShakingData> getShakingDatas() {
        return shakingDatas;
    }

    public void setShakingDatas(List<ShakingData> shakingDatas) {
        this.shakingDatas = shakingDatas;
    }


    @Override
    protected ShakeBits bitsByCooperate(byte[] tempBits, int m) {
        ExcurtionsWithIndexes excurtions = computeExcurtions(tempBits, m);
        int []indexesFromBob = askIndexesFromBob(excurtions.getIndexes());
        return getResultsBits(tempBits, indexesFromBob);
    }

    private ShakeBits getResultsBits(byte[] tempBits, int[] indexesFromBob) {
        int indexLen = indexesFromBob.length;
        List<Byte> shakeBits = new ArrayList<>();
        for (int i = 0; i < indexLen; i++) {
            shakeBits.add(tempBits[indexesFromBob[i]]);
        }
        return new ShakeBits(shakeBits);
    }

    private ExcurtionsWithIndexes computeExcurtions(byte[] tempBits, int m) {
        int len = tempBits.length;
        int consecutive = 1;
        double preBit = tempBits[0];
        List<Byte> excutions = new ArrayList<>();
        List<Integer> indexes = new ArrayList<>();
        for (int i = 1; i < len; i++) {
            byte cuBit = tempBits[i];
            if(cuBit==preBit&&cuBit!=2) consecutive++;
            else {consecutive = 1; preBit = cuBit;}
            if(consecutive==m) {
                consecutive = 0;
                excutions.add(cuBit);
                indexes.add((i-(m-1)/2));//TODO check　middle index
            }
        }
        return new ExcurtionsWithIndexes(excutions,indexes);
    }

    private int[] askIndexesFromBob(int[] indexes) {
        DataOutputStream dataOutputStream = new DataOutputStream(out);
        int len = indexes.length;
        try {
            dataOutputStream.writeInt(len);
            for (int index : indexes) {
                dataOutputStream.writeInt(index);
            }
            dataOutputStream.flush();
        }catch (IOException e){
            e.printStackTrace();

        }
        DataInputStream dataInputStream = new DataInputStream(in);
        int indexesFromBob[] = new int[0];
        try {
            int num = dataInputStream.readInt();
            indexesFromBob = new int[num];
            for (int i = 0; i < num; i++) {
                indexesFromBob[i] = dataInputStream.readInt();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return indexesFromBob;
    }

    @Override
    protected boolean compareParity(byte[] bits, int subStart, int subEnd) throws IOException {
        int parity = 0;
        boolean masterEven;
        int len = bits.length;
        for (int i = subStart; i <= subEnd; i++) parity += bits[i%len];
        masterEven = (parity % 2 == 0);
        DataInputStream dataIn = new DataInputStream(in);
        DataOutputStream dataOut = new DataOutputStream(out);
        boolean slaveEven = dataIn.readBoolean();
        if (masterEven == slaveEven) {
            dataOut.writeBoolean(true);
            return true;
        } else {
            dataOut.writeBoolean(false);
            return false;
        }
    }
}
