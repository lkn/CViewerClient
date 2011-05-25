package com.ivl.cviewer;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.ivl.cviewerclient.R;
import com.ivl.network.DetailsMap;
import com.ivl.network.ServerConnection;
import com.ivl.network.TCPListenHandler;
import com.ivl.network.TCPListener;

public class CViewerClient extends Activity implements TCPListener, OnClickListener {
	private static String TAG = "CViewerClient";

	private static int INVALID = -1;
	
	private Preview preview_;
	private InfoView infoView_;

	private ServerConnection serverConnection_;
	private int matchId_;  // id of the matched image
	private boolean sendPreviewFrames_;
	
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
			serverConnection_ = new ServerConnection(this);

			// start thread to listen to server
			Thread t = new Thread(new TCPListenHandler(this, this, serverConnection_.getSocket()));
			t.start();
		} catch (UnknownHostException e) {
			Log.e(TAG, "Couldn't connect to (no host)" + ServerConnection.HOST);
			infoView_.appendErrorText("Couldn't connect to (no host) " + ServerConnection.HOST + "\n");
		} catch (IOException e) {
			Log.e(TAG, "Couldn't connect to (io) " + ServerConnection.HOST );
			infoView_.appendErrorText("Couldn't connect (io) to " + ServerConnection.HOST  + "\n");
		}
        
        FrameLayout frame = new FrameLayout(this);
        preview_ = new Preview(this, serverConnection_);
        preview_.setOnClickListener(this);
        
        frame.addView(preview_);
        frame.addView(infoView_);
        
        setContentView(frame);
        
        matchId_ = INVALID;
        sendPreviewFrames_ = true;
        preview_.sendData();
    }

    @Override
    protected void onStop() {
    	super.onStop();
    	if (serverConnection_ != null) {
    		serverConnection_.close();
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	Toast.makeText(getApplicationContext(), "menu", Toast.LENGTH_SHORT).show();
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.layout.menu, menu);
    	return true;
    } 
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
        	case R.id.description:
                break;
        	case R.id.map:
                break;
        	case R.id.comment:
        		break;
        	case R.id.view_comments:
        		break;
        }
        return false;
    } 

	@Override
	public void onClick(View view) {
		if (sendPreviewFrames_) {
			Toast.makeText(getApplicationContext(), "resume preview action", Toast.LENGTH_SHORT).show();
			preview_.sendData();
		} else {
			Toast.makeText(getApplicationContext(), "stop preview action, send own request", Toast.LENGTH_SHORT).show();
			matchId_ = 14;  // guiness factory 2
			preview_.stopData();
			serverConnection_.getMoreDetails();
		}
		openOptionsMenu();
		sendPreviewFrames_ = !sendPreviewFrames_;
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
	
	// data from Preview
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
    				matchId_ = Integer.parseInt(arr[i]);
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
    	} else {
    		matchId_ = INVALID;
    	}
    }

    public void handleMoreDetails(DetailsMap detailsMap) {
    	Toast.makeText(getApplicationContext(), "handling more details", Toast.LENGTH_SHORT).show();
    }
}