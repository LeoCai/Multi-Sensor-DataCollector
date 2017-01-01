Multi Sensor DataCollector
===

Build Trust via Physical Handshake
detection:buld trust via physical handshake

include project of space sync：

ps: you need to change  master ble address in public constants

publiclibs: public lib of constants, sensor data manager, ble connection class

multidevicealign: project of space sync

# 项目简介
该项目用于多客户端传感器采集和发送，可用于科研采集数据，可利用wifi实时传输传感器数据；支持一个主节点进行蓝牙控制，多个从节点同时开始采集数据；支持利用sokect实时传输传感器数据到服务器

# 待解决问题
1. sockect模式下不能重复连接使用
2. sockect模式下传输数据丢包

# 采集的数据类型
* 加速度　Sensor.TYPE_ACCELEROMETE
* 陀螺仪　Sensor.TYPE_GYROSCOPE
* 磁力计　Sensor.TYPE_MAGNETIC_FIELD
* 重力　Sensor.TYPE_GRAVITY
* 线性加速度　Sensor.TYPE_LINEAR_ACCELERATION

修改位置：[MySensorManager.java]()

# 参数修改
频率周期修改: [CollectorConfig.java]()

# 项目配置
```gradle
compileSdkVersion 23
buildToolsVersion "23.0.1"
```

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
