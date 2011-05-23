package com.ivl.cviewer;

import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;


public class Infoview extends TextView {
	private static String TAG = "Infoview";
	private CountDownTimer timer_;
	
	public Infoview(Context context) {
		super(context);
		setBackgroundColor(Color.argb(200, 75, 0, 0));
	}
	
	public void setText(String str) {
		super.setText(str);
		setVisibility(View.VISIBLE);
//		timer_ = new CountDownTimer(5000, 1000) {
//			
//			@Override
//			public void onFinish() {
//				Log.i(TAG, "making infoview disappear!");
//				setVisibility(View.GONE);
//			}
//			
//			@Override
//			public void onTick(long millisUntilFinished) {
//			}
//			
//		}.start();
	}
	
}
