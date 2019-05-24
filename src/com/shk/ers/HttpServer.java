package com.shk.ers;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.shk.js.log.Level;
import com.shk.js.log.Logger;

public class HttpServer extends Server implements Deliver {
	private Deliver mDeliver;

	public void setDeliver(Deliver deliver) {
		mDeliver = deliver;
	}

	@Override
	public void onSocket(Socket socket) throws Exception {
		socket.setSoTimeout(1000);
		InputStream is = socket.getInputStream();
		
		HttpReader hr = new HttpReader(is);
		byte[] bs = hr.read();

		Logger.print(Level.V, "http read socket " + socket.hashCode());
		Logger.print(Level.V, "http read length " + bs.length);

//		try {
//			String str = new String(bs);
//			Logger.print(Level.V, "http read content", str);
//		} catch (Exception e) {
//		}

		mDeliver.deliver(socket, bs);
	}

	@Override
	public void deliver(Socket socket, byte[] bs) throws Exception {
		Logger.print(Level.V, "http write socket " + socket.hashCode());

//		try {
//			String str = new String(bs);
//			Logger.print(Level.V, "http write content", str);
//		} catch (Exception e) {
//		}

		OutputStream os = socket.getOutputStream();
		os.write(bs);
		os.flush();

		socket.close();
	}
}
