package com.ivl.network;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Comment;

import android.util.Log;

import com.ivl.cviewer.CommentEntry;

public class MatchedImage {
	public static String TAG = "MatchedImage";
	private int id_;
	private StringBuffer details_;
	private String description_;
	private float gps_lat_;
	private float gps_long_;
	private List<CommentEntry> commentEntries_;
	public String name_;
	
	public MatchedImage(String info) {
		Log.i(TAG, "parsing " + info);
		String[] chunks = info.split("@");
		Log.i(TAG, "First split: " + chunks.length);
		Log.i(TAG, "first chunk: " + chunks[0].toString());
		parseEverything(chunks[0]);
		
		// we also have comments!
		commentEntries_ = null;
		if (chunks.length > 1) {
			parseComments(chunks[1]);
		}
	}

	// only deals w/ valid comments
	private void parseComments(String string) {
		commentEntries_ = new ArrayList<CommentEntry>();
		String[] tuples = string.split("#");
		for (int i = 0; i < tuples.length; ++i) {
			String[] tuple = tuples[i].split(":");
			if (tuple.length == 2) {
				commentEntries_.add(new CommentEntry(tuple[0], tuple[1]));
			}
		}
	}

	private void parseEverything(String info) {
		Log.i(TAG, "parse everything: " + info);
		description_ = "";
		gps_lat_ = 0;
		gps_long_ = 0;
		
		details_ = new StringBuffer();
		String[] arr = info.split("#");
		Log.i(TAG, "arr length: " + arr.length);
		for (int i = 0; i < arr.length; ++i) {
			Log.i(TAG, "everything: " + arr[i]);
			switch (i) {
			case 0:  // image name
				details_.append("Name: ").append(arr[i]).append("\n");
				name_ = arr[i];
				break;
			case 1:  // id
				id_ = Integer.parseInt(arr[i]);
				break;
			case 2:  // date time
				details_.append("Date Taken: ").append(arr[i]).append("\n");
				break;
			case 3: // camera model
				details_.append("Camera Model: ").append(arr[i]).append("\n");
				break;
			case 4:  // shutter speed
				details_.append("Shutter Speed: ").append(arr[i]).append("\n");
				break;
			case 5:  // focal length
				details_.append("Focal Length: ").append(arr[i]).append("\n");
				break;
			case 6:  // description
				description_ = arr[i];
				break;
			case 7:  // gps lat
				gps_lat_ = Float.valueOf(arr[i]);
				break;
			case 8:  // gps long
				gps_long_ = Float.valueOf(arr[i]);
				break;
			}
		}
	}
	
	public int id() {
		return id_;
	}
	
	public String details() {
		return details_.toString();
	}
	
	public void setDetails(String str) {
		details_ = new StringBuffer(str);
	}
	
	public float gps_lat() {
		return gps_lat_;
	}
	
	public float gps_long() {
		return gps_long_;
	}
	
	public String description() {
		return description_;
	}
	
	public void setDescription(String str) {
		description_ = str;
	}
	
	public List<CommentEntry> commentEntries() {
		return commentEntries_;
	}
	
	public boolean hasGPS() {
		return gps_lat_ != 0 && gps_long_ != 0;
	}
	
	public String name() {
		return name_;
	}
}
