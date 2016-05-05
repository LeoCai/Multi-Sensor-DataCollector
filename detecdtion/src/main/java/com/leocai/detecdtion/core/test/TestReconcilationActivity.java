package com.leocai.detecdtion.core.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.leocai.detecdtion.R;
import com.leocai.detecdtion.core.AccecptedCallBack;
import com.leocai.detecdtion.core.ConnectedCallBack;
import com.leocai.detecdtion.core.Master;
import com.leocai.detecdtion.core.ReconcilationEndCallBack;
import com.leocai.detecdtion.core.ShakeBits;
import com.leocai.detecdtion.core.Slave;
import com.leocai.publiclibs.PublicConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class TestReconcilationActivity extends AppCompatActivity implements Observer {
    private TextView tvLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_core_main);
        findViewById(R.id.btn_slave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Slave slave = new Slave(PublicConstants.MASTER_ADDRESS);
                slave.addObserver(TestReconcilationActivity.this);
                slave.startConnect(new ConnectedCallBack() {
                    @Override
                    public void onConnected() {
                        List<Byte> bits = new ArrayList<Byte>();
                        byte[] bitsT = new byte[]{0,1,1,1,0,0,1,0};
                        for(byte b :bitsT) bits.add(b);
                        slave.startReconcilation(new ShakeBits(bits), new ReconcilationEndCallBack() {
                            @Override
                            public void onReconcilationEnd(final List<Byte> bitsList, double mismatchRate) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tvLog.setText("");
                                        for(byte b:bitsList){
                                            tvLog.append(""+b);
                                        }
                                    }
                                });

                            }
                        });
                    }
                });
            }
        });
        findViewById(R.id.btn_master).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Master master = new Master();
                master.addObserver(TestReconcilationActivity.this);
                master.startListen(new AccecptedCallBack() {
                    @Override
                    public void onAccepted() {
                        List<Byte> bits = new ArrayList<>();
                        byte[] bitsT = new byte[]{0,1,0,1,0,1,1,0};
                        for(byte b :bitsT) bits.add(b);
                        master.startReconcilation(new ShakeBits(bits), new ReconcilationEndCallBack() {
                            @Override
                            public void onReconcilationEnd(final List<Byte> bitsList, double mismatchRate) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tvLog.setText("");
                                        for (byte b : bitsList) {
                                            tvLog.append(""+b);
                                        }
                                    }
                                });

                            }
                        });
                    }
                });


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
