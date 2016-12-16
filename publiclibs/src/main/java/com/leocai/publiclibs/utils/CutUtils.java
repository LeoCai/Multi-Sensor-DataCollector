package com.leocai.publiclibs.utils;

import spacesync.SensorData;

public class CutUtils {

	public static SensorData cutData(SensorData sensorData) {
		double[] resultant_data = DataUtils.resultantData(sensorData.getLinearAccs());
		int start = 0;
		double threhold = 0.6;
		for (int i = 0; i < resultant_data.length; i++) {
			if (resultant_data[i] > threhold) {
				start = i;
				break;
			}
		}
		start = start >= 10 ? start - 10 : 0;
		double[][] cut_sensorData = DataUtils.selectRows(sensorData.getData(), start, sensorData.getData().length - 1);
//		PlotUtils.plotData(sensorData.getLinearAccs());
		sensorData.setData(cut_sensorData);
//		PlotUtils.plotData(sensorData.getLinearAccs());
		return sensorData;
	}

}
