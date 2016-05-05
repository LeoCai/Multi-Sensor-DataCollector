package com.leocai.detecdtion.core;

import android.content.Context;
import android.os.Environment;

import com.leocai.publiclibs.ShakeDetector;
import com.leocai.publiclibs.ShakingData;

import java.io.BufferedReader;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by leocai on 16-1-8.
 */
public class ShakeDatasStore implements Observer {

    public static final String SLAVE_FILE = "Slave.csv";
    public static final String MASTER_FILE = "Master.csv";
    private DataOutputStream dataOutput;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private String fileName;

    public void initFile(String fileName)  {
        File file = new File(Environment.getExternalStorageDirectory(),fileName);
        try {
            dataOutput = new DataOutputStream(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        writeTofile((List<ShakingData>) data);
    }

    public void writeTofile(List<ShakingData> data) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(new File(Environment.getExternalStorageDirectory(),fileName));
            fileWriter.write(new ShakingData().getCSVHead());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(fileWriter==null) return;
        for(ShakingData shakingData:data){
            try {
                fileWriter.write(shakingData.getCSV());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<ShakingData> readFromFile(Context context){
        List<ShakingData> shakingDatas = new ArrayList<>();
        try {
            InputStream in = context.getResources().getAssets().open(this.fileName);
            BufferedReader bf = new BufferedReader(new InputStreamReader(in));
            bf.readLine();
            for (int i = 1; i < ShakeDetector.MAX_POINT_SIZE; i++) {
//                in.read(buffer);
                String dataLine = bf.readLine();
                shakingDatas.add(new ShakingData(dataLine));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return shakingDatas;
    }
}
