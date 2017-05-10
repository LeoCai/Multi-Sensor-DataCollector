# 多传感器收集程序服务端

手机端安装：[Multi-Sensor-DataCollector](https://github.com/LeoCai/Multi-Sensor-DataCollector)中的Multi-Sensor-DataCollector
安卓子项目

<strong>writecsv必须unchecked(默认是checked)<strong>

## 简介
客户端用socket连接pc服务端
服务端注册数据监听
安卓客户端持续收集传感器数据，每次采样以逗号分割
通过socket发送采样到服务端
服务端数据监听器接收并解析数据

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
