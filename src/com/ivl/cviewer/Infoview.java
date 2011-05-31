package com.ivl.cviewer;

import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;


public class InfoView extends TextView {
	private static String TAG = "InfoView";
	
	private static int INFO_COLOR = Color.GREEN;
	private static int ERROR_COLOR = Color.RED;
	private static int BORDER_PADDING = 5;
	private static int INFO_BACKGROUND = Color.argb(150, 139, 139, 131);
	
	public InfoView(Context context) {
		super(context);
		setClickable(false);
		setPadding(BORDER_PADDING, BORDER_PADDING, BORDER_PADDING, BORDER_PADDING);
		setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	}
	
	public void setInfoText(String str) {
		setTextColor(INFO_COLOR);
		if (str.endsWith("\n")) {
			setText(str.substring(0, str.length()-1));
		} else {
			setText(str);
		}
		
		setBackgroundColor(INFO_BACKGROUND);
	}
	
	public void appendInfoText(String str) {
		setTextColor(INFO_COLOR);
		append("\n" + str);
		setBackgroundColor(INFO_BACKGROUND);
	}
	
	public void setErrorText(String str) {
		setTextColor(ERROR_COLOR);
		setText(str);
	}
	
	public void appendErrorText(String str) {
		setTextColor(ERROR_COLOR);
		append("\n" + str);
	}

	public void clear() {
		setText("");
		setBackgroundColor(Color.TRANSPARENT);
	}
}
