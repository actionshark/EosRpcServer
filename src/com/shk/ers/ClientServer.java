package com.shk.ers;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.shk.js.data.Convert;
import com.shk.js.io.StreamReader;
import com.shk.js.log.Level;
import com.shk.js.log.Logger;

public class ClientServer extends Server implements Deliver {
	private int mId = 0;
	private Map<Integer, Socket> mSocketMap = new HashMap<>();

	private Socket mSocket;

	private Deliver mDeliver;

	@Override
	public void onSocket(Socket socket) throws Exception {
		if (mSocket != null) {
			try {
				mSocket.close();
			} catch (Exception e) {
				Logger.print(Level.E, e);
			}
		}

		mSocket = socket;

		InputStream is = socket.getInputStream();
		StreamReader sr = new StreamReader(is);

		while (true) {
			byte[] bs = new byte[4];
			sr.readFull(bs);
			int length = (int) Convert.bs2n(bs) - 4;

			Logger.print(Level.V, "client read length " + length);

			sr.readFull(bs);
			int id = (int) Convert.bs2n(bs);
			Socket s = mSocketMap.remove(id);

			Logger.print(Level.V, "client read id " + id);

			bs = new byte[length];
			sr.readFull(bs);

			if (s == null) {
				Logger.print(Level.W, "cannot find id " + id);
			} else {
				mDeliver.deliver(s, bs);
			}
		}
	}

	public void setDeliver(Deliver deliver) {
		mDeliver = deliver;
	}

	@Override
	public void deliver(Socket socket, byte[] bs) throws Exception {
		OutputStream os = mSocket.getOutputStream();

		os.write(Convert.n2bs(4 + bs.length, 4));

		int id = createId();
		os.write(Convert.n2bs(id, 4));
		mSocketMap.put(id, socket);

		os.write(bs);

		os.flush();
	}

	private synchronized int createId() {
		return ++mId;
	}
}
