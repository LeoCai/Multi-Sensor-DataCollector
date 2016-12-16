package com.leocai.publiclibs.utils;

import java.awt.FlowLayout;

import javax.swing.JFrame;

import org.math.plot.Plot2DPanel;

public class PlotUtils {

	public static void plotData(double[][] multi_data) {
		Plot2DPanel plot = new Plot2DPanel();
		for (int i = 0; i < multi_data[0].length; i++) {
			plot.addLinePlot("acc:" + i, MatrixUtils.getColumn(multi_data, i));
		}
		JFrame frame = new JFrame("a plot panel");
		frame.setSize(1024, 768);
		frame.setContentPane(plot);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void plotCompareData(double[][] data1, double[][] data2) {
		for (int i = 0; i < data1[0].length; i++) {
			JFrame frame = new JFrame("compare" + i);
			frame.setSize(1024, 768);
			Plot2DPanel plot = new Plot2DPanel();
			plot.addLinePlot("data1:" + i, MatrixUtils.getColumn(data1, i));
			plot.addLinePlot("data2:" + i, MatrixUtils.getColumn(data2, i));
			frame.add(plot);
			frame.setVisible(true);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}

	}

	public static void plotData(double[] data) {
		Plot2DPanel plot = new Plot2DPanel();
		plot.addLinePlot("acc:", data);
		JFrame frame = new JFrame("a plot panel");
		frame.setSize(1024, 768);
		frame.setContentPane(plot);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void plotCompareData(double[] lacc_g, double[] gacc_g) {
		JFrame frame = new JFrame("compare");
		frame.setSize(1024, 768);
		Plot2DPanel plot = new Plot2DPanel();
		plot.addLinePlot("data1:", lacc_g);
		plot.addLinePlot("data2:", gacc_g);
		frame.add(plot);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
