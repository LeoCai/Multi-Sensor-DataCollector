package com.leocai.multisensorcollector.serverdemo;

import java.io.IOException;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

import com.dislab.leocai.spacesync.connection.DataServerMultiClient;
import com.dislab.leocai.spacesync.connection.OnConnectedListener;
import com.dislab.leocai.spacesync.core.MultiClientDataBuffer;
import com.dislab.leocai.spacesync.core.model.SensorDataSequnce;
import com.dislab.leocai.spacesync.core.model.SingleSensorData;
import com.dislab.leocai.spacesync.utils.SpaceSyncConfig;

public class SensorCollectorServerDemo implements Observer {
	
	/**
	 * 服务端
	 */
	private DataServerMultiClient dataServerMultiClient;
	
	/**
	 * 缓冲区
	 */
	private MultiClientDataBuffer buffer ;


	/**
	 * 监听客户端
	 * @throws IOException
	 */
	public SensorCollectorServerDemo() throws IOException{
		dataServerMultiClient = new DataServerMultiClient();
		log("Server Address: " + dataServerMultiClient.getAddress());
		dataServerMultiClient.setOnConnectionListener(new OnConnectedListener() {
			@Override
			public void newClientConnected(String hostAddress) {
				log(hostAddress + "connected");
			}
		});
		dataServerMultiClient.startServer();
		log("Wait for client  connecting..");
		log("After Connected, Press Enter to show data!");
	}

	public static void main(String[] args) throws IOException {
		SensorCollectorServerDemo sensorCollectorServerDemo = new SensorCollectorServerDemo();
		Scanner scanner = new Scanner(System.in);
		scanner.nextLine();
		sensorCollectorServerDemo.readyForReceive();
	}

	private static void log(String str) {
		System.out.println(str);
	}
	
	/**
	 * 注册数据监听器
	 * 准备接收数据
	 */
	private void readyForReceive() {
		int clientsNum = dataServerMultiClient.getClientsNum();
		buffer = new MultiClientDataBuffer(10, clientsNum);
		dataServerMultiClient.addDataListener(this);
		log("Ready to receive data!");
		try {
			dataServerMultiClient.receivedData();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	

	/*
	 * 接收数据
	 * 第一维代表客户端的索引
	 * 第二维代表一次采样的所有数据
	 * 
	 */
	@Override
	public void update(Observable o, Object arg) {
		double[][] data_multiClient = (double[][]) arg;
		buffer.add(data_multiClient);
		double[] clientFirstData = buffer.getClientFirstData(0);
		SingleSensorData sensorData = new SingleSensorData(clientFirstData);//封装数据
		System.out.println(Arrays.toString(sensorData.getAcc()));//输出线性加速度
	}

}
