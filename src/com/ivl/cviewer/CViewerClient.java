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
	private static String HOST = "192.168.1.104";//"pumice.ucsd.edu";
	
	private Preview preview_;
	private InfoView infoView_;

	private Socket server_;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // application specific settings
        setFullScreen();
        disableScreenTurnOff();

        infoView_ = new InfoView(this);
        
        // check WIFI 
        // TODO: extend for any network connection
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (!mWifi.isConnected()) {
            infoView_.appendErrorText("NO WIFI!!!\n");
        }
        
        try {
			server_ = new Socket(HOST, PORT);
			server_.setTcpNoDelay(true);

			// start thread to listen to server
			Thread t = new Thread(new TCPListenHandler(this, this, server_));
			t.start();
		} catch (UnknownHostException e) {
			Log.e(TAG, "Couldn't connect to (no host)" + HOST);
			infoView_.appendErrorText("Couldn't connect to (no host) " + HOST + "\n");
		} catch (IOException e) {
			Log.e(TAG, "Couldn't connect to (io) " + HOST);
			infoView_.appendErrorText("Couldn't connect (io) to " + HOST + "\n");
		}
        
        FrameLayout frame = new FrameLayout(this);
        preview_ = new Preview(this, server_);
        
        frame.addView(preview_);
        frame.addView(infoView_);
        
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
    		String[] arr = info.split("#");
    		StringBuffer buffer = new StringBuffer();
    		for (int i = 0; i < arr.length; ++i) {
    			switch (i) {
    			case 0:  // image name
    				buffer.append("Name: ").append(arr[i]).append("\n");
    				break;
    			case 1:  // id
    				// skip
    				break;
    			case 2:  // camera make
    				buffer.append("Camera Make: ").append(arr[i]).append("\n");
    				break;
    			case 3: // camera model
    				buffer.append("Camera Model: ").append(arr[i]).append("\n");
    				break;
    			case 4:  // date time
    				buffer.append("Date Taken: ").append(arr[i]).append("\n");
    				break;
    			case 5:  // shutter speed
    				buffer.append("Shutter Speed: ").append(arr[i]).append("\n");
    				break;
    			case 6:  // focal length
    				buffer.append("Focal Length: ").append(arr[i]).append("\n");
    				break;
    			}
    		}
    		infoView_.setInfoText(buffer.toString());
    	} 
    }
}