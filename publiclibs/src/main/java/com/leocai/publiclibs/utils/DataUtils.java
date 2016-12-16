package com.leocai.publiclibs.utils;

public class DataUtils {

	public static double[] resultantData(double[][] data) {
		double resultant_data[] = new double[data.length];
		for (int i = 0; i < data.length; i++) {
			double sum = 0;
			for (int j = 0; j < data[0].length; j++) {
				sum += Math.pow(data[i][j], 2);
			}
			resultant_data[i] = Math.sqrt(sum);
		}
		return resultant_data;
	}

	public static double[][] selectRows(double[][] data, int start, int end) {
		int len = end - start + 1;
		double[][] newData = new double[len][data[0].length];
		for (int i = start; i <= end; i++) {
			for (int j = 0; j < data[0].length; j++) {
				newData[i - start][j] = data[i][j];
			}
		}
		return newData;
	}

	public static double[][] smooth(double[][] data, int smoothSize) {
		return RollApply.rollApply(data, smoothSize, new RollApplyFunMean());
	}

	public static double[] parseData(String newLine, int[] ids) {
		String[] str_line = newLine.split(",");
		double[] singleData = new double[ids.length];
		for (int i = 0; i < ids.length; i++) {
			singleData[i] = Double.parseDouble(str_line[ids[i]]);
		}
		return singleData;
	}

}
