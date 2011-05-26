package com.ivl.cviewer;

import java.io.IOException;
import java.net.UnknownHostException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ivl.cviewerclient.R;
import com.ivl.network.MatchedImage;
import com.ivl.network.ServerConnection;
import com.ivl.network.TCPListenHandler;
import com.ivl.network.TCPListener;

public class CViewerClient extends Activity implements TCPListener, OnClickListener {
	private static String TAG = "CViewerClient";

	private Preview preview_;
	private InfoView infoView_;
	private Infodialog descriptionBox_;
	private Dialog commentBox_;

	private ServerConnection serverConnection_;
	private MatchedImage matchedImage_;
	
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
        
		LinearLayout wholeFrame = new LinearLayout(this);
        FrameLayout frame = new FrameLayout(this);
        preview_ = new Preview(this, serverConnection_);
        preview_.setOnClickListener(this);
        
        frame.addView(preview_);
        frame.addView(infoView_);
        wholeFrame.addView(frame);
        
        setContentView(wholeFrame);
        
        matchedImage_ = null;
        preview_.sendData();
        
        descriptionBox_ = new Infodialog(this);
        
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.commentdialog, null);
        commentBox_ = new AlertDialog.Builder(CViewerClient.this)
	        .setIcon(R.drawable.alert_dialog_icon)
	        .setTitle(R.string.commentdialog_title)
	        .setView(textEntryView)
	        .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	            	EditText a = (EditText) textEntryView.findViewById(R.id.username_edit);
	            	EditText b = (EditText) textEntryView.findViewById(R.id.comment_edit);
	            	Log.i(TAG, "user: " + a + " sending comment: " + b);
	            	serverConnection_.sendComment(a.getText().toString(), b.getText().toString(), matchedImage_.id());
	            }
	        })
	        .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	            	preview_.sendData();
	         }
        })
        .create();
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
        		descriptionBox_.setText(matchedImage_.description());
                break;
        	case R.id.map:
                break;
        	case R.id.comment:
        		commentBox_.show();
        		break;
        	case R.id.view_comments:
        		break;
        	default:
        		return false;
        }
        preview_.sendData();
        return true;
    } 

	@Override
	public void onClick(View view) {
		if (preview_.isSending()) {
			Toast.makeText(getApplicationContext(), "stop preview action", Toast.LENGTH_SHORT).show();
			preview_.stopData();
			
		} else {
			Toast.makeText(getApplicationContext(), "resume preview action", Toast.LENGTH_SHORT).show();
			preview_.sendData();
		}
		openOptionsMenu();
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
    	if (info == null) return;

		displayDetails(info);
    }

	private void displayDetails(String info) {
		if (info == null || info.equals("-1")) {
			infoView_.clear();
    		matchedImage_ = null;
    		return;
		}
		
		matchedImage_ = new MatchedImage(info);
		Log.d(TAG, "details parsed: " + matchedImage_.details());
		infoView_.setInfoText(matchedImage_.details());
	}

}