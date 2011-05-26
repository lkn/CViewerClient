package com.ivl.cviewer;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class MapsActivity extends MapActivity {    
	public static String TAG = "MapsActivity";
    MapView mapView; 
    MapController mc;
    GeoPoint p;
 
    class MapOverlay extends com.google.android.maps.Overlay {
        @Override
        public boolean draw(Canvas canvas, MapView mapView, 
        boolean shadow, long when) {
            super.draw(canvas, mapView, shadow);                   
 
            //---translate the GeoPoint to screen pixels---
            Point screenPts = new Point();
            mapView.getProjection().toPixels(p, screenPts);
 
            //---add the marker---
            Bitmap bmp = BitmapFactory.decodeResource(
                getResources(), R.drawable.pushpin);            
            canvas.drawBitmap(bmp, screenPts.x, screenPts.y-50, null);         
            return true;
        }
    } 
 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.i(TAG, "creating map");
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	float gps_lat = extras.getFloat("GPS_LAT");
        	float gps_long = extras.getFloat("GPS_LONG");
        	Log.i(TAG, "overlaying at: " + gps_lat + " " + gps_long);
            p = new GeoPoint((int) (gps_lat * 1e6), (int) (gps_long * 1e6));
            mc.animateTo(p);
            mc.setZoom(17); 
     
            //---Add a location marker---
            MapOverlay mapOverlay = new MapOverlay();
            List<Overlay> listOfOverlays = mapView.getOverlays();
            listOfOverlays.clear();
            listOfOverlays.add(mapOverlay);    
        }

        mapView.invalidate();
    }
 
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
}