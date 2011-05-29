package com.ivl.network;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

import com.ivl.cviewer.CViewerClient;
import com.ivl.cviewer.InfoView;

public class ServerConnection {
	private static String TAG = "ServerConnection";
	public static final int PORT = 1111;
	public static final String HOST = "192.168.1.104";//"pumice.ucsd.edu";

	// request details 
	private static final int R_DETAILS = 68;  // D
	// send a comment
	private static final int S_COMMENT = 67;  // C
	// request more details
	private static final int R_MORE = 77;     // M
	
	private final CViewerClient client_;
	private final Socket serverSocket_;
	
	private final InfoView infoView_;

	public ServerConnection(CViewerClient client, InfoView infoView) throws UnknownHostException, IOException {
		client_ = client;
		serverSocket_ = new Socket(HOST, PORT);
		serverSocket_.setTcpNoDelay(true);
		infoView_ = infoView;
	}
	
	public void close() {
		if (serverSocket_ != null) {
			try {
				serverSocket_.close();
			} catch (IOException e) {
				infoView_.appendErrorText("Unable to close server: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	public Socket getSocket() {
		return serverSocket_;
	}

	protected void send(byte[] bytes) throws IOException {
		if (serverSocket_ == null) {
			Log.e(TAG, "No server to send to!");
			return;
		}
		if (bytes == null) {
			Log.e(TAG, "No bytes to send!");
			return;
		}
		
		Log.d(TAG, "Sending " + bytes.length + " bytes!***");
		serverSocket_.getOutputStream().write(bytes);
		serverSocket_.getOutputStream().flush();  // TODO: hope this works...
	}
	
	// append header, send jpeg bytes to server
	public void sendPreviewFrame(byte[] jpegBytes) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream tmp = new DataOutputStream(outputStream);
        try {
		  	Log.d(TAG, "number of jpeg bytes: " + jpegBytes.length); 
			
		  	// the header
		  	tmp.writeInt(jpegBytes.length);
			tmp.flush();
			outputStream.write(R_DETAILS);
			
			outputStream.write(jpegBytes);
			send(outputStream.toByteArray());
			
			tmp.close();
			outputStream.close();
		} catch (IOException e) {
			Log.e(TAG, "ugh");
			infoView_.appendErrorText("Unable to send preview frame: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	// get more details for this image id
	// not used because now i just get all the data on the first go
	public void getMoreDetails(int imageId) {
		try {
			byte[] asciiIdBytes = Integer.toString(imageId).getBytes("US-ASCII");
			
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			DataOutputStream tmp = new DataOutputStream(outputStream);
			
			// header
			tmp.writeInt(asciiIdBytes.length);
			tmp.flush();
			outputStream.write(R_MORE);
			
			outputStream.write(asciiIdBytes);
			send(outputStream.toByteArray());
			
			tmp.close();
			outputStream.close();
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, "crap");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// need to null terminate string to send to c++ server
	public void sendComment(String user, String comment, int imageId) {
        try {
        	byte[] dataToSend = new String(user + "#" + comment + "#" + Integer.toString(imageId)).getBytes("US-ASCII");
        	
        	
        	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        	DataOutputStream tmp = new DataOutputStream(outputStream);
		  	Log.d(TAG, "number of string bytes: " + dataToSend.length); 
			
		  	// the header
		  	tmp.writeInt(dataToSend.length +1);
			tmp.flush();
			outputStream.write(S_COMMENT);
			
			outputStream.write(dataToSend);
			outputStream.write(0);
			send(outputStream.toByteArray());
			
			tmp.close();
			outputStream.close();
		} catch (IOException e) {
			Log.e(TAG, "poop");
			infoView_.appendErrorText("Unable to send comment: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
