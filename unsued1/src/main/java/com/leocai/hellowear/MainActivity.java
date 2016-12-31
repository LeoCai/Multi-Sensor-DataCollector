package com.leocai.hellowear;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends AppCompatActivity implements
        DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String COUNT_KEY = "com.example.key.count";
    private GoogleApiClient mGoogleApiClient;
    private int count = 0;

    private TextView tvLog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        tvLog = (TextView) findViewById(R.id.tv_log);

    }

    @Override
    protected void onStart(){
        super.onStart();
        int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(result  ==  0){
            tvLog.setText("Avaliable");
            mGoogleApiClient.connect();
        }else{
            tvLog.setText("Not Avaliable");
        }


//        if(ConnectionResult.SUCCESS == result){
//            mGoogleApiClient.connect();
//            tvLog.setText("Connecting to Wear...");
//        }
//        else {
//            // Show appropriate dialog
//            Dialog d = GooglePlayServicesUtil.getErrorDialog(result, this, 0);
//            d.show();
//        }
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                mGoogleApiClient.blockingConnect();
//            }
//        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                mGoogleApiClient.blockingConnect();
//            }
//        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    public void onConnected(Bundle bundle) {
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        Wearable.DataApi.removeListener(mGoogleApiClient, this);
//        mGoogleApiClient.disconnect();
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo("/count") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    updateCount(dataMap.getInt(COUNT_KEY));
                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
        }
    }

    private void updateCount(int anInt) {
        tvLog.setText(""+anInt);
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        tvLog.setText(""+connectionResult);
    }
}
