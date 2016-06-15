package com.leocai.publiclibs.multidecicealign;

import com.leocai.publiclibs.BleConnection;
import com.leocai.publiclibs.ConnectedCallBack;
import com.leocai.publiclibs.PublicConstants;
import com.leocai.publiclibs.multidecicealign.StartCallBack;
import com.leocai.publiclibs.multidecicealign.StopCallBack;

import java.io.IOException;
import java.io.InputStream;

/**
 * 从设备客户端
 * Created by leocai on 16-1-15.
 */
public class BleClient {

    /**
     * 此处可能有问题
     * 链接主机的地址
     */
    private static final String ADDRESS = PublicConstants.MASTER_ADDRESS;
    InputStream in;

    BleConnection bleConnection = new BleConnection();

    /**
     * 用蓝牙连结到主机
     * @param connectedCallBack
     * 连接成功后的回调函数
     * @param startCallBack
     * 按下开始的回调函数
     * @param stopCallBack
     * 按下停止的回调函数
     */
    public void connect(final ConnectedCallBack connectedCallBack,final StartCallBack startCallBack, final StopCallBack stopCallBack){
        bleConnection.connect(ADDRESS, new ConnectedCallBack() {
            @Override
            public void onConnected(InputStream in ) {
                connectedCallBack.onConnected(in);
                BleClient.this.in = in;
                waitForCommand(startCallBack,stopCallBack);
            }
        });

    }

    /**
     * 调用开始，开始进程完成后调用结束事件
     * @param startCallBack
     * 开始回调函数
     * @param stopCallBack
     * 结束回调函数
     */
    public void waitForCommand(StartCallBack startCallBack, final StopCallBack stopCallBack){
        byte[] buffer = new byte[1];
        try {
            in.read(buffer);
            if(buffer[0]==1) startCallBack.onStart();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    byte[] b2 = new byte[1];
                    try {
                        in.read(b2);
                        if(b2[0] == 2) stopCallBack.onStop();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
