package Connections;

import java.io.IOException;
import java.net.Socket;

import Client.Client;

public abstract class GeneralConnection extends Thread {

	// Main Client
	protected Client mainClient;

	// Socket
	protected Socket so;

	public GeneralConnection(Socket so, Client client) throws IOException {
		this.mainClient = client;
		this.so = so;
		startChannels();
	}

	/* Starts object/string channels */
	protected abstract void startChannels() throws IOException;

	/* Deals with the object received */
	protected abstract void dealWith(Object aux) throws IOException;

	/* Sends an object */
	protected abstract void send(Object ob);

}
