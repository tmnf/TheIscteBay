package Connections;

import java.io.IOException;
import java.net.Socket;

import User.Client;

public abstract class GeneralConnection extends Thread {

	protected Client mainClient;

	protected Socket so;

	public GeneralConnection(Socket so, Client client) throws IOException {
		this.mainClient = client;
		this.so = so;
		startChannels();
	}

	public abstract void startChannels() throws IOException;

	public abstract void dealWith(Object aux) throws IOException;

	public abstract void send(Object ob);

}
