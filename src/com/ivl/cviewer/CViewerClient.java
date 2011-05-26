package com.ivl.cviewer;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ivl.cviewer.CustomMenu.OnMenuItemSelectedListener;
import com.ivl.cviewerclient.R;
import com.ivl.network.MatchedImage;
import com.ivl.network.ServerConnection;
import com.ivl.network.TCPListenHandler;
import com.ivl.network.TCPListener;

public class CViewerClient extends Activity implements OnMenuItemSelectedListener, TCPListener, OnClickListener {
	private static String TAG = "CViewerClient";

	CViewerMenu menu_;
	/**
	 * Some global variables.
	 */
	private CustomMenu mMenu;
	public static final int MENU_DESCRIPTION = 1;
	public static final int MENU_MAP = 2;
	public static final int MENU_MAKE_COMMENT = 3;
	public static final int MENU_VIEW_COMMENTS = 4;
	
	
	private Preview preview_;
	private InfoView infoView_;
	private Infodialog descriptionBox_;
	private Dialog makeCommentBox_;

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
        
		RelativeLayout wholeFrame = new RelativeLayout(this);
        FrameLayout frame = new FrameLayout(this);
        preview_ = new Preview(this, serverConnection_);
        preview_.setOnClickListener(this);
        
        frame.addView(preview_);
        frame.addView(infoView_);
        wholeFrame.addView(frame);
        
        menu_ = new CViewerMenu(this);
        wholeFrame.addView(menu_);
        
        setContentView(wholeFrame);
        
        matchedImage_ = null;
        preview_.sendData();
        
        descriptionBox_ = new Infodialog(this);
        
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.commentdialog, null);
        makeCommentBox_ = new AlertDialog.Builder(CViewerClient.this)
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
        
        //initialize the menu
        mMenu = new CustomMenu(this, this, getLayoutInflater());
        mMenu.setHideOnSelect(true);
        mMenu.setItemsPerLineInPortraitOrientation(4);
        mMenu.setItemsPerLineInLandscapeOrientation(8);
        mMenu.hide();
        //load the menu items
        loadMenuItems();
    }

	/**
     * Toggle our menu on user pressing the menu key.
     */
	private void doMenu() {
		if (mMenu.isShowing()) {
			mMenu.hide();
		} else {
			//Note it doesn't matter what widget you send the menu as long as it gets view.
			mMenu.show(infoView_);
		}
	}
	

	/**
     * For the demo just toast the item selected.
     */
	@Override
	public void MenuItemSelectedEvent(CustomMenuItem selection) {
		Toast t = Toast.makeText(this, "You selected item #"+Integer.toString(selection.getId()), Toast.LENGTH_SHORT);
		t.setGravity(Gravity.CENTER, 0, 0);
		t.show();
		
		switch (selection.getId()) {
			case MENU_DESCRIPTION:
				descriptionBox_.setText(matchedImage_.description());
				break;
			case MENU_MAP:
				break;
			case MENU_MAKE_COMMENT:
				makeCommentBox_.show();
				break;
			case MENU_VIEW_COMMENTS:
				break;
		}
	}
	
    @Override
    protected void onPause() {
    	super.onPause();
    	preview_.stopData();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	preview_.sendData();
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	if (serverConnection_ != null) {
    		serverConnection_.close();
    	}
    }
   
    /**
     * Load up our menu.
     */
	private void loadMenuItems() {
		//This is kind of a tedious way to load up the menu items.
		//Am sure there is room for improvement.
		ArrayList<CustomMenuItem> menuItems = new ArrayList<CustomMenuItem>();
		CustomMenuItem cmi = new CustomMenuItem();
		Resources res = getResources();
		cmi.setCaption(res.getString(R.string.description));
		cmi.setImageResourceId(R.drawable.icon1);
		cmi.setId(MENU_DESCRIPTION);
		menuItems.add(cmi);
		cmi = new CustomMenuItem();
		cmi.setCaption(res.getString(R.string.map));
		cmi.setImageResourceId(R.drawable.icon2);
		cmi.setId(MENU_MAP);
		menuItems.add(cmi);
		cmi = new CustomMenuItem();
		cmi.setCaption(res.getString(R.string.comment));
		cmi.setImageResourceId(R.drawable.icon3);
		cmi.setId(MENU_MAKE_COMMENT);
		menuItems.add(cmi);
		cmi = new CustomMenuItem();
		cmi.setCaption(res.getString(R.string.view_comments));
		cmi.setImageResourceId(R.drawable.icon4);
		cmi.setId(MENU_VIEW_COMMENTS);
		menuItems.add(cmi);
		if (!mMenu.isShowing()) {
			try {
				mMenu.setMenuItems(menuItems);
			} catch (Exception e) {
				AlertDialog.Builder alert = new AlertDialog.Builder(this);
				alert.setTitle("Egads!");
				alert.setMessage(e.getMessage());
				alert.show();
			}
		}
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
		
		doMenu();
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