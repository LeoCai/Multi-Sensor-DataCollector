package com.leocai.detecdtion.core;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.leocai.detecdtion.R;
import com.leocai.detecdtion.ShakeBufferView;
import com.leocai.publiclibs.PublicConstants;
import com.leocai.publiclibs.ShakingData;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class WearDataGetterActivity extends AppCompatActivity implements Observer {

    private TextView tvLog;

    ShakeBufferView shakeBufferView;

    WearDataGetter wearDataGetter = new WearDataGetter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_core_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

//        ShakeBufferView shakeBufferView = (ShakeBufferView)findViewById(R.id.buffer_view);
        shakeBufferView = (ShakeBufferView) findViewById(R.id.shakebufferview);

        wearDataGetter.addObserver(shakeBufferView);
        findViewById(R.id.btn_slave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Slave slave = new Slave(PublicConstants.MASTER_ADDRESS);
                slave.addObserver(WearDataGetterActivity.this);
                tvLog.setText("I AM SLAVE\n\nWaiting ShakingDatas From Wear!");

                wearDataGetter.startListen(new ShakingDataGetterCallback() {
                    @Override
                    public void onGetShakingDatas(List<ShakingData> shakingDatas) {
//                        final StringBuilder logInfo = new StringBuilder();
//                        for(ShakingData shakingData:shakingDatas){
//                            logInfo.append(shakingData.toString());
//                            logInfo.append("\n");
//                        }
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                tvLog.setText(logInfo.toString());
//                            }
//                        });

                        slave.onGetShakingDatas(shakingDatas);
                    }
                });
            }
        });
        findViewById(R.id.btn_master).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Master master = new Master();
                master.addObserver(WearDataGetterActivity.this);
//                wearDataGetter = new WearDataGetter();
                tvLog.setText("I AM MASTER\n\nWaiting ShakingDatas From Wear!");

                wearDataGetter.startListen(new ShakingDataGetterCallback() {
                    @Override
                    public void onGetShakingDatas(List<ShakingData> shakingDatas) {
//                        StringBuilder logInfo = new StringBuilder();
//                        for(ShakingData shakingData:shakingDatas){
//                            logInfo.append(shakingData.toString());
//                            logInfo.append("\n");
//                        }
//                        tvLog.setText(logInfo);
                        master.onGetShakingDatas(shakingDatas);
                    }
                });
            }
        });

        tvLog = (TextView) findViewById(R.id.tv_log);

//        slave = new MySensorListener(shakeBufferView);
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_core_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void update(Observable observable, final Object data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvLog.setText((CharSequence) data);
            }
        });
    }
}
