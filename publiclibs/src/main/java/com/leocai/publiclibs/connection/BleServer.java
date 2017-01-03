package com.leocai.publiclibs.connection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import com.leocai.publiclibs.PublicConstants;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * Created by leocai on 16-1-15.
 * 蓝牙主机，用于发指令控制从机开始和结束
 */
public class BleServer extends Observable {

    List<OutputStream> outs = new ArrayList<>();
    List<BluetoothSocket> sockets = new ArrayList<>();
    private BluetoothServerSocket mmServerSocket;

    private String fileName;
    private volatile boolean stop;

    public BleServer() {
        BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
        try {
            mmServerSocket = mAdapter.listenUsingInsecureRfcommWithServiceRecord(PublicConstants.NAME_WEAR_DATA,
                    PublicConstants.WEAR_UUID_INSECURE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 监听并发送文件名给其它从计
     */
    public void listen() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!stop) {
                    try {
                        BluetoothSocket socket = mmServerSocket.accept();
                        sockets.add(socket);
                        OutputStream out = socket.getOutputStream();
                        outs.add(out);
                        setChanged();
                        notifyObservers(outs.size());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();


    }

    /**
     * 发送开始指令
     */
    public void sendStartCommands() {
        for (OutputStream out : outs) {
            try {
                out.write(new byte[]{1});
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送结束指令
     */
    public void sendStopCommands() {
        for (OutputStream out : outs) {
            try {
                out.write(new byte[]{2});
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void sendFileCommands(String fileName) {
        for(OutputStream out:outs){
            PrintWriter pw = new PrintWriter(out);
            pw.write(fileName + "\n");
            pw.flush();
        }
    }

    public void close() {
        stop = true;
        try {
            for(BluetoothSocket socket:sockets){
                socket.close();
            }
            mmServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
