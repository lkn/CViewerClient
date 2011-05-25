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
	
	public Infodialog(Context context) {
		super(context);
		setContentView(R.layout.infodialog);
		ImageView img = (ImageView) findViewById(R.id.ImageView01);
		img.setImageResource(R.drawable.panda);
		setCancelable(true);
		setCanceledOnTouchOutside(true);
	}

	public void setText(String str) {
		TextView text = (TextView) findViewById(R.id.TextView01);
		text.setText(str);
		show();
	}
}
