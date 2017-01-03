Multi Sensor DataCollector
===

# 项目简介
* 用于多客户端传感器采集和发送;
* 可用于科研采集数据;
* 可利用wifi实时传输传感器数据；
* 支持一个主节点进行蓝牙控制，多个从节点同时开始采集数据；
* 支持利用sokect实时传输传感器数据到服务器

<img src="./imgs/s5.png" width=200 height=380 />
<img src="./imgs/s7.png" width=200 height=380 />
<img src="./imgs/s6.png" width=500 height=500 />



# 待解决问题
1. sockect模式下不能重复连接使用
2. sockect模式下传输数据丢包

# 采集的数据类型
* 加速度　Sensor.TYPE_ACCELEROMETE
* 陀螺仪　Sensor.TYPE_GYROSCOPE
* 磁力计　Sensor.TYPE_MAGNETIC_FIELD
* 重力　Sensor.TYPE_GRAVITY
* 线性加速度　Sensor.TYPE_LINEAR_ACCELERATION

修改位置：[MySensorManager.java](publiclibs/src/main/java/com/leocai/publiclibs/multidecicealign/MySensorManager.java)

# 参数修改
频率周期修改: [CollectorConfig.java](multi_sensor_collector/src/main/java/com/leocai/multidevicesalign/CollectorConfig.java)

# 项目配置
```gradle
compileSdkVersion 23
buildToolsVersion "23.0.1"
```
## 关联项目
* [sokect传输框架](https://github.com/LeoCai/iSpaceSync)：jar包依赖
* [sokect PC 服务端](https://github.com/LeoCai/SpaceSync-PC-Demo)：若为socket模式须安装PC或Android服务端
* [sokect android 服务端](https://github.com/LeoCai/SpaceSync-Android-Demo)：若为socket模式须安装PC或Android服务端

# 使用方式
1. 安装multi_sensor_collector module到各个手机上
2. 在控制主手机上点击MASTER
3. 在传感器手机熵点击CLIENT，从手机显示CONNECTED, 主手机显示1 CONNCECTED
4. 若为本地采集CSV模式：
  1. 输入文件名
  2. 按下主手机上的INIT FILE
  3. 从手机显示FILE　INITED
  4. 主手机按下START，从手机显示STARTING，开始采集数据到本地csv
5. 若为sockect传输模式
  1. 输入主机IP地址
  2. 按下助手及上的INIT FILE
  3. 从手机显示FILE INITED，服务端显示...CONNECTED
  4. 主手机按下START，服务端按下READY，开始传输数据
  
#主要函数介绍
## module 介绍
* multi_sensor_collector 项目界面模块，用于安装app，依赖publiclibs
* publiclibs 封装了采集数据和传输框架，依赖spacesync.jar

## publiclibs
### 传感器采集核心类：[MySensorManager.java](https://github.com/LeoCai/Multi-Sensor-DataCollector/blob/master/publiclibs/src/main/java/com/leocai/publiclibs/multidecicealign/MySensorManager.java)
```java
public class MySensorManager {
    private SensorManager mSensorManager;
    private Sensor mSensorAcc;
    private Sensor mSensorGYR;
    private Sensor mSensorMAG;
    private Sensor mSensorGravity;
    private Sensor mSensorLinear;
    /**
     * 用于写文件的类
     */
    SensorGlobalWriter sensorGlobalWriter;

    private int frequency;

    public MySensorManager(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensorAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorGYR = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorMAG = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mSensorLinear = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorGlobalWriter = new SensorGlobalWriter();
    }

    /**
     * 设置写本地文件还是socket通信
     * @param globalWriter
     * SensorSocketWriter 写socket
     * SensorGlobalWriter 写文件
     */
    public void setGlobalWriter(SensorGlobalWriter globalWriter){
        sensorGlobalWriter = globalWriter;
    }

    public void startSensor() {
        if(frequency==0) frequency = (int) (1000/ PublicConstants.SENSOPR_PERIOD);
        mSensorManager.registerListener(sensorGlobalWriter, mSensorAcc, (int) (1000 / frequency * 1000)); // 根据频率调整
        mSensorManager.registerListener(sensorGlobalWriter, mSensorGYR, (int) (1000 / frequency * 1000));
        mSensorManager.registerListener(sensorGlobalWriter, mSensorMAG, (int) (1000 / frequency * 1000));
        mSensorManager.registerListener(sensorGlobalWriter, mSensorGravity, (int) (1000 / frequency * 1000));
        mSensorManager.registerListener(sensorGlobalWriter, mSensorLinear, (int) (1000 / frequency * 1000));
    }

    public void startDetection() {
        sensorGlobalWriter.startDetection();
    }

    public void stop() {
        sensorGlobalWriter.close();
    }

    public void close(){
        mSensorManager.unregisterListener(sensorGlobalWriter, mSensorAcc);
        mSensorManager.unregisterListener(sensorGlobalWriter, mSensorGYR);
        mSensorManager.unregisterListener(sensorGlobalWriter, mSensorMAG);
        mSensorManager.unregisterListener(sensorGlobalWriter, mSensorGravity);
        mSensorManager.unregisterListener(sensorGlobalWriter, mSensorLinear);
        sensorGlobalWriter.close();
    }

    /**
     * 设置文件名或ip地址
     * @param fileName
     */
    public void setFileName(String fileName) throws IOException {
        sensorGlobalWriter.setFileName(fileName);
    }

    public void addObserver(Observer observer) {
        sensorGlobalWriter.addObserver(observer);
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
}
```


### 数据写文件类：[SensorGlobalWriter.java](https://github.com/LeoCai/Multi-Sensor-DataCollector/blob/master/publiclibs/src/main/java/com/leocai/publiclibs/multidecicealign/SensorGlobalWriter.java)
```java
/**
 * 监听传感器数据，用于将传感器数据写到文件中
 * 启动一个线程，一个传感器周期写一次数据
 *
 * Created by leocai on 15-12-21.
 */
public class SensorGlobalWriter extends Observable implements SensorEventListener {
    private static final String TAG = "SensorDataWriter";

    private long preTimestamp;

    private double[] gravity = new double[3];//TODO 初始值
    private double[] linear_acceleration = new double[3];

    protected ShakingData cuShakingData = new ShakingData();
    private volatile boolean stop;

    private OutputStream outputStream;
    private FileWriter fileWriter;

    public SensorGlobalWriter(String fileName) {
        cuShakingData.setLinearAccData(null);
        cuShakingData.setGyrData(null);
        cuShakingData.setDt(0);
        cuShakingData.setIndex(0);
        cuShakingData.setTimeStamp(0);
        try {
            fileWriter = new FileWriter(new File(Environment.getExternalStorageDirectory(), fileName));
            fileWriter.write(cuShakingData.getCSVHead());
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setFileName(String fileName) throws IOException {
        try {
            fileWriter = new FileWriter(new File(Environment.getExternalStorageDirectory(), fileName));
            fileWriter.write(cuShakingData.getCSVHead());
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SensorGlobalWriter() {
        cuShakingData.setLinearAccData(null);
        cuShakingData.setGyrData(null);
        cuShakingData.setDt(0);
        cuShakingData.setIndex(0);
        cuShakingData.setTimeStamp(0);
    }

    public void startDetection() {
        stop = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                while (!stop) {
                    if (cuShakingData.getLinearAccData() == null) continue;
//                    Log.d(TAG,"dection");
                    cuShakingData.transform();
                    notifyObservers(cuShakingData);
                    setChanged();
                    try {
                        Thread.sleep(PublicConstants.SENSOPR_PERIOD);
                        String info = cuShakingData.getCSV();
//                        Log.d(TAG, info);
                        fileWriter.write(info);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void close() {
        try {
            stop = true;
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            cuShakingData.setAccData(new double[]{event.values[0],event.values[1],event.values[2]});
            cuShakingData.setTimeStamp(new Date().getTime());
            if (preTimestamp != 0)
                cuShakingData.setDt(1.0 * (event.timestamp - preTimestamp) / 1000000000);
            preTimestamp = event.timestamp;
//            Log.d(TAG, "" + event.timestamp);
        } else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            cuShakingData.setGyrData(new double[]{event.values[0], event.values[1], event.values[2]});
//            Log.d(TAG, "" + event.timestamp);
        } else if(sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            cuShakingData.setMagnetData(new double[]{event.values[0], event.values[1], event.values[2]});
        } else if(sensor.getType() == Sensor.TYPE_GRAVITY){
            cuShakingData.setGravityAccData(new double[]{event.values[0], event.values[1], event.values[2]});
        } else if(sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            cuShakingData.setLinearAccData(new double[]{event.values[0], event.values[1], event.values[2]});
        }
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }


}
```


### 数据写socket类：[SensorSokectWriter.java](https://github.com/LeoCai/Multi-Sensor-DataCollector/blob/master/publiclibs/src/main/java/com/leocai/publiclibs/multidecicealign/SensorSokectWriter.java)
```java
/**
 * 监听传感器数据，用于将传感器数据写到socket中
 *
 * Created by leocai on 15-12-21.
 */
public class SensorSokectWriter extends SensorGlobalWriter{
    private static final String TAG = "SensorDataWriter";
    private DataClientImpl dataClient;
    private volatile boolean stop;

    @Override
    public void setFileName(String address) throws IOException {
        dataClient.connect(address,10007);
    }

    public SensorSokectWriter() {
        dataClient = new DataClientImpl();
        cuShakingData.setLinearAccData(null);
        cuShakingData.setGyrData(null);
        cuShakingData.setDt(0);
        cuShakingData.setIndex(0);
        cuShakingData.setTimeStamp(0);
    }

    @Override
    public void startDetection() {
        stop = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!stop) {
                    if (cuShakingData.getLinearAccData() == null) continue;
                    cuShakingData.transform();
                    notifyObservers(cuShakingData);
                    setChanged();
                    try {
                        Thread.sleep(PublicConstants.SENSOPR_PERIOD);
                        String info = cuShakingData.getCSV();
                        Log.d(TAG, info);
                        dataClient.sendSample(info);
                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void close() {
        stop = true;
        try {
            if(dataClient!=null)
            dataClient.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
```
