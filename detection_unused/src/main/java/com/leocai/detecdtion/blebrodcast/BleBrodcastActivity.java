package com.leocai.detecdtion.blebrodcast;

import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.leocai.detecdtion.AuthenticationState;
import com.leocai.detecdtion.DataTransformation;
import com.leocai.detecdtion.ParameterLearning;
import com.leocai.detecdtion.R;
import com.leocai.detecdtion.Reconcilation;

import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

public class BleBrodcastActivity extends AppCompatActivity implements View.OnClickListener, Observer {

    private TextView tvInfo;
    private BluetoothLeAdvertiser advertiser;
    private AdvertiseSettings settings;
    private AdvertiseData data;
    private AdvertiseCallback advertisingCallback;
    private BleAdvertise bleAdvertise;
    private BleDiscover bleDiscover;

    private AuthenticationState authenticationState;

    private boolean slave;
    private boolean reconcilationReady;
    private Reconcilation reconcilation;
    private ParameterLearning parameterLearning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_brodcast);
        findViewById(R.id.btn_adv).setOnClickListener(this);
        findViewById(R.id.btn_discover).setOnClickListener(this);
        tvInfo = (TextView) findViewById(R.id.tv_Info);
//        if( !BluetoothAdapter.getDefaultAdapter().isMultipleAdvertisementSupported() ) {
//            Toast.makeText(this, "Multiple advertisement not supported", Toast.LENGTH_SHORT).show();
//        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bleAdvertise != null)
            bleAdvertise.stop();
        if (bleDiscover != null)
            bleDiscover.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bleAdvertise != null)
            bleAdvertise.stop();
        if (bleDiscover != null)
            bleDiscover.stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ble_brodcast, menu);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_discover:
                setSlave(false);
                authenticationState = new AuthenticationState();
                parameterLearning = new ParameterLearning();
                bleDiscover = BleDiscover.getInstance(this);
                bleDiscover.addObserver(this);
                bleDiscover.discover();
                break;
            case R.id.btn_adv:
                setSlave(true);
                bleAdvertise = BleAdvertise.getInstance(this);
                bleAdvertise.advertise();
                tvInfo.setText("advertising");
                break;
        }
    }


    private void discover() {
        tvInfo.setText("discovering");
    }

    @Override
    public void update(Observable observable, Object data) {
//        String advData = bleDiscover.getAdvData();
//        tvInfo.setText(advData);

        if (!isSlave()) {
            masterMsgProccess((byte[]) data);
        } else {
            slaveMsgProccess((byte[]) data);
        }
    }

    private void slaveMsgProccess(byte[] data) {
        switch (authenticationState.getAuState()) {
            case WAITE_PARAMETER:
                MixedSensorData selfMixedSensorData = new MixedSensorData(null, null);
                ShakeParameters shakeParameters = ShakeParameters.parse(data);
                DataTransformation dataTransformation = new DataTransformation(selfMixedSensorData, shakeParameters);
                MixedSensorData mixedSensorData = dataTransformation.getTransformedData();
                byte[] bytes = CrossLevelUtils.crossLevel(mixedSensorData);
                checkRenconcilationReady();
                authenticationState.setAuState(AuthenticationState.AuState.WAITE_FOR_RECONCIATION);
                break;
            case WAITE_FOR_RECONCIATION:
                if (reconcilationReady) {
                    reconcilation = new Reconcilation(bleDiscover, bleAdvertise, data);
                    reconcilation.startReconcilation();
                    authenticationState.setAuState(AuthenticationState.AuState.RECONCILATION);
                } else {
                    checkRenconcilationReady();
                }
                break;
            case RECONCILATION:
                reconcilation.reconcilation(data);
                break;
        }
    }

    private void checkRenconcilationReady() {

    }

    private void masterMsgProccess(byte[] data) {
        switch (authenticationState.getAuState()) {
            case TRAIN:
                if (!parameterLearning.addData(data)) {
                    tvInfo.setText(parameterLearning.getDataInfo());
                    parameterLearning.trainParameter();
                    ShakeParameters shakeParameters = parameterLearning.getParameters();
                    if (shakeParameters == null) break;
//                    tvInfo.setText(Arrays.toString(bleDiscover.getLinearAccData()));
                    authenticationState.setAuState(AuthenticationState.AuState.RETURN_PARAMETER);
                    bleAdvertise = BleAdvertise.getInstance(this);
                    bleAdvertise.advertiseData(shakeParameters.getBytes());
                    authenticationState.setAuState(AuthenticationState.AuState.TRANSFORMATION);
                    MixedSensorData selfMixedSensorData = new MixedSensorData(null, null);
                    DataTransformation dataTransformation = new DataTransformation(selfMixedSensorData, shakeParameters);
                    MixedSensorData mixedSensorData = dataTransformation.getTransformedData();
                    byte[] bytes = CrossLevelUtils.crossLevel(mixedSensorData);
                    reconcilation = new Reconcilation(bleDiscover, bleAdvertise, bytes);
                }
                break;
            case WAITE_FOR_RECONCIATION:
                if (reconcilationReady) {
                    reconcilation.ready();
                } else {
                    checkRenconcilationReady();
                }
                break;
            case RECONCILATION:
                reconcilation.reconcilationData();
                break;
        }
    }

    public void setSlave(boolean slave) {
        this.slave = slave;
    }

    public boolean isSlave() {
        return slave;
    }
}
