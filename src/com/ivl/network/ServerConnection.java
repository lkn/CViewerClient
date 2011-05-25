package com.ivl.network;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.ivl.cviewer.CViewerClient;

public class ServerConnection {
	public static final int PORT = 1111;
	public static final String HOST = "192.168.1.104";//"pumice.ucsd.edu";
	
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
	
	public void getMoreDetails() {
		DetailsMap moreDetails = new DetailsMap("test");
		client_.handleMoreDetails(moreDetails);
	}
}
