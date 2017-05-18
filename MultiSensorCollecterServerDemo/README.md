# 多传感器收集程序服务端

手机端安装：[Multi-Sensor-DataCollector](https://github.com/LeoCai/Multi-Sensor-DataCollector)中的Multi-Sensor-DataCollector
安卓子项目

<strong>writecsv必须unchecked(默认是checked)<strong>

## 简介
1. 手机控制端用蓝牙监听
2. pc服务端用socket开始监听
3. 手机采集端用蓝牙连接手机控制端（输入控制端蓝牙地址）
4. 手机控制端输入pc端ip地址，控制手机采集端socket连接pc服务端
5. pc服务端注册数据监听
6. 手机控制端控制手机采集端进行数据采集
7. 手机采集端持续收集传感器数据，每次采样以逗号分割
8. 通过socket发送采样到服务端
9. 服务端数据监听器接收并解析数据

## 依赖项目（目前jar包依赖，建议替换成项目依赖）
[iSpaceSync](https://github.com/LeoCai/iSpaceSync)


## demo代码预览

```java
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

```
