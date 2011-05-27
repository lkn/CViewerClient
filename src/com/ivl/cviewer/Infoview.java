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
	private static int BORDER_PADDING = 5;
	
	public InfoView(Context context) {
		super(context);
		setClickable(false);
		setPadding(BORDER_PADDING, BORDER_PADDING, BORDER_PADDING, BORDER_PADDING);
	}
	
	public void setInfoText(String str) {
		setTextColor(INFO_COLOR);
		setText(str);
	}
	
	public void appendInfoText(String str) {
		setTextColor(INFO_COLOR);
		setText(str + "\n");
	}
	
	public void setErrorText(String str) {
		setTextColor(ERROR_COLOR);
		setText(str);
	}
	
	public void appendErrorText(String str) {
		setTextColor(ERROR_COLOR);
		setText(str + "\n");
	}

	public void clear() {
		setText("");
	}
}
