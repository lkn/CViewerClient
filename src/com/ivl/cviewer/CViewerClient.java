package com.ivl.cviewer;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

public class CViewerClient extends Activity implements TCPListener {
	private static String TAG = "CViewerClient";
	private static int PORT = 1111;
	private static String HOST = "pumice.ucsd.edu";//"137.110.119.228"; // "192.168.1.122";//"10.0.2.2"; 
	
	private Preview preview_;
	private Socket server_;
	private Infodialog info_;
	private TextView errorView_;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // application specific settings
        setFullScreen();
        disableScreenTurnOff();

        errorView_ = new TextView(this);
        errorView_.setTextColor(Color.RED);
        
        // check WIFI 
        // TODO: extend for any network connection
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (!mWifi.isConnected()) {
            errorView_.append("NO WIFI!!!\n");
        }
        
        try {
			server_ = new Socket(HOST, PORT);
			server_.setTcpNoDelay(true);

			// start thread to listen to server
			Thread t = new Thread(new TCPListenHandler(this, this, server_));
			t.start();
		} catch (UnknownHostException e) {
			Log.e(TAG, "Couldn't connect to (no host)" + HOST);
			errorView_.append("Couldn't connect to (no host) " + HOST + "\n");
		} catch (IOException e) {
			Log.e(TAG, "Couldn't connect to (io) " + HOST);
			errorView_.append("Couldn't connect (io) to " + HOST + "\n");
		}
        
        FrameLayout frame = new FrameLayout(this);
        preview_ = new Preview(this, server_);
        frame.addView(preview_);
        
        info_ = new Infodialog(this);
//        info_.setText("TESTING!!!");
        
        frame.addView(errorView_);
        
        setContentView(frame);
    }

    @Override
    protected void onStop() {
    	super.onStop();
    	try {
    		if (server_ != null) {
    			server_.close();
    		}
		} catch (IOException e) {
			Log.e(TAG, "Error on server closing");
			e.printStackTrace();
		}
    }
    
	private void setFullScreen() {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
    
	public void disableScreenTurnOff() {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
							 WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
	
    public void callCompleted(String info) {
    	Log.d(TAG, "received: " + info);
    	if (info != null) {
    		info_.setText(info);
    	} 
    }
}