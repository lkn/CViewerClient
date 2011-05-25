package com.ivl.network;

import java.util.HashMap;

public class DetailsMap extends HashMap<Integer, String> {
	public static Integer DESCRIPTION_KEY = 0; 
	public static Integer COMMENTS_KEY = 1;
	public static Integer LATITUDE_KEY = 2;
	public static Integer LONGITUDE_KEY = 3;
	
	public DetailsMap(String data) {
		super();
		put(DESCRIPTION_KEY, "testdescription!!");
	}
}
