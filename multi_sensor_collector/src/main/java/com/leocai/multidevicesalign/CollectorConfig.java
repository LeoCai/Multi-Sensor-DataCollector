package com.leocai.multidevicesalign;

import java.util.UUID;

/**
 * 独立项目的公有变量
 * Created by leocai on 16-1-5.
 */
public class CollectorConfig {
    /**
     * 传感器周期 ms单位
     */
    public static final long SENSOPR_PERIOD = 20;
    public static final String MASTER_ADDRESS = "E8:B4:C8:8A:01:C7";
    public static final int SHAKING_DATA_SIZE = 50;
    public static final UUID WEAR_UUID_INSECURE = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a96");
    public static final String NAME_WEAR_DATA = "BluetoothChatWear";
}
