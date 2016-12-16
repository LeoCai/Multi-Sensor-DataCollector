package com.leocai.publiclibs.connection;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketImpl;
import java.net.UnknownHostException;
import java.util.Arrays;

import utils.DataUtils;

public class DataServerImpl implements DataServer {

	private static final int PORT = 10007;
	private ServerSocket serverSocket;
	private Socket sockect;
	private OutputStream out;
	private InputStream in;
	private boolean stop;
	private int[] selectedIds = new int[] { 0, 1, 2 };

	@Override
	public void startServer() throws IOException {
		serverSocket = new ServerSocket(PORT);
		serverSocket.setReuseAddress(true);
		System.out.println(getAddress() + ":" + PORT);
		sockect = serverSocket.accept();
		out = sockect.getOutputStream();
		in = sockect.getInputStream();
		System.out.println("connected");
	}

	@Override
	public void closeServer() throws IOException {
		in.close();
		out.close();
		sockect.close();
	}

	@Override
	public String getAddress() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			return "error ip";
		}
	}

	@Override
	public void receivedData() throws IOException {
		stop = false;
		while (!stop) {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
			String newLine = bufferedReader.readLine();
			if (newLine == null)
				continue;
			double data[] = DataUtils.parseData(newLine, selectedIds);
			dealWithSample(data);
		}
	}

	@Override
	public void dealWithSample(double[] data) {
		System.out.println(Arrays.toString(data));
	}

	@Override
	public void stopReceiveData() {
		stop = true;
	}

}
