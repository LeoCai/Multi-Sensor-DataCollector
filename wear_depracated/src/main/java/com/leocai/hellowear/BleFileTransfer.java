package com.leocai.hellowear;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by leocai on 15-11-18.
 */
public class BleFileTransfer {

    private BluetoothChatService mChatService;
    private Context context;
    private int size = 0;

    public void transferFile(Context context, BluetoothChatService mChatService, String fileName) {
        this.mChatService = mChatService;
        this.context = context;
        File file = new File(Environment.getExternalStorageDirectory(),fileName);
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String msg;
            while((msg = bufferedReader.readLine())!=null){
                sendMsg(msg);
                size++;
                Thread.sleep(50);
//                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    public void sendMsg(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(context, "Not Connected!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

        }
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
