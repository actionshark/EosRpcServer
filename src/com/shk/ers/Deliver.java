package com.shk.ers;

import java.net.Socket;

public interface Deliver {
	void deliver(Socket socket, byte[] bs) throws Exception;
}
