package com.ivl.cviewer;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

public class CViewerMenu extends RelativeLayout {

	public CViewerMenu(Context context) {
		super(context);
		setVisibility(View.GONE);
		
	}
	
	public void show() {
		setVisibility(View.VISIBLE);
	}
	
	public void hide() {
		setVisibility(View.GONE);
	}
}
