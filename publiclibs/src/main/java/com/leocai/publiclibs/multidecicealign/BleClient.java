package com.leocai.publiclibs.multidecicealign;

import com.leocai.publiclibs.BleConnection;
import com.leocai.publiclibs.ConnectedCallBack;
import com.leocai.publiclibs.PublicConstants;
import com.leocai.publiclibs.multidecicealign.StartCallBack;
import com.leocai.publiclibs.multidecicealign.StopCallBack;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by leocai on 16-1-15.
 */
public class BleClient {

    private static final String ADDRESS = PublicConstants.addressSum;
    InputStream in;

    BleConnection bleConnection = new BleConnection();

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
