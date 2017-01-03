package com.leocai.publiclibs.multidecicealign;

import android.util.Log;

import com.dislab.leocai.spacesync.connection.DataClientImpl;
import com.leocai.publiclibs.PublicConstants;

import java.io.IOException;

/**
 * 监听传感器数据，用于将传感器数据写到socket中
 *
 * Created by leocai on 15-12-21.
 */
public class SensorSokectWriter extends SensorGlobalWriter{
    private static final String TAG = "SensorDataWriter";
    private DataClientImpl dataClient;
    private volatile boolean stop;

    @Override
    public void setFileName(String address) throws IOException {
        dataClient.connect(address,10007);
    }

    public SensorSokectWriter() {
        dataClient = new DataClientImpl();
        cuShakingData.setLinearAccData(null);
        cuShakingData.setGyrData(null);
        cuShakingData.setDt(0);
        cuShakingData.setIndex(0);
        cuShakingData.setTimeStamp(0);
    }

    @Override
    public void startDetection() {
        stop = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!stop) {
                    if (cuShakingData.getLinearAccData() == null) continue;
                    cuShakingData.transform();
                    notifyObservers(cuShakingData);
                    setChanged();
                    try {
                        Thread.sleep(PublicConstants.SENSOPR_PERIOD);
                        String info = cuShakingData.getCSV();
                        Log.d(TAG, info);
                        dataClient.sendSample(info);
                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void close() {
        stop = true;
        try {
            if(dataClient!=null)
            dataClient.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
