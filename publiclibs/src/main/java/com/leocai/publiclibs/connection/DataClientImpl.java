package com.leocai.publiclibs.connection;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class DataClientImpl implements DataClient {

	private OutputStream out;
	private InputStream in;
	private Socket socket;

	@Override
	public void connect(String address, int port) throws UnknownHostException, IOException {
		socket = new Socket(address, port);
		out = socket.getOutputStream();
		in = socket.getInputStream();

	}

	@Override
	public void disconnect() throws IOException {
		in.close();
		out.close();
		socket.close();
	}

	@Override
	public void sendSample(String data) throws IOException {
		PrintWriter bufferedWriter = new PrintWriter(new OutputStreamWriter(out));
		bufferedWriter.println(data);
		bufferedWriter.flush();
	}

}
