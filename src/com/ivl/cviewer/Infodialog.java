package com.ivl.cviewer;

import android.app.Dialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.ivl.cviewerclient.R;

public class Infodialog extends Dialog {

	private static String TAG = "Infodialog";
	private CountDownTimer timer_;
	private static int NUM_SECONDS_HOLD = 5;
	
	public Infodialog(Context context) {
		super(context);
		setContentView(R.layout.infodialog);
		ImageView img = (ImageView) findViewById(R.id.ImageView01);
		img.setImageResource(R.drawable.panda);
		setCancelable(true);
		setCanceledOnTouchOutside(false);
	}

	public void setText(String str) {
		TextView text = (TextView) findViewById(R.id.TextView01);
		text.setText(str);
		show();
		
		timer_ = new CountDownTimer(NUM_SECONDS_HOLD*1000, 1000) {
			
			@Override
			public void onFinish() {
				Log.i(TAG, "making infodialog disappear!");
				hide();
			}
			
			@Override
			public void onTick(long millisUntilFinished) {
			}
			
		}.start();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (timer_ != null) {
				timer_.cancel();
				timer_ = null;
			}
		}
		
		return true;
	}
}
