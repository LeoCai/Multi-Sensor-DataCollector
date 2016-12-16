package com.leocai.publiclibs.utils;

public class VectorUtils {

	public static double getAngle(double[] v1, double[] v2) {
		double vs = vectorMultiply(v1, v2);
		double abs_1 = absVector(v1);
		double abs_2 = absVector(v2);
		return Math.acos(vs / (abs_1 * abs_2)) / Math.PI*180;
	}

	private static double vectorMultiply(double[] v1, double[] v2) {
		double sum = 0;
		for (int i = 0; i < v1.length; i++) {
			sum += v1[i] * v2[i];
		}
		return sum;
	}

	public static double[] vectorNumMultiply(double[] gv, double d) {
		double[] newdata = new double[gv.length];
		for (int i = 0; i < gv.length; i++) {
			newdata[i] = gv[i] * d;
		}
		return newdata;
	}

	public static double absVector(double[] gv) {
		double sum = 0;
		for (double d : gv) {
			sum += d * d;
		}
		return Math.sqrt(sum);
	}
}
