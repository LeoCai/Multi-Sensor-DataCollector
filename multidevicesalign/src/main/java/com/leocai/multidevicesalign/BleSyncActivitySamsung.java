package com.leocai.multidevicesalign;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.leocai.publiclibs.ConnectedCallBack;
import com.leocai.publiclibs.multidecicealign.BleClient;
import com.leocai.publiclibs.multidecicealign.MySensorManager;
import com.leocai.publiclibs.multidecicealign.StartCallBack;
import com.leocai.publiclibs.multidecicealign.StopCallBack;

import java.io.InputStream;
import java.util.Observable;
import java.util.Observer;

public class BleSyncActivitySamsung extends AppCompatActivity implements Observer {

    private static final String TAG = "BleSyncActivity";
    TextView tv_log;

    BleServer bleServer;
    BleClient bleClient;

    private boolean start;

    Button btnMaster;
    Button btnClient;
    Button btnStart;

    EditText etFileName;

    AccessoryConsumer accessoryConsumer = new AccessoryConsumer(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_sync);
        tv_log = (TextView) findViewById(R.id.tv_log);
        Log.d(TAG,"onCreate");
        btnMaster = (Button)findViewById(R.id.btn_master);
        btnClient = (Button)findViewById(R.id.btn_client);
        btnStart = (Button) findViewById(R.id.btn_start);
        etFileName = (EditText)findViewById(R.id.et_filename);
        btnClient.setEnabled(true);
        accessoryConsumer.initial();
//        etFileName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if(hasFocus){
//                    Log.d(TAG, "Focus");
//                }else{
//                    Log.d(TAG,"UNFOUCUS");
//                    mySensorManager = new MySensorManager(BleSyncActivity.this);
//                    mySensorManager.setFileName(etFileName.getText().toString()+".txt");
//                    mySensorManager.startSensor();
//                    btnClient.setEnabled(true);
//                }
//
//            }
//        });

//        Toast.makeText(this, "onCreat", Toast.LENGTH_SHORT).show();

        findViewById(R.id.btn_master).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLog("Master");
                bleServer = new BleServer();
                bleServer.addObserver(BleSyncActivitySamsung.this);
                bleServer.listen();
                btnClient.setEnabled(false);
                btnMaster.setEnabled(false);
                etFileName.setEnabled(false);
            }
        });
        findViewById(R.id.btn_client).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLog("Client");
                btnMaster.setEnabled(false);
                btnStart.setEnabled(false);
                btnClient.setEnabled(false);

                bleClient = new BleClient();
                bleClient.connect(new ConnectedCallBack() {
                    @Override
                    public void onConnected(InputStream in) {
                        showLog("Connected");
                        accessoryConsumer.connect();
                    }
                }, new StartCallBack() {
                    @Override
                    public void onStart() {
                        showLog("STATING");
                        accessoryConsumer.start();
//                        mySensorManager.startDetection();
                    }
                }, new StopCallBack() {
                    @Override
                    public void onStop() {
                        showLog("STOPED");
                        accessoryConsumer.stop();
//                        mySensorManager.stop();
                    }
                });
            }
        });

        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!start) {
                    start = true;
                    bleServer.sendStartCommands();
                    ((Button) v).setText("Stop");
                } else {
                    start = false;
                    bleServer.sendStopCommands();
                    ((Button) v).setText("Start");
                }
            }
        });
    }

    private void showLog(final String info) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_log.setText(info);
                Log.d(TAG, info);
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (mySensorManager != null)
//            mySensorManager.stop();
        Log.d(TAG,"onStop");
//        Toast.makeText(this, "onStop", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
//        Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
//        Toast.makeText(this, "onPause", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
//        Toast.makeText(this, "onStart", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
//        Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ble_sync, menu);
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
                tv_log.setText((int)data+" clients connected");
            }
        });
    }
}
