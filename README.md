Multi Sensor DataCollector
===

Build Trust via Physical Handshake
detection:buld trust via physical handshake

include project of space sync：

ps: you need to change  master ble address in public constants

publiclibs: public lib of constants, sensor data manager, ble connection class

multidevicealign: project of space sync

#项目简介
该项目用于多客户端传感器采集和发送，可用于科研采集数据，可利用wifi实时传输传感器数据

#项目配置
```gradle
compileSdkVersion 23
buildToolsVersion "23.0.1"
```
```gradle
compile project(':publiclibs')
compile 'com.android.support:appcompat-v7:23.1.1'
compile 'com.android.support:design:23.1.1'
compile files('libs/accessory-v2.3.0.jar')
compile files('libs/sdk-v1.0.0.jar')
compile files('libs/spacesync.jar')
```
#使用方式
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
