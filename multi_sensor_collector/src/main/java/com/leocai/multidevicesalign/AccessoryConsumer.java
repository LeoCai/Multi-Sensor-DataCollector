package com.leocai.multidevicesalign;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;

public class AccessoryConsumer {
	boolean mIsBound = false;
	private Context mActivity;
	private ConsumerService mConsumerService = null;
	private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mConsumerService = ((ConsumerService.LocalBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            mConsumerService = null;
            mIsBound = false;
        }
    };
	public AccessoryConsumer(Context mActivity){
		this.mActivity = mActivity;
	}
    public boolean initial(){
    	mIsBound = mActivity.bindService(new Intent(mActivity, ConsumerService.class), mConnection, Context.BIND_AUTO_CREATE);
    	return mIsBound;
    }
    public void connect(){
    	if (mIsBound == true && mConsumerService != null) {
            mConsumerService.findPeers();
        }
    }
    public void disconnext(){
    	if (mIsBound == true && mConsumerService != null) {
            if (mConsumerService.closeConnection() == false) {
                //mActivity.updateTextView("Disconnected");
                //mMessageAdapter.clear();
            }
        }
    }
    public void start(){
    	 if (mIsBound == true && mConsumerService != null) {
             if (mConsumerService.sendData("Hello Accessory!")) {
             } else {
             }
         }
    }
    public void stop(){
    	 if (mIsBound == true && mConsumerService != null) {
             if (mConsumerService.closeConnection() == false) {
                 //updateTextView("Disconnected");
                 //mMessageAdapter.clear();
             }
         }
         // Un-bind service
         if (mIsBound) {
             //unbindService(mConnection);
             mIsBound = false;
         }
    }
}