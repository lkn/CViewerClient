package com.ivl.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;


import android.app.Activity;
import android.util.Log;

public class TCPListenHandler implements Runnable {

	private TCPListener listener_;
	private Activity activity_;
	private Socket server_;

	public TCPListenHandler(TCPListener listener, Activity activity, Socket server) {
		listener_ = listener;
		activity_ = activity;
		server_ = server;
	}
	
	public synchronized void run() {
		try {
			// listen to server messages
			while (true) {
				BufferedReader in = new BufferedReader(
						new InputStreamReader(server_.getInputStream()));
				final String str = in.readLine();
				activity_.runOnUiThread(new Runnable() {
					public void run() {
						listener_.callCompleted(str);
					}
				});
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
