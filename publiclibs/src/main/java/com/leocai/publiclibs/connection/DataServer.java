package com.leocai.publiclibs.connection;

import java.io.IOException;

public interface DataServer {
	
	void startServer() throws IOException;
	void closeServer() throws IOException;
	String getAddress();
	void receivedData() throws IOException;
	void dealWithSample(double[] data);
	void stopReceiveData();

}
