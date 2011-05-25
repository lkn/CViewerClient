package com.ivl.cviewer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;


class Preview extends SurfaceView implements SurfaceHolder.Callback {
	private static String TAG = "Preview";
    SurfaceHolder holder_;
    Camera camera_;
    Socket server_;
	private static int count_ = 0;
	private boolean sendData_;
    
    Preview(Context context, Socket server) {
        super(context);
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        holder_ = getHolder();
        holder_.addCallback(this);
        holder_.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setClickable(true);
//        setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Log.d(TAG, "click click!");
//				Toast.makeText(getContext(), "click!", Toast.LENGTH_SHORT).show();
//			}
//		});
//        
        if (server == null) {
        	Log.e(TAG, "Passed bad server socket to " + TAG);
        }
        server_ = server;
        sendData_ = false;
    }

    void sendData() {
    	sendData_ = true;
    }
    
    void stopData() {
    	sendData_ = false;
    }
    
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        camera_ = Camera.open();
        try {
           camera_.setPreviewDisplay(holder);
        } catch (IOException exception) {
            camera_.release();
            camera_ = null;
            // TODO: add more exception handling logic here
        }
    }

    // Surface will be destroyed when we return, so stop the preview.
    // Because the CameraDevice object is not a shared resource, it's very
    // important to release it when the activity is paused.
    public void surfaceDestroyed(SurfaceHolder holder) {
    	camera_.setPreviewCallback(null);
        camera_.stopPreview();
        camera_.release();
        camera_ = null;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        Camera.Parameters parameters = camera_.getParameters();
//        parameters.setPreviewSize(320, 240);
//        parameters.setPreviewFrameRate(15);
//        parameters.setSceneMode(Camera.Parameters.SCENE_MODE_NIGHT);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        camera_.setParameters(parameters);
    	
    	if (camera_ == null) {
    		Log.e(TAG, "No camera for preview!");
    		return;
    	}
    	
    	camera_.setPreviewCallback(new PreviewCallback() {
    		
    		// Send preview frames to server
			@Override
			public void onPreviewFrame(byte[] data, Camera camera) {
				if (!sendData_) return;
				
				++count_;

				// Don't want to overload the server
				if (count_ % 20 != 0) return;

				Log.d(TAG, "on preview frame " + count_);
				if (camera_ == null) {
					Log.e(TAG, "no camera for preview frame!");
					return;
				}
				
				Camera.Parameters parameters = camera_.getParameters();
				int imageFormat = parameters.getPreviewFormat();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] dataToSend = null;
                byte[] jpegBytes = null;
                if (imageFormat == ImageFormat.NV21) {
                	Log.d(TAG, "image format NV21");
                	int w = parameters.getPreviewSize().width;
                    int h = parameters.getPreviewSize().height;
                    YuvImage yuvImage = new YuvImage(data, imageFormat, w, h, null);
                    
                    // TODO: hardcoded.. the server assumes its a 160x120 image
                    Rect rect = new Rect(0, 0, 400, 300);
                    ByteArrayOutputStream tmp = new ByteArrayOutputStream();
                    yuvImage.compressToJpeg(rect, 90, tmp);

                    jpegBytes = tmp.toByteArray();
                    DataOutputStream tmp1 = new DataOutputStream(outputStream);
                    try {
                    	Log.d(TAG, "number of jpeg bytes: " + jpegBytes.length); 
						tmp1.writeShort(jpegBytes.length);
						tmp1.flush();
						byte b = 1;
						outputStream.write(68);
						outputStream.write(jpegBytes);
						
						dataToSend = outputStream.toByteArray();
						tmp.close();
						tmp1.close();
						outputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
                  } else if (imageFormat == ImageFormat.JPEG || imageFormat == ImageFormat.RGB_565) {
                	 Log.e(TAG, "TODO: image format JPEG or rgb");
                  }

                  if (dataToSend != null) {
                      try {
                		  Log.d(TAG, "Sending " + dataToSend.length + " bytes!***");
                		  if (server_ != null) {
                			  server_.getOutputStream().write(dataToSend);
                		  } else {
                			  Log.e(TAG, "no server to send to!");
                		  }
//                    	  if (count % 5 == 0) {
//	                      Log.d(TAG, "storing: " + Environment.getExternalStorageDirectory().getAbsolutePath()
//  	                  		  + "\nstate: " + Environment.getExternalStorageState());
//	                          File root = Environment.getExternalStorageDirectory();
//	                          
//	                          FileOuputStream fileStream = new FileOutputStream(root + "/" + "image" + ++count + ".jpg");
//	                          fileStream.write(jpegBytes);
//	                          fileStream.flush();
//	                          fileStream.close();
//                    	  }
                      } catch (FileNotFoundException e) {
                          Log.e(TAG, e.toString() );
                      } catch (IOException e) {
                          Log.e(TAG, e.toString() );
                      }
                  } else {
                	  Log.e(TAG, "WHY NO DATA");
                  }
                  dataToSend = null;
			} 
    	});
    	
        camera_.startPreview();
    }
    
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//    	Log.d(TAG, "touch touch!!!!!!");
////    	Toast.makeText(getContext(), "touch!", Toast.LENGTH_SHORT).show();
//    	return true;
//    }
}
