package com.ivl.cviewer;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CommentsAdapter extends BaseAdapter {

	List<CommentEntry> mComments;
	Context mContext;
	
	public CommentsAdapter(Context context, List<CommentEntry> comments) {
		if (comments == null) {
			mComments = new ArrayList<CommentEntry>();
		} else {
			mComments = comments;
		}
		mContext = context;
	}
	
	public void setComments(List<CommentEntry> comments) {
		mComments = comments;
	}
	
	@Override
	public int getCount() {
		return mComments.size();
	}

	@Override
	public Object getItem(int position) {
		return mComments.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CommentEntry entry = mComments.get(position);
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.comment_row, null);
		}
		
		TextView commenter = (TextView) convertView.findViewById(R.id.commenter);;
		commenter.setText(entry.getUser());
		
		TextView blurb = (TextView) convertView.findViewById(R.id.blurb);
		blurb.setText(entry.getComment());
		
		return convertView;
	}

}
