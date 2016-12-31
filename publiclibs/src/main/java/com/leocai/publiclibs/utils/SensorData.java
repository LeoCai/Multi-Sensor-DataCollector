package com.leocai.publiclibs.utils;


import com.dislab.leocai.spacesync.utils.MatrixUtils;
import com.dislab.leocai.spacesync.utils.RotationUtils;

import java.util.Arrays;


public class SensorData {

	private static final int LINEAR_X = 0;
	private static final int LINEAR_Y = 1;
	private static final int LINEAR_Z = 2;
	private static final int GRIVATY_X = 3;
	private static final int GRIVATY_Y = 4;
	private static final int GRIVATY_Z = 5;
	private static final int GYR_X = 6;
	private static final int GYR_Y = 7;
	private static final int GYR_Z = 8;
	private static final int MAG_X = 9;
	private static final int MAG_Y = 10;
	private static final int MAG_Z = 11;
	private static final int GLOBAL_MAG_ACC_X = 12;
	private static final int GLOBAL_MAG_ACC_Y = 13;
	private static final int GLOBAL_MAG_ACC_Z = 14;
	private static final int DT_INDEX = 15;

	private double[][] data;

	public SensorData(double[][] totalDatas) {
		this.data = totalDatas;
	}

	public double[][] getData() {
		return data;
	}

	public void setData(double[][] data) {
		this.data = data;
	}

	public double[][] getLinearAccs() {
		return MatrixUtils.selectColumns(data, new int[]{LINEAR_X, LINEAR_Y, LINEAR_Z});
	}

	public double[][] getGyrs() {
		return MatrixUtils.selectColumns(data, new int[] { GYR_X, GYR_Y, GYR_Z });
	}

	public double[] getDT() {
		return MatrixUtils.selectColumn(data, DT_INDEX);
	}

	public double[][] getInitMatrix() {
		return new double[][] { { 1, 0, 0 }, { 0, 1, 0 }, { 0, 0, 1 } };
	}

	public double[][] getGravityAccs() {
		return MatrixUtils.selectColumns(data, new int[]{GRIVATY_X, GRIVATY_Y, GRIVATY_Z});
	}

	@Override
	public String toString() {
		return "SensorData [data=" + Arrays.toString(data) + "]";
	}

	public double[][] getMagnet() {
		return MatrixUtils.selectColumns(data, new int[]{MAG_X, MAG_Y, MAG_Z});
	}

	public double[][] getGlobalMagAcc() {
		return MatrixUtils.selectColumns(data, new int[]{GLOBAL_MAG_ACC_X, GLOBAL_MAG_ACC_Y, GLOBAL_MAG_ACC_Z});
	}

	public double[][] getInitGlobalMatrix_G2B() {
		double[] gravity_acc = getGravityAccs()[0];
		double[] mag_acc = getGlobalMagAcc()[0];
		return getRotationMatrixG2BByMag(gravity_acc, mag_acc);
	}

	public static double[][] getRotationMatrixG2BByMag(double[] gravity_acc, double[] mag) {
		double Ex = mag[0];
		double Ey = mag[1];
		double Ez = mag[2];
		double Ax = gravity_acc[0];
		double Ay = gravity_acc[1];
		double Az = gravity_acc[2];
		double Hx = Ey * Az - Ez * Ay;
		double Hy = Ez * Ax - Ex * Az;
		double Hz = Ex * Ay - Ey * Ax;
		double normH = Math.sqrt(Hx * Hx + Hy * Hy + Hz * Hz);
		double invH = 1.0 / normH;
		Hx = Hx * invH;
		Hy = Hy * invH;
		Hz = Hz * invH;
		double invA = 1.0 / Math.sqrt(Ax * Ax + Ay * Ay + Az * Az);
		Ax = Ax * invA;
		Ay = Ay * invA;
		Az = Az * invA;
		double Mx = Ay * Hz - Az * Hy;
		double My = Az * Hx - Ax * Hz;
		double Mz = Ax * Hy - Ay * Hx;
		return new double[][] { 
			{ Hx, Mx, Ax }, 
			{ Hy, My, Ay }, 
			{ Hz, Mz, Az } };
	}

	public double[][] computeGlobalByMag() {
		double[][] gacc = getGravityAccs();
		double[][] mag = getMagnet();
		double[][] lacc = getLinearAccs();
		double[][] global_acc = new double[lacc.length][lacc[0].length];
		for (int i = 0; i < data.length; i++) {
			double[][] matrix_g2b = getRotationMatrixG2BByMag(gacc[i], mag[i]);
			global_acc[i] = RotationUtils.getGlobalData(lacc[i], MatrixUtils.T(matrix_g2b));
		}
		return global_acc;
	}

}
