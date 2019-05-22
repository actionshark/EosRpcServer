package com.shk.ers;

import com.shk.js.log.FileLogger;
import com.shk.js.log.Level;
import com.shk.js.log.Logger;

public class Main {
	public static void main(String[] args) {
		FileLogger fl = new FileLogger();
		fl.setFiles("log1.txt", "log2.txt");

		Logger.setInstance(fl);
		Logger.setLevel(Level.V);

		int httpPort = 10002;
		int clientPort = 10003;

		for (int i = 0; i < args.length; i++) {
			String str = args[i];

			if ("-http".equals(str)) {
				httpPort = Integer.parseInt(args[i++]);
			} else if ("-client".equals(str)) {
				clientPort = Integer.parseInt(args[i++]);
			}
		}

		Logger.printf(Level.I, "http port is %d, client port is %d.", httpPort, clientPort);

		HttpServer hs = new HttpServer();
		hs.setPort(httpPort);

		ClientServer cs = new ClientServer();
		cs.setPort(clientPort);

		hs.setDeliver(cs);
		cs.setDeliver(hs);

		hs.start();
		cs.start();
	}
}
