package com.ivl.cviewer;

import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;


public class InfoView extends TextView {
	private static String TAG = "InfoView";
	
	private static int INFO_COLOR = Color.GREEN;
	private static int ERROR_COLOR = Color.RED;
	
	public InfoView(Context context) {
		super(context);
		//setBackgroundColor(Color.argb(200, 75, 0, 0));
	}
	
	public void setInfoText(String str) {
		setTextColor(INFO_COLOR);
		setText(str);
	}
	
	public void appendInfoText(String str) {
		setTextColor(INFO_COLOR);
		setText(str);
	}
	
	public void setErrorText(String str) {
		setTextColor(ERROR_COLOR);
		setText(str);
	}
	
	public void appendErrorText(String str) {
		setTextColor(ERROR_COLOR);
		setText(str);
	}
}
