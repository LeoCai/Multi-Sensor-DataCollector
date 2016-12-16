package com.leocai.publiclibs.connection;

import java.io.IOException;
import java.net.UnknownHostException;

public interface DataClient {

	void connect(String address, int port) throws UnknownHostException, IOException;

	void disconnect() throws IOException;

	void sendSample(String data) throws IOException;

}
