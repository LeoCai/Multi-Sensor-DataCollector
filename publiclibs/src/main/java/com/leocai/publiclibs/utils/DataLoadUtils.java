package com.leocai.publiclibs.utils;

import java.awt.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;

import spacesync.SensorData;

public class DataLoadUtils {

	public static SensorData loadSensorData(String fileName) throws IOException {
		FileReader fileReader = new FileReader(new File(fileName));
		String newLine = null;
		int[] ids = new int[] { 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 20 };
		LineNumberReader lnr = new LineNumberReader(fileReader);
		lnr.skip(Long.MAX_VALUE);
		int lineNumber = lnr.getLineNumber();
		double[][] totalDatas = new double[lineNumber - 1][ids.length];
		lnr.close();
		BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(fileName)));
		bufferedReader.readLine(); // title;
		int k = 0;
		while ((newLine = bufferedReader.readLine()) != null) {
			String[] str_line = newLine.split(",");
			double[] singleData = new double[ids.length];
			for (int i = 0; i < ids.length; i++) {
				singleData[i] = Double.parseDouble(str_line[ids[i]]);
			}
			totalDatas[k++] = singleData;
		}
		SensorData sensorData = new SensorData(totalDatas);
		bufferedReader.close();
		return sensorData;
	}

}
