package com.leocai.detecdtion.core;

import android.util.Log;

import com.leocai.detecdtion.blebrodcast.ShakeParameters;
import com.leocai.publiclibs.ShakingData;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by leocai on 16-1-5.
 */
public class MasterTest extends TestCase {
    private static final String TAG = "MasterTest";
    Master master = new Master();

    @Override
    public void setUp() throws Exception {
        super.setUp();
        List<ShakingData> selfShakingData = new ArrayList<>();
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < 5; i++) {
            ShakingData sd = new ShakingData();
            double[] cd = new double[]{random.nextDouble()*10,random.nextDouble()*10,random.nextDouble()*10};
            sd.setConvertedData(cd);
            selfShakingData.add(sd);
        }
        master.setShakingDatas(selfShakingData);
    }

    public void testTrainParameters() throws Exception {

        List<ShakingData> trainDatas = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            trainDatas.add(new ShakingData());
        }
        ShakeParameters shakeParameter = master.trainParameters(trainDatas);
//        System.out.println(shakeParameter.toString());
    }



    public void testIterTheta() throws Exception {
        System.out.println("asda");
        double[] gaccInit = new double[]{1,0,0};
        double initTheta = 2;
        double[][] initMatrix = master.getInitMatrix(gaccInit,initTheta);
        master.transformByParameter(initMatrix, master.getShakingDatas(),master.getShakingDatas().size());
    }


    public void testGetInitMatrix() throws Exception {
        double[] gaccInit = new double[]{0,0,1};
        double[][] initMatrix = master.getInitMatrix(gaccInit, 0);
        double[][] initMatrix2 = master.getInitMatrix(gaccInit, Math.PI/2);
        System.out.println(Arrays.toString(initMatrix));

    }


    public void testTransformByParameter() throws Exception {
        List<ShakingData> shakingDatas = master.getShakingDatas();
        double[] gaccInit = new double[]{0,0,1};
        double[][] initMatrix =master.getInitMatrix(gaccInit, Math.PI/2);
        List<ShakingData> cvDatas = master.transformByParameter(initMatrix, shakingDatas,shakingDatas.size());
        System.out.println();
    }

    public void testGenerateBits() throws Exception {
        List<ShakingData> shakingDatas = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            shakingDatas.add(new ShakingData());
        }
//        ShakeBits bits = master.generateBits(shakingDatas);
//        Log.d(TAG,bits.toString());
    }
}