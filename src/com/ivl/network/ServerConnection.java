package com.ivl.network;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

import com.ivl.cviewer.CViewerClient;

public class ServerConnection {
	private static String TAG = "Server Connection";
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

	public ServerConnection(CViewerClient client) throws UnknownHostException, IOException {
		client_ = client;
		serverSocket_ = new Socket(HOST, PORT);
		serverSocket_.setTcpNoDelay(true);
	}
	
	public void close() {
		if (serverSocket_ != null) {
			try {
				serverSocket_.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
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
	}
	
	// append header, send jpeg bytes to server
	public void sendPreviewFrame(byte[] jpegBytes) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream tmp = new DataOutputStream(outputStream);
        try {
		  	Log.d(TAG, "number of jpeg bytes: " + jpegBytes.length); 
			tmp.writeShort(jpegBytes.length);
			tmp.flush();
			outputStream.write(R_DETAILS);
			outputStream.write(jpegBytes);
			
			send(outputStream.toByteArray());
			tmp.close();
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void getMoreDetails() {
		DetailsMap moreDetails = new DetailsMap("test");
		client_.handleMoreDetails(moreDetails);
	}
}
