package com.leocai.detecdtion.core.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.leocai.detecdtion.R;
import com.leocai.detecdtion.core.Master;
import com.leocai.detecdtion.core.ShakeDatasStore;
import com.leocai.detecdtion.core.Slave;
import com.leocai.publiclibs.PublicConstants;
import com.leocai.publiclibs.ShakingData;

import java.util.List;
import java.util.Observable;
import java.util.Observer;


/**
 * off-line test data
 */
public class TestMsgTransferActivity extends AppCompatActivity implements Observer {
    private static final String TAG = "TestMsg";
    private TextView tvLog;

    private ShakeDatasStore shakeDatasStore = new ShakeDatasStore();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_core_main);
        findViewById(R.id.btn_slave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Slave keyExtractor = new Slave(PublicConstants.MASTER_ADDRESS);
                shakeDatasStore.setFileName(ShakeDatasStore.SLAVE_FILE);
                List<ShakingData> shakingDatas =shakeDatasStore.readFromFile(TestMsgTransferActivity.this);
                for(ShakingData shakingData:shakingDatas){
                    Log.d(TAG, shakingData.toString());
                }
                keyExtractor.addObserver(TestMsgTransferActivity.this);
//                List<ShakingData> shakingDatas = new ArrayList<ShakingData>();
//                for (int i = 0; i < 100; i++) {
//                    shakingDatas.add(new ShakingData());
//                }
                keyExtractor.onGetShakingDatas(shakingDatas);
            }
        });
        findViewById(R.id.btn_master).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Master keyExtractor = new Master();
                keyExtractor.addObserver(TestMsgTransferActivity.this);
                shakeDatasStore.setFileName(ShakeDatasStore.MASTER_FILE);
                List<ShakingData> shakingDatas =shakeDatasStore.readFromFile(TestMsgTransferActivity.this);
                for(ShakingData shakingData:shakingDatas){
                    Log.d(TAG, shakingData.toString());
                }
//                List<ShakingData> shakingDatas = new ArrayList<ShakingData>();
//                for (int i = 0; i < 100; i++) {
//                    shakingDatas.add(new ShakingData());
//                }
                keyExtractor.onGetShakingDatas(shakingDatas);

            }
        });
        tvLog = (TextView)findViewById(R.id.tv_log);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test_core, menu);
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
