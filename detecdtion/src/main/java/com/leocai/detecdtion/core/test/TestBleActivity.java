package com.leocai.detecdtion.core.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.leocai.detecdtion.R;
import com.leocai.detecdtion.core.ShakingDataGetterCallback;
import com.leocai.detecdtion.core.WearDataGetter;
import com.leocai.publiclibs.BleConnection;
import com.leocai.publiclibs.ConnectedCallBack;
import com.leocai.publiclibs.PublicConstants;
import com.leocai.publiclibs.ShakingData;

import java.io.InputStream;
import java.util.List;

public class TestBleActivity extends AppCompatActivity {


    BleConnection bleConnection = new BleConnection();

    WearDataGetter wearDataGetter = new WearDataGetter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_ble);
        findViewById(R.id.btn_server).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wearDataGetter.startListen(new ShakingDataGetterCallback() {
                    @Override
                    public void onGetShakingDatas(List<ShakingData> shakingDatas) {

                    }
                });
            }
        });

        findViewById(R.id.btn_client).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bleConnection.connect(PublicConstants.addressSum, new ConnectedCallBack() {
                    @Override
                    public void onConnected(InputStream in) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(TestBleActivity.this, "Connected", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                });
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test_ble, menu);
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
}
