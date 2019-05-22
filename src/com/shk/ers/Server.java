package com.shk.ers;

import java.net.ServerSocket;
import java.net.Socket;

import com.shk.js.log.Level;
import com.shk.js.log.Logger;
import com.shk.js.thread.ThreadUtil;

public abstract class Server implements Runnable {
	private int mPort;

	private ServerSocket mServerSocket;

	public void setPort(int port) {
		mPort = port;
	}

	public void start() {
		ThreadUtil.getInstance().run(this);
	}

	public synchronized void close() {
		try {
			mServerSocket.close();
		} catch (Exception e) {
			Logger.print(Level.E, e);
		}

		mServerSocket = null;

		Logger.print(Level.I, "server " + mPort + " is closed");
	}

	@Override
	public void run() {
		synchronized (this) {
			if (mServerSocket != null) {
				Logger.print(Level.W, "server " + mPort + " is started!");
				return;
			}
		}

		try {
			mServerSocket = new ServerSocket(mPort);

			Logger.print(Level.I, "server " + mPort + " started.");

			while (true) {
				Socket socket = mServerSocket.accept();

				Logger.print(Level.V, "accept port " + mPort);

				ThreadUtil.getInstance().run(new Runnable() {
					@Override
					public void run() {
						try {
							onSocket(socket);
						} catch (Exception e) {
							Logger.print(Level.E, e);
						}
					}
				});
			}
		} catch (Exception e) {
			Logger.print(Level.E, e);
		}

		close();
	}

	public abstract void onSocket(Socket socket) throws Exception;
}
