package com.leocai.publiclibs.utils;

public class RollApplyFunMean implements RollApplyFunc {

	@Override
	public double[] apply(double[][] buffer, int start, int end) {
		int colNum = buffer[0].length;
		int len = end - start + 1;
		double[] mean = new double[colNum];
		for (int i = start; i <= end; i++) {
			for (int j = 0; j < colNum; j++) {
				mean[j] += buffer[i][j];
			}
		}
		for (int i = 0; i < colNum; i++) {
			mean[i] /= len;
		}
		return mean;
	}

}
