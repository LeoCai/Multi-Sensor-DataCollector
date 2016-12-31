package com.leocai.publiclibs.utils;

/**
 * Created by leocai on 15-12-23.
 */
public class RollApply {

	public static double[][] rollApply(double[][] data, int window, RollApplyFunc rollApplyFunc) {
		double[][] newData = new double[data.length][data[0].length];
		for (int i = 0; i < data.length; i++) {
			int start = i - window / 2, end = i + window / 2;
			start = start < 0 ? 0 : start;
			end = end >= data.length ? data.length - 1 : end;
			double[] val = rollApplyFunc.apply(data, start, end);
			newData[i] = val;
		}
		return newData;
	}

}
