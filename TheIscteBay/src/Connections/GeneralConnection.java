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

	protected abstract void startChannels() throws IOException;

	protected abstract void dealWith(Object aux) throws IOException;

	protected abstract void send(Object ob);

}
