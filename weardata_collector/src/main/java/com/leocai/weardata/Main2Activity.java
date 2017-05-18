package com.leocai.weardata;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.leocai.publiclibs.ConnectedCallBack;
import com.leocai.publiclibs.multidecicealign.BleClient;
import com.leocai.publiclibs.multidecicealign.FileInitCallBack;
import com.leocai.publiclibs.multidecicealign.MySensorManager;
import com.leocai.publiclibs.multidecicealign.StartCallBack;
import com.leocai.publiclibs.multidecicealign.StopCallBack;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 握手认证系统数据采集，依赖publiclibs项目，MOTO 360
 */
public class Main2Activity extends Activity {

    private static final String TAG = "Main2Activity";
    private TextView tvInfo;
    private EditText etFileName;
    private Button btnConnect;

    private BleClient bleClient;
    MySensorManager mySensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                tvInfo = (TextView) stub.findViewById(R.id.tv_info);
                etFileName = (EditText) stub.findViewById(R.id.et_filename);
                btnConnect = (Button) stub.findViewById(R.id.btn_connect);
                btnConnect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mySensorManager = new MySensorManager(Main2Activity.this);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH_mm_ss");
                        String fileName = simpleDateFormat.format(new Date())+".csv";
                        try {
                            mySensorManager.setFileName(fileName);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mySensorManager.startSensor();
                        bleClient = new BleClient("");
                        bleClient.connect(new ConnectedCallBack() {
                            @Override
                            public void onConnected(InputStream out) {
                                showLog("Connected");

                            }

                        }, new FileInitCallBack() {
                            @Override
                            public void onFileReceived(InputStream in) {
                            }
                        }, new StartCallBack() {
                            @Override
                            public void onStart() {
                                showLog("STATING");
                                mySensorManager.startDetection();
                            }
                        }, new StopCallBack() {
                            @Override
                            public void onStop() {
                                showLog("STOPED");
                                mySensorManager.stop();
                            }
                        });

                    }
                });
            }
        });
    }

    private void showLog(final String info) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvInfo.setText(info);
                Log.d(TAG, info);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mySensorManager.stop();
    }
}
