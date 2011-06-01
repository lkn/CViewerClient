package com.ivl.cviewer;

import android.app.Dialog;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

public class Infodialog extends Dialog {

	private static String TAG = "Infodialog";
	
	public Infodialog(Context context) {
		super(context);
		setContentView(R.layout.infodialog);
		ImageView img = (ImageView) findViewById(R.id.ImageView01);
		img.setImageResource(R.drawable.ic_description_header);
		setCancelable(true);
		setCanceledOnTouchOutside(true);
	}

	public void setText(String str) {
		TextView text = (TextView) findViewById(R.id.TextView01);
		text.setText(str);
		show();
	}
}
